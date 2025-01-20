#!/usr/bin/env python3
#
# Demonstrates streaming and modifying the image via OpenCV
#


import cscore as cs
import ntcore.client as nt
import numpy as np
import cv2
import time
import ntcore
import traceback
from robotpy_apriltag import AprilTagDetector
from networktables import NetworkTables





RESOLUTION_WIDTH=1280
RESOLUTION_HEIGHT=720
TARGET_FPS=121

SETTINGS_STREAM_PORT=5800
ANNOTATED_STREAM_PORT=5801

class FrameTimer(object):

    def __init__(self,sample_count):
        self.sample_count = sample_count
        self.start_time = time.time()
        self.samples = 0
        self.sps = 0

    def tick(self):
        self.samples += 1
        if self.samples > self.sample_count:
            elapsed = time.time() - self.start_time
            self.sps = self.sample_count / elapsed
            self.start_time = time.time()
            self.samples =0

def get_stack_trace_lines(exception, wrap_length=80):
    """
    Accepts an exception object and returns a list of strings
    representing the stack trace for the exception, including
    tilde characters underlining the source of the error as
    seen with traceback.print_exc(). Lines longer than the specified
    wrap_length will be wrapped to the next line.

    :param exception: Exception object to extract the stack trace from.
    :param wrap_length: Maximum length of a line before wrapping.
    :return: List of strings corresponding to the stack trace lines.
    """
    raw_lines = traceback.format_exc().splitlines(keepends=False)
    wrapped_lines = []

    for line in raw_lines:
        while len(line) > wrap_length:
            wrapped_lines.append(line[:wrap_length])
            line = line[wrap_length:]
        wrapped_lines.append(line)

    return wrapped_lines

def put_exception_onto_frame(frame, exception ):
    VERTICAL_SPACING = 30
    INIT_VERTICAL_POS = 120
    vertical_pos = INIT_VERTICAL_POS
    HORIZONTAL_POS = 20
    for line in get_stack_trace_lines(exception):
        cv2.putText(frame, line, (HORIZONTAL_POS,vertical_pos),cv2.FONT_HERSHEY_SIMPLEX,0.8,(0,0,255),2,cv2.LINE_AA)
        vertical_pos += VERTICAL_SPACING


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

    detector = AprilTagDetector()
    detector_config = AprilTagDetector.Config()
    #photonvision settings from    https://github.com/PhotonVision/photonvision/blob/main/photon-core/src/main/java/org/photonvision/vision/pipeline/AprilTagPipelineSettings.java
    # also see https://robotpy.readthedocs.io/projects/apriltag/en/latest/robotpy_apriltag/AprilTagDetector.html
    detector_config.numThreads =4
    detector_config.refineEdges = True
    detector_config.quadDecimate = 4
    detector_config.quadSigma = 0
    quad_params = AprilTagDetector.QuadThresholdParameters()

    quad_params.maxNumMaxima = 10
    quad_params.criticalAngle = 45 * 3.14159 / 180.0
    quad_params.maxLineFitMSE = 10.0
    quad_params.minWhiteBlackDiff = 5
    quad_params.deglitch = False
    quad_params.minClusterPixels = 5
    quad_params.minWhiteBlackDiff = 5

    detector.setConfig(detector_config)
    detector.setQuadThresholdParameters(quad_params)

    frame_timer = FrameTimer(200)

    detector.addFamily("tag36h11")

    mjpegServer = cs.MjpegServer("httpserver", SETTINGS_STREAM_PORT)
    mjpegServer.setSource(camera)

    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")

    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(camera)

    cvSource = cs.CvSource("cvsource", cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH,RESOLUTION_HEIGHT, 100)
    cvMjpegServer = cs.MjpegServer("cvhttpserver", ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)

    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")

    test = np.zeros(shape=(600, 800, 3), dtype=np.uint8)

    while True:

        frame_timer.tick()
        
        time, frame = cvsink.grabFrame(test)
        if time == 0:
            print("error:", cvsink.getError())
            continue
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
            table.putNumber("idTag", tag_id)
            table.putNumber("tagHeight", tag_height)
            table.putNumber("tagWidth", tag_width)
            table.putNumber("tagX", tag_x)
            table.putNumber("tagY", tag_y)
            table.putNumber("timestamp", time)

            




            


        except Exception as e:


            # Commented out for now
            tb = traceback.print_exc()




        # gray_frame = cv2.resize(gray_frame, (frame.shape[1], frame.shape[0]))
        # gray_frame = cv2.cvtColor(gray_frame, cv2.COLOR_GRAY2BGR)

        # combined_frame = cv2.hconcat([gray_frame, frame])  


        cvSource.putFrame(frame)



if __name__ == "__main__":
    main()          
