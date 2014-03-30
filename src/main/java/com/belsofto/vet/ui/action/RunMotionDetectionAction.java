package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.detection.motion.MotionDetector;
import com.belsofto.vet.ui.dialog.DialogUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class RunMotionDetectionAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(RunMotionDetectionAction.class);

    RunMotionDetectionAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(this.getClass()
                    .getResourceAsStream("/com/belsofto/vet/resources/images/run-motion-detection.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("run motion detection");
        ApplicationContext context = ApplicationContext.getInstance();
        if (context.getVideoDetails() == null) {
            DialogUtils.showErrorMessage("noVideoLoaded");
            return;
        }
        MotionDetector motionDetector = context.getMotionDetector();
        if (motionDetector.isInProgress()) {
            DialogUtils.showErrorMessage("motionDetectionInProgress");
            return;
        }
        if (context.getSoundDetector().isInProgress()) {
            DialogUtils.showErrorMessage("soundDetectionInProgress");
            return;
        }
        motionDetector.analyzeVideo();
    }
}
