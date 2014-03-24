package com.belsofto.vet.ui.action;

import com.belsofto.vet.ui.dialog.ReportOptionsDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class ReportOptionsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ReportOptionsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        ReportOptionsDialog dialog = new ReportOptionsDialog();
        dialog.setVisible(true);
        LOGGER.debug("report options dialog opened");
    }
}
