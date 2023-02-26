package gg.clouke.mps;

import com.google.gson.Gson;
import gg.acai.acava.collect.pairs.ImmutablePair;
import gg.acai.acava.collect.pairs.Pairs;

/**
 * A key-value pair that can be used to send data in a message.
 *
 * @author Clouke
 * @since 24.02.2023 06:45
 * @deprecated Use {@link Pairs} instead.
 * © mongo-pubsub - All Rights Reserved
 */
@Deprecated
public class KeyValue<K, V> {

  private final K key;
  private final V value;

  public KeyValue(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Gets the key of the key-value pair.
   *
   * @return the key of the key-value pair.
   */
  public K getKey() {
    return key;
  }

  /**
   * Gets the value of the key-value pair.
   *
   * @return the value of the key-value pair.
   */
  public V getValue() {
    return value;
  }

  /**
   * Converts the key-value pair to a pair of strings.
   *
   * @return a pair of strings.
   */
  public Pairs<String, String> pair() {
    Gson gson = GsonSpec.getGson();
    return new ImmutablePair<>(
      gson.toJson(key, key.getClass()),
      gson.toJson(value, value.getClass())
    );
  }

  /**
   * Converts the key-value pair to a string.
   *
   * @return the string representation of the key-value pair.
   */
  @Override
  public String toString() {
    return "KeyValue{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }
}
