package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.detection.sound.SoundDetectionOptions;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class SoundDetectionOptionsDialog extends JDialog {
    private final JLabel frameGapLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.frameGap"));
    private final JLabel noiseMinLengthLabel =
            new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.noiseMinLength"));
    private final JLabel soundMinLengthLabel =
            new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.soundMinLength"));
    private final JLabel soundBoundLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.soundBound"));
    private final JLabel noiseColorLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.noiseColor"));
    private final JLabel soundColorLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.soundColor"));

    private final JLabel noiseColorValueLabel = new JLabel("                    ");
    private final JLabel soundColorValueLabel = new JLabel("                    ");

    private final JSpinner frameGapSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner noiseMinLengthSpinner = new JSpinner(new SpinnerNumberModel(250, 0, 10000, 100));
    private final JSpinner soundMinLengthSpinner = new JSpinner(new SpinnerNumberModel(500, 0, 10000, 100));
    private final JSpinner soundBoundSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));

    private final JButton resetButton = new JButton(getMessage("ui.dialog.soundDetectionOptions.button.reset"));
    private final JButton saveButton = new JButton(getMessage("ui.dialog.soundDetectionOptions.button.save"));
    private final JButton cancelButton = new JButton(getMessage("ui.dialog.soundDetectionOptions.button.cancel"));

    public SoundDetectionOptionsDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.soundDetectionOptions.title"),
                true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setResizable(false);

        CursorHandModeAction cursorHandModeAction = new CursorHandModeAction();
        LabelColorChooseAction labelColorChooseAction = new LabelColorChooseAction();
        noiseColorValueLabel.setOpaque(true);
        noiseColorValueLabel.addMouseListener(cursorHandModeAction);
        noiseColorValueLabel.addMouseListener(labelColorChooseAction);

        soundColorValueLabel.setOpaque(true);
        soundColorValueLabel.addMouseListener(cursorHandModeAction);
        soundColorValueLabel.addMouseListener(labelColorChooseAction);

        initValues();

        resetButton.addActionListener(new ResetAction());
        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(new CloseAction());
    }

    private void initValues() {
        SoundDetectionOptions options = ApplicationContext.getInstance().getSoundDetector().getOptions();

        frameGapSpinner.setValue(options.getFrameGap());
        noiseMinLengthSpinner.setValue(options.getNoiseMinLength());
        soundMinLengthSpinner.setValue(options.getSoundMinLength());

        soundBoundSpinner.setValue(toSoundBound(options.getMinNoiseBound()));

        noiseColorValueLabel.setBackground(options.getNoiseColor());
        soundColorValueLabel.setBackground(options.getSoundColor());
    }

    private int toSoundBound(double minNoiseBound) {
        if (minNoiseBound >= 0.5) {
            return 100;
        } else if (minNoiseBound <= 0) {
            return 0;
        }
        return (int) (minNoiseBound * 200);
    }

    private double toMinNoiseBound(int value) {
        if (value >= 100) {
            return 0.51;
        } else if (value <= 0) {
            return 0.0;
        }
        return (double) value / 200;
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 5);
        add(frameGapLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(frameGapSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(noiseMinLengthLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(noiseMinLengthSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(soundMinLengthLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(soundMinLengthSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(soundBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(soundBoundSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(noiseColorLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(noiseColorValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(soundColorLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(soundColorValueLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 2);
        buttonPanel.add(resetButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 2, 0, 2);
        buttonPanel.add(saveButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 2, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
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
            int noiseMinLength = (int) noiseMinLengthSpinner.getValue();
            int soundMinLength = (int) soundMinLengthSpinner.getValue();
            double minNoiseBound = toMinNoiseBound((int) soundBoundSpinner.getValue());
            double maxNoiseBound = 1.0 - minNoiseBound;
            Color noiseColor = noiseColorValueLabel.getBackground();
            Color soundColor = soundColorValueLabel.getBackground();

            SoundDetectionOptions options = new SoundDetectionOptions();
            options.setFrameGap(frameGap);
            options.setNoiseMinLength(noiseMinLength);
            options.setSoundMinLength(soundMinLength);
            options.setMinNoiseBound(minNoiseBound);
            options.setMaxNoiseBound(maxNoiseBound);
            options.setNoiseColor(noiseColor);
            options.setSoundColor(soundColor);

            ApplicationContext context = ApplicationContext.getInstance();
            context.getSoundDetector().setOptions(options);
            context.updateApplicationProperties();

            UserLogger.log("sound detection options changed to " + options.toString());

            close();
        }
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    private static final class CursorHandModeAction extends MouseAdapter {
        @Override public void mouseEntered(MouseEvent event) {
            event.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override public void mouseExited(MouseEvent event) {
            event.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }

    private final class LabelColorChooseAction extends MouseAdapter {
        @Override public void mouseClicked(MouseEvent event) {
            Component component = event.getComponent();
            Color newColor = chooseColor(component.getBackground());
            if (newColor != null) {
                component.setBackground(newColor);
                component.repaint();
            }
        }
    }

    private Color chooseColor(Color initialColor) {
        return JColorChooser.showDialog(this, getMessage("ui.dialog.colorChooser.title"), initialColor);
    }

    private final class ResetAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            SoundDetectionOptions options = new SoundDetectionOptions();
            ApplicationContext context = ApplicationContext.getInstance();
            context.getSoundDetector().setOptions(options);
            context.updateApplicationProperties();

            initValues();

            UserLogger.log("sound detection options changed to " + options.toString());
        }
    }
}
