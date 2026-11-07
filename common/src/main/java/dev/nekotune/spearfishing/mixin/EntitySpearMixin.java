package dev.nekotune.spearfishing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.spearfishing.shared.logic.SpearBehavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

/**
 * Lets thrown spears carry any number of skewered entities and items.
 *
 * @see SpearBehavior
 */
@Mixin(Entity.class)
public abstract class EntitySpearMixin {

    @Inject(method = "canAddPassenger", at = @At("HEAD"), cancellable = true)
    private void spearfishing$canCarry(final Entity passenger, final CallbackInfoReturnable<Boolean> cir) {
        if (SpearBehavior.isThrownSpear((Entity) (Object) this)) {
            cir.setReturnValue(SpearBehavior.canCarry(passenger));
        }
    }

    @Inject(method = "getPassengerAttachmentPoint", at = @At("HEAD"), cancellable = true)
    private void spearfishing$skewerAtCenter(final Entity passenger, final EntityDimensions dimensions,
            final float partialTick, final CallbackInfoReturnable<Vec3> cir) {
        if (SpearBehavior.isThrownSpear((Entity) (Object) this)) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }

    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void spearfishing$onPassengerRemoved(final Entity passenger, final CallbackInfo ci) {
        if (SpearBehavior.isThrownSpear((Entity) (Object) this)) {
            SpearBehavior.onPassengerRemoved(passenger);
        }
    }
}
