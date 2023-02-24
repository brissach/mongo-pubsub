package gg.clouke.mps;

import com.google.gson.Gson;
import gg.acai.acava.collect.pairs.ImmutablePair;
import gg.acai.acava.collect.pairs.Pairs;

/**
 * @author Clouke
 * @since 24.02.2023 06:45
 * © mongo-pubsub - All Rights Reserved
 */
public class KeyValue<K, V> {

  private final K key;
  private final V value;

  public KeyValue(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public Pairs<String, String> pair() {
    Gson gson = GsonSpec.getGson();
    return new ImmutablePair<>(
      gson.toJson(key, key.getClass()),
      gson.toJson(value, value.getClass())
    );
  }

  @Override
  public String toString() {
    return "KeyValue{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }
}
