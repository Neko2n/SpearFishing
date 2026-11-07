package dev.nekotune.spearfishing.shared.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;

public class NeoForgeRegistrationFactory implements IRegistrationProvider.Factory {

    @Override
    public <T> IRegistrationProvider<T> create(final ResourceKey<Registry<T>> registryKey, final String modId) {
        final ModContainer container = ModList.get().getModContainerById(modId)
                .orElseThrow(() -> new IllegalArgumentException("Mod with ID '" + modId + "' is not loaded"));
        final IEventBus modBus = Optional.ofNullable(container.getEventBus())
                .orElseThrow(() -> new IllegalStateException("Mod '" + modId + "' has no event bus"));
        final DeferredRegister<T> register = DeferredRegister.create(registryKey, modId);
        register.register(modBus);
        return new Provider<>(register, modId);
    }

    private record Provider<T>(DeferredRegister<T> register, String modId) implements IRegistrationProvider<T> {

        @Override
        public <I extends T> Supplier<I> register(final String name, final Supplier<I> supplier) {
            return register.register(name, supplier);
        }

        @Override
        public String getModId() {
            return modId;
        }
    }
}
