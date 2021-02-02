/*
 * Copyright (c) 2020 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.server.command;

import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {

            LiteralCommandNode<ServerCommandSource> dimensiontp_root = commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .executes(GalacticraftCommands::teleport)));
            // The entity teleporting code is bugged right now, I'll fix it later
            /* LiteralCommandNode<ServerCommandSource> dimensiontp_entities = commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(((GalacticraftCommands::teleportMultiple))))));*/
            LiteralCommandNode<ServerCommandSource> dimensiontp_pos = commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .executes(GalacticraftCommands::teleportToCoords))));

            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("dimtp").redirect(dimensiontp_root));
            //commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("dimtp").redirect(dimensiontp_entities));
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("dimtp").redirect(dimensiontp_pos));

            commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("gcr_listbodies")
                    .executes(GalacticraftCommands::listBodies));
        });
    }

    private static int listBodies(CommandContext<ServerCommandSource> context) {
        StringBuilder builder = new StringBuilder();
        CelestialBodyType.getAll().forEach(celestialBodyType -> builder.append(celestialBodyType.getTranslationKey()).append("\n"));
        context.getSource().sendFeedback(new LiteralText(builder.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int teleport(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    return;
                }
                BlockPos pos = getValidTeleportPos(serverWorld, player);
                player.teleport(serverWorld,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.yaw,
                        player.pitch);
                context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.single", serverWorld.getRegistryKey().getValue()), true);

            } catch (CommandSyntaxException ignore) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
        });
        return -1;
    }

    private static int teleportMultiple(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    return;
                }
                Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                entities.forEach((Consumer<Entity>) entity -> {
                    BlockPos pos = getValidTeleportPos(serverWorld, entity);
                    if (entity instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)entity).teleport(serverWorld,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                entity.yaw,
                                entity.pitch);
                    } else {
                        entity.moveToWorld(serverWorld);
                        entity.teleport(pos.getX(), pos.getY(), pos.getZ());
                    }
                    context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.multiple", entities.size(), serverWorld.getRegistryKey().getValue()), true);
                });
            } catch (CommandSyntaxException ignore) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
        });
        return -1;
    }

    private static int teleportToCoords(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                if (serverWorld == null || pos == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    return;
                }
                ServerPlayerEntity player = context.getSource().getPlayer();
                player.teleport(serverWorld,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.yaw,
                        player.pitch);
                context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.pos", serverWorld.getRegistryKey().getValue(), pos.getX(), pos.getY(), pos.getZ()), true);
            } catch (CommandSyntaxException ignore) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
        });
        return -1;
    }

    /**
     * Finds the highest solid block in the world to teleport to.
     * @param world The ServerWorld.
     * @param entity The entity to teleport.
     * @return A valid position (BlockPos) to teleport to.
     */
    private static BlockPos getValidTeleportPos(ServerWorld world, Entity entity) {
        int playerX = (int) entity.getX();
        int playerZ = (int) entity.getZ();

        for (int i = world.getHeight(); i > 0; i-- ) {
            BlockPos.Mutable pos = new BlockPos.Mutable(playerX, i, playerZ);
            Block currentBlock = world.getBlockState(pos).getBlock();
            if (!currentBlock.getDefaultState().isAir()) {
                pos.setY(pos.getY() + 1);
                return pos;
            }
        }
        // SHOULD NOT happen! Entity gets teleported to where they were before.
        return entity.getBlockPos();
    }
}