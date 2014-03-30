package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.ReportOptionsDialog;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class ReportOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ReportOptionsAction.class);

    ReportOptionsAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(
                    this.getClass().getResourceAsStream("/com/belsofto/vet/resources/images/report-options.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        UserLogger.log("report options dialog opened");
        ReportOptionsDialog dialog = new ReportOptionsDialog();
        dialog.setVisible(true);
        LOGGER.debug("report options dialog opened");
    }
}
