package by.bsu.fpmi.vet.ui.action;

import javax.swing.Action;

public enum Actions implements ActionSource {
    FILE(new FileAction()),
    OPEN(new OpenAction()),
    EXIT(new ExitAction());

    private final Action action;

    private Actions(Action action) {
        this.action = action;
    }

    public Action get() {
        return action;
    }
}
