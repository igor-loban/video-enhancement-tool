package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.ui.dialog.DialogUtils;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class RunSoundDetectionAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(RunSoundDetectionAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("run sound detection");
        ApplicationContext context = ApplicationContext.getInstance();
        if (context.getVideoDetails() == null) {
            DialogUtils.showErrorMessage("noVideoLoaded");
            return;
        }
        context.getSoundDetector().analyzeVideo();
    }
}
