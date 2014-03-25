package com.belsofto.vet.ui.util;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;

public final class WindowUtils {
    public static void setLocationToCenter(Window window) {
        Dimension size = window.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ((double) screenSize.height / size.height < 1.15) {
            size.height = (int) (screenSize.height * 0.85);
            window.setSize(size);
        }
        window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height - 20) / 2);
    }

    public static void maximizeByHeight(JFrame frame) {
        frame.setExtendedState(JFrame.MAXIMIZED_VERT);
    }

    private WindowUtils() {
        throwInstantiationError(this.getClass());
    }
}
