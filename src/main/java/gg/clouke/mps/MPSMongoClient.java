package gg.clouke.mps;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Collections;

/**
 * @author Clouke
 * @since 24.02.2023 05:12
 * © mongo-pubsub - All Rights Reserved
 */
public class MPSMongoClient {

  private final MongoCollection<Document> publishers;

  public MPSMongoClient(String host, int port, String username, String password, String database) {
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
            .credential(MongoCredential.createCredential(username, database, password.toCharArray()))
            .build();

    try (MongoClient client = MongoClients.create(settings)) {
      this.publishers = client
              .getDatabase(database)
              .getCollection("publishers");
    }
  }

  public MongoCollection<Document> publishers() {
    return publishers;
  }

}
