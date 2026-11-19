package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Arrays;

/** Команды через точку в чате: .bind add <модуль> <клавиша> и т.д. */
public final class Commands {

    private static void say(String s) {
        var p = MinecraftClient.getInstance().player;
        if (p != null) p.sendMessage(Text.literal("§d[Wars] §7" + s), false);
    }

    private static boolean validTarget(String m) {
        return m.equals("gui") || Arrays.asList(Config.MODULES).contains(m);
    }

    public static void run(String line) {
        String[] a = line.substring(1).trim().split("\\s+");
        if (a.length == 0 || a[0].isEmpty()) { help(); return; }
        MinecraftClient mc = MinecraftClient.getInstance();

        switch (a[0].toLowerCase()) {
            case "bind" -> bind(a);
            case "toggle", "t" -> {
                if (a.length < 2 || !Arrays.asList(Config.MODULES).contains(a[1].toLowerCase()))
                    say("Использование: .toggle <модуль>   (.modules)");
                else Binds.toggle(mc, a[1].toLowerCase());
            }
            case "theme" -> {
                if (a.length < 2) { say("Темы: " + themes()); return; }
                Theme t = Theme.get(a[1]);
                Config.I.theme = t.name();
                Config.save();
                say("Тема: " + t.name());
            }
            case "modules" -> {
                StringBuilder sb = new StringBuilder();
                for (String m : Config.MODULES)
                    sb.append(Config.I.on(m) ? "§a" : "§c").append(m).append("§7 ");
                say(sb.toString());
            }
            default -> help();
        }
    }

    private static void bind(String[] a) {
        String sub = a.length > 1 ? a[1].toLowerCase() : "";
        switch (sub) {
            case "add" -> {
                if (a.length < 4) { say("Использование: .bind add <модуль> <клавиша>"); return; }
                String m = a[2].toLowerCase();
                if (!validTarget(m)) { say("Нет такой функции. .modules"); return; }
                int code = Keys.parse(a[3]);
                if (code < 0) { say("Неизвестная клавиша: " + a[3] + " (примеры: R, F6, RSHIFT, KP1, GRAVE)"); return; }
                Config.I.binds.put(m, code);
                Config.save();
                say(m + " -> " + Keys.name(code));
            }
            case "del", "remove" -> {
                if (a.length < 3) { say("Использование: .bind del <модуль>"); return; }
                String m = a[2].toLowerCase();
                if (m.equals("gui")) { say("Для gui задай другую клавишу: .bind add gui <клавиша>"); return; }
                say(Config.I.binds.remove(m) != null ? "Бинд " + m + " удалён" : "У " + m + " нет бинда");
                Config.save();
            }
            case "list" -> {
                if (Config.I.binds.isEmpty()) { say("Биндов нет"); return; }
                Config.I.binds.forEach((k, v) -> say(k + " -> " + Keys.name(v)));
            }
            default -> say("Использование: .bind add <модуль> <клавиша> | .bind del <модуль> | .bind list");
        }
    }

    private static String themes() {
        StringBuilder sb = new StringBuilder();
        for (Theme t : Theme.ALL) sb.append(t.name()).append(' ');
        return sb.toString();
    }

    private static void help() {
        say(".bind add <модуль> <клавиша> | .bind del <модуль> | .bind list");
        say(".toggle <модуль> | .theme <имя> | .modules");
        say("Меню: Right Shift (или .bind add gui <клавиша>)");
    }
}
