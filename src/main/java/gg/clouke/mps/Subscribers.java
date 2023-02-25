package gg.clouke.mps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Clouke
 * @since 25.02.2023 10:43
 * © mongo-pubsub - All Rights Reserved
 */
public final class Subscribers {

  private static Subscribers INSTANCE;
  private final Map<String, Subscriber> subscribers;

  public static Subscribers getInstance() {
    return INSTANCE;
  }

  public Subscribers() {
    if (INSTANCE != null)
      throw new IllegalStateException("Subscribers instance already exists.");
    INSTANCE = this;
    this.subscribers = new HashMap<>();
  }

  public void addListener(Subscriber subscriber) {
    subscribers.put(subscriber.getIdentifier(), subscriber);
  }

  public void listenDirectly(String identifier, Subscriber subscriber) {
    subscribers.put(identifier, subscriber);
  }

  void dispatch(String identifier, Payload payload) {
    if (subscribers.containsKey(identifier)) {
      subscribers.get(identifier).onMessage(payload);
      return;
    }

    throw new RuntimeException("Attempted to dispatch message to non-existent subscriber " + identifier + " with payload " + payload);
  }

  public Map<String, Subscriber> getSubscribers() {
    return Collections.unmodifiableMap(subscribers);
  }

  public int size() {
    return subscribers.size();
  }

}
