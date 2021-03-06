package com.belsofto.vet.ui.component;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.application.Status;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.report.ReportOptions;
import com.belsofto.vet.report.Snapshot;
import com.google.common.base.Strings;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.JLabel;
import javax.swing.JRootPane;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class VideoPlayer extends JRootPane {
    private final EmbeddedMediaPlayer mediaPlayer;
    private final List<MediaPlayerEventListener> listeners = new ArrayList<>();

    private VideoDetails videoDetails;
    private JLabel speedLabel;

    public VideoPlayer() {
        // TODO: mediaPlayerComponent.release();
        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        setContentPane(mediaPlayerComponent);
        mediaPlayer = mediaPlayerComponent.getMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerActionHandler());
    }

    public void setSpeedLabel(JLabel speedLabel) {
        this.speedLabel = speedLabel;
    }

    public void loadVideo() {
        videoDetails = ApplicationContext.getInstance().getVideoDetails();
        mediaPlayer.prepareMedia(videoDetails.getSourceFile().getAbsolutePath());
    }

    public void play() {
        mediaPlayer.play();
        setRate(1.0F);
        mediaPlayer.setAdjustVideo(true);
        ApplicationContext.getInstance().setStatus(Status.PLAYING);
    }

    public void pause() {
        mediaPlayer.setPause(true);
        ApplicationContext.getInstance().setStatus(Status.PAUSED);
    }

    public void stop() {
        mediaPlayer.stop();
        clearMediaPlayerEventListener();
        ApplicationContext.getInstance().updateTimeline(mediaPlayer.getTime());
        ApplicationContext.getInstance().setStatus(Status.STOPPED);
    }

    public Snapshot captureFrame() {
        if (videoDetails == null) {
            return null; // TODO: replace on exception
        }

        pause();
        BufferedImage image = mediaPlayer.getSnapshot();
        return new Snapshot(processImage(image), getTime());
    }

    private BufferedImage processImage(BufferedImage image) {
        ReportOptions options = ApplicationContext.getInstance().getReportGenerator().getOptions();

        int snapshotWidth = options.getSnapshotWidth();
        int snapshotHeight = options.getSnapshotHeight();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double widthCoeff = (double) snapshotWidth / imageWidth;
        double heightCoeff = (double) snapshotHeight / imageHeight;

        int width;
        int height;
        if (widthCoeff < heightCoeff) {
            width = snapshotWidth;
            height = (int) (widthCoeff * imageHeight);
        } else {
            width = (int) (heightCoeff * imageWidth);
            height = snapshotHeight;
        }

        BufferedImage imageCopy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = imageCopy.getGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return imageCopy;
    }

    public void moveToSnapshot(Snapshot snapshot) {
        pause();
        mediaPlayer.setTime(snapshot.getTime());
        ApplicationContext.getInstance().updateTimeline(snapshot.getTime());
    }

    public void setTime(int newTime) {
        mediaPlayer.setTime(newTime);
    }

    public float getRate() {
        return mediaPlayer.getRate();
    }

    public int setRate(float rate) {
        int result = mediaPlayer.setRate(rate);
        if (result == 0) {
            updateSpeedLabel(rate);
        }
        return result;
    }

    private void updateSpeedLabel(float rate) {
        if (speedLabel != null) {
            speedLabel.setText(Strings.padEnd("x" + rate, 6, '0'));
        }
    }

    public void mute(boolean muted) {
        mediaPlayer.mute(muted);
    }

    public void setVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }

    public int getTime() {
        return (int) mediaPlayer.getTime();
    }

    public void addMediaPlayerEventListener(MediaPlayerEventListener listener) {
        listeners.add(listener);
        mediaPlayer.addMediaPlayerEventListener(listener);
    }

    public void removeMediaPlayerEventListener(MediaPlayerEventListener listener) {
        mediaPlayer.removeMediaPlayerEventListener(listener);
    }

    public void clearMediaPlayerEventListener() {
        for (MediaPlayerEventListener listener : listeners) {
            removeMediaPlayerEventListener(listener);
        }
        listeners.clear();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void skip(int deltaMillis) {
        mediaPlayer.skip(deltaMillis);
    }

    private final class MediaPlayerActionHandler extends MediaPlayerEventAdapter {
        @Override public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
            ApplicationContext.getInstance().updateTimeline(newTime);
        }

        @Override public void finished(MediaPlayer mediaPlayer) {
            stop();
        }
    }
}
