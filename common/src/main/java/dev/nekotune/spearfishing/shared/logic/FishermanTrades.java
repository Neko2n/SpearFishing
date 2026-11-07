package dev.nekotune.spearfishing.shared.logic;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import dev.nekotune.spearfishing.platform.Services;
import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import dev.nekotune.spearfishing.shared.registry.content.ModItems;

import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * Adds spears to the trades of fishermen villagers
 */
public final class FishermanTrades {

    public static final int SPEAR_TRADE_LEVEL = 2;

    public static void init() {
        Services.EVENTS.get().addVillagerTrade(VillagerProfession.FISHERMAN, SPEAR_TRADE_LEVEL,
                new EnchantedItemForEmeralds(ModItems.WOODEN_SPEAR::get, 3, 3, 10, 0.2F));
    }

    private FishermanTrades() {
    }

    /**
     * Derived from {@link VillagerTrades.EnchantedItemForEmeralds}
     */
    private record EnchantedItemForEmeralds(Supplier<? extends Item> item, int baseEmeraldCost,
            int maxUses, int villagerXp, float priceMultiplier) implements VillagerTrades.ItemListing {

        @Override
        public @Nullable MerchantOffer getOffer(final Entity trader, final RandomSource random) {
            if (!SpearFishingConfig.FISHERMEN_SELL_SPEARS.get())
                return null;
            final int enchantLevel = 5 + random.nextInt(15);
            final RegistryAccess registryAccess = trader.level().registryAccess();
            final Optional<HolderSet.Named<Enchantment>> enchantments = registryAccess
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getTag(EnchantmentTags.ON_TRADED_EQUIPMENT);
            final ItemStack stack = EnchantmentHelper.enchantItem(random, new ItemStack(this.item.get()),
                    enchantLevel, registryAccess, enchantments);
            final ItemCost cost = new ItemCost(Items.EMERALD, Math.min(this.baseEmeraldCost + enchantLevel, 64));
            return new MerchantOffer(cost, stack, this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }
}
