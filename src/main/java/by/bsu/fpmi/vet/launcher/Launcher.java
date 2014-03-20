package by.bsu.fpmi.vet.launcher;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.ReportGenerator;
import by.bsu.fpmi.vet.ui.frame.MainFrame;
import by.bsu.fpmi.vet.video.MotionDetector;
import org.slf4j.Logger;

import javax.swing.SwingUtilities;

import static by.bsu.fpmi.vet.util.error.ErrorUtils.throwInstantiationError;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Application entry point.
 */
public final class Launcher {
    private static final Logger LOGGER = getLogger(Launcher.class);

    private Launcher() {
        throwInstantiationError(this.getClass());
    }

    public static void main(String[] args) {
//        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame();
                ApplicationContext context = ApplicationContext.getInstance();
                context.setMainFrame(mainFrame);
                context.setReportGenerator(new ReportGenerator());
                context.setMotionDetector(new MotionDetector());
                LOGGER.debug("ApplicationContext initialized");
                mainFrame.init();
                LOGGER.debug("MainFrame initialized");
                mainFrame.setVisible(true);
                LOGGER.debug("MainFrame viewed");
            }
        });
    }
}
