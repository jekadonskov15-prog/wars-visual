package com.wars.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

/** Снижает нагрузку на клиент (дальность, частицы, тени). При выключении возвращает прежние значения. */
public final class Optimizer {
    private static boolean active;
    private static int pView, pSim, counter;
    private static double pEnt;
    private static boolean pShadows;
    private static Object pPart;

    public static void tick(MinecraftClient mc) {
        if (mc.options == null) return;
        boolean want = Config.I.on("optimizer");
        if (want && !active) {
            GameOptions o = mc.options;
            pView = o.getViewDistance().getValue();
            pSim = o.getSimulationDistance().getValue();
            pEnt = o.getEntityDistanceScaling().getValue();
            pShadows = o.getEntityShadows().getValue();
            pPart = o.getParticles().getValue();
            active = true;
            apply(mc);
        } else if (!want && active) {
            restore(mc);
        } else if (want && ++counter % 40 == 0) {
            apply(mc);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setMinimalParticles(SimpleOption opt) {
        Object cur = opt.getValue();
        if (!(cur instanceof Enum)) return;
        for (Object e : ((Enum) cur).getDeclaringClass().getEnumConstants()) {
            if (((Enum) e).name().equals("MINIMAL")) {
                opt.setValue(e);
                return;
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void apply(MinecraftClient mc) {
        Config c = Config.I;
        GameOptions o = mc.options;
        o.getViewDistance().setValue(c.optView);
        o.getSimulationDistance().setValue(c.optSim);
        o.getEntityDistanceScaling().setValue((double) c.optEnt);
        if (c.optShadows) o.getEntityShadows().setValue(false);
        if (c.optParticles) setMinimalParticles((SimpleOption) o.getParticles());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void restore(MinecraftClient mc) {
        if (!active || mc.options == null) return;
        GameOptions o = mc.options;
        o.getViewDistance().setValue(pView);
        o.getSimulationDistance().setValue(pSim);
        o.getEntityDistanceScaling().setValue(pEnt);
        o.getEntityShadows().setValue(pShadows);
        if (pPart != null) ((SimpleOption) o.getParticles()).setValue(pPart);
        active = false;
    }
}
