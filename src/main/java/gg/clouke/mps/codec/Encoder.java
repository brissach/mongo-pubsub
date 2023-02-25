package gg.clouke.mps.codec;

import gg.clouke.mps.Payload;

/**
 * @author Clouke
 * @since 24.02.2023 05:39
 * © mongo-pubsub - All Rights Reserved
 */
public abstract class Encoder<E, R> {

  public abstract R encode(E e);

  public final Encoder<E, R> andThen(Encoder<R, R> after) {
    return new Encoder<E, R>() {
      @Override
      public R encode(E e) {
        return after.encode(Encoder.this.encode(e));
      }
    };
  }

  protected static final Encoder<String, String> IDENTITY = new Encoder<String, String>() {
    @Override
    public String encode(String s) {
      return s;
    }
  };

  protected static final Encoder<String, Payload> PAYLOAD = new Encoder<String, Payload>() {
    @Override
    public Payload encode(String s) {
      return new Payload(s);
    }
  };


}
