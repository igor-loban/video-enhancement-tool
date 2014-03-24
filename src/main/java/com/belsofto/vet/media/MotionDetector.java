package com.belsofto.vet.media;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import javax.swing.JPanel;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.CvContour;
import static com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.CvSeq;
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

    public void analyzeVideo() {
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

    private final class MotionDetectionAnalyzer extends JPanel implements Runnable {
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

            //            setBackground(Color.GREEN);
            //            JFrame testFrame = new JFrame();
            //            testFrame.setSize(700, 700);
            //            testFrame.add(this);
            //            testFrame.setVisible(true);

            IplImage image = null;
            IplImage prevImage = null;
            IplImage diff = null;

            CvMemStorage storage = CvMemStorage.create();

            MotionThreshold motionThreshold;
            MotionThreshold prevMotionThreshold = MotionThreshold.NO;
            boolean currentBlockHasMovement = true;
            int currentBlockStartFrame = 0;
            int currentBlockLength = 0;

            List<MotionDescriptor> motionDescriptors = videoDetails.getMotionDescriptors();
            motionDescriptors.clear();

            try {
                grabber.restart();
                grabber.setFrameNumber(1);

                totalFrameCount = grabber.getLengthInFrames();

                //                getGraphics().drawImage(diffCopy.getBufferedImage(), 0, 250, null);
                //                getGraphics().clearRect(590, 260, 100, 50);
                //                getGraphics().drawString("C: " + (contour.isNull() ? "null" : contour.total()),
                // 600, 270);
                //                try {
                //                    Thread.sleep(millis);
                //                } catch (InterruptedException ignored) {
                //                }

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

                        // From HIGH to LOW
                        motionThreshold = MotionThreshold.NO;

                        IplImage diffCopy = diff.clone();
                        cvThreshold(diffCopy, diffCopy, 200, 255, CV_THRESH_BINARY);
                        CvSeq contour = new CvSeq(null);
                        cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                CV_CHAIN_APPROX_SIMPLE);
                        if (contour.isNull()) {
                            diffCopy = diff.clone();
                            cvThreshold(diffCopy, diffCopy, 150, 255, CV_THRESH_BINARY);
                            contour = new CvSeq(null);
                            cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                    CV_CHAIN_APPROX_SIMPLE);
                            if (contour.isNull()) {
                                diffCopy = diff.clone();
                                cvThreshold(diffCopy, diffCopy, 100, 255, CV_THRESH_BINARY);
                                contour = new CvSeq(null);
                                cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                        CV_CHAIN_APPROX_SIMPLE);
                                if (!contour.isNull()) {
                                    motionThreshold = MotionThreshold.LOW;
                                }
                            } else {
                                motionThreshold = MotionThreshold.MEDIUM;
                            }
                        } else {
                            motionThreshold = MotionThreshold.HIGH;
                        }

                        if (contour.isNull()) { // If no movement detected

                            currentBlockLength += options.getFrameGap();

                            if (currentBlockLength >= options.getSlideMinFrame()
                                    && currentBlockLength < options.getSlideMinFrame() + options
                                    .getFrameGap()) { // New slide detected

                                prevMotionThreshold = currentBlockHasMovement ? motionThreshold : MotionThreshold.NO;
                                motionDescriptors.add(new MotionDescriptor(getTime(currentBlockStartFrame),
                                        prevMotionThreshold));
                                currentBlockStartFrame = frameNumber - options.getSlideMinFrame();
                                currentBlockHasMovement = false;
                            }
                        } else { // If movement detected
                            currentBlockLength = 0;

                            if (!currentBlockHasMovement) { // If previous block has no movement
                                motionDescriptors
                                        .add(new MotionDescriptor(getTime(currentBlockStartFrame), MotionThreshold.NO));
                                currentBlockStartFrame = frameNumber;
                                currentBlockHasMovement = true; // New block has movement
                            } else if (motionThreshold != prevMotionThreshold) {
                                motionDescriptors
                                        .add(new MotionDescriptor(getTime(currentBlockStartFrame), prevMotionThreshold));
                                prevMotionThreshold = motionThreshold;
                                currentBlockStartFrame = frameNumber;
                                currentBlockHasMovement = true; // New block has movement
                            }
                        }
                    }
                }

                if (!currentBlockHasMovement) {
                    motionDescriptors.add(new MotionDescriptor(getTime(currentBlockStartFrame), MotionThreshold.NO));
                }

                ApplicationContext.getInstance().updateAfterMotionDetection();
            } catch (FrameGrabber.Exception e) {
                LOGGER.debug("grabber exception", e);
            }
        }

        private int getTime(int frameNumber) {
            return (int) (1000 * frameNumber / videoDetails.getFrameRate());
        }

        private Frame grabFrame() {
            try {
                Frame frame;
                int frameNumber;
                int prevFrameNumber = Integer.MIN_VALUE;
                int limit = 5_000;
                int count = 0;
                do {
                    frame = grabber.grabFrame();
                    if (frame == null) {
                        return null;
                    }
                    if (frame.image != null) {
                        return frame;
                    }
                    if (frame.samples != null) {
                        continue;
                    }
                    frameNumber = grabber.getFrameNumber();
                    if (frameNumber >= totalFrameCount) {
                        return null;
                    }
                    if (frameNumber == prevFrameNumber) {
                        count++;
                    }
                    if (count >= limit) {
                        return null;
                    }
                    prevFrameNumber = frameNumber;
                } while (true);
            } catch (FrameGrabber.Exception e) {
                return null;
            }
        }
    }
}
