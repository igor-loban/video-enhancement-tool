package com.belsofto.vet.ui.dialog;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.UserLogger;
import com.belsofto.vet.ui.util.WindowUtils;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public final class LogDialog extends JDialog {
    private static final int MINIMUM_HEIGHT = 320;

    private final JTextArea logTextArea = new JTextArea(10, 60);

    private final JButton saveButton = new JButton(getMessage("ui.dialog.log.button.save"));
    private final JButton closeButton = new JButton(getMessage("ui.dialog.log.button.close"));

    public LogDialog() {
        super(ApplicationContext.getInstance().getMainFrame(), getMessage("ui.dialog.log.title"), true);
        configureComponents();
        arrangeComponents();
        setupSizeAndLocation();
    }

    private void configureComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        logTextArea.setEditable(false);
        logTextArea.setText(UserLogger.getLog());

        saveButton.addActionListener(new SaveAction());
        closeButton.addActionListener(new CloseAction());
    }

    private void arrangeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JScrollPane(logTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 2);
        add(saveButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 2, 5, 5);
        add(closeButton, gbc);
    }

    private void setupSizeAndLocation() {
        pack();
        setMinimumSize(new Dimension(getWidth(), MINIMUM_HEIGHT));
        WindowUtils.setLocationToCenter(this);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private final class CloseAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    private final class SaveAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            if (UserLogger.save()) {
                close();
                DialogUtils.showInfoMessage("logSavedSuccessfully");
            } else {
                DialogUtils.showErrorMessage("logSavingFailed");
            }
        }
    }
}
