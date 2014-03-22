package com.belsofto.vet.ui.action;

import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class SoundDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(SoundDetectionOptionsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("sound detection options dialog");
//        MotionDetectionOptionsDialog dialog = new MotionDetectionOptionsDialog();
//        dialog.setVisible(true);
    }
}
