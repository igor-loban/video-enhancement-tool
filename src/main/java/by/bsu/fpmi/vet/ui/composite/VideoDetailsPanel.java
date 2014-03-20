package by.bsu.fpmi.vet.ui.composite;

import by.bsu.fpmi.vet.ui.component.TitledPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class VideoDetailsPanel extends TitledPanel {
    private final JLabel fileNameLabel = new JLabel(getMessage("ui.panel.videoDetails.label.fileName"));
    private final JLabel locationLabel = new JLabel(getMessage("ui.panel.videoDetails.label.location"));
    private final JLabel createdLabel = new JLabel(getMessage("ui.panel.videoDetails.label.created"));
    private final JLabel accessedLabel = new JLabel(getMessage("ui.panel.videoDetails.label.accessed"));
    private final JLabel modifiedLabel = new JLabel(getMessage("ui.panel.videoDetails.label.modified"));
    private final JLabel md5Label = new JLabel(getMessage("ui.panel.videoDetails.label.MD5"));

    private final JLabel fileNameValueLabel = new JLabel("TBD");
    private final JLabel locationValueLabel = new JLabel("TBD");
    private final JLabel createdValueLabel = new JLabel("TBD");
    private final JLabel accessedValueLabel = new JLabel("TBD");
    private final JLabel modifiedValueLabel = new JLabel("TBD");
    private final JLabel md5ValueLabel = new JLabel("TBD");

    public VideoDetailsPanel() {
        super(getMessage("ui.panel.videoDetails.title"));
        arrangeComponents();
    }

    private void arrangeComponents() {
        JPanel content = getContent();
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        content.add(fileNameLabel, gbc);

        gbc.gridy = 1;
        content.add(locationLabel, gbc);

        gbc.gridy = 2;
        content.add(createdLabel, gbc);

        gbc.gridy = 3;
        content.add(accessedLabel, gbc);

        gbc.gridy = 4;
        content.add(modifiedLabel, gbc);

        gbc.gridy = 5;
        content.add(md5Label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(fileNameValueLabel, gbc);

        gbc.gridy = 1;
        content.add(locationValueLabel, gbc);

        gbc.gridy = 2;
        content.add(createdValueLabel, gbc);

        gbc.gridy = 3;
        content.add(accessedValueLabel, gbc);

        gbc.gridy = 4;
        content.add(modifiedValueLabel, gbc);

        gbc.gridy = 5;
        content.add(md5ValueLabel, gbc);
    }
}
