package dev.nekotune.spearfishing.platform;

import dev.nekotune.spearfishing.shared.event.LivingDropsEvent;
import dev.nekotune.spearfishing.platform.services.IEventHelper;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

/**
 * Fabric API has no equivalents for some NeoForge events, so they are
 * defined here and fired from this mod's own mixins.
 */
public class FabricEventHelper implements IEventHelper {

    /**
     * Fired by {@code LivingEntityMixin} once a dying entity's drops are
     * collected. Like NeoForge, listeners are skipped once the event is cancelled.
     */
    public static final Event<LivingDropsEvent.Listener> LIVING_DROPS = EventFactory.createArrayBacked(
            LivingDropsEvent.Listener.class,
            listeners -> event -> {
                for (final LivingDropsEvent.Listener listener : listeners) {
                    if (event.isCanceled())
                        return;
                    listener.onLivingDrops(event);
                }
            });

    @Override
    public void subscribe(final LivingDropsEvent.Listener listener) {
        LIVING_DROPS.register(listener);
    }

    @Override
    public void addVillagerTrade(final VillagerProfession profession, final int level,
            final VillagerTrades.ItemListing listing) {
        TradeOfferHelper.registerVillagerOffers(profession, level,
                (factories, rebalanced) -> factories.add(listing));
    }
}
