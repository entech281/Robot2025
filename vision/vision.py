#!/usr/bin/env python3
"""
Vision processing module for FRC robot using AprilTag detection.
Streams camera feed and performs real-time AprilTag detection using OpenCV.
"""

import cscore as cs
import numpy as np
import cv2
import time
import traceback
from robotpy_apriltag import AprilTagDetector
from networktables import NetworkTables

# Constants
RESOLUTION_WIDTH = 800
RESOLUTION_HEIGHT = 600
TARGET_FPS = 121
SETTINGS_STREAM_PORT = 5800
ANNOTATED_STREAM_PORT = 5801

<<<<<<< HEAD
class FrameTimer:
    """Tracks frame processing timing statistics."""
    
    def __init__(self, sample_count):
        """Initialize the frame timer.
        
        Args:
            sample_count: Number of samples to average over
        """
=======



RESOLUTION_WIDTH=1280
RESOLUTION_HEIGHT=720
TARGET_FPS=121

SETTINGS_STREAM_PORT=5800
ANNOTATED_STREAM_PORT=5801

class FrameTimer(object):

    def __init__(self,sample_count):
>>>>>>> 1a8adb52f27790e431507616f7222eb1b1aeb6af
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

<<<<<<< HEAD
def setup_apriltag_detector():
    """Configure and return an AprilTag detector with optimal settings."""
=======

def main():


    NetworkTables.initialize()

    table = NetworkTables.getTable("vision")


    # targetPub = table.getBooleanTopic("hasTarget").publish()
    # idPub = table.getIntegerTopic("tagID").publish()
    # heightPub = table.getIntegerTopic("tagHeight").publish()
    # widthPub = table.getIntegerTopic("tagWidth").publish()
    # xPub = table.getDoubleTopic("tagX").publish()
    # yPub = table.getDoubleTopic("tagY").publish()
    # timestampPub = table.getDoubleTopic("timestamp").publish()

    inst = ntcore.NetworkTableInstance.getDefault()

    table = inst.getTable("datatable")


    targetPub = table.getBooleanTopic("hasTarget").publish()
    idPub = table.getIntegerTopic("tagID").publish()
    heightPub = table.getIntegerTopic("tagHeight").publish()
    widthPub = table.getIntegerTopic("tagWidth").publish()
    xPub = table.getDoubleTopic("tagX").publish()
    yPub = table.getDoubleTopic("tagY").publish()
    timestampPub = table.getDoubleTopic("timestamp").publish()






    camera = cs.UsbCamera("usbcam", 0)
    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH,RESOLUTION_HEIGHT, TARGET_FPS)

>>>>>>> 1a8adb52f27790e431507616f7222eb1b1aeb6af
    detector = AprilTagDetector()
    detector_config = AprilTagDetector.Config()
    
    # Configure detector settings
    detector_config.numThreads = 4
    detector_config.refineEdges = True
    detector_config.quadDecimate = 4
    # I think if we are going to be moving
    # a lot then we should increase this
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
    
    return tag_id, avg_height, avg_width, tag_x, tag_y

def main():
    """Main vision processing loop."""
    # Initialize NetworkTables
    NetworkTables.initialize()
    table = NetworkTables.getTable("vision")
    
    # Setup camera
    camera = cs.UsbCamera("usbcam", 0)
    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG, 
                       RESOLUTION_WIDTH, RESOLUTION_HEIGHT, TARGET_FPS)
    
    # Setup AprilTag detector
    detector = setup_apriltag_detector()
    
    # Setup frame timer
    frame_timer = FrameTimer(200)

    
    # Setup video streaming
    mjpegServer = cs.MjpegServer("httpserver", SETTINGS_STREAM_PORT)
    mjpegServer.setSource(camera)
    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")
    
    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(camera)
    
    cvSource = cs.CvSource("cvsource", cs.VideoMode.PixelFormat.kMJPEG, 
                          RESOLUTION_WIDTH, RESOLUTION_HEIGHT, 100)
    cvMjpegServer = cs.MjpegServer("cvhttpserver", ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)
    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")
    
    # Initialize frame buffer
    frame_buffer = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH), dtype=np.uint8)
    
    while True:
        frame_timer.tick()
        
        timestamp, frame = cvsink.grabFrame(frame_buffer)
        if timestamp == 0:
            print("error:", cvsink.getError())
            continue
<<<<<<< HEAD
            
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
                has_target = True
                detection = detections[0]  # Process first detection
                tag_id, tag_height, tag_width, tag_x, tag_y = process_apriltag_detection(
                    frame, detection, RESOLUTION_WIDTH, RESOLUTION_HEIGHT)
            
            # Update NetworkTables
            table.putBoolean("hasTarget", has_target)
=======
        fps = frame_timer.sps


        cv2.putText(frame, f"{fps:.0f} FPS",(20,30),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
        cv2.putText(frame, f"{cvSource.getActualFPS():.0f} CV FPS",(20,60),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
        gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        average_intensity = cv2.mean(gray_frame)[0]

        thresh = 1 * average_intensity

        _, gray_frame = cv2.threshold(gray_frame, thresh, 255, cv2.THRESH_BINARY)

        # gray_frame = cv2.

        try:
            #the body of this try block should be moved to a function
            hasTarget = False
            tag_id = -1
            tag_height = -1
            tag_width = -1
            tag_x = -1
            tag_y = -1


            detections = detector.detect(gray_frame)
            for detection in detections:
                hasTarget = True


                tag_id = detection.getId()
                center = detection.getCenter()  # (x, y) coordinates of the center
                corner0 = detection.getCorner(0)
                corner1 = detection.getCorner(1)
                corner2 = detection.getCorner(2)
                corner3 = detection.getCorner(3)

                #exernalize this logic and write tests!
                avg_height = ((corner0.y - corner3.y) + (corner1.y -corner2.y)) / 2
                tag_height = avg_height

                avg_width =  ((corner1.x - corner0.x) + (corner2.x -corner3.x)) / 2
                tag_width = avg_width

                tag_x = (2 * (center.x / RESOLUTION_WIDTH)) - 1
                tag_y = (2 * (center.y / RESOLUTION_HEIGHT)) - 1

                
                avg_height = ((corner3 - corner0) + (corner2 -corner1)) / 2
                tag_height = avg_height

                avg_width =  ((corner1 - corner0) + (corner2 -corner3)) / 2
                tag_width = avg_width

                tag_x = center.x
                tag_y = center.y


                # Draw the center point and tag ID on the frame
                cv2.circle(frame, (int(center[0]), int(center[1])), 5, (0, 255, 0), -1)
                cv2.putText(frame, f"ID: {tag_id}", (int(center[0]), int(center[1]) - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 1, cv2.LINE_AA)

                # Draw the tag's corners on the frame
                def line_between_pnts (pt1, pt2):

                    p1 = (int(pt1.x), int(pt1.y))
                    p2 = (int(pt2.x), int(pt2.y))
                    cv2.line(frame, p1, p2, (255, 0, 0), 2)

                line_between_pnts(detection.getCorner(0), detection.getCorner(1))
                line_between_pnts(detection.getCorner(1), detection.getCorner(2))
                line_between_pnts(detection.getCorner(2), detection.getCorner(3))
                line_between_pnts(detection.getCorner(3), detection.getCorner(0))

                # Print the tag's center coordinates
                #print(f"Detected Tag ID: {tag_id}, Center: {center}")



            # Boolean hasTarget
            # int tagID
            # int tagHeight
            # int tagWidth
            # double tagX (left -1 to right 1)
            # double tagY (bottom -1 to top 1)
            # long timestamp
            table.putBoolean("hasTarget", hasTarget)
>>>>>>> 1a8adb52f27790e431507616f7222eb1b1aeb6af
            table.putNumber("idTag", tag_id)
            table.putNumber("tagHeight", tag_height)
            table.putNumber("tagWidth", tag_width)
            table.putNumber("tagX", tag_x)
            table.putNumber("tagY", tag_y)
            table.putNumber("timestamp", timestamp)
            
        except Exception as e:


            # Commented out for now
            tb = traceback.print_exc()
        

<<<<<<< HEAD
    	# gray_frame = cv2.resize(gray_frame, (frame.shape[1], frame.shape[0]))
=======



        # gray_frame = cv2.resize(gray_frame, (frame.shape[1], frame.shape[0]))
>>>>>>> 1a8adb52f27790e431507616f7222eb1b1aeb6af
        # gray_frame = cv2.cvtColor(gray_frame, cv2.COLOR_GRAY2BGR)
        

        # combined_frame = cv2.hconcat([gray_frame, frame])  

        # Convert back to BGR for display
        if len(frame.shape) == 2:
            frame = cv2.cvtColor(frame, cv2.COLOR_GRAY2BGR)
        cvSource.putFrame(frame)

<<<<<<< HEAD
=======


>>>>>>> 1a8adb52f27790e431507616f7222eb1b1aeb6af
if __name__ == "__main__":
    main()
