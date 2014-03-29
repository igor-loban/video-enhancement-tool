package com.belsofto.vet.report;

public class ReportOptions {
    private int snapshotWidth = 320;
    private int snapshotHeight = 240;
    private boolean docxPresent = true;
    private boolean pdfPresent = true;

    public int getSnapshotWidth() {
        return snapshotWidth;
    }

    public void setSnapshotWidth(int snapshotWidth) {
        this.snapshotWidth = snapshotWidth;
    }

    public int getSnapshotHeight() {
        return snapshotHeight;
    }

    public void setSnapshotHeight(int snapshotHeight) {
        this.snapshotHeight = snapshotHeight;
    }

    public boolean isDocxPresent() {
        return docxPresent;
    }

    public void setDocxPresent(boolean docxPresent) {
        this.docxPresent = docxPresent;
    }

    public boolean isPdfPresent() {
        return pdfPresent;
    }

    public void setPdfPresent(boolean pdfPresent) {
        this.pdfPresent = pdfPresent;
    }

    @Override public String toString() {
        return "{snapshotWidth=" + snapshotWidth +
                ", snapshotHeight=" + snapshotHeight +
                ", docxPresent=" + docxPresent +
                ", pdfPresent=" + pdfPresent + "}";
    }
}
