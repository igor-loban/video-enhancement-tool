package com.belsofto.vet.detection.sound;

public final class SoundDescriptor {
    private final int time;
    private final SoundThreshold soundThreshold;

    public SoundDescriptor(int time, SoundThreshold soundThreshold) {
        this.time = time;
        this.soundThreshold = soundThreshold;
    }

    public int getTime() {
        return time;
    }

    public SoundThreshold getSoundThreshold() {
        return soundThreshold;
    }
}
