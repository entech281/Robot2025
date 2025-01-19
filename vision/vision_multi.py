import cscore as cs
import numpy as np
import cv2
import time
import multiprocessing
import os, sys
from multiprocessing import Process, Queue, Lock
from robotpy_apriltag import AprilTagDetector


class Timer:
    def __init__(self):
        self.timers = {}

    def start(self, name):
        self.timers[name] = time.perf_counter()

    def done(self,name):
        self.timers[name] = time.perf_counter() - self.timers[name]

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


SETTINGS_STREAM_PORT=5800
ANNOTATED_STREAM_PORT=5801
RESOLUTION_WIDTH=800
RESOLUTION_HEIGHT=600
FRAME_RATE=100


def frame_producer(frame_queue,source=0):
    timer = Timer()
    initial_data = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH, 3), dtype=np.uint8)
    cv2.ocl.setUseOpenCL(True)
    camera = cs.UsbCamera("usbcam", 0)
    camera.getProperty('auto_exposure').set(1)
    camera.getProperty('white_balance_automatic').set(0)
    camera.getProperty('raw_exposure_time_absolute').set(60)

    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")
    mjpegServer = cs.MjpegServer("httpserver", SETTINGS_STREAM_PORT)
    mjpegServer.setSource(camera)

    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(camera)

    # print_camera_properties(camera)

    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, FRAME_RATE)
    while True:
        timer.start('grab')
        time, frame = cvsink.grabFrame(initial_data)
        timer.done('grab')
        if time == 0:
            print("error:", cvsink.getError())

        frame_queue.put( frame)

def frame_consumer(frame_queue,done_queue, process_id):

    timer = Timer()
    detector = AprilTagDetector()
    detector_config = AprilTagDetector.Config()
    detector_config.numThreads = 4
    detector_config.refineEdges = True
    detector_config.quadDecimate = 1  ## 2 vs 1 makes a HUGE difference!!!
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


    detector.addFamily("tag36h11")

    while True:
        frame = frame_queue.get()

        #cv2.putText(frame, f"{frame_timer.sps:.0f} FPS",(20,30),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
        cv2.putText(frame, f"Multi {process_id}", (50, 100),cv2.FONT_HERSHEY_SIMPLEX,1,(0,255,0),2, cv2.LINE_AA)

        timer.start('gray')
        gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        timer.done('gray')

        timer.start('detect')
        detections = detector.detect(gray_frame)
        timer.done('detect')

        timer.start('process_detect')
        for detection in detections:
           tag_id = detection.getId()
           center = detection.getCenter()  # (x, y) coordinates of the center

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
        timer.done('process_detect')
        #frame_count+= 1
        done_queue.put(frame)
        #cvSource.putFrame(frame)

if __name__ == '__main__':

    frame_queue = multiprocessing.Queue(maxsize=10)
    result_frame_queue = multiprocessing.Queue(maxsize=10)

    frame_count = multiprocessing.Value('i', 0)
    # Create the producer process
    producer_process = multiprocessing.Process(target=frame_producer, args=(frame_queue,))

    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")
    cvSource = cs.CvSource("cvsource", cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, FRAME_RATE)
    cvMjpegServer = cs.MjpegServer("cvhttpserver", ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)

    # Create multiple consumer processes
    num_consumers = 3
    consumer_processes = [
        multiprocessing.Process(target=frame_consumer, args=(frame_queue,result_frame_queue, i)) for i in range(num_consumers)
    ]

    # Start the producer and consumer processes
    producer_process.start()
    for process in consumer_processes:
        process.start()

    last_time = time.time()
    frame_count = 0
    fps=0
    while True:
        frame = result_frame_queue.get()
        if frame is not None:
            if frame_count >= 100 :
                elapsed = time.time() - last_time
                fps = frame_count/elapsed
                frame_count = 0
                last_time = time.time()
            else:
                frame_count += 1

            cv2.putText(frame, f"{fps:.0f} FPS",(20,30),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
            cvSource.putFrame(frame)



    # Wait for the producer process to finish
    producer_process.join()

    # Wait for all consumer processes to finish
    for process in consumer_processes:
        process.join()
