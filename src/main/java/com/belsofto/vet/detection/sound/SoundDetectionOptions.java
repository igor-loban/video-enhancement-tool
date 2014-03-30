package com.belsofto.vet.detection.sound;

import java.awt.Color;

public class SoundDetectionOptions {
    private int frameGap = 5;
    private int noiseMinLength = 500;
    private int soundMinLength = 2000;
    private double minNoiseBound = 0.05;
    private double maxNoiseBound = 0.95;
    private Color noiseColor = SoundThreshold.NOISE.color();
    private Color soundColor = SoundThreshold.SOUND.color();

    public int getFrameGap() {
        return frameGap;
    }

    public void setFrameGap(int frameGap) {
        this.frameGap = frameGap;
    }

    public int getSoundMinLength() {
        return soundMinLength;
    }

    public void setSoundMinLength(int soundMinLength) {
        this.soundMinLength = soundMinLength;
    }

    public double getMinNoiseBound() {
        return minNoiseBound;
    }

    public void setMinNoiseBound(double minNoiseBound) {
        this.minNoiseBound = minNoiseBound;
    }

    public double getMaxNoiseBound() {
        return maxNoiseBound;
    }

    public void setMaxNoiseBound(double maxNoiseBound) {
        this.maxNoiseBound = maxNoiseBound;
    }

    public Color getNoiseColor() {
        return noiseColor;
    }

    public void setNoiseColor(Color noiseColor) {
        this.noiseColor = noiseColor;
        SoundThreshold.NOISE.color(noiseColor);
    }

    public Color getSoundColor() {
        return soundColor;
    }

    public void setSoundColor(Color soundColor) {
        this.soundColor = soundColor;
        SoundThreshold.SOUND.color(soundColor);
    }

    public int getNoiseMinLength() {
        return noiseMinLength;
    }

    public void setNoiseMinLength(int noiseMinLength) {
        this.noiseMinLength = noiseMinLength;
    }

    @Override public String toString() {
        return "{frameGap=" + frameGap +
                ", noiseMinLength=" + noiseMinLength +
                ", soundMinLength=" + soundMinLength +
                ", minNoiseBound=" + minNoiseBound +
                ", maxNoiseBound=" + maxNoiseBound +
                ", noiseColor=" + noiseColor +
                ", soundColor=" + soundColor + "}";
    }
}
