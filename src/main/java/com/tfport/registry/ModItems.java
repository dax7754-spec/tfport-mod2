package com.tfport.registry;

import com.tfport.TFPort;
import com.tfport.item.MedigunItem;
import com.tfport.item.MinigunItem;
import com.tfport.item.RocketLauncherItem;
import com.tfport.item.ScattergunItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TFPort.MODID);

    // Скаут
    public static final DeferredItem<ScattergunItem> SCATTERGUN =
            ITEMS.registerItem("scattergun", ScattergunItem::new);
    public static final DeferredItem<SwordItem> BAT = ITEMS.registerItem("bat",
            p -> new SwordItem(Tiers.WOOD, p.attributes(SwordItem.createAttributes(Tiers.WOOD, 3, -1.6F))));

    // Солдат
    public static final DeferredItem<RocketLauncherItem> ROCKET_LAUNCHER =
            ITEMS.registerItem("rocket_launcher", RocketLauncherItem::new);
    public static final DeferredItem<SwordItem> EQUALIZER = ITEMS.registerItem("equalizer",
            p -> new SwordItem(Tiers.IRON, p.attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F))));

    // Тяжёлый
    public static final DeferredItem<MinigunItem> MINIGUN =
            ITEMS.registerItem("minigun", MinigunItem::new);
    public static final DeferredItem<Item> SANDVICH = ITEMS.registerItem("sandvich",
            p -> new Item(p.food(new FoodProperties.Builder()
                    .nutrition(8).saturationModifier(0.8F).alwaysEdible().build())));

    // Медик
    public static final DeferredItem<MedigunItem> MEDIGUN =
            ITEMS.registerItem("medigun", MedigunItem::new);
    public static final DeferredItem<SwordItem> BONESAW = ITEMS.registerItem("bonesaw",
            p -> new SwordItem(Tiers.IRON, p.attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.0F))));

    // Яйцо спавна бота
    public static final DeferredItem<DeferredSpawnEggItem> TF_BOT_SPAWN_EGG = ITEMS.register("tf_bot_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.TF_BOT, 0xB8312F, 0x2B2B2B, new Item.Properties()));

    private ModItems() {}
}
