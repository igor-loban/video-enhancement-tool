package com.belsofto.vet.media;

public final class MotionDescriptor {
    private final int time;
    private final boolean videoFlag;

    public MotionDescriptor(int time, boolean videoFlag) {
        this.time = time;
        this.videoFlag = videoFlag;
    }

    public int getTime() {
        return time;
    }

    public boolean isVideoFlag() {
        return videoFlag;
    }
}
