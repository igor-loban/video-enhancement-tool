package com.belsofto.vet.ui.component;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.belsofto.vet.detection.motion.MotionDescriptor;
import com.belsofto.vet.detection.motion.MotionThreshold;
import com.belsofto.vet.detection.sound.SoundDescriptor;
import com.belsofto.vet.detection.sound.SoundThreshold;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.dialog.DialogUtils;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.util.List;
import java.util.ListIterator;

import static com.belsofto.vet.util.MessageUtils.format;
import static com.belsofto.vet.util.MessageUtils.getMessage;
import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayerPanel extends JPanel {
    private static final Logger LOGGER = getLogger(VideoPlayerPanel.class);

    private final VideoPlayer videoPlayer;

    private final JPanel controlPanel = new JPanel();
    private final JLabel positionLabel =
            new JLabel(format("ui.panel.videoPlayer.label.currentPosition", "00:00:00", "00:00:00"));
    private final JSlider positionSlider = new JSlider(0, 100, 0);
    private final ColoredSliderUI coloredSliderUI = new ColoredSliderUI(positionSlider);

    private final JButton rewindButton = new JButton(getMessage("ui.panel.videoPlayer.button.rewind"));
    private final JButton playButton = new JButton(getMessage("ui.panel.videoPlayer.button.play"));
    private final JButton pauseButton = new JButton(getMessage("ui.panel.videoPlayer.button.pause"));
    private final JButton stopButton = new JButton(getMessage("ui.panel.videoPlayer.button.stop"));
    private final JButton forwardButton = new JButton(getMessage("ui.panel.videoPlayer.button.forward"));

    private final JButton speedMinusButton = new JButton(getMessage("ui.panel.videoPlayer.button.speedMinus"));
    private final JButton speedPlusButton = new JButton(getMessage("ui.panel.videoPlayer.button.speedPlus"));
    private final JLabel speedLabel = new JLabel("x1.000");

    private final JLabel volumeLabel = new JLabel(getMessage("ui.panel.videoPlayer.label.volume"));
    private final JCheckBox muteSoundCheckBox = new JCheckBox(getMessage("ui.panel.videoPlayer.button.muteSound"));
    private final JSlider volumeSlider = new JSlider(0, 100);

    private final JButton captureFrameButton = new JButton(getMessage("ui.panel.videoPlayer.button.captureFrame"));

    private VideoDetails videoDetails;
    private boolean firePositionChanged;
    private boolean forwardEnabled;
    private boolean rewindEnabled;

    public VideoPlayerPanel(VideoPlayer videoPlayer) {
        this.videoPlayer = videoPlayer;

        configureComponents();
        setupControlActions();
        arrangeControlPanel();
        arrangeComponents();
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(videoPlayer, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(controlPanel, gbc);
    }

    private void configureComponents() {
        videoPlayer.setSpeedLabel(speedLabel);

        positionSlider.setUI(coloredSliderUI);

        volumeSlider.setPaintTicks(true);
        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setMinorTickSpacing(5);
    }

    private void setupControlActions() {
        positionSlider.addChangeListener(new PositionChangedHandler());

        rewindButton.addActionListener(new RewindAction());
        playButton.addActionListener(new PlayAction());
        pauseButton.addActionListener(new PauseAction());
        stopButton.addActionListener(new StopAction());
        forwardButton.addActionListener(new ForwardAction());

        speedPlusButton.addActionListener(new SpeedPlusAction());
        speedMinusButton.addActionListener(new SpeedMinusAction());

        volumeSlider.addChangeListener(new VolumeChangedHandler());
        muteSoundCheckBox.addChangeListener(new MuteChangedHandler());
        videoPlayer.setVolume(50);

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

        JPanel playButtonPanel = getPlayButtonPanel();
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

        gbc.gridx = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(2, 5, 2, 0);
        controlPanel.add(captureFrameButton, gbc);
    }

    private JPanel getPlayButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridy = 0;
        gbc2.insets = new Insets(0, 0, 0, 2);
        panel.add(rewindButton, gbc2);
        gbc2.insets = new Insets(0, 2, 0, 2);
        panel.add(playButton, gbc2);
        panel.add(pauseButton, gbc2);
        panel.add(stopButton, gbc2);
        panel.add(forwardButton, gbc2);
        return panel;
    }

    public void init(VideoDetails videoDetails) {
        this.videoDetails = videoDetails;
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(videoDetails.getTotalTimeMillis());
        updateTimeline(0);
        initColoredSlider();
    }

    public void initColoredSlider() {
        coloredSliderUI.setVideoDetails(videoDetails);
        positionSlider.repaint();
    }

    public void updateTimeline(long newTime) {
        firePositionChanged = false;
        positionSlider.setValue((int) newTime);
        firePositionChanged = true;
    }

    public void playAllMovements() {
        videoPlayer.stop();
        videoPlayer.addMediaPlayerEventListener(new PlayAllMovementsAction());
        videoPlayer.play();
        ApplicationContext.getInstance().setStatus(Status.PLAYING_ALL_MOVEMENT);
    }

    public void playAllSounds() {
        videoPlayer.stop();
        videoPlayer.addMediaPlayerEventListener(new PlayAllSoundsAction());
        videoPlayer.play();
        ApplicationContext.getInstance().setStatus(Status.PLAYING_ALL_SOUND);
    }

    public void repaintTimeline() {
        positionSlider.repaint();
    }

    private final class PlayAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            LOGGER.debug("play media");
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            videoPlayer.play();
        }
    }

    private final class PauseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            LOGGER.debug("pause media");
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            videoPlayer.pause();
        }
    }

    private final class StopAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            LOGGER.debug("stop media");
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            videoPlayer.stop();
            validate();
        }
    }

    private final class PositionChangedHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            JSlider positionSlider = (JSlider) e.getSource();
            int newTime = positionSlider.getValue();
            if (firePositionChanged) {
                videoPlayer.setTime(newTime);
            }
            updatePositionLabel(newTime, positionSlider.getMaximum());
        }

        private void updatePositionLabel(int newTime, int totalTime) {
            // TODO: optimize total time computing
            positionLabel.setText(format("ui.panel.videoPlayer.label.currentPosition", timeToString(newTime),
                    timeToString(totalTime)));
        }

        private String timeToString(int time) {
            return LocalTime.fromMillisOfDay(time).toString("HH:mm:ss");
        }
    }

    private void checkAndDisableForwarding() {
        if (forwardEnabled || rewindEnabled) {
            videoPlayer.setRate(1.0F);
            forwardEnabled = false;
            rewindEnabled = false;
        }
    }

    private final class SpeedPlusAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            float rate = videoPlayer.getRate();
            float newRate = rate * 2.0F;
            if (rate >= 8.0F) {
                videoPlayer.setRate(8.0F);
                return;
            }
            int result = videoPlayer.setRate(newRate);
            if (result == 0) {
                LOGGER.debug("speed increased from {} to {}", rate, newRate);
            }
        }
    }

    private final class SpeedMinusAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            float rate = videoPlayer.getRate();
            float newRate = rate / 2.0F;
            if (rate <= 0.250F) {
                videoPlayer.setRate(0.250F);
                return;
            }
            int result = videoPlayer.setRate(newRate);
            if (result == 0) {
                LOGGER.debug("speed decreased from {} to {}", rate, newRate);
            }
        }
    }

    private final class CaptureFrameAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            checkAndDisableForwarding();

            LOGGER.debug("capture frame");
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            ApplicationContext context = ApplicationContext.getInstance();
            Snapshot snapshot = videoPlayer.captureFrame();
            if (snapshot != null) {
                context.getReportGenerator().addSnapshot(snapshot);
                context.getMainFrame().setFocusToNotes();
            }
        }
    }

    private final class VolumeChangedHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            JSlider volumeSlider = (JSlider) e.getSource();
            int volume = volumeSlider.getValue();
            videoPlayer.setVolume(volume);
            muteSoundCheckBox.setSelected(volume == 0);
        }
    }

    private final class MuteChangedHandler implements ChangeListener {
        @Override public void stateChanged(ChangeEvent e) {
            JCheckBox muteSoundCheckBox = (JCheckBox) e.getSource();
            videoPlayer.mute(muteSoundCheckBox.isSelected());
        }
    }

    private final class ForwardAction implements ActionListener {
        private ForwardFactor forwardFactor = ForwardFactor.NONE;

        @Override public void actionPerformed(ActionEvent e) {
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            if (!videoPlayer.isPlaying()) {
                videoPlayer.play();
            }
            if (rewindEnabled) {
                rewindEnabled = false;
            }

            if (!forwardEnabled) {
                forwardFactor = ForwardFactor.X0_5;
            } else {
                if (forwardFactor != ForwardFactor.X10) {
                    forwardFactor = forwardFactor.next();
                }
            }

            LOGGER.debug("forward with factor {}", forwardFactor);

            videoPlayer.skip(forwardFactor.timeSkip());
            videoPlayer.setRate(forwardFactor.rate());

            forwardEnabled = true;
        }
    }

    private final class RewindAction implements ActionListener {
        private ForwardFactor forwardFactor = ForwardFactor.NONE;

        @Override public void actionPerformed(ActionEvent e) {
            if (videoDetails == null) {
                DialogUtils.showErrorMessage("noVideoLoaded");
                return;
            }
            if (!videoPlayer.isPlaying()) {
                videoPlayer.play();
            }
            if (forwardEnabled) {
                forwardEnabled = false;
            }

            if (!rewindEnabled) {
                forwardFactor = ForwardFactor.X0_5;
            } else {
                if (forwardFactor != ForwardFactor.X10) {
                    forwardFactor = forwardFactor.next();
                }
            }

            LOGGER.debug("rewind with factor {}", forwardFactor);

            videoPlayer.skip(-forwardFactor.timeSkip());
            videoPlayer.setRate(1.0F);

            rewindEnabled = true;
        }
    }

    private static enum ForwardFactor {
        X10(null, 10_000, 10F, "x10"),
        X5(X10, 5_000, 5F, "x5"),
        X2(X5, 2_000, 2F, "x2"),
        X1(X2, 1_000, 1F, "x1"),
        X0_5(X1, 500, 0.5F, "x0.5"),
        NONE(X0_5, -1, 0, "<none>");

        private final ForwardFactor next;
        private final int timeSkipMillis;
        private final float rate;
        private final String label;

        private ForwardFactor(ForwardFactor next, int timeSkipMillis, float rate, String label) {
            this.next = next;
            this.timeSkipMillis = timeSkipMillis;
            this.rate = rate;
            this.label = label;
        }

        public ForwardFactor next() {
            return next;
        }

        public int timeSkip() {
            return timeSkipMillis;
        }

        public float rate() {
            return rate;
        }

        @Override public String toString() {
            return label;
        }
    }

    private final class PlayAllMovementsAction extends MediaPlayerEventAdapter {
        @Override public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
            if (ApplicationContext.getInstance().getStatus() != Status.PLAYING_ALL_MOVEMENT) {
                videoPlayer.removeMediaPlayerEventListener(this);
                return;
            }

            List<MotionDescriptor> descriptors = videoDetails.getMotionDescriptors();
            ListIterator<MotionDescriptor> iterator = descriptors.listIterator(descriptors.size());
            while (iterator.hasPrevious()) {
                MotionDescriptor descriptor = iterator.previous();
                if (newTime > descriptor.getTime()) {
                    if (descriptor.getMotionThreshold() == MotionThreshold.NO) {
                        while (iterator.hasNext()) {
                            MotionDescriptor descriptorWithMotion = iterator.next();
                            if (descriptorWithMotion.getMotionThreshold() != MotionThreshold.NO) {
                                videoPlayer.setTime(descriptorWithMotion.getTime() + 3);
                                return;
                            }
                        }
                        videoPlayer.stop();
                    }
                    return;
                }
            }
        }
    }

    private final class PlayAllSoundsAction extends MediaPlayerEventAdapter {
        @Override public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
            if (ApplicationContext.getInstance().getStatus() != Status.PLAYING_ALL_SOUND) {
                videoPlayer.removeMediaPlayerEventListener(this);
                return;
            }

            List<SoundDescriptor> descriptors = videoDetails.getSoundDescriptors();
            ListIterator<SoundDescriptor> iterator = descriptors.listIterator(descriptors.size());
            while (iterator.hasPrevious()) {
                SoundDescriptor descriptor = iterator.previous();
                if (newTime > descriptor.getTime()) {
                    if (descriptor.getSoundThreshold() == SoundThreshold.NOISE) {
                        while (iterator.hasNext()) {
                            SoundDescriptor descriptorWithSound = iterator.next();
                            if (descriptorWithSound.getSoundThreshold() != SoundThreshold.NOISE) {
                                videoPlayer.setTime(descriptorWithSound.getTime() + 3);
                                return;
                            }
                        }
                        videoPlayer.stop();
                    }
                    return;
                }
            }
        }
    }
}
