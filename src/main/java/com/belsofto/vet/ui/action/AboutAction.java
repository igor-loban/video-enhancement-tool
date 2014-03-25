package com.belsofto.vet.ui.action;

import com.belsofto.vet.ui.dialog.AboutDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class AboutAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(AboutAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        AboutDialog dialog = new AboutDialog();
        dialog.setVisible(true);
        LOGGER.debug("about dialog opened");
    }
}
