package com.belsofto.vet.ui.component;

import com.belsofto.vet.application.ApplicationContext;
import com.belsofto.vet.detection.motion.MotionDescriptor;
import com.belsofto.vet.detection.sound.SoundDescriptor;
import com.belsofto.vet.media.VideoDetails;
import com.belsofto.vet.media.VideoRecordOptions;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class ColoredSliderUI extends BasicSliderUI {
    private static final Color SEPARATOR_COLOR = Color.BLACK;
    private static final Color FRAGMENT_COLOR = new Color(220, 180, 0, 140);

    private static final int MOTION_HEIGHT = 13;
    private static final int SOUND_HEIGHT = 7;

    private final VideoRecordOptions videoRecordOptions;

    private List<MotionDescriptor> motionDescriptors;
    private List<SoundDescriptor> soundDescriptors;
    private int width;
    private int motionHeight = MOTION_HEIGHT + SOUND_HEIGHT;
    private int soundHeight = MOTION_HEIGHT + SOUND_HEIGHT;
    private int maxValue = 1;

    public ColoredSliderUI(JSlider slider) {
        super(slider);
        videoRecordOptions = ApplicationContext.getInstance().getVideoRecorder().getOptions();
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        width = trackRect.width;

        boolean motionEmpty = isEmpty(motionDescriptors);
        boolean soundEmpty = isEmpty(soundDescriptors);
        if (motionEmpty && soundEmpty) {
            super.paintTrack(g2d);
        } else {
            try {
                g2d.setStroke(new BasicStroke(0));
                if (!soundEmpty) {
                    drawSoundPart(g2d);
                    motionHeight = MOTION_HEIGHT;
                } else {
                    motionHeight = MOTION_HEIGHT + SOUND_HEIGHT;
                }
                if (!motionEmpty) {
                    drawMotionPart(g2d);
                }
                if (!motionEmpty && !soundEmpty) {
                    drawSeparator(g2d);
                }
            } catch (NoSuchElementException e) {
                super.paintTrack(g2d);
            }
        }

        if (videoRecordOptions.isActive()) {
            drawFragment(g2d);
        }
    }

    private void drawFragment(Graphics2D g2d) {
        g2d.setColor(FRAGMENT_COLOR);
        int dx = getX((int) (videoRecordOptions.getLeftBoundNanos() / 1000));
        int fragmentWidth = getX((int) (videoRecordOptions.getRightBoundNanos() / 1000)) - getX(
                (int) (videoRecordOptions.getLeftBoundNanos() / 1000));
        if (fragmentWidth > 0) {
            g2d.fillRect(trackRect.x + dx, trackRect.y, fragmentWidth, soundHeight);
        }
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private void drawSeparator(Graphics2D g2d) {
        g2d.setColor(SEPARATOR_COLOR);
        g2d.drawLine(trackRect.x, trackRect.y + MOTION_HEIGHT, trackRect.x + width, trackRect.y + MOTION_HEIGHT);
    }

    private void drawMotionPart(Graphics2D g2d) {
        Iterator<MotionDescriptor> iterator = motionDescriptors.iterator();
        MotionDescriptor descriptor = iterator.next();
        while (iterator.hasNext()) {
            MotionDescriptor nextDescriptor = iterator.next();
            int dx = getX(descriptor.getTime());
            int blockWidth = getX(nextDescriptor.getTime()) - getX(descriptor.getTime());
            drawMotionBlock(g2d, descriptor, dx, blockWidth);
            descriptor = nextDescriptor;
        }
        int dx = getX(descriptor.getTime());
        int _w = width - getX(descriptor.getTime());
        drawMotionBlock(g2d, descriptor, dx, _w);
    }

    private void drawMotionBlock(Graphics2D g2d, MotionDescriptor descriptor, int dx, int width) {
        g2d.setColor(descriptor.getMotionThreshold().color());
        g2d.fillRect(trackRect.x + dx, trackRect.y, width, motionHeight);
    }

    private void drawSoundPart(Graphics2D g2d) {
        Iterator<SoundDescriptor> iterator = soundDescriptors.iterator();
        SoundDescriptor descriptor = iterator.next();
        while (iterator.hasNext()) {
            SoundDescriptor nextDescriptor = iterator.next();
            int dx = getX(descriptor.getTime());
            int blockWidth = getX(nextDescriptor.getTime()) - getX(descriptor.getTime());
            drawSoundBlock(g2d, descriptor, dx, blockWidth);
            descriptor = nextDescriptor;
        }
        int dx = getX(descriptor.getTime());
        int _w = width - getX(descriptor.getTime());
        drawSoundBlock(g2d, descriptor, dx, _w);
    }

    private void drawSoundBlock(Graphics2D g2d, SoundDescriptor descriptor, int dx, int width) {
        g2d.setColor(descriptor.getSoundThreshold().color());
        g2d.fillRect(trackRect.x + dx, trackRect.y, width, soundHeight);
    }

    @Override
    protected void scrollDueToClickInTrack(int direction) {
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int value = this.valueForXPosition(slider.getMousePosition().x);
            slider.setValue(value);
        }
    }

    private int getX(int value) {
        return width * value / maxValue;
    }

    public void setVideoDetails(VideoDetails videoDetails) {
        this.motionDescriptors = videoDetails.getMotionDescriptors();
        this.soundDescriptors = videoDetails.getSoundDescriptors();
        this.maxValue = videoDetails.getTotalTimeMillis();
    }
}
