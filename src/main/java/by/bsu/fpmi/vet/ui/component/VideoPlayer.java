package by.bsu.fpmi.vet.ui.component;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class VideoPlayer extends JComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoPlayer.class);

    private Timer timer;
    private FFmpegFrameGrabber grabber;
    private SourceDataLine soundLine;
    private File videoFile;
    private double frameRate;
    private int totalFrameCount;
    private int delay;
    private int currentFrame = 0; // ???

    private Image image; // Sync???

    public VideoPlayer() {
        LOGGER.debug("double buffered is {}", isDoubleBuffered());
        try {
            image = ImageIO.read(getClass().getResource("/by/bsu/fpmi/vet/resources/images/test.png"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void play() {
        //        grabber.setFrameNumber(currentFrame);
        try {
            grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();
            totalFrameCount = grabber.getLengthInFrames();
            frameRate = grabber.getFrameRate();
            delay = (int) (1000 / frameRate);

            // TODO: setup audio settings
            AudioFormat audioFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, grabber.getSampleRate(), 16, 1, 2,
                            grabber.getSampleRate(), false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();

            timer = new Timer(delay, new GrabbingAction(this));
            timer.start();
        } catch (FrameGrabber.Exception | LineUnavailableException e) {
            LOGGER.debug("error", e);
        }
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    @Override protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

    private final class GrabbingAction implements ActionListener {
        private final VideoPlayer videoPlayer;

        private GrabbingAction(VideoPlayer videoPlayer) {
            this.videoPlayer = videoPlayer;
        }

        @Override public void actionPerformed(ActionEvent event) {
            try {
                Frame frame;
                while ((frame = grabber.grabFrame()) != null) {
                    currentFrame = grabber.getFrameNumber();
                    //                    label1.setText(frameToTime(currentFrame));
                    //                    progressBar.setValue((int) (currentFrame * 1000 / totalFrames));
                    if (frame.image != null) {
                        image = frame.image.getBufferedImage();
                        //                        getGraphics().drawImage(image, 0, 0, videoPlayer);
                        repaint();
                        break;
                    } else {
                        Buffer samples = frame.samples[0];
                        ByteBuffer bf = new Pointer(samples).asByteBuffer();
                        byte[] ba = new byte[bf.remaining()];
                        BytePointer bytePointer = new BytePointer(bf);
                        bytePointer.get(ba);
                        soundLine.write(ba, 0, ba.length);
                        if (frame.samples.length > 1) {
                            samples = frame.samples[1];
                            bf = new Pointer(samples).asByteBuffer();
                            ba = new byte[bf.remaining()];
                            bytePointer = new BytePointer(bf);
                            bytePointer.get(ba);
                            soundLine.write(ba, 0, ba.length);
                        }
                    }
                }
            } catch (FrameGrabber.Exception ex) {
                LOGGER.debug("error", ex);
            }
        }
    }
}
