package dev.nekotune.spearfishing.client.render.entity;

import dev.nekotune.spearfishing.shared.registry.content.ModEntityTypes;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Registers this mod's entity renderers.
 */
public final class EntityRenderersHandler {

    public static void register(final Registrar registrar) {
        registrar.register(ModEntityTypes.THROWN_SPEAR.get(), ThrownSpearRenderer::new);
    }

    /**
     * Loader-specific entity renderer registration
     */
    @FunctionalInterface
    public interface Registrar {
        <T extends Entity> void register(final EntityType<? extends T> type, final EntityRendererProvider<T> provider);
    }

    private EntityRenderersHandler() {
    }
}
