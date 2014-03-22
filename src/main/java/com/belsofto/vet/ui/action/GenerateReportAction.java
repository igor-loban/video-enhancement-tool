package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class GenerateReportAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(GenerateReportAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        // TODO: implement
        ApplicationContext.getInstance().getReportGenerator().generate();
    }
}
