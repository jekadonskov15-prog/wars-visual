package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Hud {
    private static final String[] DIRS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        Config c = Config.I;
        Theme th = Theme.current();
        TextRenderer tr = mc.textRenderer;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();

        int y = 5;
        if (c.on("watermark")) y = watermark(ctx, tr, th, 5, y, mc) + 4;
        if (c.on("info")) info(ctx, tr, th, 5, y, mc);
        if (c.on("list")) list(ctx, tr, th, w);
        if (c.on("keystrokes")) keystrokes(ctx, tr, th, 5, h - 70, mc);
        if (c.on("armor")) armor(ctx, tr, th, w - 62, h / 2 - 40, mc);
        if (c.on("effects")) effects(ctx, tr, th, 5, h / 2 - 20, mc);
    }

    // ---------- helpers ----------

    private static void panel(DrawContext ctx, Theme th, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, th.bg());
        ctx.fillGradient(x, y, x + 2, y + h, th.grad(0f), th.grad(0.5f));
    }

    public static void gradText(DrawContext ctx, TextRenderer tr, Theme th, String s, int x, int y) {
        int cx = x;
        for (int i = 0; i < s.length(); i++) {
            String ch = String.valueOf(s.charAt(i));
            ctx.drawTextWithShadow(tr, ch, cx, y, th.grad(i / (float) Math.max(1, s.length())));
            cx += tr.getWidth(ch);
        }
    }

    // ---------- elements ----------

    private static int watermark(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, MinecraftClient mc) {
        int ping = 0;
        var nh = mc.getNetworkHandler();
        if (nh != null) {
            var e = nh.getPlayerListEntry(mc.player.getUuid());
            if (e != null) ping = e.getLatency();
        }
        String brand = "WARS";
        String rest = " visual  |  " + mc.getCurrentFps() + " fps  |  " + ping + " ms";
        int w = tr.getWidth(brand) + tr.getWidth(rest) + 10;
        panel(ctx, th, x, y, w, 14);
        gradText(ctx, tr, th, brand, x + 6, y + 3);
        ctx.drawTextWithShadow(tr, rest, x + 6 + tr.getWidth(brand), y + 3, th.text());
        return y + 14;
    }

    private static void info(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, MinecraftClient mc) {
        float yaw = MathHelper.wrapDegrees(mc.player.getYaw());
        String dir = DIRS[Math.floorMod(Math.round((yaw + 180f) / 45f), 8)];
        String s1 = String.format("XYZ  %.1f  %.1f  %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        String s2 = "Facing  " + dir + "  (" + Math.round(yaw) + "°)";
        int w = Math.max(tr.getWidth(s1), tr.getWidth(s2)) + 10;
        panel(ctx, th, x, y, w, 26);
        ctx.drawTextWithShadow(tr, s1, x + 6, y + 3, th.text());
        ctx.drawTextWithShadow(tr, s2, x + 6, y + 15, Theme.alpha(th.text(), 190));
    }

    /** Список включённых модулей справа сверху, по длине. */
    private static void list(DrawContext ctx, TextRenderer tr, Theme th, int sw) {
        List<String> on = new ArrayList<>();
        for (String m : Config.MODULES) if (Config.I.on(m)) on.add(m.substring(0, 1).toUpperCase() + m.substring(1));
        on.sort(Comparator.comparingInt((String s) -> tr.getWidth(s)).reversed());
        int y = 3, i = 0;
        for (String s : on) {
            int tw = tr.getWidth(s) + 8;
            int x = sw - tw - 3;
            ctx.fill(x, y, x + tw, y + 11, th.bg());
            ctx.fillGradient(x + tw, y, x + tw + 2, y + 11, th.grad(i * 0.12f), th.grad(i * 0.12f + 0.1f));
            ctx.drawTextWithShadow(tr, s, x + 4, y + 2, th.grad(i * 0.12f));
            y += 11; i++;
        }
    }

    private static void key(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, int w, int h, String label, boolean down) {
        ctx.fill(x, y, x + w, y + h, down ? Theme.alpha(th.grad(0.3f), 200) : th.bg());
        int tw = tr.getWidth(label);
        ctx.drawTextWithShadow(tr, label, x + (w - tw) / 2, y + (h - 8) / 2, down ? 0xFF000000 : th.text());
    }

    private static void keystrokes(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, MinecraftClient mc) {
        var o = mc.options;
        int s = 18, g = 2;
        key(ctx, tr, th, x + s + g, y, s, s, "W", o.forwardKey.isPressed());
        key(ctx, tr, th, x, y + s + g, s, s, "A", o.leftKey.isPressed());
        key(ctx, tr, th, x + s + g, y + s + g, s, s, "S", o.backKey.isPressed());
        key(ctx, tr, th, x + 2 * (s + g), y + s + g, s, s, "D", o.rightKey.isPressed());
        int bw = 3 * s + 2 * g, by = y + 2 * (s + g);
        key(ctx, tr, th, x, by, bw, 11, "LMB " + Cps.left() + "  |  RMB " + Cps.right(), false);
    }

    private static void armor(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, MinecraftClient mc) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        panel(ctx, th, x, y, 60, 4 * 18 + 4);
        for (int i = 0; i < 4; i++) {
            ItemStack st = mc.player.getEquippedStack(slots[i]);
            int yy = y + 2 + i * 18;
            if (st.isEmpty()) continue;
            ctx.drawItem(st, x + 5, yy);
            if (st.isDamageable()) {
                int left = st.getMaxDamage() - st.getDamage();
                float p = left / (float) st.getMaxDamage();
                int col = Theme.lerp(0xFFFF4040, 0xFF40FF70, p);
                ctx.drawTextWithShadow(tr, String.valueOf(left), x + 24, yy + 4, col);
            }
        }
    }

    private static void effects(DrawContext ctx, TextRenderer tr, Theme th, int x, int y, MinecraftClient mc) {
        var list = new ArrayList<>(mc.player.getStatusEffects());
        if (list.isEmpty()) return;
        int yy = y;
        for (StatusEffectInstance e : list) {
            String name = e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1);
            String time;
            if (e.isInfinite()) time = "∞";
            else { int sec = e.getDuration() / 20; time = (sec / 60) + ":" + String.format("%02d", sec % 60); }
            String s = name + "  " + time;
            panel(ctx, th, x, yy, tr.getWidth(s) + 10, 13);
            ctx.drawTextWithShadow(tr, s, x + 6, yy + 3, th.text());
            yy += 15;
        }
    }
}
