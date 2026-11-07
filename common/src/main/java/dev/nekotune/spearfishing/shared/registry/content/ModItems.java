package dev.nekotune.spearfishing.shared.registry.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import dev.nekotune.spearfishing.shared.definition.item.tool.SpearItem;
import dev.nekotune.spearfishing.platform.services.IRegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;

public final class ModItems {

    private static final IRegistrationProvider<Item> ITEMS = IRegistrationProvider.get(Registries.ITEM,
            SpearFishing.MOD_ID);

    public static final Supplier<SpearItem> WOODEN_SPEAR;

    public static final class Properties {
        public static final Item.Properties WOODEN_SPEAR = new Item.Properties()
                .durability(SpearFishingConfig.SPEAR_DURABILITY.get())
                .rarity(Rarity.COMMON);
    }

    static {
        WOODEN_SPEAR = new Builder<SpearItem>(
                "wooden_spear",
                () -> new SpearItem(Tiers.WOOD, 3, -3.2F, Properties.WOODEN_SPEAR))
                .register();
    }

    public static void init() {
    }

    private ModItems() {
    }

    /**
     * Class to build custom items.
     */
    public static class Builder<T extends Item> extends Item.Properties {
        private final String name;
        private final Supplier<T> provider;
        private final Collection<Consumer<T>> mutators = new ArrayList<>();

        /**
         * Creates the definition for a new item type to be registered.
         * 
         * @param name     The name ID of the item to register.
         * @param provider The supplier to create an instance of the item class from a
         *                 given set of item properties.
         */
        public Builder(final String name, final Supplier<T> provider) {
            this.name = name;
            this.provider = provider;
        }

        /**
         * Registers the item with the deferred register.
         *
         * @return A supplier of the registered item.
         */
        public Supplier<T> register() {
            return ITEMS.register(this.name, () -> {
                final T item = this.provider.get();
                this.mutators.forEach(m -> m.accept(item));
                return item;
            });
        }
    }
}
