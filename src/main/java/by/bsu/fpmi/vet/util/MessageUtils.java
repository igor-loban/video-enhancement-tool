package by.bsu.fpmi.vet.util;

import java.util.Locale;
import java.util.ResourceBundle;

import static by.bsu.fpmi.vet.util.error.ErrorUtils.throwInstantiationError;

public final class MessageUtils {
    private static final String BASE_NAME = "by.bsu.fpmi.vet.resources.i18n.messages";
    private static final String MISS_KEY_TEMPLATE = "??? %s ???";

    private MessageUtils() {
        throwInstantiationError(this.getClass());
    }

    public static boolean contains(String key) {
        return contains(key, Locale.getDefault());
    }

    public static boolean contains(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
        return bundle.containsKey(key);
    }

    public static String format(String template, Object... params) {
        return format(template, Locale.getDefault(), params);
    }

    public static String format(String template, Locale locale, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
        return bundle.containsKey(template) ? String.format(locale, bundle.getString(template), params)
                : missKeyFormat(template);
    }

    public static String getMessage(String key) {
        return getMessage(key, Locale.getDefault());
    }

    public static String getMessage(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
        return bundle.containsKey(key) ? bundle.getString(key) : missKeyFormat(key);
    }

    private static String missKeyFormat(String key) {
        return String.format(MISS_KEY_TEMPLATE, key);
    }
}
