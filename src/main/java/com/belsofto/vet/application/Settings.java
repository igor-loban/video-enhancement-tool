package com.belsofto.vet.application;

import java.util.ResourceBundle;

import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;

public final class Settings {
    private static final String CONTEXT_NAME = "com.belsofto.vet.resources.config.settings";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(CONTEXT_NAME);
    public static String getProperty(String key) {
        if (RESOURCE_BUNDLE.containsKey(key)) {
            return RESOURCE_BUNDLE.getString(key);
        }
        throw new IllegalArgumentException("Illegal key value for settings '" + key + "'");
    }

    private Settings() {
        throwInstantiationError(this.getClass());
    }
}
