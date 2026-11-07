package dev.nekotune.spearfishing.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.spearfishing.shared.logic.SpearBehavior;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Applies spear prey damage (thrown and melee) and skewering to their targets.
 *
 * @see SpearBehavior
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySpearMixin {

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float spearfishing$spearPreyDamage(final float amount, final DamageSource source) {
        return SpearBehavior.modifyIncomingDamage((LivingEntity) (Object) this, source, amount);
    }

    /**
     * TAIL is only reached when the death went through (it was not cancelled).
     */
    @Inject(method = "die", at = @At("TAIL"))
    private void spearfishing$skewerOnDeath(final DamageSource source, final CallbackInfo ci) {
        SpearBehavior.onDeath((LivingEntity) (Object) this, source);
    }
}
