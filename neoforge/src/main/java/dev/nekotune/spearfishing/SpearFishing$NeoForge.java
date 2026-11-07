package dev.nekotune.spearfishing;

import dev.nekotune.spearfishing.compat.nomansland.NoMansLandCompat;
import dev.nekotune.spearfishing.shared.logic.BuildCreativeTabs;
import dev.nekotune.spearfishing.platform.Services;
import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(SpearFishing.MOD_ID)
public class SpearFishing$NeoForge {

    public SpearFishing$NeoForge(final IEventBus eventBus, final ModContainer container) {
        Services.init(this.getClass().getClassLoader());
        container.registerConfig(ModConfig.Type.STARTUP, SpearFishingConfig.STARTUP_SPEC);
        container.registerConfig(ModConfig.Type.COMMON, SpearFishingConfig.SPEC);
        SpearFishing.init();

        eventBus.addListener((final BuildCreativeModeTabContentsEvent event) ->
                BuildCreativeTabs.addItems(event.getTabKey(), event));

        if (ModList.get().isLoaded(NoMansLandCompat.MOD_ID)) {
            NoMansLandCompat.init(eventBus);
        }
    }
}
