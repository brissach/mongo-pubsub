# Mongo-PubSub: A MongoDB Pub/Sub Messenger Written in Java

## Add Mongo-PubSub to your own build
### Maven
```xml
<repository>
  <id>mongo-pubsub</id>
  <url>https://raw.github.com/Clouke/mongo-pubsub/repository/</url>
</repository>

<dependency>
  <groupId>gg.clouke</groupId>
  <artifactId>mongo-pubsub</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Recommended
Since <b>MongoDB</b> is not primarily made for Pub/Sub messaging
I recommend using [Bridge](https://github.com/AcaiSoftware/bridge) - A <b>Redis</b> Pub/Sub Wrapper written in Java

## Usage Examples
### Building your client
<strong>NOTE:</strong> flushing is set to <b>5 seconds</b> by default, if you wish to disable flushing, use `disableFlushing()`

#### Simple builder with URI
```java
MongoPubSubClient client = MongoPubSubClient.newBuilder()
  .flushAfterWrite(10L, TimeUnit.SECONDS)
  .uri("mongodb://mongodb0.example.com:27017")
  .database("my_database")
  .build();
```
#### Compact builder
```java
 MongoPubSubClient client = MongoPubSubClient.newBuilder()
  .flushAfterWrite(10L, TimeUnit.SECONDS)
  .host("localhost")
  .port(27017)
  .username("admin")
  .password("admin")
  .database("my_database")
  .build();
```

### Subscribing
#### Functional subscriber
```java
client.subscribers()
  .listenDirectly("my-listener", // <-- the target
    payload -> {
      String rawValue = payload.getRawValue("key"); // returns value <- key
      SerializableTestObject serializableValue = payload.getValueAs("SerializedObject", SerializableTestObject.class); // deserializes into object
      payload.close(); // close the payload
    });
```

#### Creating and registering a subscriber
```java
@Identifier("my-listener") // <-- the target - @Identifier is required for implementations
public class MyListener implements Subscriber {
  @Override
  public void onMessage(Payload payload) {
    System.out.println("Received payload " + payload);
    payload.close(); // close the payload
  }
}
```
```java
client.subscribers().addListener(new MyListener());
```

### Queueing a message
```java
client.enqueue("my-listener", Payload.empty() // <-- target identifier, payload
  .withRawParameter("key", "value") // simple key-value pair
  .withSerializableParameter("SerializedObject", new SerializableTestObject("Jonathan", 20))); // serializable objects
```

## Contributing
Contributions are highly appreciated! If you feel your pull request is useful, go ahead!
Before creating a pull request, make sure your changes works as it should and give a description on what it provides.
