package com.belsofto.vet.media;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

            List<SoundDescriptor> soundDescriptors = videoDetails.getSoundDescriptors();

            try {
                grabber.restart();
                grabber.setFrameNumber(1);

                totalFrameCount = grabber.getLengthInFrames() - 10;

                List<AudioFrameDescriptor> descriptors = new ArrayList<>();

                while (true) {
                    Frame frame = grabFrameWithSamples();
                    int frameNumber = grabber.getFrameNumber();

                    if (frameNumber % 3 != 0) {
                        continue;
                    }

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, (int) (90 * (double) frameNumber / totalFrameCount));

                    if (frameNumber >= totalFrameCount || frame == null) {
                        break;
                    }

                    descriptors.add(new AudioFrameDescriptor(frameNumber, frame.samples));
                }

                List<List<Double>> meansList = new ArrayList<>();
                int count = 0;
                for (AudioFrameDescriptor descriptor : descriptors) {
                    List<byte[]> samples = descriptor.getSamplesAsBytes();
                    if (samples.size() > meansList.size()) {
                        for (int difference = samples.size() - meansList.size(); difference > 0; --difference) {
                            meansList.add(new ArrayList<Double>());
                        }
                    }

                    for (int i = 0; i < samples.size(); ++i) {
                        byte[] bytes = samples.get(i);
                        List<Double> means = meansList.get(i);

                        long sum = calcSum(bytes);
                        double mean = (double) sum / bytes.length;
                        descriptor.setMean(i, mean);
                        means.add(mean);
                    }

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, 90 + (int) (5 * (double) count++ / descriptors.size()));
                }

                List<Double> upperBounds = new ArrayList<>();
                List<Double> lowerBounds = new ArrayList<>();
                for (List<Double> means : meansList) {
                    Collections.sort(means);
                    int size = means.size();
                    upperBounds.add(means.get((int) (0.9 * size)));
                    lowerBounds.add(means.get((int) (0.1 * size)));
                }

                Iterator<AudioFrameDescriptor> iterator = descriptors.iterator();
                AudioFrameDescriptor descriptor = iterator.next();
                boolean noisePresent = hasNoise(descriptor, upperBounds, lowerBounds);
                soundDescriptors.add(new SoundDescriptor(getTime(descriptor.getFrameNumber()), noisePresent));

                count = 0;
                boolean noisePresentPrev = noisePresent;
                int frameNumber;
                int frameNumberPrev = descriptor.getFrameNumber();
                while (iterator.hasNext()) {
                    descriptor = iterator.next();
                    noisePresent = hasNoise(descriptor, upperBounds, lowerBounds);
                    if (noisePresent == noisePresentPrev) {
                        continue;
                    }
                    frameNumber = descriptor.getFrameNumber();
                    if (frameNumber - frameNumberPrev <= 10 && noisePresentPrev) {
                        continue;
                    }

                    soundDescriptors.add(new SoundDescriptor(getTime(descriptor.getFrameNumber()), noisePresent));
                    noisePresentPrev = noisePresent;

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, 95 + (int) (5 * (double) count++ / descriptors.size()));
                }

                for (int i = soundDescriptors.size() - 2; i >= 0; --i) {
                    SoundDescriptor rightDescriptor = soundDescriptors.get(i + 1);
                    SoundDescriptor leftDescriptor = soundDescriptors.get(i);
                    if (!leftDescriptor.isNoisePresent()
                            && rightDescriptor.getTime() - leftDescriptor.getTime() < 1000) {
                        soundDescriptors.remove(i + 1);
                        soundDescriptors.remove(i);
                        i--;
                    }
                }

                for (int i = soundDescriptors.size() - 2; i >= 0; --i) {
                    SoundDescriptor rightDescriptor = soundDescriptors.get(i + 1);
                    SoundDescriptor leftDescriptor = soundDescriptors.get(i);
                    if (leftDescriptor.isNoisePresent()
                            && rightDescriptor.getTime() - leftDescriptor.getTime() < 100) {
                        soundDescriptors.remove(i + 1);
                        soundDescriptors.remove(i);
                        i--;
                    }
                }

                ApplicationContext.getInstance().updateAfterSoundDetection();
            } catch (FrameGrabber.Exception e) {
                LOGGER.debug("grabber exception", e);
            } finally {
                // Release resources
                analyzeComplete.set(true);
            }
        }

        private boolean hasNoise(AudioFrameDescriptor descriptor, List<Double> upperBounds, List<Double> lowerBounds) {
            for (int i = 0; i < upperBounds.size(); i++) {
                double upperBound = upperBounds.get(i);
                double lowerBound = lowerBounds.get(i);
                Double mean = descriptor.getMean(i);
                if (mean != null && (mean >= upperBound || mean <= lowerBound)) {
                    return false;
                }
            }
            return true;
        }

        private long calcSum(byte[] bytes) {
            long sum = 0;
            for (byte value : bytes) {
                sum += value;
            }
            return sum;
        }

        private byte[] toByteArray(Buffer sample) {
            ByteBuffer byteBuffer = new Pointer(sample).asByteBuffer();
            byte[] result = new byte[byteBuffer.remaining()];
            byteBuffer.get(result);
            return result;
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

        private final class Statistics {
            private long sum;
            private List<Byte> bytes = new ArrayList<>();

            private double mean;
            private double disp;

            public void add(byte[] bytes, long sum) {
                for (byte value : bytes) {
                    this.bytes.add(value);
                }
                this.sum += sum;
            }

            public void evaluate() {
                mean = (double) sum / bytes.size();
                for (byte value : bytes) {
                    disp += square(value - mean);
                }
                disp = Math.sqrt(disp / (bytes.size() - 1));
            }

            private double square(double value) {
                return value * value;
            }

            public double getLowerBound() {
                return mean - disp;
            }

            public double getUpperBound() {
                return mean + disp;
            }
        }

        private final class AudioFrameDescriptor {
            private final int frameNumber;
            private final List<byte[]> samplesAsBytes;
            private final Map<Integer, Double> means = new HashMap<>();

            private AudioFrameDescriptor(int frameNumber, Buffer[] samples) {
                this.frameNumber = frameNumber;
                this.samplesAsBytes = new ArrayList<>(samples.length);
                for (Buffer sample : samples) {
                    samplesAsBytes.add(toByteArray(sample));
                }
            }

            public int getFrameNumber() {
                return frameNumber;
            }

            public List<byte[]> getSamplesAsBytes() {
                return samplesAsBytes;
            }

            public void setMean(int i, double mean) {
                means.put(i, mean);
            }

            public Double getMean(int i) {
                return means.get(i);
            }
        }
    }
}
