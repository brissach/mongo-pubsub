package gg.clouke.mps.codec;

import gg.clouke.mps.Payload;

/**
 * @author Clouke
 * @since 24.02.2023 05:39
 * © mongo-pubsub - All Rights Reserved
 */
public abstract class Codec<E, R> {

  public abstract R encode(E e);

  public final Codec<E, R> andThen(Codec<R, R> after) {
    return new Codec<E, R>() {
      @Override
      public R encode(E e) {
        return after.encode(Codec.this.encode(e));
      }
    };
  }

  private static final Codec<String, Payload> PAYLOAD =
    new Codec<String, Payload>() {
      @Override
      public Payload encode(String s) {
        return Payload.fromJson(s);
      }
  };

  public static Codec<String, Payload> payload() {
    return PAYLOAD;
  }


}
