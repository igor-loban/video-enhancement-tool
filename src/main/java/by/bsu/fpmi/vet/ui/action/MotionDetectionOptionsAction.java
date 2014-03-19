package by.bsu.fpmi.vet.ui.action;

import by.bsu.fpmi.vet.ui.dialog.MotionDetectionOptionsDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class MotionDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(MotionDetectionOptionsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("motion detection options dialog");
        MotionDetectionOptionsDialog dialog = new MotionDetectionOptionsDialog();
        dialog.setVisible(true);
    }
}
