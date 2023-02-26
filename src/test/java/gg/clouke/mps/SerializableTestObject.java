package gg.clouke.mps;

/**
 * A simple serializable test object for testing purposes.
 *
 * @author Clouke
 * @since 25.02.2023 15:00
 * © mongo-pubsub - All Rights Reserved
 */
public class SerializableTestObject {

  /**
   * The name of the test object.
   */
  private final String name;

  /**
   * The age of the test object.
   */
  private final int age;

  /**
   * Constructs a new serializable test object.
   *
   * @param name The name of the test object.
   * @param age The age of the test object.
   */
  public SerializableTestObject(String name, int age) {
    this.name = name;
    this.age = age;
  }

  /**
   * Gets the name of the test object.
   *
   * @return the name of the test object.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the age of the test object.
   *
   * @return the age of the test object.
   */
  public int getAge() {
    return age;
  }

  /**
   * Gets a string representation of the test object.
   *
   * @return a string representation of the test object.
   */
  @Override
  public String toString() {
    return "SerializableTestObject{"
      + "name='" + name + '\''
      + ", age=" + age
      + '}';
  }
}
