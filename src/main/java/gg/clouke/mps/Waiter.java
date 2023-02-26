package gg.clouke.mps;

/**
 * @author Clouke
 * @since 26.02.2023 15:06
 * © mongo-pubsub - All Rights Reserved
 */
public class Waiter {

  private boolean awaitTermination = false;

  public void awaitTermination() {
    awaitTermination = true;
  }

  public void reset() {
    awaitTermination = false;
  }

  public boolean isAwaitTermination() {
    return awaitTermination;
  }

}
