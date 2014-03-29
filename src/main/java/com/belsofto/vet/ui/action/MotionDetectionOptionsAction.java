package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.MotionDetectionOptionsDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class MotionDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(MotionDetectionOptionsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("motion detection options dialog");
        UserLogger.log("motion detection option dialog opened");
        MotionDetectionOptionsDialog dialog = new MotionDetectionOptionsDialog();
        dialog.setVisible(true);
    }
}
