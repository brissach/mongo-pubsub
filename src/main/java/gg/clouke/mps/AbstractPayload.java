package gg.clouke.mps;

import com.google.gson.Gson;
import gg.acai.acava.collect.maps.FixedSizeHashMap;
import gg.acai.acava.io.Closeable;
import gg.clouke.mps.codec.Codec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An abstract payload which represents payload data that can be used to send data in a message.
 * The payload is a collection of key-value pairs.
 *
 * The payload is limited to 16MB in size.
 *
 * @author Clouke
 * @since 24.02.2023 06:43
 * © mongo-pubsub - All Rights Reserved
 */
public abstract class AbstractPayload implements Closeable {

  protected static final Gson GSON = GsonSpec.getGson();
  /**
   * The maximum size of the payload in bytes -> 16MB.
   */
  private static final int MAX_SIZE = 16 * (1024 * 1024);

  /**
   * Returns the maximum size of the payload in bytes.
   *
   * @return the maximum size of the payload in bytes.
   */
  public static int getMaxSize() {
    return MAX_SIZE;
  }

  /**
   * The encoder used to encode the payload data to a Json string.
   */
  private static final Codec<Map<String, String>, String> CODEC =
    new Codec<Map<String, String>, String>() {
      @Override
      public String encode(Map<String, String> s) {
        return GSON.toJson(s, GsonSpec.getPayloadToken()
          .getType());
      }
    };

  /**
   * The decoder used to decode the payload data from a Json string.
   */
  private static final Codec<String, Map<String, String>> DECODER =
    new Codec<String, Map<String, String>>() {
      @Override @SuppressWarnings("unchecked")
      public Map<String, String> encode(String s) {
        return GSON.fromJson(s, Map.class);
      }
    };

  protected final Map<String, String> parameters;

  public AbstractPayload(String json) {
    this.parameters = DECODER.encode(json);
  }

  public AbstractPayload(Document document) {
    this(document.toJson());
  }

  public AbstractPayload() {
    this.parameters = new FixedSizeHashMap<>(MAX_SIZE);
  }

  /**
   * Converts the payload as a {@link Document}
   *
   * @return the payload as a document.
   */
  @Nonnull
  public Document asDocument() {
    synchronized (parameters) {
      Document document = new Document();
      document.putAll(parameters);
      return document;
    }
  }

  /**
   * Gets the Payload as a {@link Map} of {@link String} and {@link Object}
   *
   * @return the payload as a map.
   */
  @Nonnull
  public Map<String, Object> asMap() {
    Map<String, Object> map = new FixedSizeHashMap<>(MAX_SIZE);
    synchronized (parameters) {
      map.putAll(parameters);
    }
    return map;
  }

  /**
   * Gets the size of the payload entries.
   *
   * @return the size of the payload entries.
   */
  public int size() {
    return parameters.size();
  }

  /**
   * Returns a stream of the payload entries.
   *
   * @return a stream of the payload entries.
   */
  @Nonnull
  public Stream<Map.Entry<String, String>> stream() {
    return parameters.entrySet().stream();
  }

  /**
   * Returns a new stream that is a flattened view
   * of the results of applying a mapping function to the entries in the payload.
   *
   * @param mapper the mapping function to apply to each key-value pair.
   * @param <R> the type of the elements in the new stream.
   * @return a new stream that is a flattened view of the results of applying a mapping function to the entries in the payload.
   */
  @Nonnull
  public <R> Stream<R> flatMap(Function<? super Map.Entry<String, String>, ? extends Stream<? extends R>> mapper) {
    synchronized (parameters) {
      return parameters.entrySet()
        .stream()
        .flatMap(mapper);
    }
  }

  /**
   * Returns a new stream that is a view of the results of applying a mapping function to the entries in the payload.
   *
   * @param mapper the mapping function to apply to each key-value pair.
   * @param <R> the type of the elements in the new stream.
   * @return a new stream that is a view of the results of applying a mapping function to the entries in the payload.
   */
  @Nonnull
  public <R> Stream<R> map(Function<? super Map.Entry<String, String>, ? extends R> mapper) {
    synchronized (parameters) {
      return parameters.entrySet()
        .stream()
        .map(mapper);
    }
  }

  /**
   * Converts the payload to a Json string.
   *
   * @return the payload as a Json string.
   */
  @Override
  public String toString() {
    synchronized (parameters) {
      return CODEC.encode(parameters);
    }
  }

  /**
   * Converts the payload to a pretty Json string.
   *
   * @return the payload as a pretty Json string.
   */
  @Nonnull
  public String toPrettyJson() {
    synchronized (parameters) {
      return GsonSpec.getPrettyPrintingGson()
        .toJson(parameters, GsonSpec.getPayloadToken()
          .getType());
    }
  }

  /**
   * Closes the payload and clears the parameters.
   */
  @Override
  public void close() {
    synchronized (parameters) {
      parameters.clear();
    }
  }

}