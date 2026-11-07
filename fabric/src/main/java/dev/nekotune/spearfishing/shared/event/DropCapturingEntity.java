package dev.nekotune.spearfishing.shared.event;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.item.ItemEntity;

/**
 * Implemented on {@link net.minecraft.world.entity.Entity} by {@code EntityMixin}.
 * While a capture list is set, items the entity spawns via
 * {@code spawnAtLocation} are added to the list instead of the level.
 */
public interface DropCapturingEntity {

    @Nullable
    List<ItemEntity> spearfishing$getCapturedDrops();

    void spearfishing$setCapturedDrops(@Nullable final List<ItemEntity> drops);
}
