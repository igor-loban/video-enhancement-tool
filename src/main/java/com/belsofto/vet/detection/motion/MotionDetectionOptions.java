package com.belsofto.vet.detection.motion;

import java.awt.Color;

public class MotionDetectionOptions {
    private int frameGap = 5;
    private int slideMinFrame = 30;
    private boolean highVideoQuality = false;
    private int tinyThreshold = 30;
    private int lowThreshold = 100;
    private int mediumThreshold = 170;
    private int highThreshold = 210;
    private int veryHighThreshold = 250;
    private boolean usedLowThreshold = true;
    private boolean usedMediumThreshold = true;
    private boolean usedHighThreshold = false;
    private boolean usedVeryHighThreshold = false;
    private Color noMotionColor = MotionThreshold.NO.color();
    private Color tinyMotionColor = MotionThreshold.TINY.color();
    private Color lowMotionColor = MotionThreshold.LOW.color();
    private Color mediumMotionColor = MotionThreshold.MEDIUM.color();
    private Color highMotionColor = MotionThreshold.HIGH.color();
    private Color veryHighMotionColor = MotionThreshold.VERY_HIGH.color();

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

    public boolean isUsedLowThreshold() {
        return usedLowThreshold;
    }

    public void setUsedLowThreshold(boolean usedLowThreshold) {
        this.usedLowThreshold = usedLowThreshold;
    }

    public boolean isUsedVeryHighThreshold() {
        return usedVeryHighThreshold;
    }

    public void setUsedVeryHighThreshold(boolean usedVeryHighThreshold) {
        this.usedVeryHighThreshold = usedVeryHighThreshold;
    }

    public int getTinyThreshold() {
        return tinyThreshold;
    }

    public void setTinyThreshold(int tinyThreshold) {
        this.tinyThreshold = tinyThreshold;
    }

    public int getVeryHighThreshold() {
        return veryHighThreshold;
    }

    public void setVeryHighThreshold(int veryHighThreshold) {
        this.veryHighThreshold = veryHighThreshold;
    }

    public Color getTinyMotionColor() {
        return tinyMotionColor;
    }

    public void setTinyMotionColor(Color tinyMotionColor) {
        this.tinyMotionColor = tinyMotionColor;
    }

    public Color getVeryHighMotionColor() {
        return veryHighMotionColor;
    }

    public void setVeryHighMotionColor(Color veryHighMotionColor) {
        this.veryHighMotionColor = veryHighMotionColor;
    }

    @Override public String toString() {
        return "{frameGap=" + frameGap +
                ", slideMinFrame=" + slideMinFrame +
                ", highVideoQuality=" + highVideoQuality +
                ", tinyThreshold=" + tinyThreshold +
                ", lowThreshold=" + lowThreshold +
                ", mediumThreshold=" + mediumThreshold +
                ", highThreshold=" + highThreshold +
                ", veryHighThreshold=" + veryHighThreshold +
                ", usedMediumThreshold=" + usedMediumThreshold +
                ", usedHighThreshold=" + usedHighThreshold +
                ", noMotionColor=" + noMotionColor +
                ", tinyMotionColor=" + tinyMotionColor +
                ", lowMotionColor=" + lowMotionColor +
                ", mediumMotionColor=" + mediumMotionColor +
                ", highMotionColor=" + highMotionColor +
                ", veryHighMotionColor=" + veryHighMotionColor + "}";
    }
}
