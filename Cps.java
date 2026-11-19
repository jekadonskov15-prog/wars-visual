package com.wars.visual;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;

/** Счётчик кликов в секунду (по нажатиям кнопок атаки/использования). */
public final class Cps {
    private static final ArrayDeque<Long> L = new ArrayDeque<>(), R = new ArrayDeque<>();
    private static boolean wasL, wasR;

    public static void tick(MinecraftClient mc) {
        boolean l = mc.options.attackKey.isPressed(), r = mc.options.useKey.isPressed();
        long now = System.currentTimeMillis();
        if (l && !wasL) L.add(now);
        if (r && !wasR) R.add(now);
        wasL = l; wasR = r;
        while (!L.isEmpty() && now - L.peekFirst() > 1000) L.pollFirst();
        while (!R.isEmpty() && now - R.peekFirst() > 1000) R.pollFirst();
    }

    public static int left() { return L.size(); }
    public static int right() { return R.size(); }
}
