package dev.nekotune.spearfishing.shared.definition.entity.projectile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import dev.nekotune.spearfishing.shared.logic.SpearBehavior;
import dev.nekotune.spearfishing.shared.registry.ModSoundEvents;
import dev.nekotune.spearfishing.shared.registry.ModTags;
import dev.nekotune.spearfishing.shared.registry.content.ModEntityTypes;
import dev.nekotune.spearfishing.shared.registry.content.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownSpear extends AbstractArrow implements ItemSupplier {

    private static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(ThrownSpear.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(ThrownSpear.class,
            EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrownSpear.class,
            EntityDataSerializers.ITEM_STACK);

    private boolean dealtDamage;
    public int clientSideReturnSpearTickCount;
    public final Collection<Entity> hitEntities = new HashSet<>();
    private final float throwDamage;

    public ThrownSpear(final EntityType<? extends ThrownSpear> entityType, final Level level) {
        super(entityType, level);
        this.throwDamage = 2.0F;
    }

    public ThrownSpear(final float throwDamage, final Level level,
            final double x, final double y, final double z,
            final ItemStack pickupItemStack) {
        this(ModEntityTypes.THROWN_SPEAR.get(), throwDamage, level, x, y, z, pickupItemStack);
    }

    /**
     * For subclasses that register their own entity type, so they are saved and
     * synced to clients as themselves rather than as a plain spear.
     */
    protected ThrownSpear(final EntityType<? extends ThrownSpear> entityType, final float throwDamage,
            final Level level, final double x, final double y, final double z,
            final ItemStack pickupItemStack) {
        super(entityType, x, y, z, level, pickupItemStack, pickupItemStack);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(pickupItemStack));
        this.entityData.set(ID_FOIL, pickupItemStack.hasFoil());
        this.entityData.set(DATA_ITEM_STACK, pickupItemStack.copy());
        this.throwDamage = throwDamage;
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte) 0);
        builder.define(ID_FOIL, false);
        builder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {

        // Update loyalty recall state
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        final Entity entity = this.getOwner();
        final int i = this.entityData.get(ID_LOYALTY);
        if (i > 0 && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.discard();
            } else {
                this.setNoPhysics(true);
                final Vec3 vec3 = entity.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double) i, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }
                final double d0 = 0.05 * (double) i;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnSpearTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }
                this.clientSideReturnSpearTickCount++;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        final Entity entity = this.getOwner();
        return entity == null || !entity.isAlive() ? false : !(entity instanceof ServerPlayer) || !entity.isSpectator();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    protected boolean canHitEntity(final Entity target) {
        if (hitEntities.contains(target))
            return false;
        return super.canHitEntity(target);
    }

    /**
     * Gets the EntityHitResult representing the entity hit
     */
    @Nullable
    @Override
    protected EntityHitResult findHitEntity(final Vec3 startVec, final Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    /**
     * Called when the arrow hits an entity.
     * <p>
     * Item grabbing, skewering, and bonus damage to spear prey are handled by
     * {@link SpearBehavior}, since they apply to every projectile in
     * {@link ModTags.EntityTypes#THROWN_SPEARS}.
     */
    @Override
    protected void onHitEntity(final EntityHitResult result) {
        final Entity entity = result.getEntity();

        // Calculate damage
        float f = this.throwDamage;
        final Entity entity1 = this.getOwner();
        final DamageSource damagesource = this.damageSources().thrown(this,
                (Entity) (entity1 == null ? this : entity1));
        if (this.level() instanceof final ServerLevel serverlevel) {
            f = EnchantmentHelper.modifyDamage(serverlevel, this.getWeaponItem(), entity, damagesource, f);
        }

        this.dealtDamage = true;

        // Attempt to apply damage
        this.hitEntities.add(entity);
        if (entity.hurt(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (this.level() instanceof final ServerLevel serverlevel1) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, entity, damagesource,
                        this.getWeaponItem());
            }
            if (entity instanceof final LivingEntity livingentity) {
                this.doKnockback(livingentity, damagesource);
                this.doPostHurtEffects(livingentity);
            }
        }

        // Play the entity hit sound
        final float fv = 0.5F;
        final float pitch = random.nextFloat() * fv + 1.0F - (fv / 2.0F);
        this.playSound(ModSoundEvents.SPEAR_HIT.get(), 1.0F, pitch);

        // By default, block hits are ignored on ticks when entities are hit
        final Optional<BlockHitResult> blockHit = SpearBehavior.findBlockAhead(this);
        blockHit.ifPresent(this::onHitBlock);
    }

    @Override
    protected void doPostHurtEffects(final LivingEntity target) {
        super.doPostHurtEffects(target);
        if (target.getVehicle() == this) {
            // Skewered by SpearBehavior; keep flying
        } else if (hitEntities.size() <= this.getPierceLevel()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        }
    }

    @Override
    protected void hitBlockEnchantmentEffects(final ServerLevel level,
            final BlockHitResult hitResult, final ItemStack stack) {
        Vec3 vec3 = hitResult.getBlockPos().clampLocationWithin(hitResult.getLocation());
        EnchantmentHelper.onHitBlock(
                level,
                stack,
                this.getOwner() instanceof LivingEntity livingentity ? livingentity : null,
                this,
                null,
                vec3,
                level.getBlockState(hitResult.getBlockPos()),
                p_348680_ -> this.kill());
    }

    @Override
    public ItemStack getWeaponItem() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    public ItemStack getItem() {
        return this.entityData.get(DATA_ITEM_STACK);
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player)
                || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModItems.WOODEN_SPEAR.get().getDefaultInstance();
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void playerTouch(final Player entity) {
        if (this.ownedBy(entity) || this.getOwner() == null) {
            super.playerTouch(entity);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(final CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.getPickupItemStackOrigin()));
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    private byte getLoyaltyFromItem(final ItemStack stack) {
        return this.level() instanceof ServerLevel serverlevel
                ? (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverlevel, stack, this), 0,
                        127)
                : 0;
    }

    @Override
    public void tickDespawn() {
        int i = this.entityData.get(ID_LOYALTY);
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || i <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return SpearFishingConfig.SPEAR_WATER_INERTIA.get().floatValue();
    }

    @Override
    public boolean shouldRender(final double x, final double y, final double z) {
        return true;
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSoundEvents.SPEAR_HIT_GROUND.get();
    }

    @Override
    public void remove(final RemovalReason reason) {
        super.remove(reason);
        this.hitEntities.clear();
    }
}
