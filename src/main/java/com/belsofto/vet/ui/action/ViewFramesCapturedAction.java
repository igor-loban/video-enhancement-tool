package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.dialog.FrameGrabsDialog;
import org.slf4j.Logger;

import java.awt.event.ActionEvent;

import static org.slf4j.LoggerFactory.getLogger;

final class ViewFramesCapturedAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(ViewFramesCapturedAction.class);

    @Override public void actionPerformed(ActionEvent e) {
        LOGGER.debug("view frame captured dialog");
        UserLogger.log("frame grabs dialog opened");
        FrameGrabsDialog dialog = new FrameGrabsDialog();
        ApplicationContext context = ApplicationContext.getInstance();
        context.setFrameGrabsDialog(dialog);
        context.getMainFrame().getVideoPlayer().pause();
        dialog.setVisible(true);
    }
}
