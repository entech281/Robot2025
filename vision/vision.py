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
import video_util



# Constants
LOCAL_TEST_MODE = False  # Set to True to run NetworkTables locally
TEAM_NUMBER = 281
RESOLUTION_WIDTH = 640
RESOLUTION_HEIGHT = 480
TARGET_FPS = 121
SETTINGS_STREAM_PORT = 5800
ANNOTATED_STREAM_PORT = 5801
NOT_AVAILABLE=-999
MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP = 2
FRAMES_TO_SKIP_FOR_DEBUG_STREAM=5
DETECTOR_QUAD_DECIMATE = 1

class CameraProperties(object):
    EXPOSURE_RAW_ABSOLUTE = 'raw_exposure_time_absolute'
    AUTO_EXPOSURE = 'auto_exposure'
    ABSOLUTE_EXPOSURE = 'exposure_time_absolute'
    BRIGHTNESS= "brightness"
    AUTO_WHITE_BALANCE="white_balance_automatic"
    HUE="hue"

class CameraValues(object):
    EXPOSURE_SUPER_LOW = 2
    HIGHISH_EXPOSURE = 30
    AUTO_EXPOSURE_MANUAL=1
    NOT_PRETTY_DARN_BRIGHT=30
    NO_AUTO_WHITE_BALANCE=0
    PRETTY_BRIGHT=60
    MAX_HUE=100


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
    detector_config.quadDecimate = DETECTOR_QUAD_DECIMATE
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

def put_text( frame, location, value ):
    cv2.putText(frame, value, location,cv2.FONT_HERSHEY_SIMPLEX,0.8,(255,255,255),2,cv2.LINE_AA )


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
    camera_location = video_util.get_devices_by_location()
    cameras =  {}
    current_camera = 'top'
    for location, name in camera_location.items():
        cameras[location] = cs.UsbCamera(name['DEVNAME'], name['DEVNAME'])

        if location == 'top':
            print("Setting top properties")
            cameras[location].setVideoMode(cs.VideoMode.PixelFormat.kMJPEG,
                            RESOLUTION_WIDTH, RESOLUTION_HEIGHT, TARGET_FPS)
            cameras[location].getProperty(CameraProperties.EXPOSURE_RAW_ABSOLUTE).set(CameraValues.EXPOSURE_SUPER_LOW)
            cameras[location].getProperty(CameraProperties.AUTO_EXPOSURE).set(CameraValues.AUTO_EXPOSURE_MANUAL)
            cameras[location].getProperty(CameraProperties.BRIGHTNESS).set(CameraValues.NOT_PRETTY_DARN_BRIGHT)
            cameras[location].getProperty(CameraProperties.ABSOLUTE_EXPOSURE).set(CameraValues.EXPOSURE_SUPER_LOW)
            cameras[location].getProperty(CameraProperties.AUTO_WHITE_BALANCE).set(CameraValues.NO_AUTO_WHITE_BALANCE)
            cameras[location].getProperty(CameraProperties.HUE).set(CameraValues.MAX_HUE)
        elif location == 'side':
            print("Setting side properties")
            cameras[location].setVideoMode(cs.VideoMode.PixelFormat.kMJPEG,
                            RESOLUTION_WIDTH, RESOLUTION_HEIGHT, TARGET_FPS)
            cameras[location].getProperty(CameraProperties.AUTO_EXPOSURE).set(CameraValues.AUTO_EXPOSURE_MANUAL)
            cameras[location].getProperty(CameraProperties.BRIGHTNESS).set(CameraValues.NOT_PRETTY_DARN_BRIGHT)
            cameras[location].getProperty(CameraProperties.AUTO_WHITE_BALANCE).set(CameraValues.NO_AUTO_WHITE_BALANCE)
            cameras[location].getProperty(CameraProperties.EXPOSURE_RAW_ABSOLUTE).set(CameraValues.EXPOSURE_SUPER_LOW)
            cameras[location].getProperty(CameraProperties.ABSOLUTE_EXPOSURE).set(CameraValues.EXPOSURE_SUPER_LOW)
        elif location == 'bottom':
            print("Setting bottom properties")
            pass
        print("Properties for " + location + " camera:")
        print_camera_properties(cameras[location])


    # Setup AprilTag detector
    detector = setup_apriltag_detector()

    # Setup frame timer
    frame_timer = FrameTimer(200)


    # Setup video streaming
    mjpegServer = cs.CameraServer.addServer('annotated',SETTINGS_STREAM_PORT)

    mjpegServer.setSource(cameras['top'])
    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")


    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(cameras['top'])
    cvSource = cs.CameraServer.putVideo("vision", RESOLUTION_WIDTH, RESOLUTION_HEIGHT)

    table.putString("camera", "top")

    cvMjpegServer = cs.CameraServer.addServer("annotated",ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)
    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")

    # Initialize frame buffer
    frame_buffer = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH), dtype=np.uint8)

    counter = 0
    missed_frames_counter = 0
    missed_frames_total_counter = 0
    loop_total_counter = 0

    while True:
        camera_net = table.getString("camera", "top")
        if camera_net != current_camera:
            current_camera = camera_net
        if current_camera in cameras.keys():
            mjpegServer.setSource(cameras[current_camera])
            cvsink.setSource(cameras[current_camera])
        else:
            mjpegServer.setSource(cameras['top'])
            cvsink.setSource(cameras['top'])
            current_camera.replace(current_camera, 'top')
        frame_timer.tick()

        loop_total_counter += 1
        table.putNumber("loop_total_counter", loop_total_counter)

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
            tag_id = []
            tag_height = []
            tag_width = []
            tag_x = []
            tag_y = []
            tag_x_widths = []

            # Process detections
            detections = detector.detect(frame)
            
            if len(detections) > 0 :
                missed_frames_counter = 0
                has_target = True
                for detection in detections:
                    id, height, width, x, y, x_widths = process_apriltag_detection(
                        frame, detection, RESOLUTION_WIDTH, RESOLUTION_HEIGHT)
                    tag_id.append(id)
                    tag_height.append(float(height))
                    tag_width.append(float(width))
                    tag_x.append(float(x))
                    tag_y.append(float(y))
                    tag_x_widths.append(float(x_widths))

                # Update NetworkTables
                table.putBoolean("hasTarget", has_target)
                table.putNumberArray("tagID", tag_id)
                table.putNumberArray("tagHeight", tag_height)
                table.putNumberArray("tagWidth", tag_width)
                table.putNumberArray("tagX", tag_x)
                table.putNumberArray("tagY", tag_y)
                table.putNumber("timestamp", timestamp)
                table.putNumberArray("tagXWidths", tag_x_widths)
                table.putNumber("missed_frames_counter", missed_frames_counter)
                table.putNumber("missed_frames_total_counter", missed_frames_total_counter)
                table.putNumber("numberOfTargets", len(tag_id))
                table.putStringArray("cameraUsed", [current_camera for i in range(len(tag_id))])

                put_text(frame,(20,370),f"xws: { float(tag_x_widths[0]):.2f}")
                put_text(frame,(20,400),f"w: {float(tag_width[0]):.2f}")
                put_text(frame,(20,430),f"x: {float(tag_x[0]):.2f}")
                put_text(frame,(20,460),f"lc: { float(loop_total_counter)}")
                put_text(frame,(20,340),f"h: {float(tag_height[0]):.2f}")

            else:
                missed_frames_counter += 1
                missed_frames_total_counter += 1
                if missed_frames_counter > MISSED_FRAMES_TO_TOLERATE_BEFORE_GIVING_UP:
                    table.putBoolean("hasTarget", False)
                    table.putNumberArray("tagID", tag_id)
                    table.putNumberArray("tagHeight", tag_height)
                    table.putNumberArray("tagWidth", tag_width)
                    table.putNumberArray("tagX", tag_x)
                    table.putNumberArray("tagY", tag_y)
                    table.putNumber("timestamp", timestamp)
                    table.putNumberArray("tagXWidths", tag_x_widths)
                    table.putNumber("missed_frames_counter", missed_frames_counter)
                    table.putNumber("missed_frames_total_counter", missed_frames_total_counter)
                    table.putNumber("numberOfTargets", 0)

        except Exception as e:
            put_exception_onto_frame(frame,e)
            tb = traceback.print_exc()


        if counter % FRAMES_TO_SKIP_FOR_DEBUG_STREAM == 0:
            cvSource.putFrame(frame)

            # sd_source.putFrame(frame)
            counter = 0

        counter += 1


if __name__ == "__main__":
    main()
