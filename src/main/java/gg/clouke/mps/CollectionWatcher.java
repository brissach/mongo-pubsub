package gg.clouke.mps;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import gg.acai.acava.io.Closeable;
import gg.clouke.mps.codec.Codec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * An observer that watches for changes in the publisher collection.
 * Responsible for dispatching the payload to the subscribers.
 *
 * <p>Runs on a separate thread, and is responsible for notifying the subscribers of
 * changes in the publisher collection.
 * <p>Holds a {@link Waiter} which manipulates the executor thread to
 * wait for its next payload to be finished before closing the client.
 *
 * @author Clouke
 * @since 24.02.2023 05:41
 * © mongo-pubsub - All Rights Reserved
 */
public class CollectionWatcher implements Closeable {

  /**
   * Standard encoder converting a MongoDB document to a Json string.
   */
  private static final Codec<Document, String> CODEC =
    new Codec<Document, String>() {
      @Override
      public String encode(Document document) {
        return document.toJson();
      }
    };

  private final Thread executor;
  private final Waiter waiter;

  public CollectionWatcher(MongoPubSubClient client) {
    ChangeStreamIterable<Document> observer = client.publishers()
      .watch()
      .fullDocument(FullDocument.UPDATE_LOOKUP);

    /*
     * Set up the executor thread.
     */
    ThreadFactory factory = new ThreadFactoryBuilder()
      .setPriority(Thread.MAX_PRIORITY)
      .setNameFormat("CollectionWatcher-%d")
      .setUncaughtExceptionHandler(new ThreadInterrupter())
      .build();

    waiter = new Waiter();
    executor = factory.newThread(() ->
      observer.forEach((Consumer<? super ChangeStreamDocument<Document>>)
        change -> {
          OperationType operation = change.getOperationType();
          Document document = change.getFullDocument();
          if (document == null)
            return; // cannot handle null documents

          if (operation == OperationType.INSERT) {
            String parameters = CODEC.encode(document);
            Payload payload = new Payload(parameters);
            String target = document.getString("payload:target");
            client.subscribers().dispatch(target, payload);
            // TODO: Add to graph statistics
            if (waiter.isAwaitTermination()) {
              waiter.reset();
              synchronized (waiter) {
                waiter.notifyAll(); // notify the waiter
              }
            }
          }
        }));

    executor.start();
  }

  /**
   * Gets the executor thread of this watcher.
   *
   * @return the executor thread.
   */
  @Nonnull
  public Thread executor() {
    return executor;
  }

  /**
   * Gets the waiter of this watcher.
   *
   * @return the waiter.
   */
  @Nonnull
  public Waiter waiter() {
    synchronized (executor) {
      return waiter;
    }
  }

  /**
   * Closes this watcher, and interrupts the executor thread.
   * If the {@link Waiter} is waiting for a payload to finish, the thread will
   * wait for 1 second until it is interrupted.
   *
   * @throws RuntimeException if the thread is interrupted.
   */
  @Override
  public void close() {
    synchronized (executor) {
      synchronized (waiter) {
        if (waiter.isAwaitTermination()) {
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        executor.interrupt();
      }
    }
  }

  /**
   * Thread interrupter that prints the stack trace of the exception occurring
   */
  private static class ThreadInterrupter
    implements Thread.UncaughtExceptionHandler {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        System.err.println("Thread " +
          t.getName() +
          " threw an exception: "
        );
        e.printStackTrace();
      }
    }
}
