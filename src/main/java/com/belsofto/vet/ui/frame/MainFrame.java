package com.belsofto.vet.ui.frame;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.action.Actions;
import com.belsofto.vet.ui.component.StatusPanel;
import com.belsofto.vet.ui.component.VideoPlayer;
import com.belsofto.vet.ui.component.VideoPlayerPanel;
import com.belsofto.vet.ui.composite.ControlPanel;
import com.belsofto.vet.ui.composite.NotesPanel;
import com.belsofto.vet.ui.composite.VideoDetailsPanel;
import com.belsofto.vet.ui.util.WindowUtils;
import com.belsofto.vet.util.MessageUtils;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainFrame extends JFrame {
    private static final int MINIMUM_HEIGHT = 650;

    private final VideoPlayer videoPlayer = new VideoPlayer();
    private final VideoPlayerPanel videoPlayerPanel = new VideoPlayerPanel(videoPlayer);
    private final ControlPanel controlPanel = new ControlPanel();
    private final VideoDetailsPanel videoDetailsPanel = new VideoDetailsPanel();
    private final NotesPanel notesPanel = new NotesPanel();
    private final StatusPanel statusPanel = new StatusPanel();
    private final JToolBar toolBar = new JToolBar();

    /**
     * {@link ApplicationContext} can be used inside this method.
     */
    public void init() {
        setTitle(MessageUtils.getMessage("ui.mainFrame.title"));
        setActionOnClose();
        setupMainMenu();
        setupToolBar();
        arrangeComponents();
        setupSizeAndLocation();
    }

    public VideoPlayer getVideoPlayer() {
        return videoPlayer;
    }

    public VideoPlayerPanel getVideoPlayerPanel() {
        return videoPlayerPanel;
    }

    private void setActionOnClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Actions.EXIT.get().actionPerformed(null);
            }
        });
    }

    private void setupMainMenu() {
        JMenuBar mainMenu = new JMenuBar();

        JMenu fileMenu = new JMenu(Actions.FILE.get());
        fileMenu.add(Actions.OPEN.get());
        fileMenu.addSeparator();
        fileMenu.add(Actions.EXIT.get());
        mainMenu.add(fileMenu);

        JMenu analyzeMenu = new JMenu(Actions.ANALYZE.get());
        analyzeMenu.add(Actions.RUN_BOTH.get());
        analyzeMenu.add(Actions.RUN_MOTION_DETECTION.get());
        analyzeMenu.add(Actions.RUN_SOUND_DETECTION.get());
        analyzeMenu.addSeparator();
        analyzeMenu.add(Actions.MOTION_DETECTION_OPTIONS.get());
        analyzeMenu.add(Actions.SOUND_DETECTION_OPTIONS.get());
        mainMenu.add(analyzeMenu);

        JMenu reportMenu = new JMenu(Actions.REPORT.get());
        reportMenu.add(Actions.VIEW_FRAMES_CAPTURED.get());
        reportMenu.add(Actions.GENERATE_REPORT.get());
        reportMenu.add(Actions.REPORT_OPTIONS.get());
        reportMenu.addSeparator();
        reportMenu.add(Actions.SHOW_LOG.get());
        mainMenu.add(reportMenu);

        JMenu helpMenu = new JMenu(Actions.HELP.get());
        helpMenu.add(Actions.ABOUT.get());
        mainMenu.add(helpMenu);

        setJMenuBar(mainMenu);
    }

    private void setupToolBar() {
        toolBar.add(Actions.OPEN.get());
        toolBar.addSeparator();
        toolBar.add(Actions.RUN_BOTH.get());
        toolBar.add(Actions.RUN_MOTION_DETECTION.get());
        toolBar.add(Actions.RUN_SOUND_DETECTION.get());
        toolBar.addSeparator();
        toolBar.add(Actions.SHOW_LOG.get());
        toolBar.addSeparator();
        toolBar.add(Actions.MOTION_DETECTION_OPTIONS.get());
        toolBar.add(Actions.SOUND_DETECTION_OPTIONS.get());
        toolBar.add(Actions.REPORT_OPTIONS.get());
        toolBar.addSeparator();
        toolBar.add(Actions.SHOW_TOOLS.get());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(toolBar, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(videoPlayerPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(statusPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(controlPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(videoDetailsPanel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(notesPanel, gbc);

        setToolsVisible(false);
    }

    private void setupSizeAndLocation() {
        pack();
        setMinimumSize(new Dimension(getWidth(), MINIMUM_HEIGHT));
        WindowUtils.setLocationToCenter(this);
        WindowUtils.maximizeByHeight(this);
    }

    public void setFocusToNotes() {
        notesPanel.getNotesTextArea().grabFocus();
    }

    public void moveToSnapshot(Snapshot snapshot) {
        videoPlayer.moveToSnapshot(snapshot);
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public VideoDetailsPanel getVideoDetailsPanel() {
        return videoDetailsPanel;
    }

    public void setToolsVisible(boolean visible) {
        controlPanel.setVisible(visible);
        videoDetailsPanel.setVisible(visible);
        notesPanel.setVisible(visible);
    }

    public boolean isToolsVisible() {
        return controlPanel.isVisible();
    }
}
