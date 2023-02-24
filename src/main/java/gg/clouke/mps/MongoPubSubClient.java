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
import gg.acai.acava.annotated.Use;
import org.bson.Document;

import java.util.Collections;

/**
 * @author Clouke
 * @since 24.02.2023 05:12
 * © mongo-pubsub - All Rights Reserved
 */
@Use("Use MongoClientBuilder to create a new instance of this class.")
public class MongoPubSubClient {

  private final MongoCollection<Document> publishers;

  public MongoPubSubClient(MongoClientBuilder b) {
    MongoClientSettings settings = doBuildProcedure(b);
    try (MongoClient client = MongoClients.create(settings)) {
      publishers = client
        .getDatabase(b.database)
        .getCollection("publishers");

      if (b.flushAfterWrite != -1L) {
        publishers.createIndex(Indexes
          .ascending("_time"), new IndexOptions()
          /*
           * Automatically flush payloads that are older than the specified time.
           */
          .expireAfter(b.flushAfterWrite, b.flushUnit));
      }
    }
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

  public MongoCollection<Document> publishers() {
    return publishers;
  }

}
