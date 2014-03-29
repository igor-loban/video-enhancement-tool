package com.belsofto.vet.detection.motion;

import java.awt.Color;

public class MotionDetectionOptions {
    private int frameGap = 5;
    private int slideMinFrame = 30;
    private boolean highVideoQuality = false;
    private int lowThreshold = 20;
    private int mediumThreshold = 70;
    private int highThreshold = 150;
    private boolean usedMediumThreshold = true;
    private boolean usedHighThreshold = true;
    private Color noMotionColor = MotionThreshold.NO.color();
    private Color lowMotionColor = MotionThreshold.LOW.color();
    private Color mediumMotionColor = MotionThreshold.MEDIUM.color();
    private Color highMotionColor = MotionThreshold.HIGH.color();

    public int getFrameGap() {
        return frameGap;
    }

    public int getSlideMinFrame() {
        return slideMinFrame;
    }

    public boolean isHighVideoQuality() {
        return highVideoQuality;
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public int getMediumThreshold() {
        return mediumThreshold;
    }

    public int getHighThreshold() {
        return highThreshold;
    }

    public boolean isUsedMediumThreshold() {
        return usedMediumThreshold;
    }

    public boolean isUsedHighThreshold() {
        return usedHighThreshold;
    }

    public Color getNoMotionColor() {
        return noMotionColor;
    }

    public Color getLowMotionColor() {
        return lowMotionColor;
    }

    public Color getMediumMotionColor() {
        return mediumMotionColor;
    }

    public Color getHighMotionColor() {
        return highMotionColor;
    }

    public void setFrameGap(int frameGap) {
        this.frameGap = frameGap;
    }

    public void setSlideMinFrame(int slideMinFrame) {
        this.slideMinFrame = slideMinFrame;
    }

    public void setHighVideoQuality(boolean highVideoQuality) {
        this.highVideoQuality = highVideoQuality;
    }

    public void setLowThreshold(int lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    public void setMediumThreshold(int mediumThreshold) {
        this.mediumThreshold = mediumThreshold;
    }

    public void setHighThreshold(int highThreshold) {
        this.highThreshold = highThreshold;
    }

    public void setUsedMediumThreshold(boolean usedMediumThreshold) {
        this.usedMediumThreshold = usedMediumThreshold;
    }

    public void setUsedHighThreshold(boolean usedHighThreshold) {
        this.usedHighThreshold = usedHighThreshold;
    }

    public void setNoMotionColor(Color noMotionColor) {
        this.noMotionColor = noMotionColor;
        MotionThreshold.NO.color(noMotionColor);
    }

    public void setLowMotionColor(Color lowMotionColor) {
        this.lowMotionColor = lowMotionColor;
        MotionThreshold.LOW.color(lowMotionColor);
    }

    public void setMediumMotionColor(Color mediumMotionColor) {
        this.mediumMotionColor = mediumMotionColor;
        MotionThreshold.MEDIUM.color(mediumMotionColor);
    }

    public void setHighMotionColor(Color highMotionColor) {
        this.highMotionColor = highMotionColor;
        MotionThreshold.HIGH.color(highMotionColor);
    }

    @Override public String toString() {
        return "{frameGap=" + frameGap +
                ", slideMinFrame=" + slideMinFrame +
                ", highVideoQuality=" + highVideoQuality +
                ", lowThreshold=" + lowThreshold +
                ", mediumThreshold=" + mediumThreshold +
                ", highThreshold=" + highThreshold +
                ", usedMediumThreshold=" + usedMediumThreshold +
                ", usedHighThreshold=" + usedHighThreshold +
                ", noMotionColor=" + noMotionColor +
                ", lowMotionColor=" + lowMotionColor +
                ", mediumMotionColor=" + mediumMotionColor +
                ", highMotionColor=" + highMotionColor + "}";
    }
}
