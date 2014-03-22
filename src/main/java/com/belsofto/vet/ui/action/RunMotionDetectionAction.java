package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class RunMotionDetectionAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(RunMotionDetectionAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("run motion detection");
        ApplicationContext.getInstance().getMotionDetector().analyzeVideo();
    }
}
