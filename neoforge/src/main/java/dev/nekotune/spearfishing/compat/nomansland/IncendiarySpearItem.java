package dev.nekotune.spearfishing.compat.nomansland;

import dev.nekotune.spearfishing.shared.definition.entity.projectile.ThrownSpear;
import dev.nekotune.spearfishing.shared.definition.item.tool.SpearItem;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

/**
 * Version of the wooden spear for No Man's Land
 */
public class IncendiarySpearItem extends SpearItem {
    public IncendiarySpearItem(final Tier tier, final int attackDamageBonus,
            final float attackSpeed, final Properties properties) {
        super(tier, attackDamageBonus, attackSpeed, properties);
    }

    @Override
    public void postHurtEnemy(final ItemStack stack,
            final LivingEntity target, final LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        target.igniteForSeconds(30.0F);
    }

    @Override
    public ThrownSpear asProjectile(final Level level, final Position pos,
            final ItemStack stack, final Direction direction) {
        return new ThrownIncendiarySpear(throwDamage,
                level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
    }
}
