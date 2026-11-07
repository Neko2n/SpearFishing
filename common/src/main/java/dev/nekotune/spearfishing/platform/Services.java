package dev.nekotune.spearfishing.platform;

import dev.nekotune.spearfishing.platform.services.IEventHelper;
import dev.nekotune.spearfishing.platform.services.IPlatformHelper;
import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;
import dev.nekotune.spearfishing.SpearFishing;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

public final class Services {
    public static final Supplier<IPlatformHelper> PLATFORM = Suppliers.memoize(
            () -> load(IPlatformHelper.class));
    public static final Supplier<IRegistrationProvider.Factory> REGISTRATION = Suppliers.memoize(
            () -> load(IRegistrationProvider.Factory.class));
    public static final Supplier<IEventHelper> EVENTS = Suppliers.memoize(
            () -> load(IEventHelper.class));

    private static Optional<ClassLoader> loader = Optional.empty();

    /**
     * Initializes the service loader to use the given class loader.
     * 
     * @param loader The class loader to load services with.
     */
    public static void init(final ClassLoader loader) {
        Services.loader = Optional.of(loader);
    }

    /**
     * Loads a platform-specific service class.
     * 
     * @param <T>   The class type
     * @param clazz The class to load
     * @return The loaded service
     */
    private static <T> T load(final Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, loader.orElseThrow())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        SpearFishing.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}