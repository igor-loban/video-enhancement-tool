package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.video.VideoDetails;
import org.slf4j.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static by.bsu.fpmi.vet.util.MessageUtils.format;
import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;
import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayerComponent extends JPanel {
    private static final Logger LOGGER = getLogger(VideoPlayerComponent.class);

    private final VideoPlayer videoPlayer;

    private final JPanel controlPanel = new JPanel();
    private final JLabel positionLabel =
            new JLabel(format("ui.panel.videoPlayer.label.currentPosition", "00:00:00", "00:00:00"));
    private final JSlider positionSlider = new JSlider(0, 100, 0);

    private final JButton rewindButton = new JButton(getMessage("ui.panel.videoPlayer.button.rewind"));
    private final JButton playButton = new JButton(getMessage("ui.panel.videoPlayer.button.play"));
    private final JButton pauseButton = new JButton(getMessage("ui.panel.videoPlayer.button.pause"));
    private final JButton stopButton = new JButton(getMessage("ui.panel.videoPlayer.button.stop"));
    private final JButton forwardButton = new JButton(getMessage("ui.panel.videoPlayer.button.forward"));

    private final JButton speedMinusButton = new JButton(getMessage("ui.panel.videoPlayer.button.speedMinus"));
    private final JButton speedPlusButton = new JButton(getMessage("ui.panel.videoPlayer.button.speedPlus"));
    private final JLabel speedLabel = new JLabel("1x");

    private final JLabel volumeLabel = new JLabel(getMessage("ui.panel.videoPlayer.label.volume"));
    private final JCheckBox muteSoundCheckBox = new JCheckBox(getMessage("ui.panel.videoPlayer.button.muteSound"));
    private final JSlider volumeSlider = new JSlider(0, 100, 50);

    private final JButton captureFrameButton = new JButton(getMessage("ui.panel.videoPlayer.button.captureFrame"));

    private VideoDetails videoDetails;
    private boolean firePositionChanged;

    public VideoPlayerComponent(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;

        configureComponents();
        setupControlActions();
        arrangeControlPanel();

        setLayout(new BorderLayout());
        add(videoPlayer, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void configureComponents() {
        positionSlider.setUI(new ColoredSliderUI(positionSlider, null));

        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(5);
    }

    private void setupControlActions() {
        positionSlider.addChangeListener(new PositionChangedHandler());

        playButton.addActionListener(new PlayAction());
        pauseButton.addActionListener(new PauseAction());
        stopButton.addActionListener(new StopAction());

        // TODO: implement
        rewindButton.setEnabled(false);
        forwardButton.setEnabled(false);
        speedPlusButton.setEnabled(false);
        speedMinusButton.setEnabled(false);
        volumeSlider.setEnabled(false);
        muteSoundCheckBox.setEnabled(false);

        captureFrameButton.addActionListener(new CaptureFrameAction());
    }

    private void arrangeControlPanel() {
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        controlPanel.add(positionSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        controlPanel.add(positionLabel, gbc);

        JPanel playButtonPanel = new JPanel();
        playButtonPanel.add(rewindButton);
        playButtonPanel.add(playButton);
        playButtonPanel.add(pauseButton);
        playButtonPanel.add(stopButton);
        playButtonPanel.add(forwardButton);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 5);
        controlPanel.add(playButtonPanel, gbc);

        JPanel speedControlPanel = new JPanel();
        speedControlPanel.add(speedMinusButton);
        speedControlPanel.add(speedLabel);
        speedControlPanel.add(speedPlusButton);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 5, 2, 5);
        controlPanel.add(speedControlPanel, gbc);

        JPanel soundControlPanel = new JPanel();
        soundControlPanel.add(volumeLabel);
        soundControlPanel.add(volumeSlider);
        soundControlPanel.add(muteSoundCheckBox);
        gbc.gridx = 2;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.CENTER;
        controlPanel.add(soundControlPanel, gbc);

        JPanel captureButtonPanel = new JPanel();
        captureButtonPanel.add(captureFrameButton);
        gbc.gridx = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(2, 5, 2, 0);
        controlPanel.add(captureButtonPanel, gbc);
    }

    public void init(VideoDetails videoDetails) {
        this.videoDetails = videoDetails;
        positionSlider.setMinimum(0);
        positionSlider.setMaximum((int) videoDetails.getTotalTime());
        updateTimeline(0);
    }

    public void initColoredSlider() {
        positionSlider.setUI(new ColoredSliderUI(positionSlider, videoDetails.getMetaInfo()));
    }

    public void updateTimeline(long newTime) {
        firePositionChanged = false;
        positionSlider.setValue((int) newTime);
        firePositionChanged = true;
    }

    private final class PlayAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            LOGGER.debug("play video");
            videoPlayer.play();
        }
    }

    private final class PauseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            LOGGER.debug("pause video");
            videoPlayer.pause();
        }
    }

    private final class StopAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            LOGGER.debug("stop video");
            videoPlayer.stop();
        }
    }

    private final class CaptureFrameAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            LOGGER.debug("capture frame");
            ApplicationContext context = ApplicationContext.getInstance();
            Snapshot snapshot = videoPlayer.captureFrame();
            context.getReportGenerator().addSnapshot(snapshot);
            context.getMainFrame().setFocusToNotes();
        }
    }

    private final class PositionChangedHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            JSlider positionSlider = (JSlider) e.getSource();
            if (positionSlider.getValueIsAdjusting() && firePositionChanged) {
                videoPlayer.setTime(positionSlider.getValue());
            }
        }
    }
}
