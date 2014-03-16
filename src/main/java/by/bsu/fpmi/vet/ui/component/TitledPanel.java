package by.bsu.fpmi.vet.ui.component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

public final class TitledPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JPanel contentPanel;

    public TitledPanel(String title, JPanel contentPanel) {
        titleLabel.setText(title);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.GRAY);

        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
    }
}
