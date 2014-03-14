package by.bsu.fpmi.vet.ui.action;

import by.bsu.fpmi.vet.util.MessageUtils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

/**
 * Advanced abstract action has ability to get configuration from properties file.
 *
 * @see MessageUtils
 */
abstract class AdvancedAbstractAction extends AbstractAction {
    private static final String ACTION_PREFIX = "ui.action.";
    private static final String NAME_SUFFIX = ".name";
    private static final String MNEMONIC_CHAR_SUFFIX = ".mnemonic";
    private static final String ACCELERATOR_SUFFIX = ".accelerator";

    protected AdvancedAbstractAction() {
        putValue(Action.NAME, getName());
        putValue(Action.MNEMONIC_KEY, getMnemonic());
        putValue(Action.ACCELERATOR_KEY, getAccelerator());
    }

    private String getName() {
        return MessageUtils.getMessage(getBaseKey() + NAME_SUFFIX);
    }

    private Integer getMnemonic() {
        String mnemonicValue = MessageUtils.getMessage(getBaseKey() + MNEMONIC_CHAR_SUFFIX);
        return KeyEvent.getExtendedKeyCodeForChar(mnemonicValue.charAt(0));
    }

    private KeyStroke getAccelerator() {
        String keyStrokeValue = MessageUtils.getMessage(getBaseKey() + ACCELERATOR_SUFFIX);
        return KeyStroke.getKeyStroke(keyStrokeValue);
    }

    private String getBaseKey() {
        String className = getClass().getName();
        int startIndex = className.indexOf(ACTION_PREFIX);
        return className.substring(startIndex);
    }
}
