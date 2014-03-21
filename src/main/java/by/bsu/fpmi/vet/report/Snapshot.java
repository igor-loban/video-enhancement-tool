package by.bsu.fpmi.vet.report;

import by.bsu.fpmi.vet.util.MessageUtils;

import java.awt.image.BufferedImage;

public final class Snapshot {
    public static final String NO_COMMENT = MessageUtils.getMessage("snapshot.noComment");

    private final BufferedImage image;
    private final long time; // Millis

    private String notes = NO_COMMENT;

    public Snapshot(BufferedImage image, long time) {
        this.image = image;
        this.time = time;
    }

    public BufferedImage getImage() {
        return image;
    }

    public long getTime() {
        return time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
