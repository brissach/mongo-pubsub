package gg.clouke.mps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gg.acai.acava.collect.maps.FixedSizeHashMap;
import gg.acai.acava.io.Closeable;
import org.bson.Document;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Clouke
 * @since 24.02.2023 06:15
 * © mongo-pubsub - All Rights Reserved
 */
public final class Payload implements Closeable {

  private static final int MAX_SIZE = 16 * (1024 * 1024);
  private static final Gson GSON = GsonSpec.getGson();
  private final Map<String, String> parameters;

  public Payload(String json) {
    this.parameters = GSON
      .fromJson(json, GsonSpec
        .getPayloadToken()
        .getType());
  }

  public Payload() {
    this.parameters = new FixedSizeHashMap<>(MAX_SIZE);
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

  public Map<String, String> asMap() {
    return parameters;
  }

  public int size() {
    return parameters.size();
  }

  public Stream<Map.Entry<String, String>> stream() {
    return parameters.entrySet().stream();
  }

  public Document toDocument() {
    synchronized (parameters) {
      Document document = new Document();
      document.putAll(parameters);
      return document;
    }
  }

  @Override
  public String toString() {
    synchronized (parameters) {
      return GSON.toJson(parameters);
    }
  }

  @Override
  public void close() {
    synchronized (parameters) {
      parameters.clear();
    }
  }
}
