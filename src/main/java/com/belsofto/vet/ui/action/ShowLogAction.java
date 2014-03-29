package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.LogDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class ShowLogAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ShowLogAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("show log dialog");
        UserLogger.log("log dialog opened");
        LogDialog dialog = new LogDialog();
        dialog.setVisible(true);
    }
}
