package com.belsofto.vet.media;

import com.belsofto.vet.application.ApplicationContext;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.cpp.avcodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class VideoRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoRecorder.class);

    private static final int DEFAULT_AUDIO_CHANNEL = 1;

    private final VideoRecordOptions options = new VideoRecordOptions();

    public boolean saveFragment(File file) {
        try {
            long leftBound = options.getLeftBoundNanos();
            long rightBound = options.getRightBoundNanos();

            VideoDetails videoDetails = ApplicationContext.getInstance().getVideoDetails();

            FFmpegFrameGrabber grabber = videoDetails.getGrabber();
            grabber.restart();
            grabber.setTimestamp(leftBound);

            FFmpegFrameRecorder recorder =
                    new FFmpegFrameRecorder(file, videoDetails.getWidth(), videoDetails.getHeight());

            recorder.setFrameRate(videoDetails.getFrameRate());

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
            recorder.setAudioBitrate(44100);
            recorder.setAudioChannels(
                    grabber.getAudioChannels() > 0 ? grabber.getAudioChannels() : DEFAULT_AUDIO_CHANNEL);
            recorder.setSampleFormat(grabber.getSampleFormat());
            recorder.setSampleRate(grabber.getSampleRate());

            recorder.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
                if (grabber.getTimestamp() >= rightBound) {
                    break;
                }
            }

            recorder.stop();
            grabber.stop();
            return true;
        } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
            LOGGER.debug("record error", e);
            return false;
        }
    }

    public VideoRecordOptions getOptions() {
        return options;
    }
}
