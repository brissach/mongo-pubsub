package gg.clouke.mps;

import gg.acai.acava.annotated.Required;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that can be used to specify the identifier of a {@link Subscriber}.
 * <p>If the subscriber is a functional implementation, the identifier can be specified in the {@link MongoPubSubClient#subscribers()} method.
 * <pre>
 * {@code
 *  client.subscribers()
 *    .listenDirectly("test", // <-- without @Identifier annotation
 *      payload -> {
 *        System.out.println("Received payload: " + payload);
 *      });
 *     }
 * </pre>
 *
 * @author Clouke
 * @since 25.02.2023 10:29
 * © mongo-pubsub - All Rights Reserved
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Required
public @interface Identifier {
  /**
   * Gets the identifier of the subscriber.
   *
   * @return the identifier of the subscriber
   */
  String value();
}
