package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.exception.VideoProcessingException;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.video.VideoDetails;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.FrameGrabber;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayer extends JComponent {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private Timer timer;
    private FFmpegFrameGrabber grabber;
    private SourceDataLine soundLine;
    private VideoDetails videoDetails;
    private State state = State.NO_FILE;
    private int pausedFrameNumber;
    private int delay;

    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;

    public VideoPlayer() {
        setDoubleBuffered(true);
    }

    public void loadVideo() {
        init(); // TODO: implement as reinit
        stop();
    }

    public void play() {
        try {
            state = State.PLAY;
            grabber.restart();
            grabber.setFrameNumber(pausedFrameNumber);
            soundLine.start();
            timer.start();
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public void pause() {
        if (state == State.PAUSE) {
            return;
        }
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
        try {
            state = State.STOP;
            timer.stop();
            grabber.stop();
            soundLine.stop();
            pausedFrameNumber = 1;
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public Snapshot captureFrame() {
        // TODO: provide more secure implementation
        pause();
        BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = imageCopy.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return new Snapshot(imageCopy, pausedFrameNumber, getFrameMillis(pausedFrameNumber));
    }

    private void init() {
        try {
            videoDetails = ApplicationContext.getInstance().getVideoDetails();
            grabber = videoDetails.getGrabber();
            delay = (int) (1000 / videoDetails.getFrameRate());
            imageWidth = videoDetails.getWidth();
            imageHeight = videoDetails.getHeight();

            grabber.restart();

            // TODO: setup audio settings
            AudioFormat audioFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, grabber.getSampleRate(), 16, 1, 2,
                            grabber.getSampleRate(), false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);

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
        double heightCoeff = (double) size.height / imageHeight;
        double widthCoeff = (double) size.width / imageWidth;

        int width;
        int height;
        if (heightCoeff > widthCoeff) {
            width = size.width;
            height = (int) (widthCoeff * imageHeight);
        } else {
            width = (int) (heightCoeff * imageWidth);
            height = size.height;
        }

        int x = (size.width - width) / 2;
        int y = (size.height - height) / 2;

        // TODO: Add smooth and security
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0, 0, size.width, size.height);
        g2d.drawImage(image, x, y, width, height, this);
    }

    private long getFrameMillis(int frameNumber) {
        return (long) (frameNumber / videoDetails.getFrameRate() * 1000.0);
    }

    public void goToFrameInVideo(int frameNumber) {
        try {
            state = State.NO_FILE;
            Frame frame;
            grabber.restart();
            grabber.setFrameNumber(frameNumber);
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    pausedFrameNumber = grabber.getFrameNumber();
                    image = frame.image.getBufferedImage();
                    repaint();
                    break;
                }
            }
            pause();
        } catch (FrameGrabber.Exception e) {
            LOGGER.debug("go to frame in video error", e);
        }
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
