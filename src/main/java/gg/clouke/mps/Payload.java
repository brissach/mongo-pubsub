package gg.clouke.mps;

import com.google.gson.reflect.TypeToken;
import gg.acai.acava.collect.pairs.Pairs;
import org.bson.Document;

import java.lang.reflect.Type;

/**
 * @author Clouke
 * @since 24.02.2023 06:15
 * © mongo-pubsub - All Rights Reserved
 */
public final class Payload extends AbstractPayload {

  public static Payload fromJson(String json) {
    return new Payload(json);
  }

  public static Payload fromDocument(Document document) {
    return new Payload(document);
  }

  public static Payload empty() {
    return new Payload();
  }

  public Payload(String json) {
    super(json);
  }

  public Payload(Document document) {
    super(document);
  }

  public Payload() {
    super();
  }

  @Deprecated
  public <K, V> Payload with(KeyValue<K, V> kv) {
    Pairs<String, String> pair = kv.pair();
    return withRawParameter(pair.left(), pair.right());
  }

  public Payload withRawParameter(String key, String value) {
    parameters.put(key, value);
    return this;
  }

  public Payload withSerializableParameter(String key, Object value) {
    parameters.put(key, GSON.toJson(value, value.getClass()));
    return this;
  }

  public Payload withSerializableParameter(String key, Object value, Type type) {
    parameters.put(key, GSON.toJson(value, type));
    return this;
  }

  public Payload withSerializableParameter(String key, Object value, TypeToken<?> typeToken) {
    return withSerializableParameter(key, value, typeToken.getType());
  }

  public String getRawValue(String key) {
    return parameters.get(key);
  }

  public <V> V getValueAs(String key, Class<V> clazz) {
    return GSON.fromJson(parameters.get(key), clazz);
  }

  public <V> V getValueAs(String key, TypeToken<V> typeToken) {
    return GSON.fromJson(parameters.get(key), typeToken.getType());
  }

}
