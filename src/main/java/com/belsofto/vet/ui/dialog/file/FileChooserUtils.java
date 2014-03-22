package com.belsofto.vet.ui.dialog.file;

import javax.swing.JFileChooser;

import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;

public final class FileChooserUtils {
    private static final JFileChooser FILE_CHOOSER;

    static {
        FILE_CHOOSER = new JFileChooser(System.getProperty("user.dir"));
        // TODO: i18n file chooser
    }

    public static JFileChooser getVideoFileChooser() {
        FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
        FILE_CHOOSER.addChoosableFileFilter(new VideoFileFilter());
        return FILE_CHOOSER;
    }

    private FileChooserUtils() {
        throwInstantiationError(this.getClass());
    }
}
