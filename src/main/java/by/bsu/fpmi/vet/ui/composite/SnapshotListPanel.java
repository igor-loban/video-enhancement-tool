package by.bsu.fpmi.vet.ui.composite;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.report.Snapshot;

import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public final class SnapshotListPanel extends JPanel {
    public SnapshotListPanel() {
        arrangeComponents();
    }

    private void arrangeComponents() {
        removeAll();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        List<Snapshot> snapshots = ApplicationContext.getInstance().getReportGenerator().getSnapshots();
        for (int i = 0; i < snapshots.size(); i++) {
            gbc.gridy = i;
            add(new SnapshotPanel(i + 1, snapshots.get(i), this), gbc);
        }

        updateUI();
    }

    public void removeSnapshot(Snapshot snapshot) {
        ApplicationContext.getInstance().getReportGenerator().remove(snapshot);
        arrangeComponents();
    }

    public void removeAllSnapshots() {
        ApplicationContext.getInstance().getReportGenerator().removeAllSnapshots();
        arrangeComponents();
    }
}
