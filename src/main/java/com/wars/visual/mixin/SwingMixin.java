package com.wars.visual.mixin;

import com.wars.visual.Config;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SwingMixin {
    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    private void wars$duration(CallbackInfoReturnable<Integer> cir) {
        if (Config.I.on("swing") && (Object) this instanceof ClientPlayerEntity) {
            cir.setReturnValue(Math.max(1, Config.I.swingDuration));
        }
    }
}
