package dev.nekotune.spearfishing.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.nekotune.spearfishing.shared.event.DropCapturingEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Entity.class)
public abstract class EntityMixin implements DropCapturingEntity {

    @Unique
    @Nullable
    private List<ItemEntity> spearfishing$capturedDrops;

    @Override
    public @Nullable List<ItemEntity> spearfishing$getCapturedDrops() {
        return spearfishing$capturedDrops;
    }

    @Override
    public void spearfishing$setCapturedDrops(@Nullable final List<ItemEntity> drops) {
        spearfishing$capturedDrops = drops;
    }

    /**
     * Diverts spawned items into the capture list while drops are being captured.
     */
    @WrapOperation(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean spearfishing$captureDrop(final Level level, final Entity entity,
            final Operation<Boolean> original, final ItemStack stack, final float offsetY) {
        if (spearfishing$capturedDrops != null && entity instanceof final ItemEntity item) {
            spearfishing$capturedDrops.add(item);
            return true;
        }
        return original.call(level, entity);
    }
}
