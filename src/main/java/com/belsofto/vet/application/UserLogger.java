package com.belsofto.vet.application;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.belsofto.vet.util.MessageUtils.format;
import static com.belsofto.vet.util.MessageUtils.getMessage;
import static com.belsofto.vet.util.error.ErrorUtils.throwInstantiationError;

public final class UserLogger {
    private static final String MESSAGE_TIMESTAMP_FORMAT = getMessage("format.log.message.timestamp");
    private static final String FILE_TIMESTAMP_FORMAT = getMessage("format.log.file.timestamp");

    private static StringBuilder buffer = new StringBuilder();

    public synchronized static String getLog() {
        return buffer.toString();
    }

    public synchronized static void log(String message) {
        buffer.append(format("format.log.message", getMessageTimestamp(), message));
    }

    public synchronized static boolean save() {
        File file = new File(getLogDirectoryName());
        file.mkdirs();
        try (FileWriter writer = new FileWriter(getLogFileName())) {
            log("log saved");
            writer.write(getLog());
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public synchronized static void clear() {
        buffer = new StringBuilder();
    }

    private static String getLogDirectoryName() {
        return ApplicationContext.getInstance().getUserDirectory() + "/logs";
    }

    private static String getLogFileName() {
        return getLogDirectoryName() + "/log_" + getFileTimestamp() + ".txt";
    }

    private static String getMessageTimestamp() {
        return DateTime.now().toString(MESSAGE_TIMESTAMP_FORMAT);
    }

    private static String getFileTimestamp() {
        return DateTime.now().toString(FILE_TIMESTAMP_FORMAT);
    }

    private UserLogger() {
        throwInstantiationError(this.getClass());
    }
}
