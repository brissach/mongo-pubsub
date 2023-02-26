package gg.clouke.mps;

import gg.acai.acava.annotated.RequiredAnnotation;

/**
 * <p>
 *  A subscriber which listens to payloads with a specific identifier.
 *  When a payload is published with the same identifier, the {@link #onMessage(Payload)} method is called.
 * </p>
 *
 * <pre>
 *  Create a subscriber class and annotate it with {@link Identifier}:
 *   {@code
 *    @Identifier("test")
 *    public class TestSubscriber implements Subscriber {
 *      public void onMessage(Payload payload) {
 *        System.out.println("Received payload " + payload);
 *      }
 *    }
 *  }
 *
 * <strong>Supports</strong> functional implementations:
 *  {@link MongoPubSubClient#subscribers()}
 *    {@code
 *      client.subscribers()
 *        .listenDirectly("test", // <-- without @Identifier annotation
 *          payload -> {
 *            System.out.println("Received payload: " + payload);
 *          });
 *   }
 *
 * </pre>
 *
 * @author Clouke
 * @since 25.02.2023 10:32
 * © mongo-pubsub - All Rights Reserved
 */
@FunctionalInterface
@RequiredAnnotation(Identifier.class)
public interface Subscriber {

  /**
   * Called when a payload is published
   * with the same identifier as this subscriber.
   *
   * @param payload the payload which was published
   */
  void onMessage(Payload payload);

  /**
   * <p>Gets the identifier of this subscriber.</p>
   *
   * <strong>NOTE:</strong> If the subscriber is
   * a functional implementation, this method must be overridden.
   *
   * @throws RuntimeException if the subscriber class is missing the {@link Identifier} annotation and has no overridden implementation of this method.
   * @return the identifier of this subscriber
   */
  default String getIdentifier() {
    Class<?> clazz = getClass();
    if (clazz.isAnnotationPresent(Identifier.class))
      return getClass()
              .getAnnotation(Identifier.class)
              .value();

    throw new RuntimeException("Subscriber class " + clazz.getName() + " is missing @Identifier annotation");
  }

}


