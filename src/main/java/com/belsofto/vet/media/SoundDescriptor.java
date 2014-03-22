package com.belsofto.vet.media;

public final class SoundDescriptor {
    private final int time;
    private final boolean soundPresent;

    public SoundDescriptor(int time, boolean soundPresent) {
        this.time = time;
        this.soundPresent = soundPresent;
    }

    public int getTime() {
        return time;
    }

    public boolean isSoundPresent() {
        return soundPresent;
    }
}
