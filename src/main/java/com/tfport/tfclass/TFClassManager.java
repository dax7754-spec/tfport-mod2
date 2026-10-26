package com.tfport.tfclass;

import com.tfport.TFPort;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Хранит класс игрока и применяет к нему здоровье и скорость. */
@EventBusSubscriber(modid = TFPort.MODID)
public final class TFClassManager {
    private static final String KEY = "tfport_class";
    private static final ResourceLocation HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(TFPort.MODID, "class_health");
    private static final ResourceLocation SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(TFPort.MODID, "class_speed");

    private TFClassManager() {}

    public static Optional<TFClass> get(Player player) {
        if (!player.getPersistentData().contains(KEY)) {
            return Optional.empty();
        }
        return Optional.ofNullable(TFClass.byId(player.getPersistentData().getString(KEY)));
    }

    /** Устанавливает класс (null — сбросить) и применяет характеристики. */
    public static void set(ServerPlayer player, @Nullable TFClass tfClass) {
        if (tfClass == null) {
            player.getPersistentData().remove(KEY);
        } else {
            player.getPersistentData().putString(KEY, tfClass.id);
        }
        apply(player);
        player.setHealth(player.getMaxHealth());
    }

    public static void giveKit(ServerPlayer player, TFClass tfClass) {
        for (ItemLike item : tfClass.kitItems()) {
            player.getInventory().placeItemBackInInventory(new ItemStack(item));
        }
    }

    public static void apply(ServerPlayer player) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (health == null || speed == null) {
            return;
        }
        health.removeModifier(HEALTH_ID);
        speed.removeModifier(SPEED_ID);

        get(player).ifPresent(c -> {
            health.addPermanentModifier(new AttributeModifier(
                    HEALTH_ID, c.bonusHealth, AttributeModifier.Operation.ADD_VALUE));
            speed.addPermanentModifier(new AttributeModifier(
                    SPEED_ID, c.speedDelta, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        });

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            apply(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        // Класс переживает смерть.
        String saved = event.getOriginal().getPersistentData().getString(KEY);
        if (!saved.isEmpty()) {
            event.getEntity().getPersistentData().putString(KEY, saved);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            apply(player);
            player.setHealth(player.getMaxHealth());
        }
    }
}
