package com.belsofto.vet.ui.action;

import com.belsofto.vet.util.MessageUtils;

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
    private static final String SHORT_DESCRIPTION_SUFFIX = ".shortDescription";

    protected AdvancedAbstractAction() {
        addName();
        addMnemonic();
        addAccelerator();
        addShortDescription();
    }

    private void addName() {
        putValue(Action.NAME, getName());
    }

    private String getName() {
        return MessageUtils.getMessage(getBaseKey() + NAME_SUFFIX);
    }

    private void addMnemonic() {
        addValue(Action.MNEMONIC_KEY, getMnemonic());
    }

    private void addValue(String key, Object value) {
        if (value != null) {
            putValue(key, value);
        }
    }

    private void addAccelerator() {
        addValue(Action.ACCELERATOR_KEY, getAccelerator());
    }

    private void addShortDescription() {
        addValue(Action.SHORT_DESCRIPTION, getShortDescription());
    }

    private String getShortDescription() {
        String key = getBaseKey() + SHORT_DESCRIPTION_SUFFIX;
        return MessageUtils.contains(key) ? MessageUtils.getMessage(key) : null;
    }

    private Integer getMnemonic() {
        String key = getBaseKey() + MNEMONIC_CHAR_SUFFIX;
        if (!MessageUtils.contains(key)) {
            return null;
        }
        String mnemonicValue = MessageUtils.getMessage(key);
        return KeyEvent.getExtendedKeyCodeForChar(mnemonicValue.charAt(0));
    }

    private KeyStroke getAccelerator() {
        String key = getBaseKey() + ACCELERATOR_SUFFIX;
        return MessageUtils.contains(key) ? KeyStroke.getKeyStroke(MessageUtils.getMessage(key)) : null;
    }

    private String getBaseKey() {
        String className = getClass().getName();
        int startIndex = className.indexOf(ACTION_PREFIX);
        return className.substring(startIndex);
    }
}
