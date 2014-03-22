package com.belsofto.vet.application;

import com.belsofto.vet.media.SoundDetector;
import com.belsofto.vet.report.ReportGenerator;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.dialog.FrameGrabsDialog;
import com.belsofto.vet.ui.frame.MainFrame;
import com.belsofto.vet.media.MotionDetector;
import com.belsofto.vet.media.VideoDetails;

import javax.swing.SwingUtilities;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private Status status = Status.DEFAULT;

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
}
