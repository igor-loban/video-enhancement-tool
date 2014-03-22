package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.util.error.ErrorUtils;

import javax.swing.JOptionPane;
import java.awt.Component;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class DialogUtils {
    private static final String MESSAGE_PREFIX = "ui.dialog.";
    private static final String ERROR_PREFIX = "error.";

    private static final String ERROR_TITLE = getMessage(MESSAGE_PREFIX + ERROR_PREFIX + "title");

    public static void showErrorMessage(String key) {
        JOptionPane
                .showMessageDialog(getParentComponent(), getErrorMessage(key), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    private static Component getParentComponent() {
        return ApplicationContext.getInstance().getMainFrame();
    }

    private static String getErrorMessage(String key) {
        return getMessage(MESSAGE_PREFIX + ERROR_PREFIX + key);
    }

    private DialogUtils() {
        ErrorUtils.throwInstantiationError(this.getClass());
    }
}
