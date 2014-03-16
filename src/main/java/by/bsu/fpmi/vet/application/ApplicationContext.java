package by.bsu.fpmi.vet.application;

import by.bsu.fpmi.vet.report.ReportGenerator;
import by.bsu.fpmi.vet.ui.frame.MainFrame;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private MainFrame mainFrame;
    private ReportGenerator reportGenerator;

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
}
