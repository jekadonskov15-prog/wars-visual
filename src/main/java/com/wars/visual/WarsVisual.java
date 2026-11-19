package com.wars.visual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.util.Identifier;

public class WarsVisual implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.load();
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> Optimizer.restore(mc));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            Cps.tick(mc);
            Binds.tick(mc);
            Optimizer.tick(mc);
            if (Config.I.on("ambience") && mc.world != null)
                mc.world.getLevelProperties().setTimeOfDay(Config.I.worldTime);
        });

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.MISC_OVERLAYS,
            Identifier.of("warsvisual", "hud"),
            Hud::render);

        // Сообщения, начинающиеся с ".", не уходят на сервер — это команды мода.
        ClientSendMessageEvents.ALLOW_CHAT.register(msg -> {
            if (!msg.startsWith(".")) return true;
            Commands.run(msg);
            return false;
        });
    }
}
