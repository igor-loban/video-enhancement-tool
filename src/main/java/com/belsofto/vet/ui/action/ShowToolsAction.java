package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class ShowToolsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ShowToolsAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        ApplicationContext context = ApplicationContext.getInstance();
        boolean visible = context.getMainFrame().isToolsVisible();
        context.setToolsVisible(!visible);
        LOGGER.debug("tools visible toggled");
    }
}
