package com.belsofto.vet.ui.action;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.ui.dialog.file.FileChooserUtils;
import com.belsofto.vet.media.VideoLoader;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

final class OpenAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(OpenAction.class);

    OpenAction() {
        try {
            putValue(Action.LARGE_ICON_KEY, new ImageIcon(ImageIO.read(
                    this.getClass().getResourceAsStream("/com/belsofto/vet/resources/images/open-file.png"))));
        } catch (IOException ignored) {
        }
    }

    @Override public void actionPerformed(ActionEvent event) {
        LOGGER.debug("open file choose dialog");
        JFileChooser fileChooser = FileChooserUtils.getVideoFileChooser();
        int option = showOpenDialog(fileChooser);
        if (option == JFileChooser.APPROVE_OPTION) {
            setVideoFileAndPlay(fileChooser.getSelectedFile());
        }
    }

    private int showOpenDialog(JFileChooser fileChooser) {
        return fileChooser.showOpenDialog(ApplicationContext.getInstance().getMainFrame());
    }

    private void setVideoFileAndPlay(File videoFile) {
        VideoLoader.load(videoFile);
    }
}
