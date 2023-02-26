package gg.clouke.mps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gg.acai.acava.caches.CacheLoader;

import java.util.Map;

/**
 * A utility class that provides a Gson instance and TypeTokens.
 *
 * @author Clouke
 * @since 24.02.2023 06:16
 * © mongo-pubsub - All Rights Reserved
 */
public final class GsonSpec {

  private static final TypeToken<Map<String, String>> PAYLOAD_TOKEN = new TypeToken<Map<String, String>>(){};
  private static final TypeToken<CacheLoader> CACHE_LOADER_TOKEN = new TypeToken<CacheLoader>(){};
  private static final Gson GSON = new Gson();
  private static final Gson PRETTY_PRINTING_GSON = new GsonBuilder()
    .setPrettyPrinting()
    .create();

  /**
   * Gets the pretty printing Gson instance.
   *
   * @return the pretty printing Gson instance.
   */
  public static Gson getPrettyPrintingGson() {
    return PRETTY_PRINTING_GSON;
  }

  /**
   * Gets the Gson instance.
   *
   * @return the Gson instance.
   */
  public static Gson getGson() {
    return GSON;
  }

  /**
   * Gets the TypeToken for the CacheLoader class.
   *
   * @return the TypeToken for the CacheLoader class.
   */
  public static TypeToken<CacheLoader> getCacheLoaderToken() {
    return CACHE_LOADER_TOKEN;
  }

  /**
   * Gets the TypeToken for the Map<String, String> class.
   *
   * @return the TypeToken for the Map<String, String> class.
   */
  public static TypeToken<Map<String, String>> getPayloadToken() {
    return PAYLOAD_TOKEN;
  }

}
