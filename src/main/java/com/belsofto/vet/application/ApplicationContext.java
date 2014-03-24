package com.belsofto.vet.application;

import com.belsofto.vet.detection.motion.MotionDetectionOptions;
import com.belsofto.vet.detection.motion.MotionDetector;
import com.belsofto.vet.detection.sound.SoundDetector;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.report.ReportGenerator;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.dialog.FrameGrabsDialog;
import com.belsofto.vet.ui.frame.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public final class ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private static final String SETTINGS_RELATIVE_PATH = "/settings.properties";

    private Status status = Status.DEFAULT;

    private String userDirectory;

    private MainFrame mainFrame;
    private FrameGrabsDialog frameGrabsDialog;

    private ReportGenerator reportGenerator;
    private MotionDetector motionDetector;
    private SoundDetector soundDetector;

    private VideoDetails videoDetails;

    private ApplicationContext() {
    }

    public void blockUI() {
        mainFrame.getVideoPlayer().pause();
        // TODO: disable buttons
    }

    public void setStatus(Status newStatus, Object... params) {
        this.status = newStatus;
        mainFrame.getStatusPanel().setStatus(newStatus, params);
    }

    public void updateAfterLoad(VideoDetails videoDetails) {
        setVideoDetails(videoDetails);
        mainFrame.getVideoPlayer().loadVideo();
        mainFrame.getVideoPlayerPanel().init(videoDetails);
        mainFrame.getVideoDetailsPanel().update(videoDetails);
        setStatus(Status.LOADING_COMPLETE);
    }

    public void updateTimeline(long newTime) {
        mainFrame.getVideoPlayerPanel().updateTimeline(newTime);
    }

    public void updateAfterMotionDetection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ApplicationContext.getInstance().setStatus(Status.ANALYZE, 100);
                mainFrame.getVideoPlayerPanel().initColoredSlider();
            }
        });
    }

    public void updateAfterSoundDetection() {
        updateAfterMotionDetection();
    }

    public void moveToSnapshot(Snapshot snapshot) {
        mainFrame.moveToSnapshot(snapshot);
        if (frameGrabsDialog != null) {
            frameGrabsDialog.close();
        }
    }

    public void playAllMovement() {
        mainFrame.getVideoPlayerPanel().playAllMovement();
    }

    public void updateApplicationProperties() {
        try (FileOutputStream fos = new FileOutputStream(userDirectory + SETTINGS_RELATIVE_PATH)) {
            Properties properties = new Properties();

            MotionDetectionOptions options = motionDetector.getOptions();

            properties.put("mdo.frameGap", String.valueOf(options.getFrameGap()));
            properties.put("mdo.slideMinFrame", String.valueOf(options.getSlideMinFrame()));
            properties.put("mdo.highVideoQuality", String.valueOf(options.isHighVideoQuality()));

            properties.put("mdo.lowThreshold", String.valueOf(options.getLowThreshold()));
            properties.put("mdo.mediumThreshold", String.valueOf(options.getMediumThreshold()));
            properties.put("mdo.highThreshold", String.valueOf(options.getHighThreshold()));

            properties.put("mdo.usedMediumThreshold", String.valueOf(options.isUsedMediumThreshold()));
            properties.put("mdo.usedHighThreshold", String.valueOf(options.isUsedHighThreshold()));

            properties.put("mdo.noMotionColor", Integer.toHexString(options.getNoMotionColor().getRGB()));
            properties.put("mdo.lowMotionColor", Integer.toHexString(options.getLowMotionColor().getRGB()));
            properties.put("mdo.mediumMotionColor", Integer.toHexString(options.getMediumMotionColor().getRGB()));
            properties.put("mdo.highMotionColor", Integer.toHexString(options.getHighMotionColor().getRGB()));

            properties.store(fos, "Motion Detection Options");
            fos.flush();
            LOGGER.debug("application properties saved successfully");
        } catch (IOException e) {
            LOGGER.debug("application properties saving failed", e);
        }
    }

    public void loadApplicationSettings() {
        File settingsFile = new File(userDirectory + SETTINGS_RELATIVE_PATH);
        if (!settingsFile.isFile()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(settingsFile)) {
            Properties properties = new Properties();
            properties.load(fis);

            MotionDetectionOptions options = motionDetector.getOptions();

            Integer intValue = getIntegerValue(properties, "mdo.frameGap");
            if (intValue != null) {
                options.setFrameGap(intValue);
            }
            intValue = getIntegerValue(properties, "mdo.slideMinFrame");
            if (intValue != null) {
                options.setSlideMinFrame(intValue);
            }
            Boolean boolValue = getBooleanValue(properties, "mdo.highVideoQuality");
            if (boolValue != null) {
                options.setHighVideoQuality(boolValue);
            }

            intValue = getIntegerValue(properties, "mdo.lowThreshold");
            if (intValue != null) {
                options.setLowThreshold(intValue);
            }
            intValue = getIntegerValue(properties, "mdo.mediumThreshold");
            if (intValue != null) {
                options.setMediumThreshold(intValue);
            }
            intValue = getIntegerValue(properties, "mdo.highThreshold");
            if (intValue != null) {
                options.setHighThreshold(intValue);
            }

            boolValue = getBooleanValue(properties, "mdo.usedMediumThreshold");
            if (boolValue != null) {
                options.setUsedMediumThreshold(boolValue);
            }
            boolValue = getBooleanValue(properties, "mdo.usedHighThreshold");
            if (boolValue != null) {
                options.setUsedHighThreshold(boolValue);
            }

            Color color = getColorValue(properties, "mdo.noMotionColor");
            if (color != null) {
                options.setNoMotionColor(color);
            }
            color = getColorValue(properties, "mdo.lowMotionColor");
            if (color != null) {
                options.setLowMotionColor(color);
            }
            color = getColorValue(properties, "mdo.mediumMotionColor");
            if (color != null) {
                options.setMediumMotionColor(color);
            }
            color = getColorValue(properties, "mdo.highMotionColor");
            if (color != null) {
                options.setHighMotionColor(color);
            }

            LOGGER.debug("application properties loaded successfully");
        } catch (IOException e) {
            LOGGER.debug("application properties loading failed", e);
        }
    }

    private Color getColorValue(Properties properties, String key) {
        Integer colorAsInt = getIntegerValue(properties, key, 16);
        if (colorAsInt == null) {
            return null;
        }
        return new Color(colorAsInt, true);
    }

    private Boolean getBooleanValue(Properties properties, String key) {
        if (!properties.containsKey(key)) {
            return null;
        }
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    private Integer getIntegerValue(Properties properties, String key) {
        return getIntegerValue(properties, key, 10);
    }

    private Integer getIntegerValue(Properties properties, String key, int radix) {
        if (!properties.containsKey(key)) {
            return null;
        }
        String valueAsString = properties.getProperty(key);
        try {
            return Integer.parseInt(valueAsString, radix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    public FrameGrabsDialog getFrameGrabsDialog() {
        return frameGrabsDialog;
    }

    public void setFrameGrabsDialog(FrameGrabsDialog frameGrabsDialog) {
        this.frameGrabsDialog = frameGrabsDialog;
    }

    public MotionDetector getMotionDetector() {
        return motionDetector;
    }

    public void setMotionDetector(MotionDetector motionDetector) {
        this.motionDetector = motionDetector;
    }

    public VideoDetails getVideoDetails() {
        return videoDetails;
    }

    public void setVideoDetails(VideoDetails videoDetails) {
        this.videoDetails = videoDetails;
    }

    public SoundDetector getSoundDetector() {
        return soundDetector;
    }

    public void setSoundDetector(SoundDetector soundDetector) {
        this.soundDetector = soundDetector;
    }

    public Status getStatus() {
        return status;
    }

    public void setUserDirectory(String userDirectory) {
        this.userDirectory = userDirectory;
    }
}
