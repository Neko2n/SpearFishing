package dev.nekotune.spearfishing.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.item.ItemProperties;

public class SpearFishingClient$Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpearFishingClient.init();

        // Fabric runs client init after main init, so registries are already populated.
        SpearFishingClient.registerEntityRenderers(EntityRendererRegistry::register);
        // ItemProperties.register is made public by spearfishing.accesswidener.
        SpearFishingClient.registerItemProperties(ItemProperties::register);
    }
}
