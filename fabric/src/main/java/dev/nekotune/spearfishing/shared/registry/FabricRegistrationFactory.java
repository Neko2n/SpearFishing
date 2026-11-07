package dev.nekotune.spearfishing.shared.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;

public class FabricRegistrationFactory implements IRegistrationProvider.Factory {

    @Override
    public <T> IRegistrationProvider<T> create(final ResourceKey<Registry<T>> registryKey,
            final String modId) {
        
        @SuppressWarnings("unchecked")
        final Registry<T> registry = Optional.ofNullable((Registry<T>)BuiltInRegistries.REGISTRY.get(registryKey.location()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown registry: " + registryKey.location()));
        
        return new Provider<>(registry, modId);
    }

    private record Provider<T>(Registry<T> registry, String modId) implements IRegistrationProvider<T> {

        @Override
        public <I extends T> Supplier<I> register(final String name, final Supplier<I> supplier) {
            final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.modId, name);
            final I value = supplier.get();
            Registry.registerForHolder(this.registry, id, value);
            return () -> value;
        }

        @Override
        public String getModId() {
            return this.modId;
        }
    }
}
