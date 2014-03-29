package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.SoundDetectionOptionsDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class SoundDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(SoundDetectionOptionsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("sound detection options dialog");
        UserLogger.log("sound detection option dialog opened");
        SoundDetectionOptionsDialog dialog = new SoundDetectionOptionsDialog();
        dialog.setVisible(true);
    }
}
