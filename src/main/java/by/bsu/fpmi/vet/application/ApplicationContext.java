package by.bsu.fpmi.vet.application;

import by.bsu.fpmi.vet.report.ReportGenerator;
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

    public void setStatus(Status newStatus) {
        mainFrame.getStatusPanel().setStatus(newStatus);
    }

    public void initTimeline(VideoDetails videoDetails) {
        setVideoDetails(videoDetails);
        mainFrame.getVideoPlayerPanel().initTimeline(videoDetails);
        mainFrame.getVideoPlayer().loadVideo();
    }

    public void updateTimeline(int frameNumber) {
        mainFrame.getVideoPlayerPanel().updateTimeline(frameNumber);
    }

    public void updateAfterMotionDetection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                mainFrame.getVideoPlayerPanel().initColoredSlider();
            }
        });
    }

    public void goToFrameInVideo(int frameNumber) {
        mainFrame.goToFrameInVideo(frameNumber);
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
