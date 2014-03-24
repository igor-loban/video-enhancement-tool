package com.belsofto.vet.ui.composite;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.ui.component.TitledPanel;
import com.belsofto.vet.ui.dialog.DialogUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class ControlPanel extends TitledPanel {
    private final JButton enhancementButton =
            new JButton(getMessage("ui.panel.control.button.videoImageEnhancementControls"));
    private final JButton playMovementButton =
            new JButton(getMessage("ui.panel.control.button.playAllFramesWithMovement"));
    private final JButton playSoundButton = new JButton(getMessage("ui.panel.control.button.playAllFramesWithSound"));

    public ControlPanel() {
        super(getMessage("ui.panel.control.title"));
        configureComponents();
        arrangeComponents();
    }

    private void configureComponents() {
        enhancementButton.setEnabled(false);

        playMovementButton.addActionListener(new PlayAllMovementsAction());
        playSoundButton.addActionListener(new PlayAllSoundsAction());
    }

    private void arrangeComponents() {
        JPanel content = getContent();
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(enhancementButton, gbc);

        gbc.gridy = 1;
        content.add(playMovementButton, gbc);

        gbc.gridy = 2;
        content.add(playSoundButton, gbc);
    }

    private final class PlayAllMovementsAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext context = ApplicationContext.getInstance();
            VideoDetails videoDetails = context.getVideoDetails();
            if (videoDetails == null || videoDetails.getMotionDescriptors().isEmpty()) {
                DialogUtils.showErrorMessage("noMovementDescriptorsFound");
                return;
            }
            context.playAllMovements();
        }
    }

    private final class PlayAllSoundsAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext context = ApplicationContext.getInstance();
            VideoDetails videoDetails = context.getVideoDetails();
            if (videoDetails == null || videoDetails.getSoundDescriptors().isEmpty()) {
                DialogUtils.showErrorMessage("noSoundDescriptorsFound");
                return;
            }
            context.playAllSounds();
        }
    }
}
