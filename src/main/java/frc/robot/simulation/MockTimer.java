package frc.robot.simulation;

import edu.wpi.first.wpilibj.Timer;

/**
 * MockTimer simulates the functionality of the WPILib Timer.
 * Use advanceTime(seconds) to simulate time passing in unit tests.
 */
public class MockTimer extends Timer {
  private double currentTime;
  private double startTime;
  private boolean running;

  public MockTimer() {
    this.currentTime = 0.0;
    this.startTime = 0.0;
    this.running = false;
  }

  /**
   * Starts the timer by recording the current time as the start time.
   */
  public void start() {
    this.startTime = currentTime;
    this.running = true;
  }

  /**
   * Checks if a specified number of seconds have elapsed since the timer started.
   *
   * @param seconds the time interval to check.
   * @return true if the specified interval has elapsed, false otherwise.
   */
  public boolean hasElapsed(double seconds) {
    if (!running)
      return false;
    return (currentTime - startTime) >= seconds;
  }

  /**
   * Returns the elapsed time since the timer was started.
   *
   * @return the elapsed time in seconds.
   */
  public double get() {
    if (!running)
      return 0.0;
    return currentTime - startTime;
  }

  /**
   * Advances the internal timer by a specified number of seconds.
   *
   * @param seconds the number of seconds to advance.
   */
  public void advanceTime(double seconds) {
    this.currentTime += seconds;
  }

  /**
   * Stops the timer.
   */
  public void stop() {
    this.running = false;
  }
}
