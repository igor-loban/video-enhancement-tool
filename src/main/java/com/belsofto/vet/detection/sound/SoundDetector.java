package com.belsofto.vet.detection.sound;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.belsofto.vet.media.VideoDetails;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

public final class SoundDetector {
    private static final Logger LOGGER = getLogger(SoundDetector.class);

    private SoundDetectionOptions options = new SoundDetectionOptions();

    private final AtomicBoolean analyzeComplete = new AtomicBoolean(true);

    public void analyzeVideo() {
        if (!analyzeComplete.get()) {
            // TODO: error?
            return;
        }

        analyzeComplete.set(false);
        VideoDetails videoDetails = ApplicationContext.getInstance().getVideoDetails();
        Thread analyzeThread = new Thread(new SoundDetectionAnalyzer(videoDetails));
        analyzeThread.start();
    }

    public SoundDetectionOptions getOptions() {
        return options;
    }

    public void setOptions(SoundDetectionOptions options) {
        this.options = options;
    }

    private final class SoundDetectionAnalyzer implements Runnable {
        private final VideoDetails videoDetails;
        private final FFmpegFrameGrabber grabber;

        private long totalTimeNanos;
        private SampleFormat sampleFormat;

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
                int avSampleCode = grabber.getSampleFormat();
                switch (avSampleCode) {
                    case 1:
                    case 6:
                        sampleFormat = SampleFormat.SHORT;
                        break;
                    default:
                        sampleFormat = SampleFormat.FLOAT;
                }

                totalTimeNanos = grabber.getLengthInTime();

                List<AudioFrameDescriptor> descriptors = new ArrayList<>();
                int frameCount = 0;
                while (true) {
                    Frame frame = grabSamplesFrame();
                    if (frame == null) {
                        break;
                    }

                    if (frameCount++ % options.getFrameGap() != 0) {
                        continue;
                    }

                    long timestampNanos = grabber.getTimestamp();
                    descriptors.add(new AudioFrameDescriptor(timestampNanos, frame.samples));

                    // Update UI
                    ApplicationContext.getInstance()
                            .setStatus(Status.ANALYZE, (int) (90 * (double) timestampNanos / totalTimeNanos));
                }

                List<List<Double>> meansList = new ArrayList<>();
                for (int i = 0, length = descriptors.size(); i < length; ++i) {
                    AudioFrameDescriptor descriptor = descriptors.get(i);

                    List<double[]> samples = descriptor.getSamples();
                    if (samples.size() > meansList.size()) {
                        for (int difference = samples.size() - meansList.size(); difference > 0; --difference) {
                            meansList.add(new ArrayList<Double>());
                        }
                    }

                    for (int j = 0; j < samples.size(); ++j) {
                        double[] sample = samples.get(j);
                        List<Double> means = meansList.get(j);

                        double sum = calcSum(sample);
                        double mean = sum / sample.length;
                        descriptor.setMean(j, mean);
                        means.add(mean);
                    }

                    // Update UI
                    ApplicationContext.getInstance().setStatus(Status.ANALYZE, 90 + (int) (5 * (double) i / length));
                }

                List<Double> upperBounds = new ArrayList<>();
                List<Double> lowerBounds = new ArrayList<>();
                for (List<Double> means : meansList) {
                    Collections.sort(means);
                    int size = means.size();
                    upperBounds.add(means.get((int) (options.getMaxNoiseBound() * size)));
                    lowerBounds.add(means.get((int) (options.getMinNoiseBound() * size)));
                }


                AudioFrameDescriptor descriptor = descriptors.iterator().next();
                SoundThreshold soundThreshold = hasNoise(descriptor, upperBounds, lowerBounds);
                soundDescriptors
                        .add(new SoundDescriptor(convertToMillis(descriptor.getTimestampNanos()), soundThreshold));
                SoundThreshold soundThresholdPrev = soundThreshold;
                for (int i = 1, length = descriptors.size(); i < length; i++) {
                    descriptor = descriptors.get(i);

                    soundThreshold = hasNoise(descriptor, upperBounds, lowerBounds);
                    if (soundThreshold == soundThresholdPrev) {
                        continue;
                    }

                    soundDescriptors
                            .add(new SoundDescriptor(convertToMillis(descriptor.getTimestampNanos()), soundThreshold));
                    soundThresholdPrev = soundThreshold;

                    // Update UI
                    ApplicationContext.getInstance().setStatus(Status.ANALYZE, 95 + (int) (5 * (double) i / length));
                }

                for (int i = soundDescriptors.size() - 2; i > 0; --i) {
                    SoundDescriptor rightDescriptor = soundDescriptors.get(i + 1);
                    SoundDescriptor leftDescriptor = soundDescriptors.get(i);
                    if (rightDescriptor.getTime() - leftDescriptor.getTime() < options.getSoundLowerBound()) {
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

        private SoundThreshold hasNoise(AudioFrameDescriptor descriptor, List<Double> upperBounds,
                                        List<Double> lowerBounds) {
            for (int i = 0; i < upperBounds.size(); i++) {
                double upperBound = upperBounds.get(i);
                double lowerBound = lowerBounds.get(i);
                Double mean = descriptor.getMean(i);
                if (mean != null && (mean >= upperBound || mean <= lowerBound)) {
                    return SoundThreshold.SOUND;
                }
            }
            return SoundThreshold.NOISE;
        }

        private double calcSum(double[] sample) {
            double sum = 0;
            for (double value : sample) {
                sum += value;
            }
            return sum;
        }

        private double[] toDoubleArray(Buffer sample) {
            ByteBuffer byteBuffer = new Pointer(sample).asByteBuffer();
            switch (sampleFormat) {
                case SHORT:
                    return convertShortsToDoubles(byteBuffer);
                case FLOAT:
                    return convertFloatsToDoubles(byteBuffer);
                default:
                    return null;
            }
        }

        private double[] convertFloatsToDoubles(ByteBuffer byteBuffer) {
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            float[] data = new float[floatBuffer.remaining()];
            floatBuffer.get(data);
            double[] result = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        private double[] convertShortsToDoubles(ByteBuffer byteBuffer) {
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            short[] data = new short[shortBuffer.remaining()];
            shortBuffer.get(data);
            double[] result = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i];
            }
            return result;
        }

        private int convertToMillis(long timestampNanos) {
            return (int) (timestampNanos / 1_000);
        }

        private Frame grabSamplesFrame() {
            try {
                Frame frame;
                long timestampNanos;
                long timestampNanosPrev = Long.MIN_VALUE;
                int limit = 5_000;
                int count = 0;
                do {
                    frame = grabber.grabFrame();
                    if (frame == null) {
                        return null;
                    }
                    if (frame.samples != null) {
                        return frame;
                    }
                    if (frame.image != null) {
                        continue;
                    }
                    timestampNanos = grabber.getTimestamp();
                    if (timestampNanos >= totalTimeNanos) {
                        return null;
                    }
                    if (timestampNanos == timestampNanosPrev) {
                        if (++count >= limit) {
                            return null;
                        }
                    } else {
                        timestampNanosPrev = timestampNanos;
                    }
                } while (true);
            } catch (FrameGrabber.Exception e) {
                return null;
            }
        }

        private final class AudioFrameDescriptor {
            private final long timestampNanos;
            private final List<double[]> samples;
            private final Map<Integer, Double> means = new HashMap<>();

            private AudioFrameDescriptor(long timestampNanos, Buffer[] samples) {
                this.timestampNanos = timestampNanos;
                this.samples = new ArrayList<>(samples.length);
                for (Buffer sample : samples) {
                    this.samples.add(toDoubleArray(sample));
                }
            }

            public long getTimestampNanos() {
                return timestampNanos;
            }

            public List<double[]> getSamples() {
                return samples;
            }

            public void setMean(int i, double mean) {
                means.put(i, mean);
            }

            public Double getMean(int i) {
                return means.get(i);
            }
        }
    }

    private static enum SampleFormat {
        SHORT, FLOAT
    }
}
