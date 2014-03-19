package by.bsu.fpmi.vet.motion;

public final class MotionDetectionOptions {
    private final int frameGap;
    private final int slideDetection;
    private final int colorThreshold;
    private final boolean highVideoQuality;

    public MotionDetectionOptions(int frameGap, int slideDetection, int colorThreshold, boolean highVideoQuality) {
        this.frameGap = frameGap;
        this.slideDetection = slideDetection;
        this.colorThreshold = colorThreshold;
        this.highVideoQuality = highVideoQuality;
    }

    public int getFrameGap() {
        return frameGap;
    }

    public int getSlideDetection() {
        return slideDetection;
    }

    public int getColorThreshold() {
        return colorThreshold;
    }

    public boolean isHighVideoQuality() {
        return highVideoQuality;
    }
}
