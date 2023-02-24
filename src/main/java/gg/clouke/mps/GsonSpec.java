package gg.clouke.mps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/**
 * @author Clouke
 * @since 24.02.2023 06:16
 * © mongo-pubsub - All Rights Reserved
 */
public final class GsonSpec {

  private static final TypeToken<Map<String, String>> PAYLOAD_TOKEN = new TypeToken<Map<String, String>>() {};
  private static final Gson GSON = new Gson();
  private static final Gson PRETTY_PRINTING_GSON = new GsonBuilder()
          .setPrettyPrinting()
          .create();

  public static Gson getPrettyPrintingGson() {
    return PRETTY_PRINTING_GSON;
  }

  public static Gson getGson() {
    return GSON;
  }

  public static TypeToken<Map<String, String>> getPayloadToken() {
    return PAYLOAD_TOKEN;
  }

}
