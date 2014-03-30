package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class ShowToolsAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ShowToolsAction.class);

    ShowToolsAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(
                    this.getClass().getResourceAsStream("/com/belsofto/vet/resources/images/toggle-tools.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent e) {
        ApplicationContext context = ApplicationContext.getInstance();
        boolean visible = context.getMainFrame().isToolsVisible();
        context.setToolsVisible(!visible);
        LOGGER.debug("tools visible toggled");
    }
}
