package com.wars.visual;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/** Слайдер для числового параметра конфига. */
public class Slider extends SliderWidget {
    private final String label;
    private final double min, max;
    private final int decimals;
    private final Consumer<Float> setter;

    public Slider(int x, int y, int w, int h, String label, double min, double max,
                  double cur, int decimals, Consumer<Float> setter) {
        super(x, y, w, h, Text.empty(), (cur - min) / (max - min));
        this.label = label; this.min = min; this.max = max;
        this.decimals = decimals; this.setter = setter;
        updateMessage();
    }

    private double val() { return min + this.value * (max - min); }

    @Override
    protected void updateMessage() {
        setMessage(Text.literal(label + ": " + String.format("%." + decimals + "f", val())));
    }

    @Override
    protected void applyValue() {
        double v = val();
        if (decimals == 0) v = Math.round(v);
        setter.accept((float) v);
        Config.save();
    }
}
