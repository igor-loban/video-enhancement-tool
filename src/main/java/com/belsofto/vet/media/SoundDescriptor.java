package com.belsofto.vet.media;

public final class SoundDescriptor {
    private final int time;
    private final boolean noisePresent;

    public SoundDescriptor(int time, boolean noisePresent) {
        this.time = time;
        this.noisePresent = noisePresent;
    }

    public int getTime() {
        return time;
    }

    public boolean isNoisePresent() {
        return noisePresent;
    }
}
