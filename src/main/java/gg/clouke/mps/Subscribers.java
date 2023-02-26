package gg.clouke.mps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A house-holding class for all subscribers.
 *
 * <p>Responsible for holding all subscribers and
 * internally dispatching messages to them.
 *
 * <p>Holds an instance of the {@link Subscribers} class, which can be accessed
 * through the {@link #getInstance()} method.
 *
 * <p><strong>NOTE:</strong> cannot be instantiated multiple times.
 *
 * <p><b>Example usage:</b></p>
 * <h4>- Functional listener:
 * <pre>{@code
 * Subscribers subscribers = new Subscribers();
 * subscribers()
 *   .listenDirectly("my-listener",
 *     payload -> {
 *       System.out.println(payload.toString());
 *       payload.close();
 *     });
 * }
 * </pre>
 * <h4>- Class listener:
 * <pre>{@code
 * Subscribers subscribers = new Subscribers();
 * subscribers.addListener(new MyTestSub());
 * }</pre>
 *
 * @author Clouke
 * @since 25.02.2023 10:43
 * © mongo-pubsub - All Rights Reserved
 */
public final class Subscribers {

  private static Subscribers INSTANCE;
  private final Map<String, Subscriber> subscribers;

  /**
   * Gets the instance of the {@link Subscribers} class.
   *
   * @return the instance of this class
   */
  public static Subscribers getInstance() {
    return INSTANCE;
  }

  /**
   * Constructs a new instance of the {@link Subscribers} class.
   *
   * @throws IllegalStateException if the instance is already initialized.
   */
  public Subscribers() {
    if (INSTANCE != null)
      throw new IllegalStateException("Subscribers instance already exists.");
    INSTANCE = this;
    this.subscribers = new HashMap<>();
  }

  /**
   * Registers a new listener to the subscribers.
   *
   * @param subscriber the listener to register.
   */
  public void addListener(Subscriber subscriber) {
    subscribers.put(subscriber.getIdentifier(), subscriber);
  }

  /**
   * Allows functional direct listeners to be added to the subscribers.
   *
   * @param identifier the identifier of the listener
   * @param subscriber the listener
   */
  public void listenDirectly(String identifier, Subscriber subscriber) {
    subscribers.put(identifier, subscriber);
  }

  /**
   * Internal dispatching of messages to subscribers.
   *
   * @param identifier the identifier of the subscriber
   * @param payload the payload to dispatch
   * @throws RuntimeException if the subscriber does not exist
   */
  void dispatch(String identifier, Payload payload) {
    if (subscribers.containsKey(identifier)) {
      subscribers.get(identifier).onMessage(payload);
      return;
    }

    throw new RuntimeException("Attempted to dispatch message to non-existent subscriber " + identifier + " with payload " + payload);
  }

  /**
   * Gets an immutable map of all subscribers.
   *
   * @return an immutable map of all subscribers
   */
  public Map<String, Subscriber> getSubscribers() {
    return Collections.unmodifiableMap(subscribers);
  }

  /**
   * Gets the size of the subscribers map.
   *
   * @return the size of the subscribers map
   */
  public int size() {
    return subscribers.size();
  }

}
