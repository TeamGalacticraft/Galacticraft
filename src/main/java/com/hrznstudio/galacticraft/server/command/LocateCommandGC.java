package com.hrznstudio.galacticraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public class LocateCommandGC {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.locate.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("locate").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2));
        for (StructureFeature<?> feature : Registry.STRUCTURE_FEATURE) {
            builder.then(CommandManager.literal(feature.getName()).executes((commandContext) -> execute(commandContext.getSource(), feature.getName())));
        }

        dispatcher.register(builder);
    }

    private static int execute(ServerCommandSource source, String structure) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = source.getWorld().locateStructure(structure, blockPos, 100, false);
        if (blockPos2 == null) {
            throw FAILED_EXCEPTION.create();
        } else {
            return sendCoordinates(source, structure, blockPos, blockPos2, "commands.locate.success");
        }
    }

    public static int sendCoordinates(ServerCommandSource serverCommandSource, String string, BlockPos blockPos, BlockPos blockPos2, String successMessage) {
        int i = MathHelper.floor(getDistance(blockPos.getX(), blockPos.getZ(), blockPos2.getX(), blockPos2.getZ()));
        Text text = Texts.bracketed(new TranslatableText("chat.coordinates", blockPos2.getX(), "~", blockPos2.getZ())).styled((style) -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos2.getX() + " ~ " + blockPos2.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
        serverCommandSource.sendFeedback(new TranslatableText(successMessage, string, text, i), false);
        return i;
    }

    private static float getDistance(int x1, int y1, int x2, int y2) {
        int i = x2 - x1;
        int j = y2 - y1;
        return MathHelper.sqrt((float) (i * i + j * j));
    }
}
