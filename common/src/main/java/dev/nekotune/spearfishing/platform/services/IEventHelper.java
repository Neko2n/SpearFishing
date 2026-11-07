package dev.nekotune.spearfishing.platform.services;

import dev.nekotune.spearfishing.shared.event.LivingDropsEvent;

import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

/**
 * Subscribes listeners to loader-specific game events.
 * <p>
 * Each event type has its own {@code subscribe} overload taking that event's
 * {@code Listener}, so an explicitly typed lambda such as
 * {@code (final LivingDropsEvent event) -> ...} selects the right overload.
 */
public interface IEventHelper {

    void subscribe(final LivingDropsEvent.Listener listener);

    /**
     * Adds a trade to a villager profession's pool at the given level, for both
     * the default and the experimental (rebalanced) trade tables.
     */
    void addVillagerTrade(final VillagerProfession profession, final int level,
            final VillagerTrades.ItemListing listing);
}
