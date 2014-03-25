package com.belsofto.vet.detection.motion;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.belsofto.vet.media.VideoDetails;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import javax.swing.JPanel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    private MotionDetectionOptions options = new MotionDetectionOptions();

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

    public void setMotionDetectionOptions(MotionDetectionOptions options) {
        if (options != null) {
            this.options = options;
        }
    }

    private final class MotionDetectionAnalyzer extends JPanel implements Runnable {
        private final VideoDetails videoDetails;

        private final List<MotionDescriptor> motionDescriptors1 = new ArrayList<>();
        private final List<MotionDescriptor> motionDescriptors2 = new ArrayList<>();
        private final List<MotionDescriptor> motionDescriptors3 = new ArrayList<>();
        private final List<MotionDescriptor> motionDescriptors4 = new ArrayList<>();

        private final AtomicInteger finishedWorkerCount = new AtomicInteger();
        private final AtomicInteger timestampMillis1 = new AtomicInteger();
        private final AtomicInteger timestampMillis2 = new AtomicInteger();
        private final AtomicInteger timestampMillis3 = new AtomicInteger();
        private final AtomicInteger timestampMillis4 = new AtomicInteger();

        private MotionDetectionAnalyzer(VideoDetails videoDetails) {
            this.videoDetails = videoDetails;
        }

        @Override public void run() {
            long totalTimeNanos = videoDetails.getTotalTimeMillis() * 1_000;
            long interval = totalTimeNanos / 4;
            File file = videoDetails.getSourceFile();

            Thread worker1 = new Thread(new Worker(motionDescriptors1, timestampMillis1, 0, interval, file));
            Thread worker2 =
                    new Thread(new Worker(motionDescriptors2, timestampMillis2, interval + 1, 2 * interval, file));
            Thread worker3 =
                    new Thread(new Worker(motionDescriptors3, timestampMillis3, 2 * interval + 1, 3 * interval, file));
            Thread worker4 = new Thread(
                    new Worker(motionDescriptors4, timestampMillis4, 3 * interval + 1, totalTimeNanos, file));

            worker1.start();
            worker2.start();
            worker3.start();
            worker4.start();

            ApplicationContext context = ApplicationContext.getInstance();

            long intervalMillis = (long) ((double) interval / 1_000);
            long totalTimeMillis = (long) ((double) totalTimeNanos / 1_000);
            long time;
            while (finishedWorkerCount.get() < 4) {
                // Update UI
                time = -6 * intervalMillis;
                time += timestampMillis1.get();
                time += timestampMillis2.get();
                time += timestampMillis3.get();
                time += timestampMillis4.get();

                if (time < 0) {
                    time = 0;
                }

                context.setStatus(Status.ANALYZE, (int) ((double) time * 100.0 / totalTimeMillis));

                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {
                }
            }

            List<MotionDescriptor> result = new ArrayList<>(motionDescriptors1);
            appendMotionDescriptors(result, motionDescriptors2);
            appendMotionDescriptors(result, motionDescriptors3);
            appendMotionDescriptors(result, motionDescriptors4);

            List<MotionDescriptor> motionDescriptors = videoDetails.getMotionDescriptors();
            motionDescriptors.clear();
            motionDescriptors.addAll(result);

            context.updateAfterMotionDetection();
        }

        private void appendMotionDescriptors(List<MotionDescriptor> result, List<MotionDescriptor> descriptors) {
            if (!result.isEmpty() && !descriptors.isEmpty()
                    && getLastMotionThreshold(result) == getFirstMotionThreshold(descriptors)) {
                descriptors.remove(0);
            }
            result.addAll(descriptors);
        }

        private MotionThreshold getFirstMotionThreshold(List<MotionDescriptor> descriptors) {
            return descriptors.get(0).getMotionThreshold();
        }

        private MotionThreshold getLastMotionThreshold(List<MotionDescriptor> descriptors) {
            return descriptors.get(descriptors.size() - 1).getMotionThreshold();
        }

        private final class Worker implements Runnable {
            private final List<MotionDescriptor> motionDescriptors;
            private final AtomicInteger progress;
            private final long startTimeNanos;
            private final long endTimeNanos;
            private final FFmpegFrameGrabber grabber;

            public Worker(List<MotionDescriptor> motionDescriptors, AtomicInteger progress, long startTimeNanos,
                          long endTimeNanos, File file) {
                this.motionDescriptors = motionDescriptors;
                this.progress = progress;
                this.startTimeNanos = startTimeNanos;
                this.endTimeNanos = endTimeNanos;

                this.grabber = new FFmpegFrameGrabber(file);
            }

            @Override public void run() {
                try {
                    grabber.restart();
                    grabber.setTimestamp(startTimeNanos);


                    IplImage image = null;
                    IplImage prevImage = null;
                    IplImage diff = null;

                    CvMemStorage storage = CvMemStorage.create();

                    MotionThreshold motionThreshold;
                    MotionThreshold prevMotionThreshold = MotionThreshold.NO;
                    boolean currentBlockHasMovement = true;
                    long frameGapNanos = convertToNanos(options.getFrameGap());
                    long slideMinNanos = convertToNanos(options.getSlideMinFrame());
                    long currentBlockStartTimestamp = startTimeNanos;
                    long currentBlockLength = 0;
                    long currentTimestamp;
                    int frameCount = 0;
                    GrabResult grabResult;
                    IplImage frameImage;

                    while (true) {
                        grabResult = grabImageFrame();
                        frameImage = grabResult.getImage();
                        currentTimestamp = grabResult.getTimestamp();
                        grabResult = null;

                        progress.set((int) ((double) currentTimestamp / 1_000));

                        if (frameImage == null) {
                            break;
                        }

                        if (frameCount++ % options.getFrameGap() != 0) {
                            continue;
                        }

                        cvSmooth(frameImage, frameImage, CV_GAUSSIAN, 9, 9, 2, 2);
                        if (image == null) {
                            image = cvCreateImage(cvSize(frameImage.width(), frameImage.height()), IPL_DEPTH_8U, 1);
                            cvCvtColor(frameImage, image, CV_RGB2GRAY);
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
                            image = cvCreateImage(cvSize(frameImage.width(), frameImage.height()), IPL_DEPTH_8U, 1);
                            cvCvtColor(frameImage, image, CV_RGB2GRAY);
                        }

                        if (diff == null) {
                            diff = create(frameImage.width(), frameImage.height(), IPL_DEPTH_8U, 1);
                        }

                        if (prevImage != null) {
                            cvAbsDiff(image, prevImage, diff);

                            motionThreshold = MotionThreshold.NO;

                            CvSeq contour = new CvSeq(null);
                            IplImage diffCopy;
                            if (options.isUsedHighThreshold()) {
                                diffCopy = diff.clone();
                                cvThreshold(diffCopy, diffCopy, options.getHighThreshold(), 255, CV_THRESH_BINARY);
                                cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                        CV_CHAIN_APPROX_SIMPLE);
                            }
                            if (contour.isNull()) {
                                if (options.isUsedMediumThreshold()) {
                                    diffCopy = diff.clone();
                                    cvThreshold(diffCopy, diffCopy, options.getMediumThreshold(), 255,
                                            CV_THRESH_BINARY);
                                    contour = new CvSeq(null);
                                    cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class),
                                            CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                                }
                                if (contour.isNull()) {
                                    diffCopy = diff.clone();
                                    cvThreshold(diffCopy, diffCopy, options.getLowThreshold(), 255, CV_THRESH_BINARY);
                                    contour = new CvSeq(null);
                                    cvFindContours(diffCopy, storage, contour, Loader.sizeof(CvContour.class),
                                            CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                                    if (!contour.isNull()) {
                                        motionThreshold = MotionThreshold.LOW;
                                    }
                                } else {
                                    motionThreshold = MotionThreshold.MEDIUM;
                                }
                            } else {
                                motionThreshold = MotionThreshold.HIGH;
                            }

                            if (motionThreshold == MotionThreshold.NO) { // If no movement detected
                                currentBlockLength += frameGapNanos;
                                if (currentBlockLength >= slideMinNanos
                                        && currentBlockLength < slideMinNanos + frameGapNanos) { // New slide detected
                                    motionDescriptors
                                            .add(new MotionDescriptor(currentBlockStartTimestamp, prevMotionThreshold));
                                    prevMotionThreshold = motionThreshold;
                                    currentBlockStartTimestamp = currentTimestamp - slideMinNanos;
                                    currentBlockHasMovement = false;
                                }
                            } else { // If movement detected
                                currentBlockLength = 0;
                                if (!currentBlockHasMovement) { // If previous block has no movement
                                    motionDescriptors
                                            .add(new MotionDescriptor(currentBlockStartTimestamp, MotionThreshold.NO));
                                    currentBlockStartTimestamp = currentTimestamp;
                                    currentBlockHasMovement = true; // New block has movement
                                } else if (motionThreshold != prevMotionThreshold) {
                                    motionDescriptors
                                            .add(new MotionDescriptor(currentBlockStartTimestamp, prevMotionThreshold));
                                    prevMotionThreshold = motionThreshold;
                                    currentBlockStartTimestamp = currentTimestamp;
                                    currentBlockHasMovement = true; // New block has movement
                                }
                            }
                        }
                    }

                    if (!currentBlockHasMovement) {
                        motionDescriptors.add(new MotionDescriptor(currentBlockStartTimestamp, MotionThreshold.NO));
                    }

                    finishedWorkerCount.incrementAndGet();
                } catch (FrameGrabber.Exception e) {
                    // TODO: fail
                } finally {
                    if (grabber != null) {
                        try {
                            grabber.stop();
                            grabber.release();
                        } catch (FrameGrabber.Exception ignored) {
                        }
                    }
                }
            }

            private GrabResult grabImageFrame() {
                try {
                    Frame frame;
                    long timestampNanos;
                    long timestampNanosPrev = Long.MIN_VALUE;
                    int limit = 5_000;
                    int count = 0;
                    do {
                        frame = grabber.grabFrame();
                        timestampNanos = grabber.getTimestamp();
                        if (timestampNanos >= endTimeNanos) {
                            return new GrabResult(timestampNanos, null);
                        }
                        if (frame == null) {
                            return new GrabResult(grabber.getTimestamp(), null);
                        }
                        if (frame.image != null) {
                            return new GrabResult(grabber.getTimestamp(), frame.image);
                        }
                        if (frame.samples != null) {
                            continue;
                        }
                        if (timestampNanos == timestampNanosPrev) {
                            if (++count >= limit) {
                                return new GrabResult(timestampNanos, null);
                            }
                        } else {
                            timestampNanosPrev = timestampNanos;
                        }
                    } while (true);
                } catch (FrameGrabber.Exception e) {
                    return new GrabResult(-1, null);
                }
            }

            private long convertToNanos(int frameNumber) {
                return (long) (1_000_000 * frameNumber / videoDetails.getFrameRate());
            }

            private final class GrabResult {
                private final long timestamp;
                private final IplImage image;

                private GrabResult(long timestamp, IplImage image) {
                    this.timestamp = timestamp;
                    this.image = image;
                }

                public long getTimestamp() {
                    return timestamp;
                }

                public IplImage getImage() {
                    return image;
                }
            }
        }
    }
}
