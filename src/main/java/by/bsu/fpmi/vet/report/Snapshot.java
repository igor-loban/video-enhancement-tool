package by.bsu.fpmi.vet.report;

import org.joda.time.LocalTime;

import java.awt.image.BufferedImage;
import java.io.File;

import static by.bsu.fpmi.vet.util.MessageUtils.getMessage;

public final class Snapshot {
    public static final String NO_COMMENT = getMessage("snapshot.noComment");

    private static final String TIME_FORMAT = getMessage("format.time.snapshot");

    private final BufferedImage image;
    private final File imageFile;
    private final long time; // Millis
    private final String timeAsString; // HH:mm:ss

    private String notes = NO_COMMENT;

    public Snapshot(BufferedImage image, File file, long time) {
        this.image = image;
        this.imageFile = file;
        this.time = time;
        this.timeAsString = LocalTime.fromMillisOfDay(time).toString(TIME_FORMAT);
    }

    public BufferedImage getImage() {
        return image;
    }

    public File getImageFile() {
        return imageFile;
    }

    public long getTime() {
        return time;
    }

    public String getTimeAsString() {
        return timeAsString;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
