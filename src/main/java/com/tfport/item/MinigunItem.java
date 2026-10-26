package com.tfport.item;

import com.tfport.util.Hitscan;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Миниган тяжёлого: зажмите ПКМ, через секунду начнётся стрельба. Замедляет владельца. */
public class MinigunItem extends Item {
    private static final int SPIN_UP_TICKS = 20;

    public MinigunItem(Properties properties) {
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
        if (level.isClientSide) {
            return;
        }
        int used = getUseDuration(stack) - remainingUseDuration;
        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 1, false, false));

        if (used == 1) {
            Hitscan.playSound(user, SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 0.6F);
        }
        if (used >= SPIN_UP_TICKS && used % 2 == 0) {
            Hitscan.volley(user, 1, 30.0, 0.07, 0.9F);
            Hitscan.playSound(user, SoundEvents.ARROW_SHOOT, 0.6F, 1.4F);
        }
    }
}
