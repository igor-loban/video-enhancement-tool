package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.MotionDetectionOptionsDialog;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class MotionDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(MotionDetectionOptionsAction.class);


    MotionDetectionOptionsAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(this.getClass()
                    .getResourceAsStream("/com/belsofto/vet/resources/images/motion-detection-options.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("motion detection options dialog");
        UserLogger.log("motion detection option dialog opened");
        MotionDetectionOptionsDialog dialog = new MotionDetectionOptionsDialog();
        dialog.setVisible(true);
    }
}
