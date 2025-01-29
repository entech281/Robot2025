#!/usr/bin/env python3
"""
Vision processing module for FRC robot using AprilTag detection.
Streams camera feed and performs real-time AprilTag detection using OpenCV.
"""

import cscore as cs
import ntcore.util
import numpy as np
import cv2
import time
import traceback
from robotpy_apriltag import AprilTagDetector
import ntcore




# Constants
LOCAL_TEST_MODE = True  # Set to True to run NetworkTables locally
TEAM_NUMBER = 281
RESOLUTION_WIDTH = 640
RESOLUTION_HEIGHT = 480
TARGET_FPS = 121
SETTINGS_STREAM_PORT = 5800
ANNOTATED_STREAM_PORT = 5801
NOT_AVAILABLE=-999
MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP = 2
FRAMES_TO_SKIP_FOR_DEBUG_STREAM=5

class CameraProperties(object):
    EXPOSURE_RAW_ABSOLUTE = 'raw_exposure_time_absolute'
    AUTO_EXPOSURE = 'auto_exposure'
    BRIGHTNESS= "brightness"
    AUTO_WHITE_BALANCE="white_balance_automatic"

class CameraValues(object):
    EXPOSURE_SUPER_LOW = 10
    AUTO_EXPOSURE_MANUAL=1
    PRETTY_DARN_BRIGHT=50
    NO_AUTO_WHITE_BALANCE=0


def print_camera_properties(camera):
    print("Properties:")
    for prop in camera.enumerateProperties():
        kind = prop.getKind()
        if kind == cs.VideoProperty.Kind.kBoolean:
            print(
                prop.getName(),
                "(bool) value=%s default=%s" % (prop.get(), prop.getDefault()),
            )
        elif kind == cs.VideoProperty.Kind.kInteger:
            print(
                prop.getName(),
                "(int): value=%s min=%s max=%s step=%s default=%s"
                % (
                    prop.get(),
                    prop.getMin(),
                    prop.getMax(),
                    prop.getStep(),
                    prop.getDefault(),
                ),
            )
        elif kind == cs.VideoProperty.Kind.kString:
            print(prop.getName(), "(string):", prop.getString())
        elif kind == cs.VideoProperty.Kind.kEnum:
            print(prop.getName(), "(enum): value=%s" % prop.get())
            for i, choice in enumerate(prop.getChoices()):
                if choice:
                    print("    %s: %s" % (i, choice))


class FrameTimer:
    """Tracks frame processing timing statistics."""

    def __init__(self, sample_count):
        """Initialize the frame timer.

        Args:
            sample_count: Number of samples to average over
        """
        self.sample_count = sample_count
        self.start_time = time.time()
        self.samples = 0
        self.sps = 0

    def tick(self):
        """Update frame timing statistics."""
        self.samples += 1
        if self.samples > self.sample_count:
            elapsed = time.time() - self.start_time
            self.sps = self.sample_count / elapsed
            self.start_time = time.time()
            self.samples = 0

def get_stack_trace_lines(exception, wrap_length=80):
    """Get formatted stack trace lines from an exception.

    Args:
        exception: Exception object to extract the stack trace from
        wrap_length: Maximum length of a line before wrapping

    Returns:
        List of strings corresponding to the stack trace lines
    """
    raw_lines = traceback.format_exc().splitlines(keepends=False)
    wrapped_lines = []

    for line in raw_lines:
        while len(line) > wrap_length:
            wrapped_lines.append(line[:wrap_length])
            line = line[wrap_length:]
        wrapped_lines.append(line)

    return wrapped_lines

def put_exception_onto_frame(frame, exception):
    """Render exception information onto a frame.

    Args:
        frame: OpenCV frame to draw on
        exception: Exception to display
    """
    VERTICAL_SPACING = 30
    INIT_VERTICAL_POS = 120
    HORIZONTAL_POS = 20
    vertical_pos = INIT_VERTICAL_POS

    for line in get_stack_trace_lines(exception):
        cv2.putText(frame, line, (HORIZONTAL_POS, vertical_pos),
                   cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 255), 2, cv2.LINE_AA)
        vertical_pos += VERTICAL_SPACING

def setup_apriltag_detector():
    """Configure and return an AprilTag detector with optimal settings."""
    detector = AprilTagDetector()
    detector_config = AprilTagDetector.Config()

    # Configure detector settings
    detector_config.numThreads = 4
    detector_config.refineEdges = True
    detector_config.quadDecimate = 2
    detector_config.quadSigma = 0

    # Configure quad threshold parameters
    quad_params = AprilTagDetector.QuadThresholdParameters()
    quad_params.maxNumMaxima = 10
    quad_params.criticalAngle = 45 * 3.14159 / 180.0
    quad_params.maxLineFitMSE = 10.0
    quad_params.minWhiteBlackDiff = 5
    quad_params.deglitch = False
    quad_params.minClusterPixels = 5

    detector.setConfig(detector_config)
    detector.setQuadThresholdParameters(quad_params)
    detector.addFamily("tag36h11")

    return detector

def process_apriltag_detection(frame, detection, resolution_width, resolution_height):
    """Process a single AprilTag detection and draw visualization.

    Args:
        frame: OpenCV frame to draw on
        detection: AprilTag detection object
        resolution_width: Camera resolution width
        resolution_height: Camera resolution height

    Returns:
        Tuple of (tag_id, tag_height, tag_width, tag_x, tag_y)
    """
    tag_id = detection.getId()
    center = detection.getCenter()
    corners = [detection.getCorner(i) for i in range(4)]

    # Calculate tag dimensions
    avg_height = ((corners[0].y - corners[3].y) + (corners[1].y - corners[2].y)) / 2
    avg_width = ((corners[1].x - corners[0].x) + (corners[2].x - corners[3].x)) / 2

    # Calculate normalized coordinates
    tag_x = (2 * (center.x / resolution_width)) - 1
    tag_y = (2 * (center.y / resolution_height)) - 1
    tag_xp = (center.x - (resolution_width/2))/avg_width

    # Draw visualization
    cv2.circle(frame, (int(center[0]), int(center[1])), 5, (0, 255, 0), -1)
    cv2.putText(frame, f"ID: {tag_id}", (int(center[0]), int(center[1]) - 10),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 1, cv2.LINE_AA)

    # Draw tag outline
    def draw_line(pt1, pt2):
        p1 = (int(pt1.x), int(pt1.y))
        p2 = (int(pt2.x), int(pt2.y))
        cv2.line(frame, p1, p2, (255, 0, 0), 2)

    for i in range(4):
        draw_line(corners[i], corners[(i + 1) % 4])

    return tag_id, avg_height, avg_width, tag_x, tag_y,tag_xp



def main():
    """Main vision processing loop."""
    print("Starting Up....")
    # Initialize NetworkTables
    inst = ntcore.util.NetworkTableInstance.getDefault()
    if LOCAL_TEST_MODE:
        print("Running in local test mode - NetworkTables running as server")
        inst.startServer()
    else:
        print("Running in robot mode - NetworkTables running as client")
        inst.startClient4("Vision")
        inst.setServer(f"10.{int(TEAM_NUMBER/100)}.{TEAM_NUMBER%100}.2")  # RoboRIO IP address: 10.TE.AM.2
        inst.startDSClient()

    # Get the vision table
    table = inst.getTable("vision")

    # Setup camera
    camera = cs.UsbCamera("usbcam", 0)
    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG,
                       RESOLUTION_WIDTH, RESOLUTION_HEIGHT, TARGET_FPS)


    camera.getProperty(CameraProperties.EXPOSURE_RAW_ABSOLUTE).set(CameraValues.EXPOSURE_SUPER_LOW)
    camera.getProperty(CameraProperties.AUTO_EXPOSURE).set(CameraValues.AUTO_EXPOSURE_MANUAL)
    camera.getProperty(CameraProperties.BRIGHTNESS).set(CameraValues.PRETTY_DARN_BRIGHT)
    camera.getProperty(CameraProperties.AUTO_WHITE_BALANCE).set(CameraValues.NO_AUTO_WHITE_BALANCE)
    print_camera_properties(camera)

    # Setup AprilTag detector
    detector = setup_apriltag_detector()

    # Setup frame timer
    frame_timer = FrameTimer(200)


    # Setup video streaming
    mjpegServer = cs.CameraServer.addServer('annotated',SETTINGS_STREAM_PORT)

    mjpegServer.setSource(camera)
    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")


    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(camera)
    cvSource = cs.CameraServer.putVideo("vision", RESOLUTION_WIDTH, RESOLUTION_HEIGHT)



    cvMjpegServer = cs.CameraServer.addServer("annotated",ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)
    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")

    # Initialize frame buffer
    frame_buffer = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH), dtype=np.uint8)

    counter = 0
    missed_frame_counter = 0

    while True:
        frame_timer.tick()

        timestamp, frame = cvsink.grabFrame(frame_buffer)
        if timestamp == 0:
            print("error:", cvsink.getError())
            continue

        # Draw FPS counter
        cv2.putText(frame, f"{frame_timer.sps:.2f} FPS", (20, 30),
                   cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 255, 0), 2, cv2.LINE_AA)

        # Convert to grayscale directly in the frame buffer
        if len(frame.shape) == 3:
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        try:
            # Initialize detection values
            has_target = False
            tag_id = tag_height = tag_width = -1
            tag_x = tag_y = -1.0

            # Process detections
            detections = detector.detect(frame)
            if detections:
                missed_frame_counter = 0
                has_target = True
                detection = detections[0]  # Process first detection
                tag_id, tag_height, tag_width, tag_x, tag_y,tag_xp = process_apriltag_detection(
                    frame, detection, RESOLUTION_WIDTH, RESOLUTION_HEIGHT)

                # Update NetworkTables
                table.putBoolean("hasTarget", has_target)
                table.putNumber("idTag", tag_id)
                table.putNumber("tagHeight", float(tag_height))
                table.putNumber("tagWidth", float(tag_width))
                table.putNumber("tagX", float(tag_x))
                table.putNumber("tagY", float(tag_y))
                table.putNumber("timestamp", float(timestamp))
                table.putNumber("tagxp", tag_xp)
            else:
                missed_frame_counter += 1
                if missed_frame_counter > MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP:
                    table.putBoolean("hasTarget", False)
                    table.putNumber("idTag", NOT_AVAILABLE)
                    table.putNumber("tagHeight", NOT_AVAILABLE)
                    table.putNumber("tagWidth", NOT_AVAILABLE)
                    table.putNumber("tagX", NOT_AVAILABLE)
                    table.putNumber("tagY", NOT_AVAILABLE)
                    table.putNumber("timestamp", float(timestamp))
                    table.putNumber("tagxp", NOT_AVAILABLE)


        except Exception as e:
            # put_exception_onto_frame(frame,e)
            tb = traceback.print_exc()


        if counter % FRAMES_TO_SKIP_FOR_DEBUG_STREAM == 0:
            cvSource.putFrame(frame)

            # sd_source.putFrame(frame)
            counter = 0

        counter += 1


if __name__ == "__main__":
    main()
