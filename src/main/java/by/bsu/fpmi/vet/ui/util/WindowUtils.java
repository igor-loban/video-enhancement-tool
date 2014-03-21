package by.bsu.fpmi.vet.ui.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import static by.bsu.fpmi.vet.util.error.ErrorUtils.throwInstantiationError;

public final class WindowUtils {
    public static void setLocationToCenter(Window window) {
        Dimension size = window.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ((double) screenSize.height / size.height < 1.3) {
            size.height = (int) (screenSize.height * 0.7);
            window.setSize(size);
        }
        window.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height - 20) / 2);
    }

    private WindowUtils() {
        throwInstantiationError(this.getClass());
    }
}
