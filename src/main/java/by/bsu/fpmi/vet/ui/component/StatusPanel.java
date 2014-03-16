package by.bsu.fpmi.vet.ui.component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public final class StatusPanel extends JPanel {
    private final JLabel stateLabel = new JLabel();

    public StatusPanel() {
        setBackground(Color.CYAN);
        stateLabel.setText("Status Panel");
        add(stateLabel);
    }
}
