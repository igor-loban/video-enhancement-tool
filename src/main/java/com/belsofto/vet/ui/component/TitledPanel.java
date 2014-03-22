package com.belsofto.vet.ui.component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BorderLayout;

public class TitledPanel extends JPanel {
    private final JPanel content = new JPanel();

    public TitledPanel(String title) {
        Border border = BorderFactory.createEtchedBorder();
        setBorder(BorderFactory.createTitledBorder(border, title));
        
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return content;
    }
}
