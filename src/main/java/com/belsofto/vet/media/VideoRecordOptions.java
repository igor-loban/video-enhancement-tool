package com.belsofto.vet.media;

public class VideoRecordOptions {
    private long leftBoundNanos = 1;
    private long rightBoundNanos = 1_000_000;
    private boolean active;

    public long getLeftBoundNanos() {
        return leftBoundNanos;
    }

    public void setLeftBoundNanos(long leftBoundNanos) {
        this.leftBoundNanos = leftBoundNanos;
    }

    public long getRightBoundNanos() {
        return rightBoundNanos;
    }

    public void setRightBoundNanos(long rightBoundNanos) {
        this.rightBoundNanos = rightBoundNanos;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
