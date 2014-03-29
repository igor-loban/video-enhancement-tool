package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.report.ReportOptions;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class ReportOptionsDialog extends JDialog {
    private final JLabel snapshotWidthLabel = new JLabel(getMessage("ui.dialog.reportOptions.label.snapshotWidth"));
    private final JLabel snapshotHeightLabel = new JLabel(getMessage("ui.dialog.reportOptions.label.snapshotHeight"));
    private final JLabel docxPresentLabel = new JLabel(getMessage("ui.dialog.reportOptions.label.docxPresent"));
    private final JLabel pdfPresentLabel = new JLabel(getMessage("ui.dialog.reportOptions.label.pdfPresent"));

    private final JSpinner snapshotWidthSpinner = new JSpinner(new SpinnerNumberModel(320, 10, 3000, 10));
    private final JSpinner snapshotHeightSpinner = new JSpinner(new SpinnerNumberModel(240, 10, 3000, 10));
    private final JCheckBox docxPresentCheckBox = new JCheckBox();
    private final JCheckBox pdfPresentCheckBox = new JCheckBox();

    private final JButton saveButton = new JButton(getMessage("ui.dialog.reportOptions.button.save"));
    private final JButton cancelButton = new JButton(getMessage("ui.dialog.reportOptions.button.cancel"));

    public ReportOptionsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.reportOptions.title"), true);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ReportOptions options = ApplicationContext.getInstance().getReportGenerator().getOptions();

        snapshotWidthSpinner.setValue(options.getSnapshotWidth());
        snapshotHeightSpinner.setValue(options.getSnapshotHeight());

        docxPresentCheckBox.setSelected(options.isDocxPresent());
        pdfPresentCheckBox.setSelected(options.isPdfPresent());

        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(new CloseAction());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 5);
        add(snapshotWidthLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(snapshotWidthSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(snapshotHeightLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(snapshotHeightSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(docxPresentLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(docxPresentCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(pdfPresentLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(pdfPresentCheckBox, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 2);
        buttonPanel.add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 2, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 10, 5, 5);
        add(buttonPanel, gbc);
    }

    private void setupSizeAndLocation() {
        pack();
        setMinimumSize(getSize());
        WindowUtils.setLocationToCenter(this);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private final class SaveAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            int snapshotWidth = (int) snapshotWidthSpinner.getValue();
            int snapshotHeight = (int) snapshotHeightSpinner.getValue();
            boolean docxPresent = docxPresentCheckBox.isSelected();
            boolean pdfPresent = pdfPresentCheckBox.isSelected();

            ReportOptions options = new ReportOptions();
            options.setSnapshotWidth(snapshotWidth);
            options.setSnapshotHeight(snapshotHeight);
            options.setDocxPresent(docxPresent);
            options.setPdfPresent(pdfPresent);

            ApplicationContext context = ApplicationContext.getInstance();
            context.getReportGenerator().setOptions(options);
            context.updateApplicationProperties();

            UserLogger.log("report options changed to " + options.toString());

            close();
        }
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }
}
