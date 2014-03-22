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
import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayer extends JRootPane {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private static final int SNAPSHOT_WIDTH = 320;
    private static final int SNAPSHOT_HEIGHT = 240;

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
        mediaPlayer.prepareMedia(videoDetails.getSourceFile().getAbsolutePath());
        ApplicationContext.getInstance().setStatus(Status.STOPPED);
    }

    public void play() {
        mediaPlayer.play();
        ApplicationContext.getInstance().setStatus(Status.PLAYING);
    }

    public void pause() {
        mediaPlayer.setPause(true);
        ApplicationContext.getInstance().setStatus(Status.PAUSED);
    }

    public void stop() {
        mediaPlayer.stop();
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
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double widthCoeff = (double) SNAPSHOT_WIDTH / imageWidth;
        double heightCoeff = (double) SNAPSHOT_HEIGHT / imageHeight;

        int width;
        int height;
        if (widthCoeff < heightCoeff) {
            width = SNAPSHOT_WIDTH;
            height = (int) (widthCoeff * imageHeight);
        } else {
            width = (int) (heightCoeff * imageWidth);
            height = SNAPSHOT_HEIGHT;
        }

        BufferedImage imageCopy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = imageCopy.getGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.drawImage(demo, 0, 0, width, height, null);
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
        return mediaPlayer.setRate(rate);
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

    private final class MediaPlayerActionHandler extends MediaPlayerEventAdapter {
        @Override public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
            ApplicationContext.getInstance().updateTimeline(newTime);
        }
    }
}
