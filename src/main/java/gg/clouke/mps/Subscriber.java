package gg.clouke.mps;

/**
 * @author Clouke
 * @since 25.02.2023 10:32
 * © mongo-pubsub - All Rights Reserved
 */
public interface Subscriber {

  void onMessage(Payload payload);

  default String getIdentifier() {
    Class<?> clazz = getClass();
    if (clazz.isAnnotationPresent(Identifier.class))
      return getClass()
        .getAnnotation(Identifier.class)
        .value();

    throw new RuntimeException("Subscriber class " + clazz.getName() + " is missing @Identifier annotation");
  }

}
