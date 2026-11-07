package dev.nekotune.spearfishing.shared.registry;

import java.util.function.Supplier;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSoundEvents {
    private static final IRegistrationProvider<SoundEvent> SOUND_EVENTS = IRegistrationProvider
            .get(Registries.SOUND_EVENT, SpearFishing.MOD_ID);

    public static final Supplier<SoundEvent> SPEAR_THROW;
    public static final Supplier<SoundEvent> SPEAR_HIT;
    public static final Supplier<SoundEvent> SPEAR_HIT_GROUND;

    static {
        SPEAR_THROW = register("spear_throw");
        SPEAR_HIT = register("spear_hit");
        SPEAR_HIT_GROUND = register("spear_hit_ground");
    }

    private static Supplier<SoundEvent> register(final String name) {
        final ResourceLocation location = ResourceLocation.fromNamespaceAndPath(SpearFishing.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }

    public static void init() {
    }

    private ModSoundEvents() {
    }
}
