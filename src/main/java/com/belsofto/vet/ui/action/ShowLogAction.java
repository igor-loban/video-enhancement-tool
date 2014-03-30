package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.LogDialog;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class ShowLogAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ShowLogAction.class);

    ShowLogAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(
                    ImageIO.read(this.getClass().getResourceAsStream("/com/belsofto/vet/resources/images/log.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("show log dialog");
        UserLogger.log("log dialog opened");
        LogDialog dialog = new LogDialog();
        dialog.setVisible(true);
    }
}
