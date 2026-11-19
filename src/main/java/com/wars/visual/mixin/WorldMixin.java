package com.wars.visual.mixin;

import com.wars.visual.Config;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Клиентская подмена погоды (только визуал, сервер ничего не знает). */
@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "getRainGradient", at = @At("RETURN"), cancellable = true)
    private void wars$rain(float delta, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientWorld && Config.I.on("ambience"))
            cir.setReturnValue(Config.I.weather >= 1 ? 1f : 0f);
    }

    @Inject(method = "getThunderGradient", at = @At("RETURN"), cancellable = true)
    private void wars$thunder(float delta, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientWorld && Config.I.on("ambience"))
            cir.setReturnValue(Config.I.weather >= 2 ? 1f : 0f);
    }
}
