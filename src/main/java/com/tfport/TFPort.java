package com.tfport;

import com.tfport.entity.TFBotEntity;
import com.tfport.registry.ModCreativeTabs;
import com.tfport.registry.ModEntities;
import com.tfport.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(TFPort.MODID)
public class TFPort {
    public static final String MODID = "tfport";

    public TFPort(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(TFPort::registerAttributes);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TF_BOT.get(), TFBotEntity.createAttributes().build());
    }
}
