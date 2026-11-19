package com.wars.visual;

import net.minecraft.util.math.MathHelper;

/** Цветовые темы: два акцента (анимированный градиент), фон панелей, цвет текста. */
public record Theme(String name, int a, int b, int bg, int text) {

    public static final Theme[] ALL = {
        new Theme("Nebula",  0xFFB06CFF, 0xFFFF5FA2, 0xB0140F1E, 0xFFF2EAFF),
        new Theme("Ember",   0xFFFF8A3D, 0xFFFF3D3D, 0xB01E1210, 0xFFFFEFE6),
        new Theme("Glacier", 0xFF4DE1FF, 0xFF4D7BFF, 0xB00E1620, 0xFFE8F6FF),
        new Theme("Toxic",   0xFFB6FF3D, 0xFF1FD38A, 0xB0101A12, 0xFFF0FFE8),
        new Theme("Mono",    0xFFFFFFFF, 0xFF8A8A8A, 0xB0101010, 0xFFEDEDED),
    };

    public static Theme get(String name) {
        for (Theme t : ALL) if (t.name.equalsIgnoreCase(name)) return t;
        return ALL[0];
    }

    public static Theme current() { return get(Config.I.theme); }

    /** Анимированный градиент: p в [0..1] — позиция вдоль элемента. */
    public int grad(float p) {
        float phase = (System.currentTimeMillis() % 4000L) / 4000f;
        float f = (p + phase) % 1f;
        float tri = 1f - Math.abs(2f * f - 1f);
        return lerp(a, b, tri);
    }

    public static int lerp(int c1, int c2, float t) {
        int al = (int) MathHelper.lerp(t, c1 >>> 24, c2 >>> 24);
        int r = (int) MathHelper.lerp(t, (c1 >> 16) & 255, (c2 >> 16) & 255);
        int g = (int) MathHelper.lerp(t, (c1 >> 8) & 255, (c2 >> 8) & 255);
        int bl = (int) MathHelper.lerp(t, c1 & 255, c2 & 255);
        return al << 24 | r << 16 | g << 8 | bl;
    }

    public static int alpha(int c, int a) { return (a << 24) | (c & 0xFFFFFF); }
}
