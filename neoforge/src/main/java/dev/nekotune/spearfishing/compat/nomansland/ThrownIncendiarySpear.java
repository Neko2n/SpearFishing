package dev.nekotune.spearfishing.compat.nomansland;

import com.farcr.nomansland.NMLConfig;
import com.farcr.nomansland.common.entity.Ember;
import dev.nekotune.spearfishing.shared.definition.entity.projectile.ThrownSpear;
import dev.nekotune.spearfishing.shared.registry.content.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Version of the wooden spear for No Man's Land
 */
public class ThrownIncendiarySpear extends ThrownSpear {

    public ThrownIncendiarySpear(final EntityType<? extends ThrownSpear> entityType, final Level level) {
        super(entityType, level);
    }

    public ThrownIncendiarySpear(final float throwDamage, final Level level,
            final double x, final double y, final double z,
            final ItemStack pickupItemStack) {
        super(NoMansLandCompat.THROWN_INCENDIARY_SPEAR.get(), throwDamage, level, x, y, z,
                pickupItemStack.transmuteCopy(ModItems.WOODEN_SPEAR.get()));
        this.setRemainingFireTicks(600);
    }

    @Override
    protected void onHitEntity(final EntityHitResult result) {
        super.onHitEntity(result);
        final Entity entity = result.getEntity();
        entity.igniteForSeconds(30.0F);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isOnFire()) {
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + 0.1, this.getZ(),
                    ((double) this.random.nextFloat() - (double) 0.5F) * 0.02, -0.01,
                    ((double) this.random.nextFloat() - (double) 0.5F) * 0.02);
        }
        this.getPassengers().forEach(passenger -> {
            if (this.isOnFire()) {
                passenger.igniteForTicks(2);
            }
        });
    }

    @Override
    protected void onHitBlock(final BlockHitResult result) {
        super.onHitBlock(result);
        final Direction direction = result.getDirection();
        final BlockPos pos = result.getBlockPos();
        final Level level = this.level();
        final BlockState state = level.getBlockState(pos);
        final BlockPos neighbourPos = pos.relative(direction);
        if (level instanceof final ServerLevel serverLevel) {
            boolean ignitedFire = false;
            if (this.isOnFire() && (Boolean) NMLConfig.INCENDIARY_ARROW_PLACES_FIRE.get()) {
                if (level.getBlockState(neighbourPos).is(Blocks.FIRE)) {
                    BlockPos.withinManhattan(neighbourPos, 1, 0, 1).forEach((firePos) -> {
                        if ((BaseFireBlock.canBePlacedAt(level, firePos, direction)
                                || level.getBlockState(firePos).isFlammable(level, firePos, direction))
                                && level.random.nextFloat() < 0.4F) {
                            level.setBlockAndUpdate(firePos, BaseFireBlock.getState(level, firePos));
                        }

                    });
                    ignitedFire = true;
                } else if (BaseFireBlock.canBePlacedAt(level, neighbourPos, direction)
                        || state.isFlammable(level, pos, direction) || direction == Direction.UP) {
                    level.setBlockAndUpdate(neighbourPos, BaseFireBlock.getState(level, neighbourPos));
                    ignitedFire = true;
                }
            }

            if (ignitedFire) {
                level.playSound(null, this.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                        1.0F, 0.9F + level.random.nextFloat() * 0.2F);
                level.playSound(null, this.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS,
                        0.6F, 0.7F + level.random.nextFloat() * 0.3F);

                for (int i = 0; i < 10; ++i) {
                    final double velX = (this.random.nextDouble() - (double) 0.5F) * 0.3;
                    final double velY = 0.05 + this.random.nextDouble() * 0.15;
                    final double velZ = (this.random.nextDouble() - (double) 0.5F) * 0.3;
                    serverLevel.sendParticles(ParticleTypes.FLAME, this.position().x, this.position().y,
                            this.position().z, 1, velX, velY, velZ, 0.1);
                    serverLevel.sendParticles(ParticleTypes.SMOKE, this.position().x, this.position().y,
                            this.position().z, 1, velX * (double) 0.5F, velY * (double) 0.5F, velZ * (double) 0.5F,
                            0.2);
                    serverLevel.sendParticles(ParticleTypes.LAVA, this.position().x, this.position().y,
                            this.position().z, 1, velX, 0.05, velZ, 0.3);
                }
            } else if (this.isOnFire()) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.position().x, this.position().y, this.position().z,
                        8, (double) 0.0F, 0.05, (double) 0.0F, 0.01);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.position().x, this.position().y,
                        this.position().z, 4, (double) 0.0F, (double) 0.0F, (double) 0.0F, 0.01);
                level.playSound((Player) null, this.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,
                        0.6F, 1.2F);
                serverLevel.sendParticles(ParticleTypes.LAVA, this.position().x, this.position().y, this.position().z,
                        4, (double) 0.0F, -0.05, (double) 0.0F, 0.01);
                if ((Boolean) NMLConfig.INCENDIARY_ARROW_PLACES_FIRE.get()) {
                    final Ember ember = new Ember(level, this.getX(), this.getY(), this.getZ());
                    level.addFreshEntity(ember);
                }

                this.clearFire();
            }

            if (this.isOnFire()) {
                this.discard();
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.position().x, this.position().y, this.position().z,
                        5, (double) 0.0F, (double) 0.0F, (double) 0.0F, 0.01);
            }
        }
    }

	@Override
    protected ItemStack getPickupItem() {
        final ItemStack stack = this.getPickupItemStackOrigin();
        return this.isOnFire()
            ? stack.transmuteCopy(NoMansLandCompat.INCENDIARY_SPEAR.get())
            : stack.transmuteCopy(ModItems.WOODEN_SPEAR.get());
    }
}
