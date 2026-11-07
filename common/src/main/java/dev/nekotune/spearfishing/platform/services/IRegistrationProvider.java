package dev.nekotune.spearfishing.platform.services;

import dev.nekotune.spearfishing.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface IRegistrationProvider<T> {

    /**
     * Gets a provider for the given registry and mod ID.
     *
     * @param registryKey The registry to register objects to.
     * @param modId       The namespace to register objects under.
     * @return The provider.
     */
    static <T> IRegistrationProvider<T> get(final ResourceKey<Registry<T>> registryKey, final String modId) {
        return Services.REGISTRATION.get().create(registryKey, modId);
    }

    /**
     * Registers an object.
     *
     * @param name     The path of the object's id (the namespace is the provider's mod id).
     * @param supplier A supplier creating the object. It may be called later, depending on the loader.
     * @return A reference to the registered object. Only call {@code get()} on it once registries
     *         are populated (i.e. not during static init or mod construction).
     */
    <I extends T> Supplier<I> register(final String name, final Supplier<I> supplier);

    /**
     * @return The namespace objects are registered under.
     */
    String getModId();

    /**
     * Platform-specific factory for {@link IRegistrationProvider}s.
     */
    interface Factory {
        <T> IRegistrationProvider<T> create(final ResourceKey<Registry<T>> registryKey, final String modId);
    }
}
