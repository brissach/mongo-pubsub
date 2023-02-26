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
 * @author Clouke
 * @since 24.02.2023 05:12
 * © mongo-pubsub - All Rights Reserved
 */
@Use("Use MongoClientBuilder to create a new instance of this class.")
public final class MongoPubSubClient implements Closeable {

  private static MongoPubSubClient INSTANCE;

  public static MongoPubSubClient getInstance() {
    return INSTANCE;
  }

  public static MongoClientBuilder newBuilder() {
    return new MongoClientBuilder();
  }

  private final CollectionWatcher watcher;
  private final MongoClient client;
  private final MongoCollection<Document> publishers;
  private final Subscribers subscribers;

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

  @Nonnull
  public MongoClient client() {
    return client;
  }

  @Nonnull
  public MongoCollection<Document> publishers() {
    return publishers;
  }

  @Nonnull
  public CollectionWatcher watcher() {
    return watcher;
  }

  @Nonnull
  public Subscribers subscribers() {
    return subscribers;
  }

  public MongoPubSubClient updateFlushAfterWrite(long time, TimeUnit unit) {
    publishers.dropIndex(Indexes.ascending("payload:send"));
    publishers.createIndex(Indexes
      .ascending("payload:send"), new IndexOptions()
      .expireAfter(time, unit));
    return this;
  }

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

  public void enqueue(@Nonnull String target, Payload payload) {
    Requisites.requireNonNull(target, "target cannot be null.");
    Document document = payload.asDocument()
      .append("payload:target", target)
      .append("payload:send", new Date());

    publishers.insertOne(document);
  }

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
