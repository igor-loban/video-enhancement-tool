package com.belsofto.vet.detection.motion;

import java.awt.Color;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public enum MotionThreshold {
    NO(new Color(Integer.parseInt(getMessage("motionThreshold.no.color"), 16))),
    TINY(new Color(Integer.parseInt(getMessage("motionThreshold.tiny.color"), 16))),
    LOW(new Color(Integer.parseInt(getMessage("motionThreshold.low.color"), 16))),
    MEDIUM(new Color(Integer.parseInt(getMessage("motionThreshold.medium.color"), 16))),
    HIGH(new Color(Integer.parseInt(getMessage("motionThreshold.high.color"), 16))),
    VERY_HIGH(new Color(Integer.parseInt(getMessage("motionThreshold.veryHigh.color"), 16)));

    private Color color;

    private MotionThreshold(Color defaultColor) {
        this.color = defaultColor;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }
}
