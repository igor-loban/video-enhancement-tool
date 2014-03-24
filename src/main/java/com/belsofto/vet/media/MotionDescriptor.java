package com.belsofto.vet.media;

public final class MotionDescriptor {
    private final int time;
    private final MotionThreshold motionThreshold;

    public MotionDescriptor(int time, MotionThreshold motionThreshold) {
        this.time = time;
        this.motionThreshold = motionThreshold;
    }

    public int getTime() {
        return time;
    }

    public MotionThreshold getMotionThreshold() {
        return motionThreshold;
    }
}
