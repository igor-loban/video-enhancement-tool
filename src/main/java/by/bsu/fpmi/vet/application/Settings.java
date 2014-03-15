package by.bsu.fpmi.vet.application;

import java.util.ResourceBundle;

import static by.bsu.fpmi.vet.util.error.ErrorUtils.throwInstantiationError;

public final class Settings {
    private static final String CONTEXT_NAME = "by.bsu.fpmi.vet.resources.config.settings";
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
