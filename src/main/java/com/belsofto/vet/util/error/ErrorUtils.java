package com.belsofto.vet.util.error;

import com.belsofto.vet.util.MessageUtils;

public final class ErrorUtils {
    private ErrorUtils() {
        throwInstantiationError(this.getClass());
    }

    public static void throwInstantiationError(Class<?> clazz) {
        throw new InstantiationError(MessageUtils.format("error.instantiation", clazz.getName()));
    }
}
