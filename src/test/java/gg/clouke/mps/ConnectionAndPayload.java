package gg.clouke.mps;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Test class for the MongoPubSubClient.
 *
 * @author Clouke
 * @since 25.02.2023 14:56
 * © mongo-pubsub - All Rights Reserved
 */
public class ConnectionAndPayload {

  public static void main(String[] args) {
    @Nonnull String uri;
    @Nonnull String database;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      try {
        System.out.print("Enter MongoDB URI: ");
        uri = reader.readLine(); // apply your own URI here
        System.out.print("Enter MongoDB Database: ");
        database = reader.readLine(); // apply your own database here
        break;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /*
     * Connect to the MongoDB instance and create a new client.
     */
    MongoPubSubClient client = MongoPubSubClient.newBuilder()
      .flushAfterWrite(10L, TimeUnit.SECONDS)
      .uri(uri)
      .database(database)
      .build();

    try {
      Thread.sleep(1000L); // Sleep to make sure the client is connected (just for testing purposes)
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    /*
     * Listen directly to identifier "my-listener"
     */
    client.subscribers()
      .listenDirectly("my-listener",
        payload -> {
          String rawValue = payload.getRawValue("Hello");
          SerializableTestObject serializableValue = payload.getValueAs("SerializedObject", SerializableTestObject.class);
          StringBuilder output = new StringBuilder()
            .append("Received payload: \n")
            .append("- Simple Value: ")
            .append(rawValue) // Should be "World!"
            .append("\n")
            .append("- Serializable Value: ")
            .append(serializableValue); // Should be "SerializableTestObject{name='Jonathan', age=20}"

          System.out.println(output);
          payload.close();
        });

    // send a payload to the target "my-listener"
    client.enqueue("my-listener", Payload.empty()
      .withRawParameter("Hello", "World!")
      .withSerializableParameter("SerializedObject", new SerializableTestObject("Jonathan", 20)));
  }

}
