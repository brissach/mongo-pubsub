package gg.clouke.mps;

import gg.acai.acava.Requisites;

import java.util.concurrent.TimeUnit;

/**
 * @author Clouke
 * @since 24.02.2023 09:43
 * © mongo-pubsub - All Rights Reserved
 */
public class MongoClientBuilder {

  protected long flushAfterWrite = 5L;
  protected TimeUnit flushUnit = TimeUnit.SECONDS;

  protected String host = "localhost";
  protected int port = 27017;
  protected String username;
  protected String password;
  protected String database;
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
    this.flushAfterWrite = -1L;
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

  public MongoClientBuilder uri(String uri) {
    this.uri = uri;
    return this;
  }

  public MongoPubSubClient build() {
    if (uri == null) {
      // If the uri is not set, we need to check if the other fields are set.
      Requisites.requireNonNull(host, "host");
      Requisites.requireNonNull(port, "port");
    }

    return new MongoPubSubClient(this);
  }

}
