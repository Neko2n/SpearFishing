package dev.nekotune.spearfishing.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.spearfishing.shared.logic.SpearBehavior;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Lets thrown spears hit and grab item entities.
 *
 * @see SpearBehavior
 */
@Mixin(Projectile.class)
public abstract class ProjectileSpearMixin {

    @Shadow
    protected abstract void onHit(final HitResult result);

    @Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
    private void spearfishing$canHitItems(final Entity target, final CallbackInfoReturnable<Boolean> cir) {
        final Projectile self = (Projectile) (Object) this;
        if (target instanceof final ItemEntity item && SpearBehavior.isThrownSpear(self)) {
            cir.setReturnValue(SpearBehavior.canGrabItem(self, item));
        }
    }

    /**
     * Grabs items instead of passing them to {@code onHitEntity}, which for
     * most projectiles would hurt (and so destroy) them.
     */
    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void spearfishing$grabItems(final HitResult result, final CallbackInfo ci) {
        final Projectile self = (Projectile) (Object) this;
        if (result instanceof final EntityHitResult entityHit
                && entityHit.getEntity() instanceof final ItemEntity item
                && SpearBehavior.isThrownSpear(self)) {
            ci.cancel();
            SpearBehavior.grabItem(self, item);
            final Optional<BlockHitResult> blockHit = SpearBehavior.findBlockAhead(self);
            blockHit.ifPresent(this::onHit);
        }
    }
}
