package by.bsu.fpmi.vet.ui.composite;

import by.bsu.fpmi.vet.ui.component.TitledPanel;
import by.bsu.fpmi.vet.video.VideoDetails;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.joda.time.DateTime;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class VideoDetailsPanel extends TitledPanel {
    private static final String NO_VALUE = "-";
    private static final String DATE_TIME_FORMAT = getMessage("format.dateTime.fileAttribute");

    private final JLabel fileNameLabel = new JLabel(getMessage("ui.panel.videoDetails.label.fileName"));
    private final JLabel locationLabel = new JLabel(getMessage("ui.panel.videoDetails.label.location"));
    private final JLabel creationTimeLabel = new JLabel(getMessage("ui.panel.videoDetails.label.creationTime"));
    private final JLabel lastAccessTimeLabel = new JLabel(getMessage("ui.panel.videoDetails.label.lastAccessTime"));
    private final JLabel lastModifiedTimeLabel = new JLabel(getMessage("ui.panel.videoDetails.label.modified"));
    private final JLabel md5Label = new JLabel(getMessage("ui.panel.videoDetails.label.MD5"));

    private final JLabel fileNameValueLabel = new JLabel();
    private final JLabel locationValueLabel = new JLabel();
    private final JLabel creationTimeValueLabel = new JLabel();
    private final JLabel lastAccessTimeValueLabel = new JLabel();
    private final JLabel lastModifiedTimeValueLabel = new JLabel();
    private final JLabel md5ValueLabel = new JLabel();

    public VideoDetailsPanel() {
        super(getMessage("ui.panel.videoDetails.title"));
        arrangeComponents();
    }

    public void update(VideoDetails videoDetails) {
        File file = videoDetails.getSourceFile();
        fileNameValueLabel.setText(file.getName());
        locationValueLabel.setText(file.getParentFile().getAbsolutePath());

        Path filePath = file.toPath();
        try {
            BasicFileAttributes fileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            creationTimeValueLabel.setText(getDateTime(fileAttributes.creationTime()));
            lastAccessTimeValueLabel.setText(getDateTime(fileAttributes.lastAccessTime()));
            lastModifiedTimeValueLabel.setText(getDateTime(fileAttributes.lastModifiedTime()));
        } catch (Exception ignored) {
            creationTimeValueLabel.setText(NO_VALUE);
            lastAccessTimeValueLabel.setText(NO_VALUE);
            lastModifiedTimeValueLabel.setText(NO_VALUE);
        }

        try {
            HashCode hashCode = com.google.common.io.Files.hash(file, Hashing.md5());
            md5ValueLabel.setText(hashCode.toString());
        } catch (IOException e) {
            md5ValueLabel.setText(NO_VALUE);
        }
    }

    private String getDateTime(FileTime fileTime) {
        return fileTime == null ? NO_VALUE : new DateTime(fileTime.toMillis()).toString(DATE_TIME_FORMAT);
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
        content.add(creationTimeLabel, gbc);

        gbc.gridy = 3;
        content.add(lastAccessTimeLabel, gbc);

        gbc.gridy = 4;
        content.add(lastModifiedTimeLabel, gbc);

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
        content.add(creationTimeValueLabel, gbc);

        gbc.gridy = 3;
        content.add(lastAccessTimeValueLabel, gbc);

        gbc.gridy = 4;
        content.add(lastModifiedTimeValueLabel, gbc);

        gbc.gridy = 5;
        content.add(md5ValueLabel, gbc);
    }
}
