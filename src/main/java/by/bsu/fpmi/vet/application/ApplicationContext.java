package by.bsu.fpmi.vet.application;

import by.bsu.fpmi.vet.ui.frame.MainFrame;

public final class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private MainFrame mainFrame;

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
}
