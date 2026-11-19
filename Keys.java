package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

/** Имена клавиш <-> коды GLFW, опрос состояния. */
public final class Keys {
    private static final Map<String, Integer> N = new LinkedHashMap<>();

    static {
        for (char c = 'A'; c <= 'Z'; c++) N.put(String.valueOf(c), GLFW.GLFW_KEY_A + (c - 'A'));
        for (int d = 0; d <= 9; d++) {
            N.put(String.valueOf(d), GLFW.GLFW_KEY_0 + d);
            N.put("KP" + d, GLFW.GLFW_KEY_KP_0 + d);
        }
        for (int f = 1; f <= 12; f++) N.put("F" + f, GLFW.GLFW_KEY_F1 + f - 1);
        N.put("SPACE", 32); N.put("TAB", 258); N.put("ENTER", 257); N.put("BACKSPACE", 259);
        N.put("INSERT", 260); N.put("DELETE", 261);
        N.put("RIGHT", 262); N.put("LEFT", 263); N.put("DOWN", 264); N.put("UP", 265);
        N.put("PAGEUP", 266); N.put("PAGEDOWN", 267); N.put("HOME", 268); N.put("END", 269);
        N.put("LSHIFT", 340); N.put("LCTRL", 341); N.put("LALT", 342);
        N.put("RSHIFT", 344); N.put("RCTRL", 345); N.put("RALT", 346);
        N.put("GRAVE", 96); N.put("MINUS", 45); N.put("EQUALS", 61); N.put("COMMA", 44);
        N.put("PERIOD", 46); N.put("SLASH", 47); N.put("SEMICOLON", 59); N.put("APOSTROPHE", 39);
        N.put("LBRACKET", 91); N.put("RBRACKET", 93); N.put("BACKSLASH", 92);
    }

    public static Iterable<Integer> all() { return N.values(); }

    public static int parse(String s) {
        String k = s.toUpperCase().replace("RIGHT_SHIFT", "RSHIFT").replace("LEFT_SHIFT", "LSHIFT")
            .replace("RIGHTSHIFT", "RSHIFT").replace("LEFTSHIFT", "LSHIFT");
        return N.getOrDefault(k, -1);
    }

    public static String name(Integer code) {
        if (code == null || code < 0) return "NONE";
        for (var e : N.entrySet()) if (e.getValue().equals(code)) return e.getKey();
        return "KEY" + code;
    }

    public static boolean down(Integer code) {
        if (code == null || code < 0) return false;
        long h = MinecraftClient.getInstance().getWindow().getHandle();
        return GLFW.glfwGetKey(h, code) == GLFW.GLFW_PRESS;
    }
}
