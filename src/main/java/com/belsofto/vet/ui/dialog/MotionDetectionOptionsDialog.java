package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.media.MotionDetectionOptions;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class MotionDetectionOptionsDialog extends JDialog {
    private final JLabel frameGapLabel = new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.frameGap"));
    private final JLabel slideDetectionLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.slideDetection"));
    private final JLabel colorThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.colorThreshold"));
    private final JLabel videoQualityLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality"));

    private final JSpinner frameGapSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner slideDetectionSpinner = new JSpinner(new SpinnerNumberModel(30, 30, 100, 1));
    private final JSpinner colorThresholdSpinner = new JSpinner(new SpinnerNumberModel(16, 0, 255, 1));
    private final JRadioButton lowVideoQualityButton =
            new JRadioButton(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality.low"), true);
    private final JRadioButton highVideoQualityButton =
            new JRadioButton(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality.high"));
    private final ButtonGroup videoQualityButtonGroup = new ButtonGroup();

    private final JButton saveButton = new JButton(getMessage("ui.dialog.motionDetectionOptions.button.save"));
    private final JButton cancelButton = new JButton(getMessage("ui.dialog.motionDetectionOptions.button.cancel"));

    public MotionDetectionOptionsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.motionDetectionOptions.title"),
                true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setResizable(false);
        videoQualityButtonGroup.add(lowVideoQualityButton);
        videoQualityButtonGroup.add(highVideoQualityButton);

        MotionDetectionOptions options = ApplicationContext.getInstance().getMotionDetector().getOptions();
        frameGapSpinner.setValue(options.getFrameGap());
        slideDetectionSpinner.setValue(options.getSlideMinFrame());
        colorThresholdSpinner.setValue(options.getColorThreshold());
        if (options.isHighVideoQuality()) {
            highVideoQualityButton.setSelected(true);
        } else {
            lowVideoQualityButton.setSelected(true);
        }

        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(new CloseAction());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 5);
        add(frameGapLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(frameGapSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(slideDetectionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(slideDetectionSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(colorThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(colorThresholdSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(videoQualityLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(lowVideoQualityButton, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(highVideoQualityButton, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 2);
        buttonPanel.add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 2, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
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
            int frameGap = (int) frameGapSpinner.getValue();
            int slideDetection = (int) slideDetectionSpinner.getValue();
            int colorThreshold = (int) colorThresholdSpinner.getValue();
            boolean highVideoQuality = highVideoQualityButton.isSelected();
            MotionDetectionOptions options =
                    new MotionDetectionOptions(frameGap, slideDetection, colorThreshold, highVideoQuality);
            ApplicationContext.getInstance().getMotionDetector().setOptions(options);
            close();
        }
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }
}
