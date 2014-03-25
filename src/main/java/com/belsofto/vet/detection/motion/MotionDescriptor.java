package com.belsofto.vet.detection.motion;

public final class MotionDescriptor {
    private final int time;
    private final MotionThreshold motionThreshold;

    public MotionDescriptor(long timeNanos, MotionThreshold motionThreshold) {
        this.time = (int) (timeNanos / 1_000);
        this.motionThreshold = motionThreshold;
    }

    public int getTime() {
        return time;
    }

    public MotionThreshold getMotionThreshold() {
        return motionThreshold;
    }
}
