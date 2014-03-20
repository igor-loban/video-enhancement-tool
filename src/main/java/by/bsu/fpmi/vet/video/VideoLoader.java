package by.bsu.fpmi.vet.video;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.ui.component.VideoPlayer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoLoader implements Runnable {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private final File videoFile;

    private VideoLoader(File videoFile) {
        this.videoFile = videoFile;
    }

    public static void load(File videoFile) {
        Thread videoLoader = new Thread(new VideoLoader(videoFile));
        videoLoader.start();
    }

    @Override public void run() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();

            VideoDetails videoDetails = new VideoDetails(videoFile);
            videoDetails.setGrabber(grabber);
            videoDetails.setTotalFrameCount(grabber.getLengthInFrames());
            videoDetails.setFrameRate(grabber.getFrameRate());
            videoDetails.setWidth(grabber.getImageWidth());
            videoDetails.setHeight(grabber.getImageHeight());

            grabber.stop();

            ApplicationContext context = ApplicationContext.getInstance();
            context.setVideoDetails(videoDetails);
            context.getMainFrame().getVideoPlayer().loadVideo();

            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    JOptionPane.showMessageDialog(null, "File loading complete.", "File Loader",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } catch (FrameGrabber.Exception e) {
            LOGGER.debug("video loading exception", e);
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    JOptionPane
                            .showMessageDialog(null, "File loading failed.", "File Loader", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
