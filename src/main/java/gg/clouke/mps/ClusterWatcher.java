package gg.clouke.mps;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import gg.acai.acava.io.Closeable;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * @author Clouke
 * @since 24.02.2023 05:41
 * © mongo-pubsub - All Rights Reserved
 */
public class ClusterWatcher implements Closeable {

  private final Thread executor;

  public ClusterWatcher(MongoPubSubClient client) {
    ChangeStreamIterable<Document> observer = client.publishers()
      .watch()
      .fullDocument(FullDocument.UPDATE_LOOKUP);

    ThreadFactory factory = new ThreadFactoryBuilder()
      .setPriority(Thread.MAX_PRIORITY)
      .setNameFormat("ClusterWatcher-%d")
      .setUncaughtExceptionHandler(new ThreadInterrupter())
      .build();

    executor = factory.newThread(() ->
      observer.forEach((Consumer<? super ChangeStreamDocument<Document>>)
        change -> {
          OperationType operationType = change.getOperationType();
          Document document = change.getFullDocument();
          if (document == null)
            return;

          switch (operationType) {
            case INSERT:
              if (document.containsKey("packet::schema")) {
                System.out.println("Schema: " + document);
              }
              break;
            case DELETE:
              System.out.println("Dropped: " + document);
              break;
          }
        }));

    executor.start();
  }

  @Nonnull
  public Thread executor() {
    return executor;
  }

  @Override
  public void close() {
    synchronized (executor) {
      executor.interrupt();
    }
  }

  private static class ThreadInterrupter implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      System.err.println("Thread " +
              t.getName() +
              " threw an exception: " +
              e.getMessage());
    }
  }
}
