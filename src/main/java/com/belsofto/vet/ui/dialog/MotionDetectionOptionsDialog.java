package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.detection.motion.MotionDetectionOptions;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public final class MotionDetectionOptionsDialog extends JDialog {
    private final JLabel frameGapLabel = new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.frameGap"));
    private final JLabel slideDetectionLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.slideMinFrame"));
    private final JLabel noMotionColorLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.noMotionColor"));
    private final JLabel tinyThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.tinyThreshold"));
    private final JLabel lowThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.lowThreshold"));
    private final JLabel mediumThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.mediumThreshold"));
    private final JLabel highThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.highThreshold"));
    private final JLabel veryHighThresholdLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.veryHighThreshold"));
    private final JLabel videoQualityLabel =
            new JLabel(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality"));

    private final JLabel noMotionColorValueLabel = new JLabel("                    ");
    private final JLabel tinyMotionColorValueLabel = new JLabel("                    ");
    private final JLabel lowMotionColorValueLabel = new JLabel("                    ");
    private final JLabel mediumMotionColorValueLabel = new JLabel("                    ");
    private final JLabel highMotionColorValueLabel = new JLabel("                    ");
    private final JLabel veryHighMotionColorValueLabel = new JLabel("                    ");

    private final JSpinner frameGapSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
    private final JSpinner slideMinFrameSpinner = new JSpinner(new SpinnerNumberModel(30, 30, 100, 1));
    private final JSpinner tinyThresholdSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
    private final JSpinner lowThresholdSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 255, 1));
    private final JSpinner mediumThresholdSpinner = new JSpinner(new SpinnerNumberModel(70, 0, 255, 1));
    private final JSpinner highThresholdSpinner = new JSpinner(new SpinnerNumberModel(150, 0, 255, 1));
    private final JSpinner veryHighThresholdSpinner = new JSpinner(new SpinnerNumberModel(150, 0, 255, 1));

    private final JCheckBox usedLowThresholdCheckbox =
            new JCheckBox(getMessage("ui.dialog.motionDetectionOptions.label.use"));
    private final JCheckBox usedMediumThresholdCheckbox =
            new JCheckBox(getMessage("ui.dialog.motionDetectionOptions.label.use"));
    private final JCheckBox usedHighThresholdCheckbox =
            new JCheckBox(getMessage("ui.dialog.motionDetectionOptions.label.use"));
    private final JCheckBox usedVeryHighThresholdCheckbox =
            new JCheckBox(getMessage("ui.dialog.motionDetectionOptions.label.use"));

    private final JRadioButton lowVideoQualityButton =
            new JRadioButton(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality.low"), true);
    private final JRadioButton highVideoQualityButton =
            new JRadioButton(getMessage("ui.dialog.motionDetectionOptions.label.videoQuality.high"));
    private final ButtonGroup videoQualityButtonGroup = new ButtonGroup();

    private final JButton resetButton = new JButton(getMessage("ui.dialog.motionDetectionOptions.button.reset"));
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

    private void initValues() {
        MotionDetectionOptions options = ApplicationContext.getInstance().getMotionDetector().getOptions();

        frameGapSpinner.setValue(options.getFrameGap());
        slideMinFrameSpinner.setValue(options.getSlideMinFrame());

        tinyThresholdSpinner.setValue(options.getTinyThreshold());
        lowThresholdSpinner.setValue(options.getLowThreshold());
        mediumThresholdSpinner.setValue(options.getMediumThreshold());
        highThresholdSpinner.setValue(options.getHighThreshold());
        veryHighThresholdSpinner.setValue(options.getVeryHighThreshold());

        noMotionColorValueLabel.setBackground(options.getNoMotionColor());
        tinyMotionColorValueLabel.setBackground(options.getTinyMotionColor());
        lowMotionColorValueLabel.setBackground(options.getLowMotionColor());
        mediumMotionColorValueLabel.setBackground(options.getMediumMotionColor());
        highMotionColorValueLabel.setBackground(options.getHighMotionColor());
        veryHighMotionColorValueLabel.setBackground(options.getVeryHighMotionColor());

        usedLowThresholdCheckbox.setSelected(options.isUsedLowThreshold());
        usedMediumThresholdCheckbox.setSelected(options.isUsedMediumThreshold());
        usedHighThresholdCheckbox.setSelected(options.isUsedHighThreshold());
        usedVeryHighThresholdCheckbox.setSelected(options.isUsedVeryHighThreshold());

        if (options.isHighVideoQuality()) {
            highVideoQualityButton.setSelected(true);
        } else {
            lowVideoQualityButton.setSelected(true);
        }
    }

    private void configureComponents() {
        setResizable(false);
        videoQualityButtonGroup.add(lowVideoQualityButton);
        videoQualityButtonGroup.add(highVideoQualityButton);

        CursorHandModeAction cursorHandModeAction = new CursorHandModeAction();
        LabelColorChooseAction labelColorChooseAction = new LabelColorChooseAction();
        noMotionColorValueLabel.setOpaque(true);
        noMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        noMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        noMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        tinyMotionColorValueLabel.setOpaque(true);
        tinyMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        tinyMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        tinyMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        lowMotionColorValueLabel.setOpaque(true);
        lowMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        lowMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        lowMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        mediumMotionColorValueLabel.setOpaque(true);
        noMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        mediumMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        mediumMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        highMotionColorValueLabel.setOpaque(true);
        highMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        highMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        highMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        veryHighMotionColorValueLabel.setOpaque(true);
        veryHighMotionColorValueLabel.setBorder(BorderFactory.createEtchedBorder());
        veryHighMotionColorValueLabel.addMouseListener(cursorHandModeAction);
        veryHighMotionColorValueLabel.addMouseListener(labelColorChooseAction);

        usedLowThresholdCheckbox.addChangeListener(new UsedLowThresholdChangeHandler());
        usedMediumThresholdCheckbox.addChangeListener(new UsedMediumThresholdChangeHandler());
        usedHighThresholdCheckbox.addChangeListener(new UsedHighThresholdChangeHandler());
        usedVeryHighThresholdCheckbox.addChangeListener(new UsedVeryHighThresholdChangeHandler());

        initValues();

        resetButton.addActionListener(new ResetAction());
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
        add(slideMinFrameSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(tinyThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(tinyThresholdSpinner, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(tinyMotionColorValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(lowThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(lowThresholdSpinner, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(lowMotionColorValueLabel, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(usedLowThresholdCheckbox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(mediumThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(mediumThresholdSpinner, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(mediumMotionColorValueLabel, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(usedMediumThresholdCheckbox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(highThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(highThresholdSpinner, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(highMotionColorValueLabel, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(usedHighThresholdCheckbox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(veryHighThresholdLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(veryHighThresholdSpinner, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(veryHighMotionColorValueLabel, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(usedVeryHighThresholdCheckbox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(noMotionColorLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(noMotionColorValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
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
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 2);
        buttonPanel.add(resetButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 2, 0, 2);
        buttonPanel.add(saveButton, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 2, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 5;
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
            int slideMinFrame = (int) slideMinFrameSpinner.getValue();
            int tinyThreshold = (int) tinyThresholdSpinner.getValue();
            int lowThreshold = (int) lowThresholdSpinner.getValue();
            int mediumThreshold = (int) mediumThresholdSpinner.getValue();
            int highThreshold = (int) highThresholdSpinner.getValue();
            int veryHighThreshold = (int) veryHighThresholdSpinner.getValue();
            boolean usedLowThreshold = usedLowThresholdCheckbox.isSelected();
            boolean usedMediumThreshold = usedMediumThresholdCheckbox.isSelected();
            boolean usedHighThreshold = usedHighThresholdCheckbox.isSelected();
            boolean usedVeryHighThreshold = usedVeryHighThresholdCheckbox.isSelected();
            boolean highVideoQuality = highVideoQualityButton.isSelected();
            Color noMotionColor = noMotionColorValueLabel.getBackground();
            Color tinyMotionColor = tinyMotionColorValueLabel.getBackground();
            Color lowMotionColor = lowMotionColorValueLabel.getBackground();
            Color mediumMotionColor = mediumMotionColorValueLabel.getBackground();
            Color highMotionColor = highMotionColorValueLabel.getBackground();
            Color veryHighMotionColor = veryHighMotionColorValueLabel.getBackground();

            MotionDetectionOptions options = new MotionDetectionOptions();
            options.setFrameGap(frameGap);
            options.setSlideMinFrame(slideMinFrame);

            options.setTinyThreshold(tinyThreshold);
            options.setLowThreshold(lowThreshold);
            options.setMediumThreshold(mediumThreshold);
            options.setHighThreshold(highThreshold);
            options.setVeryHighThreshold(veryHighThreshold);

            options.setUsedLowThreshold(usedLowThreshold);
            options.setUsedMediumThreshold(usedMediumThreshold);
            options.setUsedHighThreshold(usedHighThreshold);
            options.setUsedVeryHighThreshold(usedVeryHighThreshold);

            options.setHighVideoQuality(highVideoQuality);

            options.setNoMotionColor(noMotionColor);
            options.setTinyMotionColor(tinyMotionColor);
            options.setLowMotionColor(lowMotionColor);
            options.setMediumMotionColor(mediumMotionColor);
            options.setHighMotionColor(highMotionColor);
            options.setVeryHighMotionColor(veryHighMotionColor);

            ApplicationContext context = ApplicationContext.getInstance();
            context.getMotionDetector().setMotionDetectionOptions(options);
            context.updateApplicationProperties();

            UserLogger.log("motion detection options changed to " + options.toString());

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

    private final class UsedLowThresholdChangeHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            if (!usedLowThresholdCheckbox.isSelected()) {
                usedMediumThresholdCheckbox.setSelected(false);
                usedHighThresholdCheckbox.setSelected(false);
                usedVeryHighThresholdCheckbox.setSelected(false);
            }
        }
    }

    private final class UsedMediumThresholdChangeHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            if (!usedLowThresholdCheckbox.isSelected()) {
                usedMediumThresholdCheckbox.setSelected(false);
            }
            if (!usedMediumThresholdCheckbox.isSelected()) {
                usedHighThresholdCheckbox.setSelected(false);
                usedVeryHighThresholdCheckbox.setSelected(false);
            }
        }
    }

    private final class UsedHighThresholdChangeHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            if (!usedLowThresholdCheckbox.isSelected() || !usedMediumThresholdCheckbox.isSelected()) {
                usedHighThresholdCheckbox.setSelected(false);
            }
            if (!usedHighThresholdCheckbox.isSelected()) {
                usedVeryHighThresholdCheckbox.setSelected(false);
            }
        }
    }

    private final class UsedVeryHighThresholdChangeHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            if (!usedLowThresholdCheckbox.isSelected() || !usedMediumThresholdCheckbox.isSelected()
                    || !usedHighThresholdCheckbox.isSelected()) {
                usedVeryHighThresholdCheckbox.setSelected(false);
            }
        }
    }

    private final class ResetAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext context = ApplicationContext.getInstance();
            MotionDetectionOptions options = new MotionDetectionOptions();
            context.getMotionDetector().setMotionDetectionOptions(options);
            context.updateApplicationProperties();

            initValues();

            UserLogger.log("motion detection options changed to " + options.toString());
        }

    }
}
