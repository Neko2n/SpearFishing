package dev.nekotune.spearfishing.platform;

import java.util.Collection;

import dev.nekotune.spearfishing.shared.event.LivingDropsEvent;
import dev.nekotune.spearfishing.platform.services.IEventHelper;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/**
 * Forwards NeoForge's game-bus events to the common event listeners.
 */
public class NeoForgeEventHelper implements IEventHelper {

    @Override
    public void subscribe(final LivingDropsEvent.Listener listener) {
        NeoForge.EVENT_BUS.addListener(
                (final net.neoforged.neoforge.event.entity.living.LivingDropsEvent event) ->
                        listener.onLivingDrops(new LivingDrops(event)));
    }

    @Override
    public void addVillagerTrade(final VillagerProfession profession, final int level,
            final VillagerTrades.ItemListing listing) {
        NeoForge.EVENT_BUS.addListener((final VillagerTradesEvent event) -> {
            if (event.getType() == profession)
                event.getTrades().get(level).add(listing);
        });
    }

    private record LivingDrops(net.neoforged.neoforge.event.entity.living.LivingDropsEvent event)
            implements LivingDropsEvent {

        @Override
        public LivingEntity getEntity() {
            return event.getEntity();
        }

        @Override
        public DamageSource getSource() {
            return event.getSource();
        }

        @Override
        public Collection<ItemEntity> getDrops() {
            return event.getDrops();
        }

        @Override
        public boolean isRecentlyHit() {
            return event.isRecentlyHit();
        }

        @Override
        public boolean isCanceled() {
            return event.isCanceled();
        }

        @Override
        public void setCanceled(final boolean canceled) {
            event.setCanceled(canceled);
        }
    }
}
