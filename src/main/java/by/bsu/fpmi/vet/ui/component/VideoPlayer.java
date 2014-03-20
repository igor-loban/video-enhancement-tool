package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.application.ApplicationContext;
import by.bsu.fpmi.vet.application.Status;
import by.bsu.fpmi.vet.exception.VideoProcessingException;
import by.bsu.fpmi.vet.report.Snapshot;
import by.bsu.fpmi.vet.video.VideoDetails;
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public final class VideoPlayer extends JComponent {
    private static final Logger LOGGER = getLogger(VideoPlayer.class);

    private State state = State.NO_FILE;

    private Timer timer;
    private FFmpegFrameGrabber grabber;
    private SourceDataLine soundLine;
    private VideoDetails videoDetails;
    private int pausedFrameNumber;

    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
    private final BufferedImage demo;

    public VideoPlayer() {
        try {
            setDoubleBuffered(true);
            demo = ImageIO.read(getClass().getResource("/demo.png"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void loadVideo() {
        init(); // TODO: implement as reinit
        stop();
    }

    public void play() {
        try {
            setState(State.PLAY);
            startPlaying();
            ApplicationContext.getInstance().setStatus(Status.PLAYING);
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public void pause() {
        if (state == State.PAUSE) {
            return;
        }

        try {
            setState(State.PAUSE);
            pausedFrameNumber = grabber.getFrameNumber();
            timer.stop();
            grabber.stop();
            soundLine.stop();
            ApplicationContext.getInstance().setStatus(Status.PAUSED);
        } catch (FrameGrabber.Exception e) {
            throw new VideoProcessingException(e);
        }
    }

    public void stop() {
        try {
            setState(State.STOP);
            jumpToFrame(1);
            timer.stop();
            grabber.stop();
            soundLine.stop();
            pausedFrameNumber = 1;
            ApplicationContext.getInstance().setStatus(Status.STOPPED);
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
        g.drawImage(demo, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return new Snapshot(imageCopy, pausedFrameNumber, videoDetails.getFrameRate());
    }

    private void init() {
        try {
            setState(State.INITIATED);

            videoDetails = ApplicationContext.getInstance().getVideoDetails();
            grabber = videoDetails.getGrabber();
            imageWidth = videoDetails.getWidth();
            imageHeight = videoDetails.getHeight();

            grabber.restart();

            // TODO: setup audio settings
            AudioFormat audioFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, grabber.getSampleRate(), 16, 1, 2,
                            grabber.getSampleRate(), false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open();

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    pausedFrameNumber = grabber.getFrameNumber();
                    image = frame.image.getBufferedImage();
                    repaint();
                    break;
                }
            }

            int delay = (int) (1000 / videoDetails.getFrameRate());
            timer = new Timer(delay, new Grabber());
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
        g2d.drawImage(demo, x, y, width, height, this);
    }

    public void goToFrameInVideo(int frameNumber) {
        jumpToFrame(frameNumber);
        pause();
    }

    private void jumpToFrame(int frameNumber) {
        try {
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
            ApplicationContext.getInstance().updateTimeline(pausedFrameNumber);
        } catch (FrameGrabber.Exception e) {
            LOGGER.debug("go to frame in video error", e);
        }
    }

    private final class Grabber implements ActionListener {
        @Override public void actionPerformed(ActionEvent event) {
            //            playing.set(true);
            //            while (state == State.PLAY) {
            try {
                Frame frame;
                while ((frame = grabber.grabFrame()) != null) {
                    pausedFrameNumber = grabber.getFrameNumber();
                    ApplicationContext.getInstance().updateTimeline(pausedFrameNumber);
                    //                    if (frame.samples != null) {
                    //                        for (Buffer sample : frame.samples) {
                    //                            ByteBuffer bf = new Pointer(sample).asByteBuffer();
                    //                            byte[] ba = new byte[bf.remaining()];
                    //                            BytePointer bytePointer = new BytePointer(bf);
                    //                            bytePointer.get(ba);
                    //                            soundLine.write(ba, 0, ba.length);
                    //                        }
                    //                    }
                    if (frame.image != null) {
                        image = frame.image.getBufferedImage();
                        repaint();
                        break;
                    }
                }
            } catch (FrameGrabber.Exception ex) {
                LOGGER.debug("error while grabbing", ex);
            }
            //            }
            //            playing.set(false);
        }
    }

    private void startPlaying() throws FrameGrabber.Exception {
        //        Thread grabberThread = new Thread(new Grabber());
        grabber.restart();
        grabber.setFrameNumber(pausedFrameNumber);
        soundLine.start();
        timer.start();
        //        grabberThread.start();
    }

    private void setState(State newState) {
        state = newState;
        //        if (newState != State.PLAY) {
        //            waitPlayingEnd();
        //        }
    }

    //    private void waitPlayingEnd() {
    //        while (playing.get()) {
    //            try {
    //                Thread.sleep(10);
    //            } catch (InterruptedException ignored) {
    //            }
    //        }
    //    }

    private static enum State {
        NO_FILE, INITIATED, PLAY, PAUSE, STOP
    }
}
