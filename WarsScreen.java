package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

/**
 * Меню функций. Использует ванильные виджеты (кнопки/слайдеры), поэтому не зависит от
 * сигнатур обработки мыши/клавиатуры, которые менялись в 1.21.9+.
 */
public class WarsScreen extends Screen {
    private static String selected = Config.MODULES[0];
    private String listening = null;
    private Set<Integer> prevKeys = new HashSet<>();
    private boolean prevGui;

    public WarsScreen() {
        super(Text.literal("Wars Visual"));
        prevGui = Keys.down(Config.I.binds.get("gui"));
    }

    @Override public boolean shouldPause() { return false; }

    private int x0() { return width / 2 - 190; }
    private int y0() { return height / 2 - 130; }

    @Override
    protected void init() {
        int x0 = x0(), y0 = y0();
        Config c = Config.I;

        addDrawableChild(ButtonWidget.builder(Text.literal("Theme: " + c.theme), b -> {
            Theme[] all = Theme.ALL;
            int i = 0;
            for (int k = 0; k < all.length; k++) if (all[k].name().equals(c.theme)) i = k;
            c.theme = all[(i + 1) % all.length].name();
            Config.save();
            clearAndInit();
        }).dimensions(x0 + 200, y0 + 4, 180, 18).build());

        int y = y0 + 32;
        for (String m : Config.MODULES) {
            String title = (m.equals(selected) ? "> " : "") + Character.toUpperCase(m.charAt(0)) + m.substring(1);
            addDrawableChild(ButtonWidget.builder(Text.literal(title), b -> { selected = m; clearAndInit(); })
                .dimensions(x0, y, 80, 20).build());

            boolean on = c.on(m);
            addDrawableChild(ButtonWidget.builder(
                Text.literal(on ? "ON" : "OFF").formatted(on ? Formatting.GREEN : Formatting.RED),
                b -> { c.modules.put(m, !c.on(m)); Config.save(); clearAndInit(); })
                .dimensions(x0 + 84, y, 40, 20).build());

            String bl = m.equals(listening) ? "..." : "[" + Keys.name(c.binds.get(m)) + "]";
            addDrawableChild(ButtonWidget.builder(Text.literal(bl), b -> startListening(m))
                .dimensions(x0 + 128, y, 62, 20).build());
            y += 22;
        }

        // bind для самого меню
        String gl = "gui".equals(listening) ? "..." : "[" + Keys.name(c.binds.get("gui")) + "]";
        addDrawableChild(ButtonWidget.builder(Text.literal("Menu key: " + gl), b -> startListening("gui"))
            .dimensions(x0 + 200, y0 + 226, 180, 18).build());

        settings(x0 + 200, y0 + 60, 180);
    }

    private void settings(int x, int y, int w) {
        Config c = Config.I;
        switch (selected) {
            case "viewmodel" -> {
                addDrawableChild(new Slider(x, y, w, 18, "X", -1, 1, c.vmX, 2, v -> c.vmX = v));
                addDrawableChild(new Slider(x, y + 22, w, 18, "Y", -1, 1, c.vmY, 2, v -> c.vmY = v));
                addDrawableChild(new Slider(x, y + 44, w, 18, "Z", -1, 1, c.vmZ, 2, v -> c.vmZ = v));
                addDrawableChild(new Slider(x, y + 66, w, 18, "Scale", 0.3, 1.5, c.vmScale, 2, v -> c.vmScale = v));
                addDrawableChild(new Slider(x, y + 88, w, 18, "Rotation", -45, 45, c.vmRotY, 0, v -> c.vmRotY = v));
            }
            case "swing" -> {
                addDrawableChild(new Slider(x, y, w, 18, "Duration", 1, 30, c.swingDuration, 0, v -> c.swingDuration = Math.round(v)));
                addDrawableChild(new Slider(x, y + 22, w, 18, "Arc", 0, 90, c.swingArc, 0, v -> c.swingArc = v));
                addDrawableChild(new Slider(x, y + 44, w, 18, "Lift", 0, 0.4, c.swingLift, 2, v -> c.swingLift = v));
            }
            case "ambience" -> {
                String[] sk = {"Off", "Galaxy", "Sunset", "Aqua", "Crimson", "Toxic"};
                addDrawableChild(ButtonWidget.builder(Text.literal("Sky: " + sk[c.skyPreset]), bt -> {
                    c.skyPreset = (c.skyPreset + 1) % sk.length;
                    if (c.skyPreset == 1) { c.worldTime = 18000; c.weather = 0; } // Galaxy: ночь со звёздами
                    Config.save(); clearAndInit();
                }).dimensions(x, y, w, 18).build());
                addDrawableChild(new Slider(x, y + 22, w, 18, "Time", 0, 24000, c.worldTime, 0, v -> c.worldTime = Math.round(v)));
                String[] wn = {"Clear", "Rain", "Thunder"};
                addDrawableChild(ButtonWidget.builder(Text.literal("Weather: " + wn[c.weather]), bt -> {
                    c.weather = (c.weather + 1) % 3; Config.save(); clearAndInit();
                }).dimensions(x, y + 44, w, 18).build());
            }
            case "optimizer" -> {
                addDrawableChild(new Slider(x, y, w, 18, "View distance", 2, 16, c.optView, 0, v -> c.optView = Math.round(v)));
                addDrawableChild(new Slider(x, y + 22, w, 18, "Simulation", 5, 12, c.optSim, 0, v -> c.optSim = Math.round(v)));
                addDrawableChild(new Slider(x, y + 44, w, 18, "Entity dist", 0.5, 1.0, c.optEnt, 2, v -> c.optEnt = v));
                addDrawableChild(ButtonWidget.builder(Text.literal("Min particles: " + (c.optParticles ? "ON" : "OFF")), bt -> {
                    c.optParticles = !c.optParticles; Config.save(); clearAndInit();
                }).dimensions(x, y + 66, w, 18).build());
                addDrawableChild(ButtonWidget.builder(Text.literal("No shadows: " + (c.optShadows ? "ON" : "OFF")), bt -> {
                    c.optShadows = !c.optShadows; Config.save(); clearAndInit();
                }).dimensions(x, y + 88, w, 18).build());
            }
            default -> {}
        }
    }

    private void startListening(String target) {
        listening = target;
        prevKeys = pressedNow();
        clearAndInit();
    }

    private Set<Integer> pressedNow() {
        Set<Integer> s = new HashSet<>();
        for (int k : Keys.all()) if (Keys.down(k)) s.add(k);
        return s;
    }

    @Override
    public void tick() {
        Config c = Config.I;
        if (listening != null) {
            Set<Integer> now = pressedNow();
            for (int k : now) {
                if (prevKeys.contains(k)) continue;
                if (k == 259) { // Backspace — очистить бинд
                    if (!listening.equals("gui")) c.binds.remove(listening);
                } else {
                    c.binds.put(listening, k);
                }
                listening = null;
                Config.save();
                prevGui = true; // не закрывать меню от только что выбранной клавиши
                clearAndInit();
                return;
            }
            prevKeys = now;
            return;
        }
        boolean g = Keys.down(c.binds.get("gui"));
        if (g && !prevGui) { close(); return; }
        prevGui = g;
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        Theme th = Theme.current();
        int x0 = x0(), y0 = y0();
        ctx.fill(0, 0, width, height, 0x66000000);
        ctx.fill(x0 - 8, y0 - 8, x0 + 388, y0 + 252, th.bg());
        ctx.fill(x0 - 8, y0 - 8, x0 + 388, y0 - 6, th.grad(0.2f));
        ctx.fill(x0 + 194, y0 + 4, x0 + 195, y0 + 244, Theme.alpha(th.text(), 40));
        Hud.gradText(ctx, textRenderer, th, "WARS", x0, y0 + 10);
        ctx.drawTextWithShadow(textRenderer, " visual", x0 + textRenderer.getWidth("WARS"), y0 + 10, th.text());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        Theme th = Theme.current();
        int x0 = x0(), y0 = y0();
        String name = Character.toUpperCase(selected.charAt(0)) + selected.substring(1);
        ctx.drawTextWithShadow(textRenderer, name, x0 + 200, y0 + 32, th.grad(0.4f));
        if (!selected.equals("viewmodel") && !selected.equals("swing") && !selected.equals("ambience") && !selected.equals("optimizer"))
            ctx.drawTextWithShadow(textRenderer, "Нет отдельных настроек", x0 + 200, y0 + 60, 0xFF9A9A9A);
        String hint = listening != null
            ? "Нажми клавишу (Backspace - очистить)"
            : "ЛКМ: ON/OFF и клавиша | .bind add <модуль> <клавиша>";
        ctx.drawTextWithShadow(textRenderer, hint, x0, y0 + 236, 0xFFB0B0B0);
    }
}
