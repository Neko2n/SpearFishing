package dev.nekotune.spearfishing.client;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.client.render.entity.ThrownSpearRenderer;
import dev.nekotune.spearfishing.compat.nomansland.NoMansLandCompat;

import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = SpearFishing.MOD_ID, dist = Dist.CLIENT)
public class SpearFishingClient$NeoForge {

    public SpearFishingClient$NeoForge(final IEventBus eventBus) {
        SpearFishingClient.init();

        eventBus.addListener((final EntityRenderersEvent.RegisterRenderers event) -> {
            SpearFishingClient.registerEntityRenderers(event::registerEntityRenderer);
            if (ModList.get().isLoaded(NoMansLandCompat.MOD_ID)) {
                event.registerEntityRenderer(NoMansLandCompat.THROWN_INCENDIARY_SPEAR.get(), ThrownSpearRenderer::new);
            }
        });

        // ItemProperties isn't thread-safe, so register on the main thread.
        eventBus.addListener((final FMLClientSetupEvent event) -> event.enqueueWork(
                () -> SpearFishingClient.registerItemProperties(ItemProperties::register)));
    }
}
