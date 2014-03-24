package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
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
    private final JLabel soundLowerBoundLabel =
            new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.soundLowerBound"));
    private final JLabel minNoiseBoundLabel =
            new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.minNoiseBound"));
    private final JLabel maxNoiseBoundLabel =
            new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.maxNoiseBound"));
    private final JLabel noiseColorLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.noiseColor"));
    private final JLabel soundColorLabel = new JLabel(getMessage("ui.dialog.soundDetectionOptions.label.soundColor"));

    private final JLabel noiseColorValueLabel = new JLabel("                    ");
    private final JLabel soundColorValueLabel = new JLabel("                    ");

    private final JSpinner frameGapSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner soundLowerBoundSpinner = new JSpinner(new SpinnerNumberModel(500, 0, 10000, 100));
    private final JSpinner minNoiseBoundSpinner = new JSpinner(new SpinnerNumberModel(0.05, 0, 1.0, 0.01));
    private final JSpinner maxNoiseBoundSpinner = new JSpinner(new SpinnerNumberModel(0.95, 0, 1.0, 0.01));

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

        SoundDetectionOptions options = ApplicationContext.getInstance().getSoundDetector().getOptions();

        frameGapSpinner.setValue(options.getFrameGap());
        soundLowerBoundSpinner.setValue(options.getSoundLowerBound());

        minNoiseBoundSpinner.setValue(options.getMinNoiseBound());
        maxNoiseBoundSpinner.setValue(options.getMaxNoiseBound());

        CursorHandModeAction cursorHandModeAction = new CursorHandModeAction();
        LabelColorChooseAction labelColorChooseAction = new LabelColorChooseAction();
        noiseColorValueLabel.setOpaque(true);
        noiseColorValueLabel.setBackground(options.getNoiseColor());
        noiseColorValueLabel.addMouseListener(cursorHandModeAction);
        noiseColorValueLabel.addMouseListener(labelColorChooseAction);

        soundColorValueLabel.setOpaque(true);
        soundColorValueLabel.setBackground(options.getSoundColor());
        soundColorValueLabel.addMouseListener(cursorHandModeAction);
        soundColorValueLabel.addMouseListener(labelColorChooseAction);

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
        add(frameGapLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(frameGapSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(soundLowerBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(soundLowerBoundSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(minNoiseBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(minNoiseBoundSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(maxNoiseBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(maxNoiseBoundSpinner, gbc);

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
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 2);
        buttonPanel.add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 2, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
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
            int frameGap = (int) frameGapSpinner.getValue();
            int soundLowerBound = (int) soundLowerBoundSpinner.getValue();
            double minNoiseBound = (double) minNoiseBoundSpinner.getValue();
            double maxNoiseBound = (double) maxNoiseBoundSpinner.getValue();
            Color noiseColor = noiseColorValueLabel.getBackground();
            Color soundColor = soundColorValueLabel.getBackground();

            SoundDetectionOptions options = new SoundDetectionOptions();
            options.setFrameGap(frameGap);
            options.setSoundLowerBound(soundLowerBound);
            options.setMinNoiseBound(minNoiseBound);
            options.setMaxNoiseBound(maxNoiseBound);
            options.setNoiseColor(noiseColor);
            options.setSoundColor(soundColor);

            ApplicationContext context = ApplicationContext.getInstance();
            context.getSoundDetector().setOptions(options);
            context.updateApplicationProperties();
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
}
