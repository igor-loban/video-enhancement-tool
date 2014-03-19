package by.bsu.fpmi.vet.ui.dialog;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.ui.action.Actions;
import by.bsu.fpmi.vet.ui.composite.SnapshotListPanel;
import by.bsu.fpmi.vet.ui.util.WindowUtils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static by.bsu.fpmi.vet.util.MessageUtils.format;
import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class FrameGrabsDialog extends JDialog {
    private static final int MINIMUM_HEIGHT = 320;

    private final SnapshotListPanel snapshotListPanel = new SnapshotListPanel();
    private final JButton deleteAllFramesButton =
            new JButton(getMessage("ui.dialog.frameGrabs.button.deleteAllFrames"));
    private final JButton exportFramesButton =
            new JButton(format("ui.dialog.frameGrabs.button.exportFrames", "-", "-"));
    private final JButton generateReportButton = new JButton(getMessage("ui.dialog.frameGrabs.button.generateReport"));
    private final JButton closeButton = new JButton(getMessage("ui.dialog.frameGrabs.button.close"));

    public FrameGrabsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.frameGrabs.title"), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        // TODO: implement
        exportFramesButton.setEnabled(false);

        deleteAllFramesButton.addActionListener(new DeleteAllFramesAction());
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
        pack();
        setMinimumSize(new Dimension(getWidth(), MINIMUM_HEIGHT));
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
}
