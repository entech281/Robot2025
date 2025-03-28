package entech.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.entech.util.EntechGeometryUtils;

/**
 * @author aheitkamp
 */
class TestEntechGeometryUtils {
  @Test
  void testAveragePose2dCase1() {
    Pose2d a = new Pose2d(new Translation2d(1.0, 1.0), Rotation2d.fromDegrees(360));
    Pose2d b = new Pose2d(new Translation2d(0.0, 0.0), Rotation2d.fromDegrees(0));

    Pose2d result = EntechGeometryUtils.averagePose2d(a, b);

    assertEquals(0.5, result.getX());
    assertEquals(0.5, result.getY());
    assertEquals(180, result.getRotation().getDegrees());
  }
}
