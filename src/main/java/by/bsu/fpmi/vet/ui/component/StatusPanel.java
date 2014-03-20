package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.application.Status;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class StatusPanel extends JPanel {
    private static final String PREFIX = "status.";

    private final JLabel statusLabel = new JLabel();

    public StatusPanel() {
        setBorder(BorderFactory.createEtchedBorder());
        setStatus(Status.DEFAULT);
        add(statusLabel);
    }

    public void setStatus(Status status) {
        String key = status.toString().toLowerCase();
        statusLabel.setText(getMessage(PREFIX + key));
        int rgb = Integer.parseInt(getMessage(PREFIX + key + ".color"), 16);
        setBackground(new Color(rgb));
        updateUI();
    }
}
