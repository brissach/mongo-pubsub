package gg.clouke.mps.codec;

/**
 * @author Clouke
 * @since 24.02.2023 05:39
 * © mongo-pubsub - All Rights Reserved
 */
public interface Encoder<E, R> {

  R encode(E e);

}
