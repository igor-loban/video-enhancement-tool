package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.ui.dialog.DialogUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class RunBothAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(RunBothAction.class);

    RunBothAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(this.getClass()
                    .getResourceAsStream("/com/belsofto/vet/resources/images/motion-sound-detections.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("run motion and sound detection");
        ApplicationContext context = ApplicationContext.getInstance();
        if (context.getVideoDetails() == null) {
            DialogUtils.showErrorMessage("noVideoLoaded");
            return;
        }
        if (context.getMotionDetector().isInProgress()) {
            DialogUtils.showErrorMessage("videoDetectionInProgress");
            return;
        }
        if (context.getSoundDetector().isInProgress()) {
            DialogUtils.showErrorMessage("soundDetectionInProgress");
            return;
        }
        context.analyzeVideoAndSound();
    }
}
