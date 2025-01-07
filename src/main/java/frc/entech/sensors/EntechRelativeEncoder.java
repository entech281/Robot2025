package frc.entech.sensors;

import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;

public class EntechRelativeEncoder implements RelativeEncoder {
  private double resetOffset = 0.0;
  private RelativeEncoder encoder;

  public EntechRelativeEncoder(RelativeEncoder encoder) {
    this.encoder = encoder;
  }

  public boolean equals(Object obj) {
    return encoder.equals(obj);
  }

  public double getPosition() {
    return encoder.getPosition() - resetOffset;
  }

  public double getVelocity() {
    return encoder.getVelocity();
  }

  public REVLibError setPosition(double position) {
    resetOffset = getPosition();
    return encoder.setPosition(position);
  }
  public int hashCode() {
    return encoder.hashCode();
  }

  public String toString() {
    return encoder.toString();
  }
}
