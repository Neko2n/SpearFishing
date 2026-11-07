package dev.nekotune.spearfishing.client;

import dev.nekotune.spearfishing.client.logic.RegisterItemProperties;
import dev.nekotune.spearfishing.client.render.entity.EntityRenderersHandler;

public final class SpearFishingClient {

    public static void init() {
    }

    /**
     * Called when the loader lets mods register entity renderers.
     */
    public static void registerEntityRenderers(final EntityRenderersHandler.Registrar registrar) {
        EntityRenderersHandler.register(registrar);
    }

    /**
     * Called once items are registered and it is safe to register item model properties.
     */
    public static void registerItemProperties(final RegisterItemProperties.Registrar registrar) {
        RegisterItemProperties.register(registrar);
    }

    private SpearFishingClient() {
    }
}
