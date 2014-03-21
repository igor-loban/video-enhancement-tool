package by.bsu.fpmi.vet.launcher;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.ReportGenerator;
import by.bsu.fpmi.vet.ui.frame.MainFrame;
import by.bsu.fpmi.vet.video.MotionDetector;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.JOptionPane;
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
        try {
            LOGGER.debug("try to load VLC library");
            String vlcHomePath = System.getenv("VLC_HOME");
            vlcHomePath = vlcHomePath.replace("\\", "\\\\");
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcHomePath);
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
            LOGGER.debug("VLC library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            LOGGER.debug("VLC library loading failed");
            String vlcLibName = RuntimeUtil.getLibVlcLibraryName();
            JOptionPane.showMessageDialog(null,
                    "VLC libraries not found. Please, setup 'VLC_HOME' environment variable which specified path to "
                            + vlcLibName + " and " + vlcLibName + "core libraries.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                JFrame frame = new JFrame("Video");
//                EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
//
//                frame.setContentPane(mediaPlayerComponent);
//
//                frame.setLocation(100, 100);
//                frame.setSize(1050, 600);
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                frame.setVisible(true);
//
//                EmbeddedMediaPlayer mediaPlayer = mediaPlayerComponent.getMediaPlayer();
//                mediaPlayer.playMedia("d:\\test2.avi");


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
