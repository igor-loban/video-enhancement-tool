package com.belsofto.vet.detection.sound;

import java.awt.Color;

import static com.belsofto.vet.util.MessageUtils.getMessage;

public enum SoundThreshold {
    NOISE(new Color(Integer.parseInt(getMessage("soundThreshold.noise.color"), 16))),
    SOUND(new Color(Integer.parseInt(getMessage("soundThreshold.sound.color"), 16)));

    private Color color;

    private SoundThreshold(Color defaultColor) {
        this.color = defaultColor;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }
}
