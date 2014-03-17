package by.bsu.fpmi.vet.ui.composite;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.ui.component.TitledPanel;
import com.google.common.base.Strings;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class NotesPanel extends TitledPanel {
    private final JTextArea notesTextArea = new JTextArea(4, 1);
    private final JButton viewFramesCapturedButton = new JButton(getMessage("ui.panel.notes.button.viewFrameCaptured"));
    private final JButton generateReportButton = new JButton(getMessage("ui.panel.notes.button.generateReport"));
    private final JButton saveFrameButton = new JButton(getMessage("ui.panel.notes.button.saveFrame"));

    public NotesPanel() {
        super(getMessage("ui.panel.notes.title"));
        configureComponents();
        arrangeComponents();
    }

    private void arrangeComponents() {
        JPanel content = getContent();
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(new JScrollPane(notesTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(viewFramesCapturedButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(generateReportButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(saveFrameButton, gbc);
    }

    private void configureComponents() {
        saveFrameButton.addActionListener(new SaveFrameAction());
    }

    private final class SaveFrameAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            Snapshot snapshot = ApplicationContext.getInstance().getReportGenerator().getLastSnapshot();
            String notes = notesTextArea.getText();
            if (snapshot != null && !Strings.isNullOrEmpty(notes)) {
                snapshot.setNotes(notes);
                notesTextArea.setText("");
            }
        }
    }
}
