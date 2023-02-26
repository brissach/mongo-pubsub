package gg.clouke.mps;

import com.google.gson.reflect.TypeToken;
import gg.acai.acava.collect.pairs.Pairs;
import org.bson.Document;

import java.lang.reflect.Type;

/**
 * Represents a payload object that can be used to send data in a message.
 *
 * @author Clouke
 * @since 24.02.2023 06:15
 * © mongo-pubsub - All Rights Reserved
 */
public final class Payload extends AbstractPayload {

  /**
   * Creates a new payload object from a JSON string.
   *
   * @param json the JSON string representing the payload data.
   * @return a new payload object.
   */
  public static Payload fromJson(String json) {
    return new Payload(json);
  }

  /**
   * Creates a new payload object from a MongoDB document.
   *
   * @param document the MongoDB document representing the payload data.
   * @return a new payload object.
   */
  public static Payload fromDocument(Document document) {
    return new Payload(document);
  }

  /**
   * Creates a new empty payload object.
   *
   * @return a new empty payload object.
   */
  public static Payload empty() {
    return new Payload();
  }

  /**
   * Creates a new payload object from a JSON string.
   *
   * @param json the JSON string representing the payload data.
   */
  public Payload(String json) {
    super(json);
  }

  /**
   * Creates a new payload object from a MongoDB document.
   *
   * @param document the MongoDB document representing the payload data.
   */
  public Payload(Document document) {
    super(document);
  }

  /**
   * Creates a new empty payload object.
   */
  public Payload() {
    super();
  }

  /**
   * Adds a new key-value pair to the payload object.
   *
   * @param kv the key-value pair to add.
   * @return the updated payload object.
   * @deprecated Use {@link #withRawParameter(String, String)} instead.
   */
  @Deprecated
  public <K, V> Payload with(KeyValue<K, V> kv) {
    Pairs<String, String> pair = kv.pair();
    return withRawParameter(pair.left(), pair.right());
  }

  /**
   * Adds a new raw string parameter to the payload object.
   *
   * @param key the parameter key.
   * @param value the parameter value.
   * @return the updated payload object.
   */
  public Payload withRawParameter(String key, String value) {
    parameters.put(key, value);
    return this;
  }

  /**
   * Adds a new serializable parameter to the payload object.
   *
   * @param key the parameter key.
   * @param value the parameter value.
   * @return the updated payload object.
   */
  public Payload withSerializableParameter(String key, Object value) {
    parameters.put(key, GSON.toJson(value, value.getClass()));
    return this;
  }

  /**
   * Adds a new serializable parameter to the payload object.
   *
   * @param key the parameter key.
   * @param value the parameter value.
   * @param type the type of the parameter value.
   * @return the updated payload object.
   */
  public Payload withSerializableParameter(String key, Object value, Type type) {
    parameters.put(key, GSON.toJson(value, type));
    return this;
  }

  /**
   * Adds a new serializable parameter to the payload object.
   *
   * @param key the parameter key.
   * @param value the parameter value.
   * @param typeToken the type of the parameter value.
   * @return the updated payload object.
   */
  public Payload withSerializableParameter(String key, Object value, TypeToken<?> typeToken) {
    return withSerializableParameter(key, value, typeToken.getType());
  }

  /**
   * Gets the simple string value of a parameter.
   *
   * @param key the parameter key.
   * @return the raw string value of the parameter.
   */
  public String getRawValue(String key) {
    return parameters.get(key);
  }

  /**
   * Gets the deserialized value of a parameter.
   *
   * @param key the parameter key.
   * @param clazz the class of the parameter value.
   * @param <V> the type of the parameter value.
   * @return the deserialized value of the parameter.
   */
  public <V> V getValueAs(String key, Class<V> clazz) {
    return GSON.fromJson(parameters.get(key), clazz);
  }

  /**
   * Gets the deserialized value of a parameter.
   *
   * @param key the parameter key.
   * @param typeToken the type token of the parameter value.
   * @param <V> the type of the parameter value.
   * @return the deserialized value of the parameter.
   */
  public <V> V getValueAs(String key, TypeToken<V> typeToken) {
    return GSON.fromJson(parameters.get(key), typeToken.getType());
  }

}
