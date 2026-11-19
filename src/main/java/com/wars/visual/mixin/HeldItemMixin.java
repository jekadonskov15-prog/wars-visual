package com.wars.visual.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wars.visual.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// float-аргументы: 0 tickProgress, 1 pitch, 2 swingProgress, 3 equipProgress
@Mixin(HeldItemRenderer.class)
public class HeldItemMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void wars$transform(CallbackInfo ci,
            @Local(argsOnly = true) MatrixStack m,
            @Local(argsOnly = true) Hand hand,
            @Local(argsOnly = true, ordinal = 2) float swing) {
        Config c = Config.I;
        var mc = MinecraftClient.getInstance();
        boolean right = mc.player == null
            || (hand == Hand.MAIN_HAND) == (mc.player.getMainArm() == Arm.RIGHT);
        float side = right ? 1f : -1f;

        if (c.on("viewmodel")) {
            m.translate(c.vmX * side, c.vmY, c.vmZ);
            m.scale(c.vmScale, c.vmScale, c.vmScale);
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(c.vmRotY * side));
        }
        if (c.on("swing") && swing > 0f) {
            float s = MathHelper.sin(swing * (float) Math.PI);
            m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-s * c.swingArc));
            m.translate(0f, -s * c.swingLift, 0f);
        }
    }
}
