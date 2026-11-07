package dev.nekotune.spearfishing.client.logic;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.shared.definition.item.tool.SpearItem;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Registers this mod's item model properties
 */
public final class RegisterItemProperties {

    public static final ResourceLocation THROWING = ResourceLocation.fromNamespaceAndPath(SpearFishing.MOD_ID,
            "throwing");

    public static void register(final Registrar registrar) {
        BuiltInRegistries.ITEM.entrySet().forEach(entry -> {
            final Item item = entry.getValue();
            if (item instanceof SpearItem
                    && entry.getKey().location().getNamespace().equals(SpearFishing.MOD_ID)) {
                registrar.register(item, THROWING,
                        (stack, level, entity, seed) -> entity != null && entity.isUsingItem()
                                && entity.getUseItem() == stack ? 1.0F : 0.0F);
            }
        });
    }

    /**
     * Loader-specific item property registration
     */
    @FunctionalInterface
    public interface Registrar {
        void register(final Item item, final ResourceLocation id, final ClampedItemPropertyFunction function);
    }

    private RegisterItemProperties() {
    }
}
