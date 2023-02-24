package gg.clouke.mps.codec;

import gg.clouke.mps.Payload;

/**
 * @author Clouke
 * @since 24.02.2023 14:58
 * © mongo-pubsub - All Rights Reserved
 */
public class PayloadEncoder extends Encoder<String, Payload> {
  @Override
  public Payload encode(String s) {
    return new Payload(s);
  }
}
