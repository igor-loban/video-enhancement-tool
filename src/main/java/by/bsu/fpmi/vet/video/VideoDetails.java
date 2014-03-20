package by.bsu.fpmi.vet.video;

import com.googlecode.javacv.FFmpegFrameGrabber;

import java.io.File;
import java.util.Map;

public final class VideoDetails {
    private final File sourceFile;

    private FFmpegFrameGrabber grabber;
    private Map<Integer, Boolean> metaInfo;

    private double frameRate;
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

    public Map<Integer, Boolean> getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(Map<Integer, Boolean> metaInfo) {
        this.metaInfo = metaInfo;
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
}
