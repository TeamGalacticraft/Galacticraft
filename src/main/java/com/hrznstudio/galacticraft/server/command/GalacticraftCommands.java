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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCommands {

    private static final HashMap<UUID,Integer> GCR_HOUSTON_TIMERS = new HashMap<>();
    private static final int GCR_HOUSTON_TIMER_LENGTH = 12; // in seconds

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {

            commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("gcrhouston")
                    .executes(GalacticraftCommands::teleportToEarth));

            /* This looks convoluted, but it works. Essentially, it registers three branches of the same command.
             * One as the base, one to also teleport entities, and one to also teleport to a specific position.
             * This is because the command I added, to teleport to a specific position, breaks when combined with
             * teleporting multiple non-player entities for some reason. So, I made it where you can pick
             * teleporting entities OR setting a custom position to go to, but not both :P
             */
            LiteralCommandNode<ServerCommandSource> dimensiontp_root = commandDispatcher.register(
                    CommandManager.literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .executes(GalacticraftCommands::teleport)));
            // TODO: either fix this or remove it
            /* LiteralCommandNode<ServerCommandSource> dimensiontp_entities = commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(((GalacticraftCommands::teleportMultiple)))))); */
            LiteralCommandNode<ServerCommandSource> dimensiontp_pos = commandDispatcher.register(
                    CommandManager.literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .executes(GalacticraftCommands::teleportToCoords))));

            // Because I don't like to type
            commandDispatcher.register(CommandManager.literal("dimtp").redirect(dimensiontp_root));
            //commandDispatcher.register(CommandManager.literal("dimtp").redirect(dimensiontp_entities));
            commandDispatcher.register(CommandManager.literal("dimtp").redirect(dimensiontp_pos));

            commandDispatcher.register(
                    CommandManager.literal("gcrlistbodies")
                    .executes(GalacticraftCommands::listBodies));
        });
    }

    private static int teleportToEarth(CommandContext<ServerCommandSource> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        // Clear the expired timers
        for (UUID id : GCR_HOUSTON_TIMERS.keySet()) {
            if (GCR_HOUSTON_TIMERS.get(id) + GCR_HOUSTON_TIMER_LENGTH < (((int) System.currentTimeMillis()) / 1000)) {
                GCR_HOUSTON_TIMERS.remove(id);
            }
        }
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                ServerWorld serverWorld = context.getSource().getMinecraftServer().getWorld(World.OVERWORLD);
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getWorld().equals(serverWorld)) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.gcrhouston.on_earth_already").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                }
                UUID playerID = context.getSource().getPlayer().getGameProfile().getId();
                if (!GCR_HOUSTON_TIMERS.containsKey(playerID)) {
                    GCR_HOUSTON_TIMERS.put(playerID, ((int)System.currentTimeMillis()) / 1000);
                    context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.gcrhouston.confirm", serverWorld.getRegistryKey().getValue()).setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                } else if (GCR_HOUSTON_TIMERS.get(playerID) + GCR_HOUSTON_TIMER_LENGTH > (((int) System.currentTimeMillis()) / 1000)) {
                    GCR_HOUSTON_TIMERS.remove(playerID);
                    BlockPos pos = getValidTeleportPos(serverWorld, player);
                    player.teleport(serverWorld,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            player.yaw,
                            player.pitch);
                    context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.gcrhouston.success", serverWorld.getRegistryKey().getValue()).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), true);
                }
            } catch (CommandSyntaxException e) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int teleport(CommandContext<ServerCommandSource> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getWorld().equals(serverWorld)) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.getRegistryKey().getValue()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
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
            } catch (CommandSyntaxException e) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                retval[0] = -1;
            }
        });
        return retval[0];
    }
    /*
    private static int teleportMultiple(CommandContext<ServerCommandSource> context) {
        final int[] retval = new int[1]{Command.SINGLE_SUCCESS};
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getWorld().equals(serverWorld)) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.getRegistryKey().getValue()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                }
                Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                entities.forEach((Consumer<Entity>) entity -> {
                    BlockPos pos = getValidTeleportPos(serverWorld, entity);
                    entity.moveToWorld(serverWorld);
                    entity.teleport(pos.getX(), pos.getY(), pos.getZ());
                });
                context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.multiple", entities.size(), serverWorld.getRegistryKey().getValue()), true);
            } catch (CommandSyntaxException e) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                retval[0] = -1;
            }
        });
        return retval[0];
    } */

    private static int teleportToCoords(CommandContext<ServerCommandSource> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                if (serverWorld == null || pos == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getWorld().equals(serverWorld)) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.getRegistryKey().getValue()).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    retval[0] = -1;
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
            } catch (CommandSyntaxException e) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int listBodies(CommandContext<ServerCommandSource> context) {
        StringBuilder builder = new StringBuilder();
        CelestialBodyType.getAll().forEach(celestialBodyType -> builder.append(celestialBodyType.getTranslationKey()).append("\n"));
        context.getSource().sendFeedback(new LiteralText(builder.toString()), true);
        return Command.SINGLE_SUCCESS;
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