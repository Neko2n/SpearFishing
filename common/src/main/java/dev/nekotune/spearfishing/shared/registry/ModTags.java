package dev.nekotune.spearfishing.shared.registry;

import dev.nekotune.spearfishing.SpearFishing;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public final class ModTags {
    private ModTags() {
    }

    public static final class Items {
        private Items() {
        }

        /** Items with spear fishing melee behavior (prey damage, quick drop pickup) */
        public static final TagKey<Item> SPEARS = create("spears");

        private static TagKey<Item> create(final String name) {
            return TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath(SpearFishing.MOD_ID, name));
        }
    }

    public static final class EntityTypes {
        private EntityTypes() {
        }

        /** Entities that spears deal extra damage to */
        public static final TagKey<EntityType<?>> SPEAR_PREY = create("spear_prey");

        private static TagKey<EntityType<?>> create(final String name) {
            return TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(SpearFishing.MOD_ID, name));
        }
    }
}
