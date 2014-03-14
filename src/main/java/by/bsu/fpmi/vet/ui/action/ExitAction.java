package by.bsu.fpmi.vet.ui.action;

import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class ExitAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ExitAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("application would be closed");
        System.exit(0);
    }
}
