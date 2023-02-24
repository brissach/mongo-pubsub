package gg.clouke.mps;

import com.google.gson.Gson;
import gg.acai.acava.collect.maps.FixedSizeHashMap;
import gg.acai.acava.io.Closeable;
import gg.clouke.mps.codec.Encoder;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Clouke
 * @since 24.02.2023 06:43
 * © mongo-pubsub - All Rights Reserved
 */
public abstract class AbstractPayload implements Closeable {

  protected static final Gson GSON = GsonSpec.getGson();
  private static final int MAX_SIZE = 16 * (1024 * 1024);

  private static final Encoder<Map<String, String>, String> ENCODER =
    new Encoder<Map<String, String>, String>() {
      @Override
      public String encode(Map<String, String> s) {
        return GSON.toJson(s, GsonSpec.getPayloadToken()
          .getType());
      }
    };

  protected final Map<String, String> parameters;

  public AbstractPayload(String json) {
    this.parameters = GSON
      .fromJson(json, GsonSpec
        .getPayloadToken()
        .getType());
  }

  public AbstractPayload(Document document) {
    this(document.toJson());
  }

  public AbstractPayload() {
    this.parameters = new FixedSizeHashMap<>(MAX_SIZE);
  }

  @Nonnull
  public Document asDocument() {
    synchronized (parameters) {
      Document document = new Document();
      document.putAll(parameters);
      return document;
    }
  }

  @Nonnull
  public Map<String, String> asMap() {
    return parameters;
  }

  public int size() {
    return parameters.size();
  }

  @Nonnull
  public Stream<Map.Entry<String, String>> stream() {
    return parameters.entrySet().stream();
  }

  @Nonnull
  public <R> Stream<R> flatMap(Function<? super Map.Entry<String, String>, ? extends Stream<? extends R>> mapper) {
    synchronized (parameters) {
      return parameters.entrySet()
        .stream()
        .flatMap(mapper);
    }
  }

  @Nonnull
  public <R> Stream<R> map(Function<? super Map.Entry<String, String>, ? extends R> mapper) {
    synchronized (parameters) {
      return parameters.entrySet()
        .stream()
        .map(mapper);
    }
  }

  @Override
  public String toString() {
    synchronized (parameters) {
      return ENCODER.encode(parameters);
    }
  }

  @Nonnull
  public String toPrettyJson() {
    synchronized (parameters) {
      return GsonSpec.getPrettyPrintingGson()
        .toJson(parameters, GsonSpec.getPayloadToken()
          .getType());
    }
  }

  @Override
  public void close() {
    synchronized (parameters) {
      parameters.clear();
    }
  }

}