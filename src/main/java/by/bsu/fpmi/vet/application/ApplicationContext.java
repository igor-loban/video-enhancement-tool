package by.bsu.fpmi.vet.application;

import by.bsu.fpmi.vet.report.ReportGenerator;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.ui.dialog.FrameGrabsDialog;
import by.bsu.fpmi.vet.ui.frame.MainFrame;
import by.bsu.fpmi.vet.video.MotionDetector;
import by.bsu.fpmi.vet.video.VideoDetails;

import javax.swing.SwingUtilities;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private MainFrame mainFrame;
    private FrameGrabsDialog frameGrabsDialog;

    private ReportGenerator reportGenerator;
    private MotionDetector motionDetector;

    private VideoDetails videoDetails;

    private ApplicationContext() {
    }

    public void blockUI() {
        mainFrame.getVideoPlayer().pause();
        // TODO: disable buttons
    }

    public void setStatus(Status newStatus, Object... params) {
        mainFrame.getStatusPanel().setStatus(newStatus, params);
    }

    public void updateAfterLoad(VideoDetails videoDetails) {
        setVideoDetails(videoDetails);
        mainFrame.getVideoPlayer().loadVideo();
        mainFrame.getVideoPlayerPanel().init(videoDetails);
        mainFrame.getVideoDetailsPanel().update(videoDetails);
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
//        ApplicationContext.getInstance().setStatus(Status.ANALYZE, 100);
//        mainFrame.getVideoPlayerPanel().initColoredSlider();
//        mainFrame.getVideoPlayerPanel().updateUI();
    }

    public void moveToSnapshot(Snapshot snapshot) {
        mainFrame.moveToSnapshot(snapshot);
        if (frameGrabsDialog != null) {
            frameGrabsDialog.close();
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
}
