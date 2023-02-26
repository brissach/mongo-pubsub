package gg.clouke.mps;

import gg.acai.acava.Requisites;
import gg.acai.acava.annotated.Optionally;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * A builder for the {@link MongoPubSubClient} instance.
 *
 * <p>By default, the client will flush the payload 5 seconds after write.
 * If you want to disable this feature, use {@link #disableFlushing()} method,
 * or set the flush interval to <b>-1L</b>.
 *
 * <p>If the flushing was disabled after applying a flush interval, the builder will throw a {@link IllegalStateException}
 * with the message <b>"Cannot disable flushing after it has been enabled."</b>
 *
 * <p>Flushing can also be modified during runtime by using the {@link MongoPubSubClient#updateFlushAfterWrite(long, TimeUnit)}
 *
 * <p>The client will also clear previous indexes on the collection if the {@link #clearPreviousIndexes()} method is called.
 * This is useful if you want to change the index options, or if you want to change the index key.
 *
 * <p><b>Example usage:
 *
 * <pre>{@code
 * MongoPubSubClient client = MongoPubSubClient.newBuilder()
 *   .host("localhost")
 *   .port(27017)
 *   .database("test")
 *   .username("root")
 *   .password("root")
 *   .flushAfterWrite(5L, TimeUnit.SECONDS)
 *   .build();
 * }</pre>
 *
 * <p><b>Optionally, you can use a URI to connect to the database:
 * <pre>{@code
 * MongoPubSubClient client = MongoPubSubClient.newBuilder()
 *   .uri("mongodb://root:root@localhost:27017/test")
 *   .database("test")
 *   .flushAfterWrite(5L, TimeUnit.SECONDS)
 *   .build();
 * }</pre>
 * <strong>NOTE:</strong> {@link #database(String)} is still required, even if you use a URI.
 *
 * @author Clouke
 * @since 24.02.2023 09:43
 * © mongo-pubsub - All Rights Reserved
 */
public class MongoClientBuilder {

  // default values
  protected long flushAfterWrite = 5L;
  protected TimeUnit flushUnit = TimeUnit.SECONDS;
  protected boolean clearPreviousIndexes;

  protected String host;
  protected int port = 27017;
  protected String username;
  protected String password;
  protected String database;

  @Optionally // if the uri is set, the host, port, username, password and database will be ignored.
  protected String uri;

  /**
   * Applies the flush interval & flush unit to the client.
   *
   * @param flushAfterWrite the amount of time to wait before flushing the payload.
   * @param flushUnit the unit of the flushAfterWrite parameter.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder flushAfterWrite(long flushAfterWrite, TimeUnit flushUnit) {
    this.flushAfterWrite = flushAfterWrite;
    this.flushUnit = flushUnit;
    return this;
  }

  /**
   * <p>Disables the flushing feature. This will cause the client to not flush the payload
   * after write.
   *
   * <strong>NOTE:</strong> This is not recommended, as it will cause the payload to be stored
   *
   * @throws IllegalStateException if the flushing was already enabled.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder disableFlushing() {
    if (flushAfterWrite != 5L)
      throw new IllegalStateException("Cannot disable flushing after it has been enabled.");
    this.flushAfterWrite = -1L;
    return this;
  }

  /**
   * Clears the previous indexes on the collection.
   * This is useful if you want to change the index options, or if you want to change the index key.
   *
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder clearPreviousIndexes() {
    this.clearPreviousIndexes = true;
    return this;
  }

  /**
   * Sets the host of the database.
   *
   * @param host the host of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder host(String host) {
    this.host = host;
    return this;
  }

  /**
   * Sets the port of the database.
   *
   * @param port the port of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder port(int port) {
    this.port = port;
    return this;
  }

  /**
   * Sets the username of the database.
   *
   * @param username the username of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Sets the password of the database.
   *
   * @param password the password of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the database name.
   *
   * @param database the database name.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder database(String database) {
    this.database = database;
    return this;
  }

  /**
   * Sets the URI of the database.
   * <strong>NOTE:</strong> This will override the host, port, username, password and database fields.
   * Marked as {@link Optionally}
   *
   * @param uri the URI of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  @Optionally
  public MongoClientBuilder uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Sets the URI of the database.
   * <strong>NOTE:</strong> This will override the host, port, username, password and database fields.
   * Marked as {@link Optionally}
   *
   * @param uri the URI of the database.
   * @return this {@link MongoClientBuilder} instance for chaining.
   */
  public MongoClientBuilder uri(URI uri) {
    this.uri = uri.toString();
    return this;
  }

  /**
   * Builds the {@link MongoPubSubClient} instance.
   *
   * @throws NullPointerException if the uri is not set & the host is null.
   * @return a new {@link MongoPubSubClient} instance.
   */
  public MongoPubSubClient build() {
    if (uri == null) {
      // If the uri is not set, we need to check if the other fields are set.
      Requisites.requireNonNull(host, "host cannot be null. use uri() to set the uri, or set the host, port, username, password and database fields.");
    }

    return new MongoPubSubClient(this);
  }

}
