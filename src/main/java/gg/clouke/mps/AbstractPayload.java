package gg.clouke.mps;

import com.google.gson.Gson;
import gg.acai.acava.collect.maps.FixedSizeHashMap;
import gg.acai.acava.io.Closeable;
import org.bson.Document;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Clouke
 * @since 24.02.2023 06:43
 * © mongo-pubsub - All Rights Reserved
 */
public abstract class AbstractPayload implements Closeable {

  private static final int MAX_SIZE = 16 * (1024 * 1024);
  protected static final Gson GSON = GsonSpec.getGson();
  protected final Map<String, String> parameters;

  public AbstractPayload(String json) {
    this.parameters = GSON
      .fromJson(json, GsonSpec
        .getPayloadToken()
        .getType());
  }

  public AbstractPayload() {
    this.parameters = new FixedSizeHashMap<>(MAX_SIZE);
  }

  public Document toDocument() {
    synchronized (parameters) {
      Document document = new Document();
      document.putAll(parameters);
      return document;
    }
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
