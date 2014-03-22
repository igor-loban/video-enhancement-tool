package com.belsofto.vet.ui.component;

import com.belsofto.vet.application.Status;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

import static com.belsofto.vet.util.MessageUtils.format;
import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class StatusPanel extends JPanel {
    private static final String PREFIX = "status.";

    private final JLabel statusLabel = new JLabel();

    public StatusPanel() {
        setBorder(BorderFactory.createEtchedBorder());
        setStatus(Status.DEFAULT);
        add(statusLabel);
    }

    public void setStatus(Status status, Object... params) {
        String key = status.toString().toLowerCase();
        statusLabel.setText(format(PREFIX + key, params));
        int rgb = Integer.parseInt(getMessage(PREFIX + key + ".color"), 16);
        setBackground(new Color(rgb));
        repaint();
    }
}
