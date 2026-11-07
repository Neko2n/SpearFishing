package dev.nekotune.spearfishing.shared.registry.content;

import java.util.function.Supplier;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.shared.definition.entity.projectile.ThrownSpear;
import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntityTypes {

    private static final IRegistrationProvider<EntityType<?>> ENTITY_TYPES = IRegistrationProvider
            .get(Registries.ENTITY_TYPE, SpearFishing.MOD_ID);

    public static final Supplier<EntityType<ThrownSpear>> THROWN_SPEAR;

    static {
        THROWN_SPEAR = register("thrown_spear",
            EntityType.Builder.<ThrownSpear>of(ThrownSpear::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .clientTrackingRange(4)
                    .updateInterval(20));
    }

    private static <T extends Entity> Supplier<EntityType<T>> register(final String name,
            final EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(name));
    }

    public static void init() {
    }

    private ModEntityTypes() {
    }
}
