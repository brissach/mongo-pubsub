package gg.clouke.mps;

/**
 * A simple waiter extension which can be used to wait for a termination signal for its thread.
 *
 * @author Clouke
 * @since 26.02.2023 15:06
 * © mongo-pubsub - All Rights Reserved
 */
public class Waiter {

  /**
   * The termination signal.
   */
  private boolean awaitTermination = false;

  /**
   * Applies the termination signal.
   */
  public void awaitTermination() {
    awaitTermination = true;
  }

  /**
   * Resets the termination signal.
   */
  public void reset() {
    awaitTermination = false;
  }

  /**
   * Gets the termination signal.
   *
   * @return the termination signal.
   */
  public boolean isAwaitTermination() {
    return awaitTermination;
  }

}
