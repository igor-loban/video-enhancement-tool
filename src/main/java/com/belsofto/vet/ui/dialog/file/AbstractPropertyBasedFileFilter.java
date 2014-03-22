package com.belsofto.vet.ui.dialog.file;

import com.belsofto.vet.application.Settings;
import com.belsofto.vet.util.MessageUtils;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.List;

abstract class AbstractPropertyBasedFileFilter extends FileFilter {
    private static final String SEPARATOR = "|";

    protected final List<String> extensions;
    protected final String description;

    protected AbstractPropertyBasedFileFilter(String suffixKey) {
        description = MessageUtils.getMessage("ui.dialog.fileFilter.description." + suffixKey);
        String extensionsAsString = Settings.getProperty("fileFilter.format." + suffixKey);
        extensions = Lists.transform(Splitter.on(SEPARATOR).splitToList(extensionsAsString), new DotPrependFunction());
    }

    private static final class DotPrependFunction implements Function<String, String> {
        @Override public String apply(String input) {
            return "." + input;
        }
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String fileName = file.getName();
        for (String extension : extensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
