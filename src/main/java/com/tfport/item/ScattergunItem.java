package com.tfport.item;

import com.tfport.util.Hitscan;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Дробовик скаута: 10 дробинок за выстрел. */
public class ScattergunItem extends Item {
    public ScattergunItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Hitscan.volley(player, 10, 25.0, 0.10, 1.0F);
            Hitscan.playSound(player, SoundEvents.CROSSBOW_SHOOT, 1.0F, 0.8F);
        }
        player.getCooldowns().addCooldown(this, 14);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
