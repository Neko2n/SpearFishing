package dev.nekotune.spearfishing.shared.logic;

import java.util.Iterator;
import java.util.Optional;

import dev.nekotune.spearfishing.SpearFishing;
import dev.nekotune.spearfishing.mixin.EntitySpearMixin;
import dev.nekotune.spearfishing.mixin.LivingEntitySpearMixin;
import dev.nekotune.spearfishing.mixin.ProjectileSpearMixin;
import dev.nekotune.spearfishing.platform.Services;
import dev.nekotune.spearfishing.shared.config.SpearFishingConfig;
import dev.nekotune.spearfishing.shared.event.LivingDropsEvent;
import dev.nekotune.spearfishing.shared.registry.ModSoundEvents;
import dev.nekotune.spearfishing.shared.registry.ModTags;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Spear fishing behavior, shared by all projectiles which satisfy
 * {@link SpearBehavior#isSpear}
 * 
 * @see ProjectileSpearMixin
 * @see EntitySpearMixin
 * @see LivingEntitySpearMixin
 */
public final class SpearBehavior {

    private SpearBehavior() {
    }

    public static void init() {
        Services.EVENTS.get().subscribe(SpearBehavior::onLivingDrops);
    }

    private static void onLivingDrops(final LivingDropsEvent event) {
        final DamageSource source = event.getSource();
        final Entity direct = source.getDirectEntity();

        // Thrown spears grab the drops of entities they kill
        if (isThrownSpear(direct)) {
            for (final ItemEntity drop : event.getDrops()) {
                if (drop.startRiding(direct))
                    drop.setNeverPickUp();
            }
            return;
        }

        // Spears automatically pick up drops of entities killed in melee,
        // helping collect fish more easily
        if (direct instanceof final Player player && isSpear(source.getWeaponItem())) {
            final Iterator<ItemEntity> it = event.getDrops().iterator();
            while (it.hasNext()) {
                final ItemStack stack = it.next().getItem();
                player.getInventory().add(stack);
                if (stack.isEmpty())
                    it.remove();
            }
        }
    }

    /**
     * @return Whether the item has spear fishing melee behavior.
     */
    public static boolean isSpear(final ItemStack stack) {
        if (stack == null)
            return false;
        final boolean r = stack.is(ModTags.Items.SPEARS);
        if (r) {
            SpearFishing.LOG.debug("Item is a spear!");
        }
        return r;
    }

    /**
     * @return Whether the entity is a projectile with spear fishing behavior.
     */
    public static boolean isThrownSpear(final Entity entity) {
        if (entity instanceof final Projectile projectile) {
            final Optional<ItemStack> weaponItem = Optional.ofNullable(projectile.getWeaponItem());
            return weaponItem.map(SpearBehavior::isSpear).orElse(false);
        }
        return false;
    }

    /**
     * @return Whether spears deal bonus damage to the entity.
     */
    public static boolean isSpearPrey(final Entity entity) {
        return entity.getType().is(ModTags.EntityTypes.SPEAR_PREY);
    }

    public static float getSpearPreyDamageMultiplier() {
        return SpearFishingConfig.SPEAR_PREY_DAMAGE_MULTIPLIER.get().floatValue();
    }

    /**
     * Applies the spear prey damage bonus to damage dealt by a thrown spear,
     * or by a melee attack with a spear.
     */
    public static float modifyIncomingDamage(final LivingEntity target, final DamageSource source,
            final float amount) {
        return isSpearPrey(target) && isSpearDamage(source)
                ? amount * getSpearPreyDamageMultiplier()
                : amount;
    }

    /**
     * @return Whether the damage was dealt by a thrown spear, or by an entity
     *         hitting directly with a spear (not through a projectile, so the
     *         bonus is never applied twice).
     */
    public static boolean isSpearDamage(final DamageSource source) {
        final Entity direct = source.getDirectEntity();
        return isThrownSpear(direct)
                || direct instanceof LivingEntity && isSpear(source.getWeaponItem());
    }

    /**
     * Whether a spear should skewer the entity it killed, carrying it as a
     * passenger.
     */
    public static boolean shouldStick(final LivingEntity target) {
        return !target.isAlive() && !(target instanceof Player)
                && target.getBoundingBox().getSize() <= 1.0D;
    }

    /**
     * Called once an entity has died. Skewers it onto the thrown spear that
     * killed it, if any.
     */
    public static void onDeath(final LivingEntity target, final DamageSource source) {
        if (target.level().isClientSide)
            return;
        final Entity spear = source.getDirectEntity();
        if (isThrownSpear(spear) && !spear.isRemoved() && shouldStick(target)) {
            target.startRiding(spear);
        }
    }

    /**
     * Which entities a thrown spear can carry.
     */
    public static boolean canCarry(final Entity passenger) {
        return passenger instanceof LivingEntity
                || passenger instanceof ItemEntity;
    }

    /**
     * Whether a thrown spear may hit (and so grab) an item entity.
     */
    public static boolean canGrabItem(final Projectile spear, final ItemEntity item) {
        return !spear.getPassengers().contains(item);
    }

    /**
     * Grabs an item entity the spear flew into.
     */
    public static void grabItem(final Projectile spear, final ItemEntity item) {
        if (!item.startRiding(spear))
            return;
        item.setNeverPickUp();

        final Level level = item.level();
        level.playSound(null, item.blockPosition(),
                ModSoundEvents.SPEAR_HIT.get(), SoundSource.BLOCKS,
                0.8f, 1.4f);
        if (level instanceof final ServerLevel serverlevel) {
            serverlevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item.getItem()),
                    item.getX(), item.getY() + item.getBbHeight() * 0.5, item.getZ(),
                    6, 0, 0, 0, 0.1);
        }
    }

    /**
     * Called when a passenger stops riding a thrown spear.
     */
    public static void onPassengerRemoved(final Entity passenger) {
        if (passenger instanceof final ItemEntity item) {
            item.setDefaultPickUpDelay();
        }
    }

    /**
     * Projectiles ignore block hits on ticks when they hit an entity, which
     * would let a spear phase through a block right behind something it hit.
     *
     * @return The block in the spear's path this tick, or {@code null} if none.
     */
    public static Optional<BlockHitResult> findBlockAhead(final Projectile spear) {
        final Vec3 start = spear.position();
        final Vec3 end = start.add(spear.getDeltaMovement());
        final BlockHitResult blockHit = spear.level()
                .clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, spear));
        return blockHit.getType() == HitResult.Type.MISS ? Optional.empty() : Optional.of(blockHit);
    }
}
