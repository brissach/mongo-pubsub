package gg.clouke.mps;

/**
 * @author Clouke
 * @since 25.02.2023 15:00
 * © mongo-pubsub - All Rights Reserved
 */
public class SerializableTestObject {

  private final String name;
  private final int age;

  public SerializableTestObject(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  @Override
  public String toString() {
    return "SerializableTestObject{"
      + "name='" + name + '\''
      + ", age=" + age
      + '}';
  }
}
