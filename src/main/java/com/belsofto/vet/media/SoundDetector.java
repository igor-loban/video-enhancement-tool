package com.belsofto.vet.media;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

public final class SoundDetector {
    private static final Logger LOGGER = getLogger(SoundDetector.class);

//    private MotionDetectionOptions options = new MotionDetectionOptions(5, 30, 16, false);

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
        Thread analyzeThread = new Thread(new SoundDetectionAnalyzer(videoDetails));
        analyzeThread.start();
    }

//    public MotionDetectionOptions getOptions() {
//        return options;
//    }

//    public void setOptions(MotionDetectionOptions options) {
//        if (options != null) {
//            this.options = options;
//        }
//    }

    private final class SoundDetectionAnalyzer implements Runnable {
        private final VideoDetails videoDetails;
        private final FFmpegFrameGrabber grabber;

        private int totalFrameCount;

        private SoundDetectionAnalyzer(VideoDetails videoDetails) {
            this.videoDetails = videoDetails;
            this.grabber = videoDetails == null ? null : videoDetails.getGrabber();
        }

        @Override public void run() {
            if (grabber == null) {
                // TODO: Add error message?
                return;
            }

//            IplImage image = null;
//            IplImage prevImage = null;
//            IplImage diff = null;

//            CvMemStorage storage = CvMemStorage.create();

            boolean noisePresent = true;
            int currentBlockFrame = 0;
            int currentBlockCounter = 0;

            //            Map<Integer, Integer> thumbnailInfo = new LinkedHashMap<>(); // TODO: why it is used?
            List<SoundDescriptor> soundDescriptors = videoDetails.getSoundDescriptors();

            try {
                grabber.restart();
                grabber.setFrameNumber(1);

                totalFrameCount = grabber.getLengthInFrames() - 10;

                double mean1 = 1;
                double mean2;

                while (true) {
                    Frame frame = grabFrameWithSamples();
                    int frameNumber = grabber.getFrameNumber();

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, (int) (100 * (double) frameNumber / totalFrameCount));

                    if (frameNumber >= totalFrameCount || frame == null) {
                        break;
                    }

//                    mean2 = getSamplesMean(frame.samples);

//                    double diff = Math.abs(mean2 / mean1 - 1);
//                    noisePresent = diff < 0.5 ? noisePresent : !noisePresent;

//                    if (diff > ) {
//                    }

//                    if (frameNumber % options.getFrameGap() != 0) {
//                        continue;
//                    }

//                    cvSmooth(frame.image, frame.image, CV_GAUSSIAN, 9, 9, 2, 2);
//                    if (image == null) {
//                        image = cvCreateImage(cvSize(frame.image.width(), frame.image.height()), IPL_DEPTH_8U, 1);
//                        cvCvtColor(frame.image, image, CV_RGB2GRAY);
//                    } else {
//                        if (prevImage != null) {
//                            try {
//                                cvReleaseImage(prevImage);
//                            } catch (Exception ignored) {
//                            }
//                        }
//                        prevImage = cvCloneImage(image);
//                        try {
//                            cvReleaseImage(image);
//                        } catch (Exception ignored) {
//                        }
//                        image = cvCreateImage(cvSize(frame.image.width(), frame.image.height()), IPL_DEPTH_8U, 1);
//                        cvCvtColor(frame.image, image, CV_RGB2GRAY);
//                    }
//
//                    if (diff == null) {
//                        diff = create(frame.image.width(), frame.image.height(), IPL_DEPTH_8U, 1);
//                    }
//
//                    if (prevImage != null) {
//                        cvAbsDiff(image, prevImage, diff);
//
//                        cvThreshold(diff, diff, options.getColorThreshold(), 255, CV_THRESH_BINARY);
//
//                        opencv_core.CvSeq contour = new opencv_core.CvSeq(null);
//
//                        cvFindContours(diff, storage, contour, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST,
//                                CV_CHAIN_APPROX_SIMPLE);
//                        if (contour.isNull()) {
//                            currentBlockCounter += options.getFrameGap();
//                            if ((currentBlockCounter >= options.getSlideDetection()) && (currentBlockCounter
//                                    < options.getSlideDetection() + options.getFrameGap())) {
//                                // TODO: what it is 5?
//                                if (frameNumber < currentBlockCounter + 5 + options.getFrameGap()) {
//                                    soundPresent = false;
//                                }
//                                soundDescriptors
//                                        .add(new MotionDescriptor(getTime(currentBlockFrame), soundPresent));
//                                // thumbnailInfo.put(currentBlockFrame, currentBlockFrame);
//                                currentBlockFrame = frameNumber - options.getSlideDetection();
//                                soundPresent = false;
//                            }
//                        } else {
//                            currentBlockCounter = 0;
//                            if (!soundPresent) {
//                                soundDescriptors.add(new MotionDescriptor(getTime(currentBlockFrame), false));
//                                // thumbnailInfo.put(currentBlockFrame, currentBlockFrame);
//                                currentBlockFrame = frameNumber;
//                                soundPresent = true;
//                            }
//                        }
//                    }
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

        private Frame grabFrameWithSamples() {
            try {
                Frame frame;
                int frameNumber;
                do {
                    frame = grabber.grabFrame();
                    frameNumber = grabber.getFrameNumber();
                    if (frame != null && frame.samples != null) {
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
