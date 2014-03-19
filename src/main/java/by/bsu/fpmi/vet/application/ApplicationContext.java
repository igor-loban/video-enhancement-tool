package by.bsu.fpmi.vet.application;

import by.bsu.fpmi.vet.motion.MotionDetector;
import by.bsu.fpmi.vet.report.ReportGenerator;
import by.bsu.fpmi.vet.ui.dialog.FrameGrabsDialog;
import by.bsu.fpmi.vet.ui.frame.MainFrame;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private MainFrame mainFrame;
    private FrameGrabsDialog frameGrabsDialog;

    private ReportGenerator reportGenerator;
    private MotionDetector motionDetector;

    private ApplicationContext() {
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

    public void goToFrameInVideo(int frameNumber) {
        mainFrame.goToFrameInVideo(frameNumber);
        if (frameGrabsDialog != null) {
            frameGrabsDialog.close();
        }
    }
}
