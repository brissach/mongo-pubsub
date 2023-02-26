package gg.clouke.mps;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import gg.acai.acava.io.Closeable;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * @author Clouke
 * @since 24.02.2023 05:41
 * © mongo-pubsub - All Rights Reserved
 */
public class CollectionWatcher implements Closeable {

  private final Thread executor;
  private final Waiter waiter;

  public CollectionWatcher(MongoPubSubClient client) {
    ChangeStreamIterable<Document> observer = client.publishers()
      .watch()
      .fullDocument(FullDocument.UPDATE_LOOKUP);

    ThreadFactory factory = new ThreadFactoryBuilder()
      .setPriority(Thread.MAX_PRIORITY)
      .setNameFormat("CollectionWatcher-%d")
      .setUncaughtExceptionHandler(new ThreadInterrupter())
      .build();

    executor = factory.newThread(() ->
      observer.forEach((Consumer<? super ChangeStreamDocument<Document>>)
        change -> {
          OperationType operationType = change.getOperationType();
          Document document = change.getFullDocument();
          if (document == null)
            return;

          if (operationType == OperationType.INSERT) {
            Payload payload = new Payload(document.toJson());
            String target = document.getString("payload:target");
            client.subscribers().dispatch(target, payload);
            Date date = document.getDate("payload:send");
            if (date != null) {
              long time = date.getTime();
              long now = System.currentTimeMillis();
              long diff = now - time;
              System.out.println("Dispatched " + payload + " to " + target + " after " + diff + "ms");
            }
          }
        }));

    waiter = new Waiter();
    executor.start();
  }

  @Nonnull
  public Thread executor() {
    return executor;
  }

  @Nonnull
  public Waiter waiter() {
    synchronized (executor) {
      return waiter;
    }
  }

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
