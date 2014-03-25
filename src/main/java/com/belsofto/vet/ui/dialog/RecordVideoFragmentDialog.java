package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.media.VideoRecordOptions;
import com.belsofto.vet.ui.dialog.file.FileChooserUtils;
import com.belsofto.vet.ui.util.WindowUtils;
import org.joda.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class RecordVideoFragmentDialog extends JDialog {
    private final JLabel leftBoundLabel = new JLabel(getMessage("ui.dialog.recordVideoFragment.label.leftBound"));
    private final JLabel rightBoundLabel = new JLabel(getMessage("ui.dialog.recordVideoFragment.label.rightBound"));

    private final JLabel leftBoundValueLabel = new JLabel();
    private final JLabel rightBoundValueLabel = new JLabel();

    private final JSlider leftBoundSlider = new JSlider();
    private final JSlider rightBoundSlider = new JSlider();

    private final JButton saveButton = new JButton(getMessage("ui.dialog.recordVideoFragment.button.save"));
    private final JButton cancelButton = new JButton(getMessage("ui.dialog.recordVideoFragment.button.cancel"));

    public RecordVideoFragmentDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.recordVideoFragment.title"));
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ApplicationContext context = ApplicationContext.getInstance();
        context.getVideoRecorder().getOptions().setActive(true);
        VideoDetails videoDetails = context.getVideoDetails();
        int totalTimeMillis = videoDetails.getTotalTimeMillis();

        leftBoundSlider.setMinimum(1);
        leftBoundSlider.setMaximum(totalTimeMillis);
        leftBoundSlider.setValue(1);
        leftBoundSlider.addChangeListener(new SliderValueChangeHandler(leftBoundValueLabel));
        leftBoundValueLabel.setText(convertMillisToFormattedTime(1));

        rightBoundSlider.setMinimum(1);
        rightBoundSlider.setMaximum(totalTimeMillis);
        rightBoundSlider.setValue(totalTimeMillis);
        rightBoundSlider.addChangeListener(new SliderValueChangeHandler(rightBoundValueLabel));
        rightBoundValueLabel.setText(convertMillisToFormattedTime(totalTimeMillis));

        updateOptions();

        saveButton.addActionListener(new SaveAction());
        cancelButton.addActionListener(new CloseAction());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(leftBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(leftBoundSlider, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(leftBoundValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(rightBoundLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        add(rightBoundSlider, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        add(rightBoundValueLabel, gbc);

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
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(buttonPanel, gbc);
    }

    private void setupSizeAndLocation() {
        pack();
        setMinimumSize(getSize());
        WindowUtils.setLocationToCenter(this);
    }

    public void close() {
        ApplicationContext.getInstance().getVideoRecorder().getOptions().setActive(false);
        setVisible(false);
        dispose();
    }

    private final class SaveAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext context = ApplicationContext.getInstance();

            File targetFile = null;
                    JFileChooser fileChooser = FileChooserUtils.getVideoFileChooser();
            int option = fileChooser.showSaveDialog(context.getMainFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                targetFile = fileChooser.getSelectedFile();
            }

            if (targetFile == null) {
                return;
            }

            updateOptions();

            if (context.getVideoRecorder().saveFragment(targetFile)) {
                DialogUtils.showInfoMessage("videoFragmentSaved");
            } else {
                DialogUtils.showErrorMessage("videoFragmentSavingFailed");
            }
            close();
        }
    }

    private void updateOptions() {
        int leftBoundMillis = leftBoundSlider.getValue();
        int rightBoundMillis = rightBoundSlider.getValue();

        if (leftBoundMillis >= rightBoundMillis) {
            return;
        }

        ApplicationContext context = ApplicationContext.getInstance();
        VideoRecordOptions options = context.getVideoRecorder().getOptions();
        options.setLeftBoundNanos(leftBoundMillis * 1_000);
        options.setRightBoundNanos(rightBoundMillis * 1_000);

        context.repaintTimeline();
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    private final class SliderValueChangeHandler implements ChangeListener {
        private final JLabel valueLabel;

        public SliderValueChangeHandler(JLabel valueLabel) {
            this.valueLabel = valueLabel;
        }

        @Override public void stateChanged(ChangeEvent event) {
            JSlider slider = (JSlider) event.getSource();
            valueLabel.setText(convertMillisToFormattedTime(slider.getValue()));
            updateOptions();
        }
    }

    private static String convertMillisToFormattedTime(int timeMillis) {
        return LocalTime.fromMillisOfDay(timeMillis).toString("HH:mm:ss");
    }
}
