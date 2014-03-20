package by.bsu.fpmi.vet.ui.component;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Map;

public final class ColoredSliderUI extends BasicSliderUI {
    Map<Integer, Boolean> arr = null;
    Integer min;
    Integer max;
    Integer w;

    //OpenCVFrameGrabber grab;
    public ColoredSliderUI(JSlider b, Map<Integer, Boolean> metaInfo) {
        super(b);
        min = b.getMinimum();
        max = b.getMaximum();
        arr = metaInfo;
    }

    private int val2XPos(int val) {
        return w * (val - min) / (max - min);
    }

    @Override
    protected void scrollDueToClickInTrack(int direction) {
        // this is the default behaviour, let's comment that out
        //scrollByBlock(direction);

        int value = slider.getValue();
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            value = this.valueForXPosition(slider.getMousePosition().x);
        } else if (slider.getOrientation() == JSlider.VERTICAL) {
            value = this.valueForYPosition(slider.getMousePosition().y);
        }
        slider.setValue(value);
    }

    @Override
    public void paintTrack(Graphics g) {
        // Draw track.
        if (arr == null) {
            super.paintTrack(g);
            return;
        }

        Rectangle r = trackRect;
        w = r.width;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 255, 100));
        BasicStroke pen1 = new BasicStroke(0); //толщина линии 20
        g2d.setStroke(pen1);

        Iterator it = arr.entrySet().iterator();
        Map.Entry<Integer, Boolean> pairs = (Map.Entry<Integer, Boolean>) it.next();
        while (it.hasNext()) {
            Map.Entry<Integer, Boolean> pairs2 = (Map.Entry<Integer, Boolean>) it.next();
            int _x = r.x + val2XPos((int) pairs.getKey());
            int _y = r.y;
            int _w = val2XPos((int) pairs2.getKey()) - val2XPos((int) pairs.getKey());
            int _h = 20;
            g2d.setColor(new Color(255, 0, 0, 200));
            g2d.fillRect(_x - 1, _y, 3, 20);
            if (pairs.getValue()) {
                g2d.setColor(new Color(0, 0, 255, 200));
            } else {
                g2d.setColor(new Color(0, 255, 0, 200));
            }
            g2d.fillRect(_x + 1, _y, _w - 1, _h);
            pairs = pairs2;
        }
        int _x = r.x + val2XPos((int) pairs.getKey());
        int _y = r.y;
        int _w = w - val2XPos((int) pairs.getKey());
        int _h = 20;
        g2d.setColor(new Color(255, 0, 0, 200));
        g2d.fillRect(_x - 1, _y, 3, 20);
        if (pairs.getValue()) {
            g2d.setColor(new Color(0, 0, 255, 200));
        } else {
            g2d.setColor(new Color(0, 255, 0, 200));
        }
        g2d.fillRect(_x + 1, _y, _w - 1, _h);
        //g2d.fillRect(r.x+val2XPos((int)pairs.getKey()), r.y, w-val2XPos((int)pairs.getKey()), 20);
    }
}
