package gg.clouke.mps;

import gg.acai.acava.Requisites;
import gg.acai.acava.annotated.Optionally;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * A builder of {@link MongoPubSubClient} instances.
 *
 * <p>Example usage:
 *  <pre>{@code
 *    MongoPubSubClient client = MongoPubSubClient.newBuilder()
 *      .host("localhost")
 *      .port(27017)
 *      .database("test")
 *      .username("root")
 *      .password("root")
 *      .flushAfterWrite(5L, TimeUnit.SECONDS)
 *      .build();
 *  }</pre>
 *
 *  <p>Optionally, you can use a URI to connect to the database:
 *  <pre>{@code
 *  MongoPubSubClient client = MongoPubSubClient.newBuilder()
 *    .uri("mongodb://root:root@localhost:27017/test")
 *    .database("test")
 *    .flushAfterWrite(5L, TimeUnit.SECONDS)
 *    .build();
 *  }</pre>
 *
 * @author Clouke
 * @since 24.02.2023 09:43
 * © mongo-pubsub - All Rights Reserved
 */
public class MongoClientBuilder {

  protected long flushAfterWrite = 5L;
  protected TimeUnit flushUnit = TimeUnit.SECONDS;
  protected boolean clearPreviousIndexes = false;

  protected String host;
  protected int port = 27017;
  protected String username;
  protected String password;
  protected String database;

  @Optionally
  protected String uri;

  /**
   * @param flushAfterWrite the amount of time to wait before flushing the payload.
   * @param flushUnit the unit of the flushAfterWrite parameter.
   */
  public MongoClientBuilder flushAfterWrite(long flushAfterWrite, TimeUnit flushUnit) {
    this.flushAfterWrite = flushAfterWrite;
    this.flushUnit = flushUnit;
    return this;
  }

  public MongoClientBuilder disableFlushing() {
    if (flushAfterWrite != 5L)
      throw new IllegalStateException("Cannot disable flushing after it has been enabled.");
    this.flushAfterWrite = -1L;
    return this;
  }

  public MongoClientBuilder clearPreviousIndexes() {
    this.clearPreviousIndexes = true;
    return this;
  }

  public MongoClientBuilder host(String host) {
    this.host = host;
    return this;
  }

  public MongoClientBuilder port(int port) {
    this.port = port;
    return this;
  }

  public MongoClientBuilder username(String username) {
    this.username = username;
    return this;
  }

  public MongoClientBuilder password(String password) {
    this.password = password;
    return this;
  }

  public MongoClientBuilder database(String database) {
    this.database = database;
    return this;
  }

  @Optionally
  public MongoClientBuilder uri(String uri) {
    this.uri = uri;
    return this;
  }

  public MongoClientBuilder uri(URI uri) {
    this.uri = uri.toString();
    return this;
  }

  public MongoPubSubClient build() {
    if (uri == null) {
      // If the uri is not set, we need to check if the other fields are set.
      Requisites.requireNonNull(host, "host cannot be null. use uri() to set the uri, or set the host, port, username, password and database fields.");
    }

    return new MongoPubSubClient(this);
  }

}
