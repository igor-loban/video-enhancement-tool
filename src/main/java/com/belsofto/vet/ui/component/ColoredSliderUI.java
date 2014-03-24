package com.belsofto.vet.ui.component;

import com.belsofto.vet.media.MotionDescriptor;
import com.belsofto.vet.media.SoundDescriptor;
import com.belsofto.vet.media.VideoDetails;

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
    private static final Color RED = Color.RED; // Separator
    private static final Color LIGHT_GREEN = new Color(150, 255, 150); // Noise present
    private static final Color LIGHT_BLUE = new Color(150, 150, 255); // Sound present

    private static final int MOTION_HEIGHT = 15;
    private static final int SOUND_HEIGHT = 5;

    private List<MotionDescriptor> motionDescriptors;
    private List<SoundDescriptor> soundDescriptors;
    private int width;
    private int motionHeight = MOTION_HEIGHT + SOUND_HEIGHT;
    private int soundHeight = MOTION_HEIGHT + SOUND_HEIGHT;
    private int maxValue = 1;

    public ColoredSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        boolean motionEmpty = isEmpty(motionDescriptors);
        boolean soundEmpty = isEmpty(soundDescriptors);
        if (motionEmpty && soundEmpty) {
            super.paintTrack(g);
            return;
        }

        try {
            width = trackRect.width;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(0));
            if (!soundEmpty) {
                drawSoundPart(g2d);
                motionHeight = MOTION_HEIGHT;
            }
            if (!motionEmpty) {
                drawMotionPart(g2d);
            }
            if (!motionEmpty && !soundEmpty) {
                drawSeparator(g2d);
            }
        } catch (NoSuchElementException e) {
            super.paintTrack(g);
        }
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private void drawSeparator(Graphics2D g2d) {
        g2d.setColor(RED);
//        g2d.fillRect(trackRect.x, trackRect.y + MOTION_HEIGHT, width, 3);
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
        g2d.setColor(RED);
        g2d.fillRect(trackRect.x + dx - 1, trackRect.y, 3, motionHeight);
//        g2d.drawLine(trackRect.x + dx, trackRect.y, trackRect.x + dx, trackRect.y + motionHeight);
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
        g2d.setColor(descriptor.isNoisePresent() ? LIGHT_GREEN : LIGHT_BLUE);
        g2d.fillRect(trackRect.x + dx, trackRect.y, width, soundHeight);
        g2d.setColor(RED);
//        g2d.fillRect(trackRect.x + dx - 1, trackRect.y, 3, soundHeight);
        g2d.drawLine(trackRect.x + dx, trackRect.y, trackRect.x + dx, trackRect.y + soundHeight);
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
        this.maxValue = (int) videoDetails.getTotalTime();
    }
}
