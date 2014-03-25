package com.belsofto.vet.launcher;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.detection.sound.SoundDetector;
import com.belsofto.vet.media.VideoRecorder;
import com.belsofto.vet.report.ReportGenerator;
import com.belsofto.vet.ui.frame.MainFrame;
import com.belsofto.vet.detection.motion.MotionDetector;
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
                ApplicationContext context = ApplicationContext.getInstance();
                context.setReportGenerator(new ReportGenerator());
                context.setMotionDetector(new MotionDetector());
                context.setSoundDetector(new SoundDetector());
                context.setVideoRecorder(new VideoRecorder());
                context.setUserDirectory(System.getProperty("user.dir"));
                context.loadApplicationSettings();

                MainFrame mainFrame = new MainFrame();
                context.setMainFrame(mainFrame);

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

            String vlcLibName = RuntimeUtil.getLibVlcLibraryName();

            String vlcHomePath = System.getenv("VLC_HOME");
            if (vlcHomePath != null) {
                vlcHomePath = vlcHomePath.replace("\\", "\\\\");
                NativeLibrary.addSearchPath(vlcLibName, vlcHomePath);
            }

            if (RuntimeUtil.isWindows()) {
                if (is32BitOsArch()) {
                    NativeLibrary.addSearchPath(vlcLibName, "C:\\Program Files (x86)\\VideoLAN\\VLC");
                } else {
                    NativeLibrary.addSearchPath(vlcLibName, "C:\\Program Files\\VideoLAN\\VLC");
                }
            }

            String userDir = System.getProperty("user.dir");
            String separatorChar = System.getProperty("file.separator");
            if (userDir != null && separatorChar != null) {
                String localVlcPath = userDir + separatorChar + "lib" + separatorChar + "VLC";
                if (RuntimeUtil.isWindows()) {
                    localVlcPath += separatorChar + (is32BitOsArch() ? "win32" : "win64");
                } else if (RuntimeUtil.isMac()) {
                    localVlcPath += separatorChar + "macosx";
                } else {
                    localVlcPath += separatorChar + "unix";
                }

                if (separatorChar.equals("\\")) {
                    localVlcPath = localVlcPath.replace("\\", "\\\\");
                }
                NativeLibrary.addSearchPath(vlcLibName, localVlcPath);
            }

            Native.loadLibrary(vlcLibName, LibVlc.class);

            LOGGER.debug("VLC library loaded successfully");

            return true;
        } catch (UnsatisfiedLinkError e) {
            LOGGER.debug("VLC library loading failed", e);
            JOptionPane.showMessageDialog(null,
                    "VLC libraries not found. Please, setup 'VLC_HOME' environment variable which specified path to "
                            + "VLC directory with core libraries.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean is32BitOsArch() {
        String osArch = System.getProperty("os.arch");
        return osArch != null && !osArch.contains("64");
    }
}
