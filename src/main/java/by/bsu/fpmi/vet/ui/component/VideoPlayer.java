package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.exception.VideoProcessingException;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayer extends JComponent {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private Timer timer;
    private FFmpegFrameGrabber grabber;
    private SourceDataLine soundLine;
    private State state = State.NO_FILE;
    private int pausedFrameNumber;

    private File videoFile;
    private double frameRate;
    private int totalFrameCount;
    private int delay;

    private int imageWidth;
    private int imageHeight;
    private Image image; // Sync???

    public VideoPlayer() {
        setDoubleBuffered(true);
        try {
            image = ImageIO.read(getClass().getResource("/by/bsu/fpmi/vet/resources/images/test.png"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void play() {
        try {
            state = State.PLAY;
            grabber.start();
            grabber.setFrameNumber(pausedFrameNumber);
            soundLine.start();
            timer.start();
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public void pause() {
        try {
            state = State.PAUSE;
            timer.stop();
            pausedFrameNumber = grabber.getFrameNumber();
            grabber.stop();
            soundLine.stop();
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public void stop() {
        state = State.STOP;
        // reinit
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
        init(); // TODO: implement as reinit
        stop();
    }

    private void init() {
        try {
            grabber = new FFmpegFrameGrabber(videoFile);
            grabber.start();
            totalFrameCount = grabber.getLengthInFrames();
            frameRate = grabber.getFrameRate();
            delay = (int) (1000 / frameRate);
            imageWidth = grabber.getImageWidth();
            imageHeight = grabber.getImageHeight();

            // TODO: setup audio settings
            AudioFormat audioFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, grabber.getSampleRate(), 16, 1, 2,
                            grabber.getSampleRate(), false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            //            soundLine.start();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    pausedFrameNumber = grabber.getFrameNumber();
                    image = frame.image.getBufferedImage();
                    repaint();
                    break;
                }
            }

            timer = new Timer(delay, new GrabbingAction());

            state = State.INITIATED;
        } catch (FrameGrabber.Exception | LineUnavailableException e) {
            throw new VideoProcessingException(e);
        }
    }

    @Override protected void paintComponent(Graphics g) {
        Dimension size = getSize();
        int x = (size.width - imageWidth) / 2;
        int y = (size.height - imageHeight) / 2;
        g.drawImage(image, x, y, this);
    }

    private final class GrabbingAction implements ActionListener {
        @Override public void actionPerformed(ActionEvent event) {
            try {
                Frame frame;
                while ((frame = grabber.grabFrame()) != null) {
                    pausedFrameNumber = grabber.getFrameNumber();
                    //                    label1.setText(frameToTime(currentFrame));
                    //                    progressBar.setValue((int) (currentFrame * 1000 / totalFrames));
                    if (frame.image != null) {
                        image = frame.image.getBufferedImage();
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
                LOGGER.debug("error while grabbing", ex);
            }
        }
    }

    private static enum State {
        NO_FILE, INITIATED, PLAY, PAUSE, STOP
    }
}
