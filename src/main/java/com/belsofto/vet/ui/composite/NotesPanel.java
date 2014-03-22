package com.belsofto.vet.ui.composite;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.report.Snapshot;
import com.belsofto.vet.ui.action.Actions;
import com.belsofto.vet.ui.component.TitledPanel;
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

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class NotesPanel extends TitledPanel {
    private final JTextArea notesTextArea = new JTextArea(4, 1);
    private final JButton viewFramesCapturedButton = new JButton(getMessage("ui.panel.notes.button.viewFrameCaptured"));
    private final JButton generateReportButton = new JButton(getMessage("ui.panel.notes.button.generateReport"));
    private final JButton saveNotesButton = new JButton(getMessage("ui.panel.notes.button.saveNotes"));

    public NotesPanel() {
        super(getMessage("ui.panel.notes.title"));
        configureComponents();
        arrangeComponents();
    }

    private void arrangeComponents() {
        JPanel content = getContent();
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        content.add(new JScrollPane(notesTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 5, 2);
        content.add(saveNotesButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 5, 2);
        content.add(generateReportButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.33;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 2, 5, 5);
        content.add(viewFramesCapturedButton, gbc);
    }

    private void configureComponents() {
        saveNotesButton.addActionListener(new SaveNotesAction());
        generateReportButton.addActionListener(Actions.GENERATE_REPORT.get());
        viewFramesCapturedButton.addActionListener(Actions.VIEW_FRAMES_CAPTURED.get());
    }

    private final class SaveNotesAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            Snapshot snapshot = ApplicationContext.getInstance().getReportGenerator().getLastSnapshot();
            String notes = notesTextArea.getText();
            if (snapshot != null && !Strings.isNullOrEmpty(notes)) {
                snapshot.setNotes(notes);
                notesTextArea.setText("");
            }
        }
    }

    public JTextArea getNotesTextArea() {
        return notesTextArea;
    }
}
