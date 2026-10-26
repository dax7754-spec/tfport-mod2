package com.tfport.item;

import com.tfport.entity.RocketEntity;
import com.tfport.util.Hitscan;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Ракетомёт солдата. */
public class RocketLauncherItem extends Item {
    public RocketLauncherItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            RocketEntity rocket = new RocketEntity(level, player);
            rocket.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.0F);
            level.addFreshEntity(rocket);
            Hitscan.playSound(player, SoundEvents.FIREWORK_ROCKET_LAUNCH, 1.0F, 0.9F);
        }
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
