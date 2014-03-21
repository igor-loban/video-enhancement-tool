package by.bsu.fpmi.vet.ui.component;

import by.bsu.fpmi.vet.video.MotionDescriptor;
import by.bsu.fpmi.vet.video.VideoDetails;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;

public final class ColoredSliderUI extends BasicSliderUI {
    private static final Color RED = new Color(255, 0, 0); // Нет движения
    private static final Color GREEN = new Color(0, 255, 0); // Нет движения
    private static final Color BLUE = new Color(0, 0, 255); // Есть движение

    private static final int height = 20;

    private List<MotionDescriptor> motionDescriptors;
    private int width;
    private int maxValue = 1;

    public ColoredSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        if (motionDescriptors == null || motionDescriptors.isEmpty()) {
            super.paintTrack(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        width = trackRect.width;

        g2d.setColor(BLUE);
        g2d.setStroke(new BasicStroke(0));

        Iterator<MotionDescriptor> iterator = motionDescriptors.iterator();
        MotionDescriptor descriptor = iterator.next();
        while (iterator.hasNext()) {
            MotionDescriptor nextDescriptor = iterator.next();
            int dx = getX(descriptor.getTime());
            int blockWidth = getX(nextDescriptor.getTime()) - getX(descriptor.getTime());
            drawColorBlock(g2d, descriptor, dx, blockWidth);
            descriptor = nextDescriptor;
        }
        int dx = getX(descriptor.getTime());
        int _w = width - getX(descriptor.getTime());
        drawColorBlock(g2d, descriptor, dx, _w);
    }

    private void drawColorBlock(Graphics2D g2d, MotionDescriptor descriptor, int dx, int width) {
        g2d.setColor(descriptor.isVideoFlag() ? BLUE : GREEN);
        g2d.fillRect(trackRect.x + dx, trackRect.y, width, height);
        g2d.setColor(RED);
        g2d.fillRect(trackRect.x + dx - 1, trackRect.y, 3, height);
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
        this.maxValue = (int) videoDetails.getTotalTime();
    }
}
