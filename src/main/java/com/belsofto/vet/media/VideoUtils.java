package com.belsofto.vet.media;

import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;

public final class VideoUtils {
    public static long getFrameMillis(int frameNumber, double frameRate) {
        return (long) (frameNumber / frameRate * 1000.0);
    }

    private VideoUtils() {
        throwInstantiationError(this.getClass());
    }
}
