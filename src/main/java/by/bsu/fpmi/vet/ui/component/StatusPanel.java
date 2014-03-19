package by.bsu.fpmi.vet.ui.component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public final class StatusPanel extends JPanel {
    private final JLabel stateLabel = new JLabel();

    public StatusPanel() {
        setBorder(BorderFactory.createEtchedBorder());
        setBackground(Color.CYAN);
        stateLabel.setText("Status Panel (TBD)");
        add(stateLabel);
    }
}
