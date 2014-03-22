package com.belsofto.vet.media;

import com.googlecode.javacv.FFmpegFrameGrabber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class VideoDetails {
    private final File sourceFile;

    private FFmpegFrameGrabber grabber;
    private List<MotionDescriptor> motionDescriptors = new ArrayList<>();
    private List<SoundDescriptor> soundDescriptors = new ArrayList<>();

    private double frameRate;
    private long totalTime; // Millis
    private int totalFrameCount;
    private int width;
    private int height;

    public VideoDetails(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }

    public void setGrabber(FFmpegFrameGrabber grabber) {
        this.grabber = grabber;
    }

    public List<MotionDescriptor> getMotionDescriptors() {
        return motionDescriptors;
    }

    public void setMotionDescriptors(List<MotionDescriptor> motionDescriptors) {
        this.motionDescriptors = motionDescriptors;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public int getTotalFrameCount() {
        return totalFrameCount;
    }

    public void setTotalFrameCount(int totalFrameCount) {
        this.totalFrameCount = totalFrameCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public List<SoundDescriptor> getSoundDescriptors() {
        return soundDescriptors;
    }
}
