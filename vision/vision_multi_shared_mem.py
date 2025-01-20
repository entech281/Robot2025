import cscore as cs
import numpy as np
import cv2
import time
import multiprocessing
from multiprocessing.shared_memory import SharedMemory
import os, sys
from multiprocessing import Process, Queue, Lock
from robotpy_apriltag import AprilTagDetector


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

class Timer:
    def __init__(self):
        self.timers = {}

    def start(self, name):
        self.timers[name] = time.perf_counter()

    def done(self,name):
        self.timers[name] = time.perf_counter() - self.timers[name]


SETTINGS_STREAM_PORT=5800
ANNOTATED_STREAM_PORT=5801
RESOLUTION_WIDTH=800
RESOLUTION_HEIGHT=600
FRAME_RATE=100
NUM_SLOTS=5
FRAME_SHAPE = (RESOLUTION_HEIGHT, RESOLUTION_WIDTH)


def frame_producer(shared_mem_name, to_grab_q, ready_to_process_q):
    produced = 0
    timer = Timer()
    grabbed_frames_mem = SharedMemory(name=shared_mem_name)

    grabbed_frames = np.ndarray((NUM_SLOTS, *FRAME_SHAPE), dtype=np.uint8, buffer=grabbed_frames_mem.buf)
    initial_data = np.zeros(shape=(RESOLUTION_HEIGHT, RESOLUTION_WIDTH, 3), dtype=np.uint8)

    camera = cs.UsbCamera("usbcam", 0)
    camera.getProperty('auto_exposure').set(1)
    camera.getProperty('white_balance_automatic').set(0)
    camera.getProperty('raw_exposure_time_absolute').set(60)
    camera.setVideoMode(cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, FRAME_RATE)

    print(f"mjpg server listening at http://0.0.0.0:{SETTINGS_STREAM_PORT}")
    mjpegServer = cs.MjpegServer("httpserver", SETTINGS_STREAM_PORT)
    mjpegServer.setSource(camera)

    cvsink = cs.CvSink("cvsink")
    cvsink.setSource(camera)

    while True:

        timer.start('grab')
        time, frame = cvsink.grabFrame(initial_data)
        timer.done('grab')

        if time == 0:
            print("error:", cvsink.getError())

        gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        slot_idx = to_grab_q.get()
        #print(f"Grabbed-> {slot_idx} total {produced}, waiting:{to_grab_q.qsize()}")
        grabbed_frames[slot_idx] = gray_frame
        try:
            ready_to_process_q.put_nowait(slot_idx)
        except:
            #print("no room to notify detector!")
            pass
        produced += 1

def frame_detector(grabbed_shared_mem_name, ready_shared_mem_name, to_process_q, to_send_q,to_grab_q, process_id):
    processed =0
    grabbed_slots = SharedMemory(name=grabbed_shared_mem_name)
    ready_slots = SharedMemory(name=ready_shared_mem_name)

    grabbed_frames = np.ndarray((NUM_SLOTS, *FRAME_SHAPE), dtype=np.uint8, buffer=grabbed_slots.buf)
    processed_frames = np.ndarray((NUM_SLOTS, *FRAME_SHAPE), dtype=np.uint8, buffer=ready_slots.buf)

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
        #print("waiting for another to process...")
        slot_idx = to_process_q.get()
        #print(f"processing..{slot_idx}")
        gray_frame = grabbed_frames[slot_idx]
        #print("processing: got frame")
        #cv2.putText(frame, f"{frame_timer.sps:.0f} FPS",(20,30),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
        cv2.putText(gray_frame, f"Multi {process_id}", (50, 100),cv2.FONT_HERSHEY_SIMPLEX,1,(0,255,0),2, cv2.LINE_AA)

        timer.start('detect')
        detections = detector.detect(gray_frame)
        timer.done('detect')
        #print("processing:done detection")
        timer.start('process_detect')
        for detection in detections:
           tag_id = detection.getId()
           center = detection.getCenter()  # (x, y) coordinates of the center

           # Draw the center point and tag ID on the frame
           cv2.circle(gray_frame, (int(center[0]), int(center[1])), 5, (0, 255, 0), -1)
           cv2.putText(gray_frame, f"ID: {tag_id}", (int(center[0]), int(center[1]) - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 1, cv2.LINE_AA)

           # Draw the tag's corners on the frame
           def line_between_pnts (pt1, pt2):

               p1 = (int(pt1.x), int(pt1.y))
               p2 = (int(pt2.x), int(pt2.y))
               cv2.line(gray_frame, p1, p2, (255, 0, 0), 2)

           line_between_pnts(detection.getCorner(0), detection.getCorner(1))
           line_between_pnts(detection.getCorner(1), detection.getCorner(2))
           line_between_pnts(detection.getCorner(2), detection.getCorner(3))
           line_between_pnts(detection.getCorner(3), detection.getCorner(0))
        timer.done('process_detect')
        processed_frames[slot_idx] = gray_frame
        #print("processing:send frame")
        try:
            to_send_q.put_nowait(slot_idx)
        except:
            #print("no room to notify publisher")
            pass
        #print("processing:freed sender")
        try:
            to_grab_q.put_nowait(slot_idx)
        except :
            #print(f"Cant free grabber")
            pass
        #print(f"Processed->  {slot_idx} total {processed} waiting:{to_process_q.qsize()}")
        #print(f"Freed to  Grab -> {slot_idx}")
        processed += 1

def frame_publisher(ready_shared_mem_name,to_send_q,to_process_q):
    published = 0
    frame_timer = FrameTimer(100)
    ready_slots = SharedMemory(name=ready_shared_mem_name)
    processed_frames = np.ndarray((NUM_SLOTS, *FRAME_SHAPE), dtype=np.uint8, buffer=ready_slots.buf)

    print(f"OpenCV output mjpg server listening at http://0.0.0.0:{ANNOTATED_STREAM_PORT}")
    cvSource = cs.CvSource("cvsource", cs.VideoMode.PixelFormat.kMJPEG, RESOLUTION_WIDTH, RESOLUTION_HEIGHT, FRAME_RATE)
    cvMjpegServer = cs.MjpegServer("cvhttpserver", ANNOTATED_STREAM_PORT)
    cvMjpegServer.setSource(cvSource)

    while True:
        # Get a slot index from the consumer queue
        #print("waiting for another to send...")
        slot_idx = to_send_q.get()
        # Read the frame from shared memory
        gray_frame = processed_frames[slot_idx]
        cv2.putText(gray_frame, f"{frame_timer.sps:.0f} FPS",(20,30),cv2.FONT_HERSHEY_SIMPLEX,1.2,(0,255,0),2, cv2.LINE_AA)
        cvSource.putFrame(gray_frame)

        try:
            to_process_q.put_nowait(slot_idx)
        except:
            #print("no room to free processor")
            pass
        frame_timer.tick()
        #print(f"Published->  {slot_idx} total {published} waiting:{to_send_q.qsize()}")
        #print(f"Freed to Process-> {slot_idx}")
        published += 1

if __name__ == '__main__':
    NUM_CONSUMERS = 2
    grabbed_gray_mem = SharedMemory(create=True, size=NUM_SLOTS * np.prod(FRAME_SHAPE) * np.dtype(np.uint8).itemsize, name="grabbed_frames")
    ready_to_send_mem = SharedMemory(create=True, size=NUM_SLOTS * np.prod(FRAME_SHAPE) * np.dtype(np.uint8).itemsize, name="ready_to_send_frames")

    to_grab_q = multiprocessing.Queue(maxsize=NUM_SLOTS)
    to_process_q = multiprocessing.Queue(maxsize=NUM_SLOTS)
    to_send_q = multiprocessing.Queue(maxsize=NUM_SLOTS)

    # Initialize producer queue with all available slots
    for i in range(NUM_SLOTS):
        to_grab_q.put(i)

    # Start processes
    producer_process = multiprocessing.Process(target=frame_producer, args=("grabbed_frames", to_grab_q,to_process_q))
    consumer_processes = [
        multiprocessing.Process(target=frame_detector, args=("grabbed_frames","ready_to_send_frames",  to_process_q,  to_send_q,to_grab_q,i))
        for i in range(NUM_CONSUMERS)
    ]
    result_publisher_process = multiprocessing.Process(target=frame_publisher, args=("ready_to_send_frames",to_send_q,to_process_q))

    try:
        producer_process.start()
        for consumer_process in consumer_processes:
            consumer_process.start()
        result_publisher_process.start()

        producer_process.join()
        print("Producer Ended")
        for consumer_process in consumer_processes:
            consumer_process.join()
        print("Consumers Ended")
        result_publisher_process.join()
        print("Producer Ended")
    except KeyboardInterrupt:
        print("Shutting down...")
        producer_process.terminate()
        for consumer_process in consumer_processes:
            consumer_process.terminate()
        result_publisher_process.terminate()

    print("Ending Program!")
    grabbed_gray_mem.close()
    grabbed_gray_mem.unlink()
    ready_to_send_mem.close()
    ready_to_send_mem.unlink()