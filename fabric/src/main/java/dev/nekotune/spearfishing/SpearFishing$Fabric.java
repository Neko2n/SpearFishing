package dev.nekotune.spearfishing;

import dev.nekotune.spearfishing.shared.logic.BuildCreativeTabs;
import dev.nekotune.spearfishing.platform.Services;
import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.neoforged.fml.config.ModConfig;

public class SpearFishing$Fabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Services.init(this.getClass().getClassLoader());
        NeoForgeConfigRegistry.INSTANCE.register(SpearFishing.MOD_ID, ModConfig.Type.STARTUP, SpearFishingConfig.STARTUP_SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(SpearFishing.MOD_ID, ModConfig.Type.COMMON, SpearFishingConfig.SPEC);
        SpearFishing.init();

        BuildCreativeTabs.modifiedTabs().forEach(tab -> ItemGroupEvents.modifyEntriesEvent(tab)
                .register(entries -> BuildCreativeTabs.addItems(tab, entries)));
    }
}
