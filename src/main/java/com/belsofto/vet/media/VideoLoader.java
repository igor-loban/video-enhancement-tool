package com.belsofto.vet.media;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.belsofto.vet.ui.component.VideoPlayer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoLoader implements Runnable {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private final File videoFile;

    private VideoLoader(File videoFile) {
        this.videoFile = videoFile;
    }

    public static void load(File videoFile) {
        ApplicationContext.getInstance().setStatus(Status.LOADING);
        Thread videoLoader = new Thread(new VideoLoader(videoFile));
        videoLoader.start();
    }

    @Override public void run() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();

            VideoDetails videoDetails = new VideoDetails(videoFile);
            videoDetails.setGrabber(grabber);
            int totalFrameCount = grabber.getLengthInFrames();
            videoDetails.setTotalFrameCount(totalFrameCount);
            double frameRate = grabber.getFrameRate();
            videoDetails.setFrameRate(frameRate);
            videoDetails.setTotalTimeMillis((int) (grabber.getLengthInTime() / 1_000));
            videoDetails.setWidth(grabber.getImageWidth());
            videoDetails.setHeight(grabber.getImageHeight());

            grabber.stop();

            ApplicationContext.getInstance().updateAfterLoad(videoDetails);
        } catch (FrameGrabber.Exception e) {
            LOGGER.debug("media loading exception", e);
        }
    }
}
