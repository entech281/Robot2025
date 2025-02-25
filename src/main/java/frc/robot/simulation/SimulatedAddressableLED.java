package frc.robot.simulation;


/**
 * Simulated implementation of AddressableLED for environments where hardware is unavailable.
 */
public class SimulatedAddressableLED {

  private SimulatedAddressableLEDBuffer buffer;

  public SimulatedAddressableLED(int port) {
    
  }



  public void setLength(int length) {
    // Create a simulated buffer.
    this.buffer = new SimulatedAddressableLEDBuffer(length);
  }


  public void setData(SimulatedAddressableLEDBuffer buffer) {
    // In a simulation, store the buffer or print the data for debugging.
    this.buffer = buffer;
  }
}
