package com.tfport.tfclass;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tfport.TFPort;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** /tfclass scout|soldier|heavy|medic|none */
@EventBusSubscriber(modid = TFPort.MODID)
public final class TFClassCommand {
    private TFClassCommand() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tfclass")
                .then(Commands.argument("class", StringArgumentType.word())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                Stream.concat(
                                        Stream.of(TFClass.values()).map(c -> c.id),
                                        Stream.of("none")),
                                builder))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String arg = StringArgumentType.getString(ctx, "class");

                            if (arg.equalsIgnoreCase("none")) {
                                TFClassManager.set(player, null);
                                ctx.getSource().sendSuccess(
                                        () -> Component.translatable("message.tfport.class_cleared"), false);
                                return 1;
                            }

                            TFClass tfClass = TFClass.byId(arg);
                            if (tfClass == null) {
                                throw new SimpleCommandExceptionType(
                                        Component.translatable("message.tfport.class_unknown", arg)).create();
                            }
                            TFClassManager.set(player, tfClass);
                            TFClassManager.giveKit(player, tfClass);
                            ctx.getSource().sendSuccess(() -> Component.translatable(
                                    "message.tfport.class_set",
                                    Component.translatable("tfclass.tfport." + tfClass.id)), false);
                            return 1;
                        })));
    }
}
