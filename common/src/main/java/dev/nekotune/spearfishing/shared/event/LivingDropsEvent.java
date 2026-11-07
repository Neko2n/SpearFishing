package dev.nekotune.spearfishing.shared.event;

import java.util.Collection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

/**
 * Fired on the server when a living entity dies, after its drops have been
 * created but before they are added to the level.
 * <p>
 * Modifying {@link #getDrops()} changes what is spawned. Cancelling the event
 * prevents all of the drops from spawning.
 */
public interface LivingDropsEvent {

    /**
     * @return The entity that died.
     */
    LivingEntity getEntity();

    /**
     * @return The damage source that killed the entity.
     */
    DamageSource getSource();

    /**
     * @return The mutable collection of item entities about to be spawned.
     */
    Collection<ItemEntity> getDrops();

    /**
     * @return Whether the entity was recently hit by a player.
     */
    boolean isRecentlyHit();

    boolean isCanceled();

    void setCanceled(final boolean canceled);

    @FunctionalInterface
    interface Listener {
        void onLivingDrops(final LivingDropsEvent event);
    }
}
