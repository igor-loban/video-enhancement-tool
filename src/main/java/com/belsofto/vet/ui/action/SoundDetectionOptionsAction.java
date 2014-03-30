package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.SoundDetectionOptionsDialog;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class SoundDetectionOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(SoundDetectionOptionsAction.class);

    SoundDetectionOptionsAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(this.getClass()
                    .getResourceAsStream("/com/belsofto/vet/resources/images/sound-detection-options.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("sound detection options dialog");
        UserLogger.log("sound detection option dialog opened");
        SoundDetectionOptionsDialog dialog = new SoundDetectionOptionsDialog();
        dialog.setVisible(true);
    }
}
