package by.bsu.fpmi.vet.video;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.application.Status;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.IplImage.create;
import static com.googlecode.javacv.cpp.opencv_core.cvAbsDiff;
import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RGB2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import static org.slf4j.LoggerFactory.getLogger;

public final class MotionDetector {
    private static final Logger LOGGER = getLogger(MotionDetector.class);

    private MotionDetectionOptions options = new MotionDetectionOptions(5, 30, 16, false);

    private final AtomicBoolean analyzeComplete = new AtomicBoolean(true);

    public void analyzeVideo() {
        if (!analyzeComplete.get()) {
            // TODO: error?
            return;
        }

        analyzeComplete.set(false);
        // TODO: Block UI
        ApplicationContext.getInstance().blockUI();
        VideoDetails videoDetails = ApplicationContext.getInstance().getVideoDetails();
        Thread analyzeThread = new Thread(new MotionDetectionAnalyzer(videoDetails));
        analyzeThread.start();
    }

    public MotionDetectionOptions getOptions() {
        return options;
    }

    public void setOptions(MotionDetectionOptions options) {
        if (options != null) {
            this.options = options;
        }
    }

    private final class MotionDetectionAnalyzer implements Runnable {
        private final VideoDetails videoDetails;
        private final FFmpegFrameGrabber grabber;

        private int totalFrameCount;

        private MotionDetectionAnalyzer(VideoDetails videoDetails) {
            this.videoDetails = videoDetails;
            this.grabber = videoDetails == null ? null : videoDetails.getGrabber();
        }

        @Override public void run() {
            if (grabber == null) {
                // TODO: Add error message?
                return;
            }

            IplImage image = null;
            IplImage prevImage = null;
            IplImage diff = null;

            CvMemStorage storage = CvMemStorage.create();

            boolean currentVideoFlag = true;
            int currentBlockFrame = 0;
            int currentBlockCounter = 0;

            //            Map<Integer, Integer> thumbnailInfo = new LinkedHashMap<>(); // TODO: why it is used?
            List<MotionDescriptor> motionDescriptors = videoDetails.getMotionDescriptors();

            try {
                grabber.restart();
                grabber.setFrameNumber(1);

                totalFrameCount = grabber.getLengthInFrames() - options.getFrameGap();

                while (true) {
                    Frame frame = grabFrame();
                    int frameNumber = grabber.getFrameNumber();

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, (int) (100 * (double) frameNumber / totalFrameCount));

                    if (frameNumber >= totalFrameCount || frame == null) {
                        break;
                    }

                    if (frameNumber % options.getFrameGap() != 0) {
                        continue;
                    }

                    cvSmooth(frame.image, frame.image, CV_GAUSSIAN, 9, 9, 2, 2);
                    if (image == null) {
                        image = cvCreateImage(cvSize(frame.image.width(), frame.image.height()), IPL_DEPTH_8U, 1);
                        cvCvtColor(frame.image, image, CV_RGB2GRAY);
                    } else {
                        if (prevImage != null) {
                            try {
                                cvReleaseImage(prevImage);
                            } catch (Exception ignored) {
                            }
                        }
                        prevImage = cvCloneImage(image);
                        try {
                            cvReleaseImage(image);
                        } catch (Exception ignored) {
                        }
                        image = cvCreateImage(cvSize(frame.image.width(), frame.image.height()), IPL_DEPTH_8U, 1);
                        cvCvtColor(frame.image, image, CV_RGB2GRAY);
                    }

                    if (diff == null) {
                        diff = create(frame.image.width(), frame.image.height(), IPL_DEPTH_8U, 1);
                    }

                    if (prevImage != null) {
                        cvAbsDiff(image, prevImage, diff);

                        cvThreshold(diff, diff, options.getColorThreshold(), 255, CV_THRESH_BINARY);

                        opencv_core.CvSeq contour = new opencv_core.CvSeq(null);

                        cvFindContours(diff, storage, contour, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST,
                                CV_CHAIN_APPROX_SIMPLE);
                        if (contour.isNull()) {
                            currentBlockCounter += options.getFrameGap();
                            if ((currentBlockCounter >= options.getSlideDetection()) && (currentBlockCounter
                                    < options.getSlideDetection() + options.getFrameGap())) {
                                // TODO: what it is 5?
                                if (frameNumber < currentBlockCounter + 5 + options.getFrameGap()) {
                                    currentVideoFlag = false;
                                }
                                motionDescriptors
                                        .add(new MotionDescriptor(getTime(currentBlockFrame), currentVideoFlag));
                                // thumbnailInfo.put(currentBlockFrame, currentBlockFrame);
                                currentBlockFrame = frameNumber - options.getSlideDetection();
                                currentVideoFlag = false;
                            }
                        } else {
                            currentBlockCounter = 0;
                            if (!currentVideoFlag) {
                                motionDescriptors.add(new MotionDescriptor(getTime(currentBlockFrame), false));
                                // thumbnailInfo.put(currentBlockFrame, currentBlockFrame);
                                currentBlockFrame = frameNumber;
                                currentVideoFlag = true;
                            }
                        }
                    }
                }

                ApplicationContext.getInstance().updateAfterMotionDetection();
            } catch (FrameGrabber.Exception e) {
                LOGGER.debug("grabber exception", e);
            } finally {
                // Release resources
                analyzeComplete.set(true);
            }
        }

        private int getTime(int frameNumber) {
            return (int) (1000 * frameNumber / videoDetails.getFrameRate());
        }

        private Frame grabFrame() {
            try {
                Frame frame;
                int frameNumber;
                do {
                    frame = grabber.grabFrame();
                    frameNumber = grabber.getFrameNumber();
                    if (frame != null && frame.image != null) {
                        return frame;
                    }
                    if (frameNumber >= totalFrameCount) {
                        return null;
                    }
                } while (true);
            } catch (FrameGrabber.Exception e) {
                return null;
            }
        }
    }
}
