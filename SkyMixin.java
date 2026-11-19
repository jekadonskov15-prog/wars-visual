package com.wars.visual.mixin;

import com.wars.visual.Config;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Цвет неба. В разных версиях getSkyColor возвращает либо Vec3d, либо int (ARGB),
 * поэтому тип определяем по текущему значению. require = 0: если метод в 1.21.11 переименован,
 * мод всё равно запустится, просто цвет неба не изменится.
 */
@Mixin(ClientWorld.class)
public class SkyMixin {
    private static final int[] RGB = {0, 0x4B1E8C, 0xFF7A3D, 0x2EC4FF, 0xC4152B, 0x3DFF8A};

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true, require = 0)
    private void wars$sky(CallbackInfoReturnable<Object> cir) {
        Config c = Config.I;
        if (!c.on("ambience") || c.skyPreset <= 0 || c.skyPreset >= RGB.length) return;
        int rgb = RGB[c.skyPreset];
        Object cur = cir.getReturnValue();
        if (cur instanceof Integer) {
            cir.setReturnValue(0xFF000000 | rgb);
        } else if (cur instanceof Vec3d) {
            cir.setReturnValue(new Vec3d(((rgb >> 16) & 255) / 255.0, ((rgb >> 8) & 255) / 255.0, (rgb & 255) / 255.0));
        }
    }
}
