package com.tfport.item;

import com.tfport.util.Hitscan;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Медиган: зажмите ПКМ, наведясь на союзника (не враждебного моба) — он лечится. */
public class MedigunItem extends Item {
    private static final double RANGE = 7.0;
    private static final int HEAL_INTERVAL = 10;
    private static final float HEAL_AMOUNT = 1.0F;

    public MedigunItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        LivingEntity target = Hitscan.findLivingTarget(user, RANGE);
        if (target == null || target instanceof Enemy) {
            return;
        }

        Vec3 from = user.getEyePosition().add(user.getLookAngle().scale(0.6)).subtract(0, 0.25, 0);
        Vec3 to = target.position().add(0, target.getBbHeight() * 0.6, 0);
        for (int i = 0; i <= 8; i++) {
            Vec3 p = from.lerp(to, i / 8.0);
            server.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }

        int used = getUseDuration(stack) - remainingUseDuration;
        if (used % HEAL_INTERVAL == 0) {
            target.heal(HEAL_AMOUNT);
            server.sendParticles(ParticleTypes.HEART, target.getX(), target.getEyeY() + 0.3, target.getZ(),
                    1, 0.2, 0.1, 0.2, 0);
        }
    }
}
