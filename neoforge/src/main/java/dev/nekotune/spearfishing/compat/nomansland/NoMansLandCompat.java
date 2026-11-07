package dev.nekotune.spearfishing.compat.nomansland;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.shared.logic.BuildCreativeTabs;
import dev.nekotune.spearfishing.shared.registry.content.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NoMansLandCompat {

    public static final String MOD_ID = "nomansland";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpearFishing.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            Registries.ENTITY_TYPE, SpearFishing.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownIncendiarySpear>> THROWN_INCENDIARY_SPEAR =
            ENTITY_TYPES.register("thrown_incendiary_spear",
                    () -> EntityType.Builder.<ThrownIncendiarySpear>of(ThrownIncendiarySpear::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .eyeHeight(0.13F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("thrown_incendiary_spear"));

    public static final DeferredItem<IncendiarySpearItem> INCENDIARY_SPEAR = ITEMS.register(
            "incendiary_spear",
            () -> new IncendiarySpearItem(Tiers.WOOD, 3, -3.2F, ModItems.Properties.WOODEN_SPEAR));

    public static void init(final IEventBus modBus) {
        ITEMS.register(modBus);
        ENTITY_TYPES.register(modBus);
        modBus.addListener((final BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == BuildCreativeTabs.Tabs.COMBAT.key) {
                event.accept(INCENDIARY_SPEAR);
            }
        });
    }

    private NoMansLandCompat() {
    }
}
