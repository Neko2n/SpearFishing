package dev.nekotune.spearfishing.shared.event;

import java.util.Collection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Fabric implementation of {@link LivingDropsEvent}.
 */
public final class FabricLivingDropsEvent implements LivingDropsEvent {

    private final LivingEntity entity;
    private final DamageSource source;
    private final Collection<ItemEntity> drops;
    private final boolean recentlyHit;
    private boolean canceled;

    public FabricLivingDropsEvent(final LivingEntity entity, final DamageSource source,
            final Collection<ItemEntity> drops, final boolean recentlyHit) {
        this.entity = entity;
        this.source = source;
        this.drops = drops;
        this.recentlyHit = recentlyHit;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public DamageSource getSource() {
        return source;
    }

    @Override
    public Collection<ItemEntity> getDrops() {
        return drops;
    }

    @Override
    public boolean isRecentlyHit() {
        return recentlyHit;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }
}
