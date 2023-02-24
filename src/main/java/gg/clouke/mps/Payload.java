package gg.clouke.mps;

import com.google.gson.reflect.TypeToken;
import gg.acai.acava.collect.pairs.Pairs;

/**
 * @author Clouke
 * @since 24.02.2023 06:15
 * © mongo-pubsub - All Rights Reserved
 */
public final class Payload extends AbstractPayload {

  public <K, V> Payload with(KeyValue<K, V> kv) {
    Pairs<String, String> pair = kv.pair();
    return withRawParameter(pair.left(), pair.right());
  }

  public Payload withRawParameter(String key, String value) {
    parameters.put(key, value);
    return this;
  }

  public Payload withSerializedParameter(String key, Object value) {
    parameters.put(key, GSON.toJson(value));
    return this;
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
