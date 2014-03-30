package com.belsofto.vet.application;

import com.belsofto.vet.detection.motion.MotionDetectionOptions;
import com.belsofto.vet.detection.motion.MotionDetector;
import com.belsofto.vet.detection.sound.SoundDetectionOptions;
import com.belsofto.vet.detection.sound.SoundDetector;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.media.VideoRecorder;
import com.belsofto.vet.report.ReportGenerator;
import com.belsofto.vet.report.ReportOptions;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.dialog.FrameGrabsDialog;
import com.belsofto.vet.ui.frame.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import static com.belsofto.vet.util.MessageUtils.format;

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
    private VideoRecorder videoRecorder;

    private ApplicationContext() {
    }

    public boolean saveSnapshot(Snapshot snapshot) {
        try {
            File snapshotDirectory = new File(getSnapshotDirectory());
            snapshotDirectory.mkdirs();
            File snapshotFile = new File(getSnapshotDirectory() + getSnapshotFileName(snapshot));
            return ImageIO.write(snapshot.getImage(), "png", snapshotFile);
        } catch (IOException e) {
            LOGGER.debug("snapshot saving failed", e);
            return false;
        }
    }

    private String getSnapshotDirectory() {
        return userDirectory + "/snapshots/";
    }

    private String getSnapshotFileName(Snapshot snapshot) {
        return format("format.file.snapshot", getVideoName(), snapshot.getTimeAsString().replace(':', '-'), "png");
    }

    public String getVideoName() {
        String fileName = videoDetails.getSourceFile().getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    public void setStatus(Status newStatus, Object... params) {
        this.status = newStatus;
        mainFrame.getStatusPanel().setStatus(newStatus, params);
    }

    public void updateAfterLoad(VideoDetails videoDetails) {
        UserLogger.clear();
        UserLogger.log("video loaded from file " + videoDetails.getSourceFile().getAbsolutePath());
        setVideoDetails(videoDetails);
        mainFrame.getVideoPlayer().loadVideo();
        mainFrame.getVideoPlayerPanel().init(videoDetails);
        mainFrame.getVideoDetailsPanel().update(videoDetails);
        setStatus(Status.LOADING_COMPLETE);
    }

    public void repaintTimeline() {
        mainFrame.getVideoPlayerPanel().repaintTimeline();
    }

    public void updateTimeline(long newTime) {
        mainFrame.getVideoPlayerPanel().updateTimeline(newTime);
    }

    public void updateAfterMotionDetection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ApplicationContext.getInstance().setStatus(Status.ANALYZE_MOTION, 100);
                mainFrame.getVideoPlayerPanel().initColoredSlider();
            }
        });
    }

    public void updateAfterSoundDetection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ApplicationContext.getInstance().setStatus(Status.ANALYZE_SOUND, 100);
                mainFrame.getVideoPlayerPanel().initColoredSlider();
            }
        });
    }

    public void moveToSnapshot(Snapshot snapshot) {
        UserLogger.log("move to snapshot in video at " + snapshot.getTimeAsString());
        mainFrame.moveToSnapshot(snapshot);
        if (frameGrabsDialog != null) {
            frameGrabsDialog.close();
        }
    }

    public void analyzeVideoAndSound() {
        Thread analyzer = new Thread(new Runnable() {
            @Override public void run() {
                motionDetector.analyzeVideo();
                do {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {
                    }
                } while (motionDetector.isInProgress());
                soundDetector.analyzeSound();
            }
        });
        analyzer.start();
    }

    public void playAllMovements() {
        mainFrame.getVideoPlayerPanel().playAllMovements();
    }

    public void playAllSounds() {
        mainFrame.getVideoPlayerPanel().playAllSounds();
    }

    public void updateApplicationProperties() {
        try (FileOutputStream fos = new FileOutputStream(userDirectory + SETTINGS_RELATIVE_PATH)) {
            Properties properties = new Properties();
            updateMotionDetectionProperties(properties, fos);
            properties = new Properties();
            updateSoundDetectionProperties(properties, fos);
            properties = new Properties();
            updateReportProperties(properties, fos);
            fos.flush();
            LOGGER.debug("application properties saved successfully");
        } catch (IOException e) {
            LOGGER.debug("application properties saving failed", e);
        }
    }

    private void updateReportProperties(Properties properties, OutputStream outputStream) throws IOException {
        ReportOptions options = reportGenerator.getOptions();

        properties.put("ro.snapshotWidth", String.valueOf(options.getSnapshotWidth()));
        properties.put("ro.snapshotHeight", String.valueOf(options.getSnapshotHeight()));

        properties.put("ro.docxPresent", String.valueOf(options.isDocxPresent()));
        properties.put("ro.pdfPresent", String.valueOf(options.isPdfPresent()));

        properties.store(outputStream, "Report Options");
    }

    private void updateSoundDetectionProperties(Properties properties, OutputStream outputStream) throws IOException {
        SoundDetectionOptions options = soundDetector.getOptions();

        properties.put("sdo.frameGap", String.valueOf(options.getFrameGap()));
        properties.put("sdo.soundLowerBound", String.valueOf(options.getSoundMinLength()));

        properties.put("sdo.minNoiseBound", String.valueOf(options.getMinNoiseBound()));
        properties.put("sdo.maxNoiseBound", String.valueOf(options.getMaxNoiseBound()));

        properties.put("sdo.noiseColor", Integer.toHexString(options.getNoiseColor().getRGB()));
        properties.put("sdo.soundColor", Integer.toHexString(options.getSoundColor().getRGB()));

        properties.store(outputStream, "Sound Detection Options");
    }

    private void updateMotionDetectionProperties(Properties properties, OutputStream outputStream) throws IOException {
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

        properties.store(outputStream, "Motion Detection Options");
    }

    public void loadApplicationSettings() {
        File settingsFile = new File(userDirectory + SETTINGS_RELATIVE_PATH);
        if (!settingsFile.isFile()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(settingsFile)) {
            Properties properties = new Properties();
            properties.load(fis);
            loadMotionDetectionOptions(properties);
            loadSoundDetectionOptions(properties);
            loadReportOptions(properties);
            LOGGER.debug("application properties loaded successfully");
        } catch (IOException e) {
            LOGGER.debug("application properties loading failed", e);
        }
    }

    private void loadReportOptions(Properties properties) {
        ReportOptions options = reportGenerator.getOptions();

        Integer intValue = getIntegerValue(properties, "ro.snapshotWidth");
        if (intValue != null) {
            options.setSnapshotWidth(intValue);
        }
        intValue = getIntegerValue(properties, "ro.snapshotHeight");
        if (intValue != null) {
            options.setSnapshotHeight(intValue);
        }

        Boolean boolValue = getBooleanValue(properties, "ro.docxPresent");
        if (boolValue != null) {
            options.setDocxPresent(boolValue);
        }
        boolValue = getBooleanValue(properties, "mdo.pdfPresent");
        if (boolValue != null) {
            options.setPdfPresent(boolValue);
        }
    }

    private void loadSoundDetectionOptions(Properties properties) {
        SoundDetectionOptions options = soundDetector.getOptions();

        Integer intValue = getIntegerValue(properties, "sdo.frameGap");
        if (intValue != null) {
            options.setFrameGap(intValue);
        }
        intValue = getIntegerValue(properties, "sdo.soundLowerBound");
        if (intValue != null) {
            options.setSoundMinLength(intValue);
        }

        Double doubleValue = getDoubleValue(properties, "sdo.minNoiseBound");
        if (doubleValue != null) {
            options.setMinNoiseBound(doubleValue);
        }
        doubleValue = getDoubleValue(properties, "sdo.maxNoiseBound");
        if (doubleValue != null) {
            options.setMaxNoiseBound(doubleValue);
        }

        Color color = getColorValue(properties, "sdo.noiseColor");
        if (color != null) {
            options.setNoiseColor(color);
        }
        color = getColorValue(properties, "sdo.soundColor");
        if (color != null) {
            options.setSoundColor(color);
        }
    }

    private void loadMotionDetectionOptions(Properties properties) {
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

    private Double getDoubleValue(Properties properties, String key) {
        if (!properties.containsKey(key)) {
            return null;
        }
        String valueAsString = properties.getProperty(key);
        try {
            return Double.parseDouble(valueAsString);
        } catch (NumberFormatException e) {
            return null;
        }
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

    public String getUserDirectory() {
        return userDirectory;
    }

    public VideoRecorder getVideoRecorder() {
        return videoRecorder;
    }

    public void setVideoRecorder(VideoRecorder videoRecorder) {
        this.videoRecorder = videoRecorder;
    }
}
