package io.github.teamgalacticraft.galacticraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.teamgalacticraft.galacticraft.world.dimension.GalacticraftDimensions;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class GalacticraftCommands {
    public static void register() {
        CommandRegistry instance = CommandRegistry.INSTANCE;

        instance.register(false, source -> {
            source.register(
                    literal("dimensiontp")
                            .then(CommandManager.argument("dimension", DimensionArgumentType.create())
                                    .executes(context -> {
                                        try {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            DimensionType dim = DimensionArgumentType.getDimensionArgument(context, "dimension");
                                            message(context, "Teleported to " + Registry.DIMENSION.getId(dim));
                                            GalacticraftDimensions.teleport(context.getSource(), player, context.getSource().getMinecraftServer().getWorld(dim));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            message(context, TextFormat.RED + "Error: " + e.getMessage());
                                        }
                                        return 1;
                                    }))
                            .executes(context -> missingArgs(context, "<dimension>")));
        });
    }

    private static int missingArgs(CommandContext<ServerCommandSource> context, String args) throws CommandSyntaxException {
        message(context, "Missing arguments for command: " + args);
        return 0;
    }

    private static void message(CommandContext<ServerCommandSource> context, String message) throws CommandSyntaxException {
        context.getSource().getPlayer().sendChatMessage(new StringTextComponent(message), ChatMessageType.CHAT);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }
}