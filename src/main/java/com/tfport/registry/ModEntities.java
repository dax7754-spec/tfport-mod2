package com.tfport.registry;

import com.tfport.TFPort;
import com.tfport.entity.RocketEntity;
import com.tfport.entity.TFBotEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TFPort.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<RocketEntity>> ROCKET = ENTITIES.register("rocket",
            () -> EntityType.Builder.<RocketEntity>of(RocketEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build("rocket"));

    public static final DeferredHolder<EntityType<?>, EntityType<TFBotEntity>> TF_BOT = ENTITIES.register("tf_bot",
            () -> EntityType.Builder.<TFBotEntity>of(TFBotEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("tf_bot"));

    private ModEntities() {}
}
