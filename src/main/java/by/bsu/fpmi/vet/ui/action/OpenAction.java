package by.bsu.fpmi.vet.ui.action;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.ui.dialog.file.FileChooserUtils;
import by.bsu.fpmi.vet.video.VideoLoader;
import org.slf4j.Logger;

import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

final class OpenAction extends AdvancedAbstractAction {
    private static final Logger LOGGER = getLogger(OpenAction.class);

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
