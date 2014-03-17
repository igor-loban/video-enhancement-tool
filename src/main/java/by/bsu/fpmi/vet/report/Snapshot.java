package by.bsu.fpmi.vet.report;

import org.joda.time.DateTime;

import java.awt.image.BufferedImage;

public final class Snapshot {
    public static final String NO_COMMENT = "No comment.";

    private final BufferedImage image;

    private String notes = NO_COMMENT;
    private DateTime dateTime;

    public Snapshot(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
