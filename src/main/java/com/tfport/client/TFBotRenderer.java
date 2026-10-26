package com.tfport.client;

import com.tfport.TFPort;
import com.tfport.entity.TFBotEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TFBotRenderer extends HumanoidMobRenderer<TFBotEntity, HumanoidModel<TFBotEntity>> {
    public TFBotRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TFBotEntity bot) {
        return ResourceLocation.fromNamespaceAndPath(TFPort.MODID,
                "textures/entity/bot_" + bot.getTfClass().id + ".png");
    }
}
