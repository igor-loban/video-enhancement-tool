package com.belsofto.vet.detection.sound;

import java.awt.Color;

public class SoundDetectionOptions {
    private int frameGap = 5;
    private int soundLowerBound = 500;
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

    public int getSoundLowerBound() {
        return soundLowerBound;
    }

    public void setSoundLowerBound(int soundLowerBound) {
        this.soundLowerBound = soundLowerBound;
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
}
