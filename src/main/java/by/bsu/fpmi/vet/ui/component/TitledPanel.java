package by.bsu.fpmi.vet.ui.component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

public class TitledPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JPanel content = new JPanel();

    public TitledPanel(String title) {
        titleLabel.setText(title);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.GRAY);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public JPanel getContent() {
        return content;
    }
}
