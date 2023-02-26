package gg.clouke.mps;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import gg.acai.acava.Requisites;
import gg.acai.acava.annotated.Use;
import gg.acai.acava.io.Closeable;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Represents the main class of the mongo-pubsub library.
 *
 * <p>Responsible for managing the connection to the database and the collection,
 * allowing interaction with the mongo client, watcher and the subscribers.
 *
 * <p>Holds an instance of the {@link MongoPubSubClient} class, which can be accessed
 * through the {@link #getInstance()} method.
 *
 * <p><strong>NOTE:</strong> cannot be instantiated directly, use {@link
 * #newBuilder()} to create a new instance.
 *
 * <p><b>Throws:</b>
 * <ul>
 *   <li>{@link IllegalStateException} if the client is already initialized.</li>
 * </ul>
 *
 * @author Clouke
 * @since 24.02.2023 05:12
 * © mongo-pubsub - All Rights Reserved
 */
@Use("Use MongoClientBuilder to create a new instance of this class.")
public final class MongoPubSubClient implements Closeable {

  private static MongoPubSubClient INSTANCE;

  /**
   * Gets the instance of the {@link MongoPubSubClient} class.
   *
   * @return the instance of this client.
   */
  public static MongoPubSubClient getInstance() {
    return INSTANCE;
  }

  /**
   * Creates a new instance of the {@link MongoPubSubClient} class.
   *
   * @return a new instance of this client.
   */
  public static MongoClientBuilder newBuilder() {
    return new MongoClientBuilder();
  }

  private final CollectionWatcher watcher;
  private final MongoClient client;
  private final MongoCollection<Document> publishers;
  private final Subscribers subscribers;

  /**
   * Constructs a new instance of the {@link MongoPubSubClient} class.
   * delegates the construction from the {@link MongoClientBuilder}.
   *
   * @param b the builder to construct the client.
   * @throws IllegalStateException if the client is already initialized.
   */
  public MongoPubSubClient(MongoClientBuilder b) {
    if (INSTANCE != null)
      throw new IllegalStateException("MongoPubSubClient is already initialized.");

    INSTANCE = this;
    MongoClientSettings settings = doBuildProcedure(b);
    MongoClient client = MongoClients.create(settings);
    this.client = client;
    publishers = client
      .getDatabase(b.database)
      .getCollection("publishers");

    /*
     * Check for the clearPreviousIndexes flag, if true, drop the index
     */
    if (b.clearPreviousIndexes) {
      publishers.dropIndex(Indexes.ascending("payload:send"));
    }

    if (b.flushAfterWrite != -1L) {
      publishers.createIndex(Indexes
        .ascending("payload:send"), new IndexOptions()
        /*
         * automatically flush payloads that are older than the specified time.
         */
        .expireAfter(b.flushAfterWrite, b.flushUnit));
    }

    this.watcher = new CollectionWatcher(this);
    this.subscribers = new Subscribers();
  }

  /**
   * Gets the mongo client.
   *
   * @return the mongo client.
   */
  @Nonnull
  public MongoClient client() {
    return client;
  }

  /**
   * Gets the publishers collection.
   *
   * @return the publishers collection.
   */
  @Nonnull
  public MongoCollection<Document> publishers() {
    return publishers;
  }

  /**
   * Gets the watcher.
   *
   * @return the watcher.
   */
  @Nonnull
  public CollectionWatcher watcher() {
    return watcher;
  }

  /**
   * Gets the subscribers.
   *
   * @return the subscribers.
   */
  @Nonnull
  public Subscribers subscribers() {
    return subscribers;
  }

  /**
   * Updates the flush after write time.
   *
   * @param time the time to flush after.
   * @param unit the unit of the time.
   * @return the instance of this client.
   */
  public MongoPubSubClient updateFlushAfterWrite(long time, TimeUnit unit) {
    publishers.dropIndex(Indexes.ascending("payload:send"));
    publishers.createIndex(Indexes
      .ascending("payload:send"), new IndexOptions()
      .expireAfter(time, unit));
    return this;
  }

  /**
   * Flushes the publishers collection.
   *
   * @return the size of the collection before flushing, or -1 if an error occurred.
   */
  public long flush() {
    long size = -1L;
    boolean count = false;
    synchronized (this) {
      try {
        size = publishers.countDocuments();
        count = true;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (count) publishers.drop();
      }
    }

    return size;
  }

  /**
   * Enqueues a new payload to the publishers collection.
   *
   * @param target the target of the payload.
   * @param payload the payload to enqueue.
   *
   * @return Returns a waiter that can be used to
   * ensure the payload being sent with thread locking
   * before closing the client.
   */
  @Nonnull @SuppressWarnings("UnusedReturnValue")
  public Waiter enqueue(@Nonnull String target, Payload payload) {
    Requisites.requireNonNull(target, "target cannot be null.");
    Document document = payload.asDocument()
      .append("payload:target", target)
      .append("payload:send", new Date());

    publishers.insertOne(document);
    return watcher().waiter();
  }

  /**
   * Internal procedure to build the mongo client settings.
   *
   * @param b the builder to construct the client.
   * @return the mongo client settings.
   */
  private MongoClientSettings doBuildProcedure(MongoClientBuilder b) {
    MongoClientSettings.Builder builder = MongoClientSettings.builder();
    if (b.uri != null) {
      builder.applyConnectionString(new ConnectionString(b.uri));
    } else {
      builder.applyToClusterSettings(block -> block.hosts(Collections.singletonList(new ServerAddress(b.host, b.port))));
    }
    if (b.password != null) {
      builder.credential(MongoCredential.createCredential(b.username, b.database, b.password.toCharArray()));
    }
    return builder.build();
  }

  /**
   * Closes the client.
   */
  @Override
  public void close() {
    synchronized (this) {
      /*
       * drop the collection to prevent storing payloads.
       * especially if the client doesn't have a flush procedure.
       */
      publishers.drop();
      /*
       * finally, close the watcher & client.
       */
      watcher.close();
      client.close();
    }
  }
}
