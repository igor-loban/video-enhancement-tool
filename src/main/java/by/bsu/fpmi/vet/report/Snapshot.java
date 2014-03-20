package by.bsu.fpmi.vet.report;

import by.bsu.fpmi.vet.util.MessageUtils;
import by.bsu.fpmi.vet.video.VideoUtils;
import org.joda.time.LocalTime;

import java.awt.image.BufferedImage;

public final class Snapshot {
    public static final String NO_COMMENT = MessageUtils.getMessage("snapshot.noComment");

    private final BufferedImage image;
    private final LocalTime time;
    private final int frameNumber;

    private String notes = NO_COMMENT;

    public Snapshot(BufferedImage image, int frameNumber, double frameRate) {
        this.image = image;
        this.frameNumber = frameNumber;
        long millis = VideoUtils.getFrameMillis(frameNumber, frameRate);
        this.time = LocalTime.fromMillisOfDay(millis);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public String getTime() {
        return time.toString("HH:mm:ss");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
