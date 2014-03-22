package com.belsofto.vet.launcher;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.report.ReportGenerator;
import com.belsofto.vet.ui.frame.MainFrame;
import com.belsofto.vet.media.MotionDetector;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;
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
        if (!loadVLCNativeLibrary()) {
            return;
        }

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

    private static boolean loadVLCNativeLibrary() {
        try {
            LOGGER.debug("try to load VLC library");
            String vlcHomePath = System.getenv("VLC_HOME");
            if (vlcHomePath != null) {
                vlcHomePath = vlcHomePath.replace("\\", "\\\\");
                NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcHomePath);
            }
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC");
            String userDir = System.getProperty("user.dir");
            if (userDir != null) {
                userDir += "\\VLC";
                userDir = userDir.replace("\\", "\\\\");
                NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), userDir);
            }
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
            LOGGER.debug("VLC library loaded successfully");
            return true;
        } catch (UnsatisfiedLinkError e) {
            LOGGER.debug("VLC library loading failed");
            String vlcLibName = RuntimeUtil.getLibVlcLibraryName();
            JOptionPane.showMessageDialog(null,
                    "VLC libraries not found. Please, setup 'VLC_HOME' environment variable which specified path to "
                            + vlcLibName + " and " + vlcLibName + "core libraries.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
