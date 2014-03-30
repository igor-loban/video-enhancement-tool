package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.action.Actions;
import com.belsofto.vet.ui.composite.SnapshotListPanel;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class FrameGrabsDialog extends JDialog {
    private static final int MINIMUM_HEIGHT = 320;
    private static final int MINIMUM_WIDTH = 820;

    private final SnapshotListPanel snapshotListPanel = new SnapshotListPanel();

    private final JButton deleteAllFramesButton =
            new JButton(getMessage("ui.dialog.frameGrabs.button.deleteAllFrames"));
    private final JButton exportFramesButton = new JButton(getMessage("ui.dialog.frameGrabs.button.exportFrames"));
    private final JButton generateReportButton = new JButton(getMessage("ui.dialog.frameGrabs.button.generateReport"));
    private final JButton closeButton = new JButton(getMessage("ui.dialog.frameGrabs.button.close"));

    public FrameGrabsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.frameGrabs.title"), true);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        deleteAllFramesButton.addActionListener(new DeleteAllFramesAction());

        exportFramesButton.addActionListener(new ExportFramesAction());
        generateReportButton.addActionListener(Actions.GENERATE_REPORT.get());
        closeButton.addActionListener(new CloseAction());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(snapshotListPanel), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(deleteAllFramesButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 25, 5, 2);
        add(exportFramesButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 2, 5, 2);
        add(generateReportButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 2, 5, 5);
        add(closeButton, gbc);
    }

    private void setupSizeAndLocation() {
        setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
        pack();
        WindowUtils.setLocationToCenter(this);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private final class DeleteAllFramesAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            snapshotListPanel.removeAllSnapshots();
        }
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    private final class ExportFramesAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext context = ApplicationContext.getInstance();
            List<Snapshot> snapshots = context.getReportGenerator().getSnapshots();
            if (snapshots.isEmpty()) {
                DialogUtils.showErrorMessage("noSnapshotsFound");
                return;
            }

            boolean result = true;
            for (Snapshot snapshot : snapshots) {
                result &= context.saveSnapshot(snapshot);
            }
            if (result) {
                UserLogger.log(snapshots.size() + " frame(s) exported successfully");
                DialogUtils.showInfoMessage("allSnapshotsSaved");
            } else {
                UserLogger.log(snapshots.size() + " frame(s) exporting failed");
                DialogUtils.showErrorMessage("snapshotsSavedFailed");
            }
        }
    }
}
