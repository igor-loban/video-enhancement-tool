package by.bsu.fpmi.vet.ui.composite;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.Snapshot;
import com.google.common.base.Strings;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class SnapshotPanel extends JPanel {
    private final JLabel pictureLabel = new JLabel();
    private final JLabel orderNumberLabel = new JLabel(getMessage("ui.panel.snapshot.label.orderNumber"));
    private final JLabel orderNumberValueLabel = new JLabel();
    private final JLabel timeIndexLabel = new JLabel(getMessage("ui.panel.snapshot.label.timeIndex"));
    private final JLabel timeIndexValueLabel = new JLabel();
    private final JLabel notesLabel = new JLabel(getMessage("ui.panel.snapshot.label.notes"));
    private final JTextArea notesTextArea = new JTextArea(4, 1);
    private final JButton saveNotesButton = new JButton(getMessage("ui.panel.snapshot.button.saveNotes"));
    private final JButton goToFrameInVideoButton = new JButton(getMessage("ui.panel.snapshot.button.goToFrameInVideo"));
    private final JButton deleteFrameGrabButton = new JButton(getMessage("ui.panel.snapshot.button.deleteFrameGrab"));

    private final int orderNumber;
    private final Snapshot snapshot;
    private final SnapshotListPanel snapshotListPanel;

    public SnapshotPanel(int orderNumber, Snapshot snapshot, SnapshotListPanel snapshotListPanel) {
        this.orderNumber = orderNumber;
        this.snapshot = snapshot;
        this.snapshotListPanel = snapshotListPanel;
        configureComponents();
        arrangeComponents();
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(pictureLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 2, 5);
        add(orderNumberLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 2, 5);
        add(orderNumberValueLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);
        add(timeIndexLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);
        add(timeIndexValueLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);
        add(notesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 5, 2, 5);
        add(new JScrollPane(notesTextArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 5, 5, 2);
        add(saveNotesButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 5, 2);
        add(goToFrameInVideoButton, gbc);

        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 5, 5);
        add(deleteFrameGrabButton, gbc);
    }

    private void configureComponents() {
        setBorder(BorderFactory.createEtchedBorder());

        pictureLabel.setIcon(getScaledIcon(snapshot.getImage()));
        orderNumberValueLabel.setText(String.valueOf(orderNumber));
        timeIndexValueLabel.setText(String.valueOf(snapshot.getTime()));
        notesTextArea.setText(snapshot.getNotes());

        saveNotesButton.addActionListener(new SaveNotesAction());
        goToFrameInVideoButton.addActionListener(new GoToFrameInVideoAction());
        deleteFrameGrabButton.addActionListener(new DeleteFrameGrabAction());
    }

    private Icon getScaledIcon(Image image) {
        return new ImageIcon(image.getScaledInstance(-1, 200, Image.SCALE_SMOOTH));
    }

    private final class SaveNotesAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            String notes = notesTextArea.getText();
            if (snapshot != null && !Strings.isNullOrEmpty(notes)) {
                snapshot.setNotes(notes);
            }
        }
    }

    private final class GoToFrameInVideoAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            ApplicationContext.getInstance().moveToSnapshot(snapshot);
        }
    }

    private final class DeleteFrameGrabAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            snapshotListPanel.removeSnapshot(snapshot);
        }
    }
}
