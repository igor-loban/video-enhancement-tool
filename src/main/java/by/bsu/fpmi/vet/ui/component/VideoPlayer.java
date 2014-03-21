package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.application.Status;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.video.VideoDetails;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.imageio.ImageIO;
import javax.swing.JRootPane;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayer extends JRootPane {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private final EmbeddedMediaPlayer mediaPlayer;

    private VideoDetails videoDetails;

    private final BufferedImage demo;

    public VideoPlayer() {
        try {
            // TODO: mediaPlayerComponent.release();
            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
            setContentPane(mediaPlayerComponent);
            mediaPlayer = mediaPlayerComponent.getMediaPlayer();
            mediaPlayer.addMediaPlayerEventListener(new MediaPlayerActionHandler());

            // TODO: remove before release
            demo = ImageIO.read(getClass().getResource("/demo.png"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void loadVideo() {
        videoDetails = ApplicationContext.getInstance().getVideoDetails();

        mediaPlayer.setSnapshotDirectory("/snapshots");
        mediaPlayer.prepareMedia(videoDetails.getSourceFile().getAbsolutePath());

        ApplicationContext.getInstance().setStatus(Status.STOPPED);
    }

    public void play() {
        mediaPlayer.play();
        ApplicationContext.getInstance().setStatus(Status.PLAYING);
    }

    public void pause() {
        mediaPlayer.pause();
        ApplicationContext.getInstance().setStatus(Status.PAUSED);
    }

    public void stop() {
        mediaPlayer.stop();
        ApplicationContext.getInstance().updateTimeline(mediaPlayer.getTime());
        ApplicationContext.getInstance().setStatus(Status.STOPPED);
    }

    public Snapshot captureFrame() {
        pause();

        // TODO: take normal snapshot

        BufferedImage image = mediaPlayer.getVideoSurfaceContents();
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = imageCopy.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(demo, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return new Snapshot(imageCopy, mediaPlayer.getTime());
    }

    public void moveToSnapshot(Snapshot snapshot) {
        mediaPlayer.setTime(snapshot.getTime());
        pause();
    }

    public void setTime(int newTime) {
        mediaPlayer.setTime(newTime);
    }

    public float getRate() {
        return mediaPlayer.getRate();
    }

    public int setRate(float rate) {
        return mediaPlayer.setRate(rate);
    }

    public void mute(boolean muted) {
        mediaPlayer.mute(muted);
    }

    public void setVolume(int volume) {
        mediaPlayer.setVolume(volume);
    }

    private class MediaPlayerActionHandler extends MediaPlayerEventAdapter {
        @Override public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
            ApplicationContext.getInstance().updateTimeline(newTime);
        }
    }
}
