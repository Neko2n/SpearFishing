package dev.nekotune.spearfishing.shared.definition.item.tool;

import dev.nekotune.spearfishing.shared.definition.entity.projectile.ThrownSpear;
import dev.nekotune.spearfishing.shared.logic.SpearBehavior;
import dev.nekotune.spearfishing.shared.registry.ModSoundEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Prey damage and quick drop pickup are handled by {@link SpearBehavior} for
 * every item in {@link dev.nekotune.spearfishing.shared.registry.ModTags.Items#SPEARS}.
 */
public class SpearItem extends TieredItem implements ProjectileItem {
    protected static final RandomSource random = RandomSource.create();

    protected final float throwDamage;

    public SpearItem(final Tier tier, final int attackDamageBonus,
            final float attackSpeed, final Properties properties) {
        super(tier, properties.attributes(
                SpearItem.createAttributes(tier, attackDamageBonus, attackSpeed)));
        throwDamage = attackDamageBonus + tier.getAttackDamageBonus() + 4.0f;
    }

    protected static ItemAttributeModifiers createAttributes(final Tier tier,
            final int attackDamage, final float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                                (double) (attackDamage + tier.getAttackDamageBonus()),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) attackSpeed,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean canAttackBlock(final BlockState state, final Level level,
            final BlockPos pos, final Player player) {
        return !player.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(final ItemStack stack, final LivingEntity entity) {
        return 72000;
    }

    @Override
    public void releaseUsing(final ItemStack stack, final Level level,
            final LivingEntity entityLiving, final int timeLeft) {
        if (!(entityLiving instanceof final Player player))
            return;
        int i = this.getUseDuration(stack, entityLiving) - timeLeft;
        if (i < 10)
            return;
        if (isTooDamagedToUse(stack))
            return;
        if (!level.isClientSide) {

            final ThrownSpear thrownSpear = asProjectile(level, player.position().add(0.0f, 1.65f, 0.0f), stack,
                    player.getDirection());
            thrownSpear.setOwner(player);
            if (player.hasInfiniteMaterials()) {
                thrownSpear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            } else {
                thrownSpear.pickup = AbstractArrow.Pickup.ALLOWED;
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
                player.getInventory().removeItem(stack);
            }
            thrownSpear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 1.0F);

            level.addFreshEntity(thrownSpear);
            final float fv = 0.2F;
            final float pitch = random.nextFloat() * fv + 1.0F - (fv / 2.0F);
            level.playSound(null, thrownSpear,
                    ModSoundEvents.SPEAR_THROW.get(), SoundSource.PLAYERS,
                    1.0F, pitch);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        if (isTooDamagedToUse(itemstack)) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    private static boolean isTooDamagedToUse(final ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    // @Override
    // public boolean canPerformAction(final ItemStack stack, final
    // net.neoforged.neoforge.common.ItemAbility itemAbility) {
    // return ItemAbilities.DEFAULT_TRIDENT_ACTIONS.contains(itemAbility);
    // }

    @Override
    public ThrownSpear asProjectile(final Level level, final Position pos, final ItemStack stack,
            final Direction direction) {
        return new ThrownSpear(throwDamage,
                level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
    }
}
