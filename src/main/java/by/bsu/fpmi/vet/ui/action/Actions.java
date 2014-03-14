package by.bsu.fpmi.vet.ui.action;

import javax.swing.Action;

public enum Actions implements ActionSource {
    FILE {
        private final Action action = new FileAction();

        @Override public Action get() {
            return action;
        }
    },
    EXIT {
        private final Action action = new ExitAction();

        @Override public Action get() {
            return action;
        }
    }
}
