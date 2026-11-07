package dev.nekotune.spearfishing.shared.registry;

import dev.nekotune.spearfishing.shared.registry.content.ModEntityTypes;
import dev.nekotune.spearfishing.shared.registry.content.ModItems;
import dev.nekotune.spearfishing.shared.registry.content.ModRecipeSerializers;

public final class ModRegistries {

    public static void init() {
        ModItems.init();
        ModEntityTypes.init();
        ModSoundEvents.init();
        ModRecipeSerializers.init();
    }

    private ModRegistries() {
    }
}
