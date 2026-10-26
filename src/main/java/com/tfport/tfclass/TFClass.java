package com.tfport.tfclass;

import com.tfport.registry.ModItems;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Игровые классы. Показатели переведены в масштаб Майнкрафта (20 HP по умолчанию).
 */
public enum TFClass {
    SCOUT("scout", 0.0, 0.33, 3.0,
            () -> List.<ItemLike>of(ModItems.SCATTERGUN, ModItems.BAT)),
    SOLDIER("soldier", 12.0, -0.20, 2.0,
            () -> List.<ItemLike>of(ModItems.ROCKET_LAUNCHER, ModItems.EQUALIZER)),
    HEAVY("heavy", 28.0, -0.23, 6.0,
            () -> List.<ItemLike>of(ModItems.MINIGUN, ModItems.SANDVICH)),
    MEDIC("medic", 4.0, 0.07, 2.0,
            () -> List.<ItemLike>of(ModItems.MEDIGUN, ModItems.BONESAW));

    /** Идентификатор для команд, текстур и перевода. */
    public final String id;
    /** Добавка к максимальному здоровью (к базовым 20). */
    public final double bonusHealth;
    /** Изменение скорости относительно базовой (+0.33 = на 33% быстрее). */
    public final double speedDelta;
    /** Урон ближнего боя у бота этого класса. */
    public final double botMeleeDamage;

    private final Supplier<List<ItemLike>> kit;

    TFClass(String id, double bonusHealth, double speedDelta, double botMeleeDamage, Supplier<List<ItemLike>> kit) {
        this.id = id;
        this.bonusHealth = bonusHealth;
        this.speedDelta = speedDelta;
        this.botMeleeDamage = botMeleeDamage;
        this.kit = kit;
    }

    public List<ItemLike> kitItems() {
        return kit.get();
    }

    /** Основное оружие класса — его бот держит в руке. */
    public ItemStack primaryWeapon() {
        return new ItemStack(kit.get().get(0));
    }

    public static TFClass byId(String id) {
        String key = id.toLowerCase(Locale.ROOT);
        for (TFClass c : values()) {
            if (c.id.equals(key)) {
                return c;
            }
        }
        return null;
    }

    public static TFClass byOrdinal(int ordinal) {
        TFClass[] all = values();
        return all[Math.floorMod(ordinal, all.length)];
    }
}
