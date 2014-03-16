package by.bsu.fpmi.vet.report;

import java.awt.image.BufferedImage;

public final class Snapshot {
    private final BufferedImage image;

    private String comment = "No comment.";

    public Snapshot(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
