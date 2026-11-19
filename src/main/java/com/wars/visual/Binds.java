package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

/** Опрос клавиш-биндов каждый тик. Работает только когда нет открытого экрана (чат, меню). */
public final class Binds {
    private static final Map<String, Boolean> prev = new HashMap<>();

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) return;
        boolean free = mc.currentScreen == null;
        for (var e : Config.I.binds.entrySet()) {
            boolean now = Keys.down(e.getValue());
            boolean was = prev.getOrDefault(e.getKey(), false);
            prev.put(e.getKey(), now);
            if (!free || !now || was) continue;

            if (e.getKey().equals("gui")) {
                mc.setScreen(new WarsScreen());
            } else {
                toggle(mc, e.getKey());
            }
        }
    }

    public static void toggle(MinecraftClient mc, String module) {
        boolean v = !Config.I.on(module);
        Config.I.modules.put(module, v);
        Config.save();
        if (mc.player != null)
            mc.player.sendMessage(Text.literal("§d" + module + " §7- " + (v ? "§aON" : "§cOFF")), true);
    }
}
