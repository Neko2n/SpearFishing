package dev.nekotune.spearfishing.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.spearfishing.shared.event.DropCapturingEntity;
import dev.nekotune.spearfishing.shared.event.FabricLivingDropsEvent;
import dev.nekotune.spearfishing.platform.FabricEventHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Recreates NeoForge's {@code LivingDropsEvent}: drops spawned during
 * {@code dropAllDeathLoot} are captured, passed to listeners, and only then
 * added to the level (unless the event was cancelled).
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected int lastHurtByPlayerTime;

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    private void spearfishing$startCapturingDrops(final ServerLevel level, final DamageSource source,
            final CallbackInfo ci) {
        ((DropCapturingEntity) this).spearfishing$setCapturedDrops(new ArrayList<>());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void spearfishing$fireLivingDrops(final ServerLevel level, final DamageSource source,
            final CallbackInfo ci) {
        final DropCapturingEntity capturing = (DropCapturingEntity) this;
        final List<ItemEntity> drops = capturing.spearfishing$getCapturedDrops();
        capturing.spearfishing$setCapturedDrops(null);
        if (drops == null)
            return;

        final FabricLivingDropsEvent event = new FabricLivingDropsEvent(
                (LivingEntity) (Object) this, source, drops, lastHurtByPlayerTime > 0);
        FabricEventHelper.LIVING_DROPS.invoker().onLivingDrops(event);
        if (!event.isCanceled()) {
            event.getDrops().forEach(level::addFreshEntity);
        }
    }
}
