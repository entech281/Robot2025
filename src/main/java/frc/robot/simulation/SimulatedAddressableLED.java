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
  }


  @Override
  public void setLength(int length) {
    // Create a simulated buffer.
    this.buffer = new SimulatedAddressableLEDBuffer(length);
  }

  @Override
  public void setData(AddressableLEDBuffer buffer) {
    // In a simulation, store the buffer or print the data for debugging.
    this.buffer = buffer;
  }
}
