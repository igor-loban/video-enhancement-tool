package by.bsu.fpmi.vet.ui.dialog;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.ui.action.Actions;
import by.bsu.fpmi.vet.ui.composite.SnapshotListPanel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class FrameGrabsDialog extends JDialog {
    private final SnapshotListPanel snapshotListPanel = new SnapshotListPanel();
    private final JButton deleteAllFramesButton = new JButton(getMessage("ui.dialog.frameGrabs.button.deleteAllFrames"));
    private final JButton generateReportButton = new JButton(getMessage("ui.dialog.frameGrabs.button.generateReport"));
    private final JButton closeButton = new JButton(getMessage("ui.dialog.frameGrabs.button.close"));

    public FrameGrabsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.frameGrabs.title"), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        configureComponents();
        arrangeComponents();
        pack();
    }

    private void configureComponents() {
        deleteAllFramesButton.addActionListener(new DeleteAllFramesAction());
        generateReportButton.addActionListener(Actions.GENERATE_REPORT.get());
        closeButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
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
        add(generateReportButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(closeButton, gbc);
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
}
