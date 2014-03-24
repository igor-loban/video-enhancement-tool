package com.belsofto.vet.media;

import java.awt.Color;

public final class MotionDetectionOptions {
    private int frameGap = 5;
    private int slideMinFrame = 30;
    private boolean highVideoQuality = false;
    private int lowThreshold = 20;
    private int mediumThreshold = 70;
    private int highThreshold = 150;
    private boolean usedMediumThreshold = true;
    private boolean usedHighThreshold = true;
    private Color noMotionColor = new Color(0x7C7C7C);
    private Color lowMotionColor = new Color(0x267C26);
    private Color mediumMotionColor = new Color(0x267CFF);
    private Color highMotionColor = new Color(0x26FFFF);

    public MotionDetectionOptions() {
    }

    public MotionDetectionOptions(int frameGap, int slideMinFrame, boolean highVideoQuality, int lowThreshold,
                                  int mediumThreshold, int highThreshold, boolean usedMediumThreshold,
                                  boolean usedHighThreshold) {
        this.frameGap = frameGap;
        this.slideMinFrame = slideMinFrame;
        this.lowThreshold = lowThreshold;
        this.mediumThreshold = mediumThreshold;
        this.highThreshold = highThreshold;
        this.highVideoQuality = highVideoQuality;
        this.usedMediumThreshold = usedMediumThreshold;
        this.usedHighThreshold = usedHighThreshold;
    }

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
    }

    public void setLowMotionColor(Color lowMotionColor) {
        this.lowMotionColor = lowMotionColor;
    }

    public void setMediumMotionColor(Color mediumMotionColor) {
        this.mediumMotionColor = mediumMotionColor;
    }

    public void setHighMotionColor(Color highMotionColor) {
        this.highMotionColor = highMotionColor;
    }
}
