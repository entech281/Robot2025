package frc.robot.simulation;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

/**
 * Simulated implementation of AddressableLED for environments where hardware is unavailable.
 */
public class SimulatedAddressableLED extends AddressableLED {

  private AddressableLEDBuffer buffer;

  public SimulatedAddressableLED(int port) {
    super(port);
    // Optionally, log or track that we are in simulation mode.
    System.out.println("SimulatedAddressableLED created on port " + port);
  }

  @Override
  public void start() {
    System.out.println("SimulatedAddressableLED started");
  }

  @Override
  public void setLength(int length) {
    // Create a simulated buffer.
    this.buffer = new SimulatedAddressableLEDBuffer(length);
    System.out.println("SimulatedAddressableLED set length to " + length);
  }

  @Override
  public void setData(AddressableLEDBuffer buffer) {
    // In a simulation, store the buffer or print the data for debugging.
    this.buffer = buffer;
    System.out.println("SimulatedAddressableLED set data with length " + buffer.getLength());
  }
}
