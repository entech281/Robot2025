#!/usr/bin/env python3
"""
Vision processing module for FRC robot using AprilTag detection.
This module handles camera setup, AprilTag detection, and NetworkTables communication
for real-time vision processing on an FRC robot.

The system performs the following tasks:
1. Initializes camera and NetworkTables connection
2. Processes video frames to detect AprilTags
3. Tracks detected tags using ROI-based optimization
4. Streams processed frames for debugging
"""

import cscore as cs
import ntcore.util
import numpy as np
import cv2
import time
import traceback
from robotpy_apriltag import AprilTagDetector

# System Configuration
LOCAL_TEST_MODE = False
TEAM_NUMBER = 281
RESOLUTION_WIDTH = 640
RESOLUTION_HEIGHT = 480
TARGET_FPS = 121
SETTINGS_STREAM_PORT = 5800
ANNOTATED_STREAM_PORT = 5801
NOT_AVAILABLE = -999
MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP = 2
FRAMES_TO_SKIP_FOR_DEBUG_STREAM = 5


class CameraProperties:
    """Camera configuration property names."""
    EXPOSURE_RAW_ABSOLUTE = 'raw_exposure_time_absolute'
    AUTO_EXPOSURE = 'auto_exposure'
    BRIGHTNESS = "brightness"
    AUTO_WHITE_BALANCE = "white_balance_automatic"


class CameraValues:
    """Camera configuration values."""
    EXPOSURE_SUPER_LOW = 10
    AUTO_EXPOSURE_MANUAL = 1
    PRETTY_DARN_BRIGHT = 50
    NO_AUTO_WHITE_BALANCE = 0


class FrameTimer:
    """Tracks frame processing timing statistics."""
    
    def __init__(self, sample_count):
        """Initialize frame timer with specified sample count."""
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


class DetectionState:
    """Maintains state for AprilTag detection tracking and region of interest."""
    
    def __init__(self):
        """Initialize detection state with default values."""
        self.last_good_width = RESOLUTION_WIDTH * 0.1
        self.last_good_x = RESOLUTION_WIDTH / 2
        self.missed_frame_counter = 0
        self.previous_width = None
        
    def update_width(self, new_width):
        """Update tracking width based on new detection.
        
        Args:
            new_width: Width of the newly detected tag
            
        Returns:
            Updated width to use for ROI
        """
        if new_width > 0:
            self.last_good_width = new_width
            self.previous_width = new_width
            self.missed_frame_counter = 0
            return new_width
        else:
            self.missed_frame_counter += 1
            return (self.last_good_width 
                   if self.missed_frame_counter <= MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP 
                   else (RESOLUTION_WIDTH * 0.1))
            
    def update_position(self, new_x):
        """Update tracking position based on new detection.
        
        Args:
            new_x: X-coordinate of the newly detected tag
            
        Returns:
            Updated x-coordinate to use for ROI
        """
        if new_x >= 0:
            self.last_good_x = new_x
        return self.last_good_x


def print_camera_properties(camera):
    """Print all available camera properties and their current values."""
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


def setup_apriltag_detector():
    """Initialize and configure AprilTag detector with optimal settings."""
    detector = AprilTagDetector()
    config = AprilTagDetector.Config()
    config.numThreads = 4
    config.refineEdges = True
    config.quadDecimate = 0.01
    config.quadSigma = 0

    quad_params = AprilTagDetector.QuadThresholdParameters()
    quad_params.maxNumMaxima = 10
    quad_params.criticalAngle = 45 * 3.14159 / 180.0
    quad_params.maxLineFitMSE = 10.0
    quad_params.minWhiteBlackDiff = 5
    quad_params.deglitch = False
    quad_params.minClusterPixels = 5

    detector.setConfig(config)
    detector.setQuadThresholdParameters(quad_params)
    detector.addFamily("tag36h11")
    return detector


def process_apriltag_detection(frame, detection, resolution_width, resolution_height):
    """Process AprilTag detection and draw visualization.
    
    Args:
        frame: OpenCV frame to draw on
        detection: AprilTag detection object
        resolution_width: Camera resolution width
        resolution_height: Camera resolution height

    Returns:
        Tuple of (tag_id, tag_height, tag_width, tag_x, tag_y, tag_xp)
    """
    tag_id = detection.getId()
    center = detection.getCenter()
    corners = [detection.getCorner(i) for i in range(4)]

    avg_height = ((corners[0].y - corners[3].y) + (corners[1].y - corners[2].y)) / 2
    avg_width = ((corners[1].x - corners[0].x) + (corners[2].x - corners[3].x)) / 2

    tag_x = (2 * (center.x / resolution_width)) - 1
    tag_y = (2 * (center.y / resolution_height)) - 1
    tag_xp = (center.x - (resolution_width/2))/avg_width

    # Draw detection visualization
    cv2.circle(frame, (int(center[0]), int(center[1])), 5, (0, 255, 0), -1)
    cv2.putText(frame, f"ID: {tag_id}", (int(center[0]), int(center[1]) - 10),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 1, cv2.LINE_AA)

    for i in range(4):
        p1 = (int(corners[i].x), int(corners[i].y))
        p2 = (int(corners[(i + 1) % 4].x), int(corners[(i + 1) % 4].y))
        cv2.line(frame, p1, p2, (255, 0, 0), 2)

    return tag_id, avg_height, avg_width, tag_x, tag_y, tag_xp


def put_text(frame, location, value):
    """Draw text on frame with consistent style."""
    cv2.putText(frame, value, location, cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2, cv2.LINE_AA)


def setup_network_tables():
    """Initialize and configure NetworkTables connection."""
    inst = ntcore.util.NetworkTableInstance.getDefault()
    if LOCAL_TEST_MODE:
        print("Running in local test mode - NetworkTables running as server")
        inst.startServer()
    else:
        print("Running in robot mode - NetworkTables running as client")
        inst.startClient4("Vision")
        inst.setServer(f"10.{int(TEAM_NUMBER/100)}.{TEAM_NUMBER%100}.2")
        inst.startDSClient()
    return inst.getTable("vision")


def setup_camera():
    """Initialize and configure the camera with optimal settings."""
    camera = cs.UsbCamera("usbcam", 0)
    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG,
                       RESOLUTION_WIDTH, RESOLUTION_HEIGHT, TARGET_FPS)

    camera.getProperty(CameraProperties.EXPOSURE_RAW_ABSOLUTE).set(CameraValues.EXPOSURE_SUPER_LOW)
    camera.getProperty(CameraProperties.AUTO_EXPOSURE).set(CameraValues.AUTO_EXPOSURE_MANUAL)
    camera.getProperty(CameraProperties.BRIGHTNESS).set(CameraValues.PRETTY_DARN_BRIGHT)
    camera.getProperty(CameraProperties.AUTO_WHITE_BALANCE).set(CameraValues.NO_AUTO_WHITE_BALANCE)
    
    print("\nConfigured camera with settings:")
    print_camera_properties(camera)
    return camera


def setup_video_streams(camera):
    """Configure video streaming servers for raw and processed frames."""
    mjpeg_server = cs.CameraServer.addServer("raw", SETTINGS_STREAM_PORT)
    mjpeg_server.setSource(camera)
    print(f"Raw camera stream listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")

    cv_sink = cs.CvSink("cvsink")
    cv_sink.setSource(camera)
    cv_source = cs.CameraServer.putVideo("vision", RESOLUTION_WIDTH, RESOLUTION_HEIGHT)

    cv_mjpeg_server = cs.CameraServer.addServer("annotated", ANNOTATED_STREAM_PORT)
    cv_mjpeg_server.setSource(cv_source)
    print(f"Annotated vision stream listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")
    
    return cv_sink, cv_source


def update_network_tables(table, has_target, tag_id, tag_height, tag_width, tag_x, tag_y, frame_count, tag_xp):
    """Update NetworkTables with current detection results."""
    table.putBoolean("hasTarget", has_target)
    table.putNumber("idTag", tag_id)
    table.putNumber("tagHeight", tag_height)
    table.putNumber("tagWidth", tag_width)
    table.putNumber("tagX", tag_x)
    table.putNumber("tagY", tag_y)
    table.putNumber("frame_count", frame_count)
    table.putNumber("tagxp", tag_xp)


def draw_detection_info(frame, tag_xp, tag_width, tag_x, frame_count):
    """Draw detection information on the frame."""
    put_text(frame, (20, 370), f"xp {float(tag_xp):.2f}")
    put_text(frame, (20, 400), f"w: {float(tag_width):.2f}")
    put_text(frame, (20, 430), f"x: {float(tag_x):.2f}")
    put_text(frame, (20, 460), f"frame: {frame_count}")


def process_frame(frame, detection_frame, has_target, missed_frame_counter, tag_x, detection_state, detector):
    """Process a single frame for AprilTag detection."""
    if has_target or missed_frame_counter < MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP:
        try:
            last_tag_x = ((tag_x + 1) / 2) * RESOLUTION_WIDTH
            crop_width = abs(detection_state.last_good_width * 1.5)
            x_start = int(max(0, last_tag_x - crop_width))
            x_end = int(min(RESOLUTION_WIDTH, last_tag_x + crop_width))

            # print("\nFull frame properties:")
            # print(f"Shape: {detection_frame.shape}")
            # print(f"Type: {detection_frame.dtype}")
            # print(f"Min/Max values: {detection_frame.min()}/{detection_frame.max()}")
            
            cropped_detection_frame = detection_frame[0:RESOLUTION_HEIGHT, x_start:x_end]
            
            # print("\nCropped frame properties:")
            # print(f"Shape: {cropped_detection_frame.shape}")
            # print(f"Type: {cropped_detection_frame.dtype}")
            # print(f"Min/Max values: {cropped_detection_frame.min()}/{cropped_detection_frame.max()}")
            # print(f"Crop width: {x_end - x_start} pixels")
            
            cv2.rectangle(frame, (x_start, 0), (x_end, RESOLUTION_HEIGHT), (0, 255, 0), 2)
            
            detections = detector.detect(cropped_detection_frame)
            
            if detections:
                for det in detections:
                    det.getCenter().x += x_start
                    for i in range(4):
                        corner = det.getCorner(i)
                        corner.x += x_start
            return detections, cropped_detection_frame

        except Exception as e:
            print(f"Error in crop/detection: {e}")
            return [], None
    else:
        return detector.detect(detection_frame), None


def main():
    """Main vision processing loop."""
    print("Starting Up....")
    
    table = setup_network_tables()
    camera = setup_camera()
    cv_sink, cv_source = setup_video_streams(camera)
    detector = setup_apriltag_detector()
    frame_timer = FrameTimer(200)
    detection_state = DetectionState()

    frame_buffer = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH, 3), dtype=np.uint8)
    frame_count = 0
    missed_frame_counter = 0
    has_target = False
    tag_id = tag_height = tag_width = -1
    tag_x = tag_y = -1.0

    while True:
        frame_timer.tick()
        frame_count += 1

        _, frame = cv_sink.grabFrame(frame_buffer)
        if frame is None:
            print("error:", cv_sink.getError())
            continue

        display_frame = frame.copy()
        if len(display_frame.shape) == 2:
            display_frame = cv2.cvtColor(display_frame, cv2.COLOR_GRAY2BGR)

        detection_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY) if len(frame.shape) == 3 else frame

        detections, cropped_frame = process_frame(
            display_frame, detection_frame, has_target, missed_frame_counter,
            tag_x, detection_state, detector
        )

        has_target = False
        tag_id = tag_height = tag_width = -1
        tag_x = tag_y = -1.0
        tag_xp = NOT_AVAILABLE

        if detections:
            missed_frame_counter = 0
            has_target = True
            detection = detections[0]
            tag_id, tag_height, tag_width, tag_x, tag_y, tag_xp = process_apriltag_detection(
                display_frame, detection, RESOLUTION_WIDTH, RESOLUTION_HEIGHT)

            update_network_tables(table, has_target, tag_id, tag_height, tag_width,
                                tag_x, tag_y, frame_count, tag_xp)
            draw_detection_info(display_frame, tag_xp, tag_width, tag_x, frame_count)
        else:
            missed_frame_counter += 1
            if missed_frame_counter > MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP:
                update_network_tables(table, False, NOT_AVAILABLE, NOT_AVAILABLE,
                                   NOT_AVAILABLE, NOT_AVAILABLE, NOT_AVAILABLE,
                                   frame_count, NOT_AVAILABLE)

        cv2.putText(display_frame, f"{frame_timer.sps:.2f} FPS", (20, 30),
                   cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0, 255, 0), 2, cv2.LINE_AA)

        if frame_count % FRAMES_TO_SKIP_FOR_DEBUG_STREAM == 0:
            cv_source.putFrame(display_frame)

        if frame_count > 1000000:  # Reset counter to prevent overflow
            frame_count = 0


if __name__ == "__main__":
    main()
