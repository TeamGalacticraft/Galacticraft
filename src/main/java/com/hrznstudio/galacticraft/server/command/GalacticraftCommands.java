/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCommands {

    private static final HashMap<UUID,Integer> GCR_HOUSTON_TIMERS = new HashMap<>();
    private static final int GCR_HOUSTON_TIMER_LENGTH = 12 * 20; // seconds * tps

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {

            commandDispatcher.register(
                    Commands.literal("gcrhouston")
                    .executes(GalacticraftCommands::teleportToEarth));

            /* This looks convoluted, but it works. Essentially, it registers three branches of the same command.
             * One as the base, one to also teleport entities, and one to also teleport to a specific position.
             * This is because the command I added, to teleport to a specific position, breaks when combined with
             * teleporting multiple non-player entities for some reason. So, I made it where you can pick
             * teleporting entities OR setting a custom position to go to, but not both :P
             */
            LiteralCommandNode<CommandSourceStack> dimensiontp_root = commandDispatcher.register(
                    Commands.literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                    .then(Commands.argument("dimension", DimensionArgument.dimension())
                    .executes(GalacticraftCommands::teleport)));
            // TODO: either fix this or remove it
            /* LiteralCommandNode<ServerCommandSource> dimensiontp_entities = commandDispatcher.register(
                    LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                    .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(((GalacticraftCommands::teleportMultiple)))))); */
            LiteralCommandNode<CommandSourceStack> dimensiontp_pos = commandDispatcher.register(
                    Commands.literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                    .then(Commands.argument("dimension", DimensionArgument.dimension())
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(GalacticraftCommands::teleportToCoords))));

            // Because I don't like to type
            commandDispatcher.register(Commands.literal("dimtp").redirect(dimensiontp_root));
            //commandDispatcher.register(CommandManager.literal("dimtp").redirect(dimensiontp_entities));
            commandDispatcher.register(Commands.literal("dimtp").redirect(dimensiontp_pos));

            commandDispatcher.register(
                    Commands.literal("gcrlistbodies")
                    .executes(GalacticraftCommands::listBodies));
        });
    }

    private static int teleportToEarth(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        // Clear the expired timers
        for (UUID id : GCR_HOUSTON_TIMERS.keySet()) {
            if (GCR_HOUSTON_TIMERS.get(id) + GCR_HOUSTON_TIMER_LENGTH < context.getSource().getServer().getTickCount()) {
                GCR_HOUSTON_TIMERS.remove(id);
            }
        }
        context.getSource().getServer().execute(() -> {
            try {
                if (!CelestialBodyType.getByDimType(context.getSource().getLevel().dimension()).isPresent()) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.gcrhouston.cannot_detect_signal").setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
                ServerPlayer player = context.getSource().getPlayerOrException();
                ServerLevel serverWorld = context.getSource().getServer().getLevel(Level.OVERWORLD);
                if (serverWorld == null) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.gcrhouston.on_earth_already").setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
                UUID playerID = context.getSource().getPlayerOrException().getGameProfile().getId();
                if (!GCR_HOUSTON_TIMERS.containsKey(playerID)) {
                    GCR_HOUSTON_TIMERS.put(playerID, context.getSource().getServer().getTickCount());
                    context.getSource().sendSuccess(new TranslatableComponent("commands.galacticraft-rewoven.gcrhouston.confirm", serverWorld.dimension().location()).setStyle(Constants.Styles.RED_STYLE), false);
                } else if (GCR_HOUSTON_TIMERS.get(playerID) + GCR_HOUSTON_TIMER_LENGTH > context.getSource().getServer().getTickCount()) {
                    GCR_HOUSTON_TIMERS.remove(playerID);
                    BlockPos pos = getValidTeleportPos(serverWorld, player);
                    player.teleportTo(serverWorld,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            player.yRot,
                            player.xRot);
                    context.getSource().sendSuccess(new TranslatableComponent("commands.galacticraft-rewoven.gcrhouston.success", serverWorld.dimension().location()).setStyle(Constants.Styles.GREEN_STYLE), true);
                }
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.gchouston.error").setStyle(Constants.Styles.RED_STYLE));
                e.printStackTrace();
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int teleport(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getServer().execute(() -> {
            ServerPlayer player;
            try {
                player = context.getSource().getPlayerOrException();
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Constants.Styles.RED_STYLE));
                retval[0] = -1;
                return;
            }
            try {
                ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.dimension().location()).setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
                BlockPos pos = getValidTeleportPos(serverWorld, player);
                player.teleportTo(serverWorld,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.yRot,
                        player.xRot);
                context.getSource().sendSuccess(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.success.single", serverWorld.dimension().location()), true);
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Styles.RED_STYLE));
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
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Misc.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getWorld().equals(serverWorld)) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.getRegistryKey().getValue()).setStyle(Constants.Misc.RED_STYLE));
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
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Constants.Misc.RED_STYLE));
                retval[0] = -1;
            }
        });
        return retval[0];
    } */

    private static int teleportToCoords(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getServer().execute(() -> {
            ServerLevel serverWorld;
            BlockPos pos;
            try {
                serverWorld = DimensionArgument.getDimension(context, "dimension");
                pos = BlockPosArgument.getOrLoadBlockPos(context, "pos");
                if (serverWorld == null || pos == null) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.already_in_dimension", serverWorld.dimension().location()).setStyle(Constants.Styles.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Constants.Styles.RED_STYLE));
                retval[0] = -1;
                return;
            }
            try {
                ServerPlayer player = context.getSource().getPlayerOrException();
                player.teleportTo(serverWorld,
                        Mth.clamp(pos.getX(), -30000000, 30000000),
                        Mth.clamp(pos.getY(), 0, serverWorld.getHeight() - 1),
                        Mth.clamp(pos.getZ(), -30000000, 30000000),
                        player.yRot,
                        player.xRot);
                context.getSource().sendSuccess(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.success.pos", serverWorld.dimension().location(), pos.getX(), pos.getY(), pos.getZ()), true);
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(new TranslatableComponent("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Constants.Styles.RED_STYLE));
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int listBodies(CommandContext<CommandSourceStack> context) {
        StringBuilder builder = new StringBuilder();
        CelestialBodyType.getAll().forEach(celestialBodyType -> builder.append(celestialBodyType.getTranslationKey()).append("\n"));
        context.getSource().sendSuccess(new TextComponent(builder.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Finds the highest solid block in the world to teleport to.
     * @param world The ServerWorld.
     * @param entity The entity to teleport.
     * @return A valid position (BlockPos) to teleport to.
     */
    private static BlockPos getValidTeleportPos(ServerLevel world, Entity entity) {
        int posX = (int) entity.getX();
        int posZ = (int) entity.getZ();

        for (int i = world.getMaxBuildHeight(); i > 0; i--) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(posX, i, posZ);
            Block currentBlock = world.getBlockState(pos).getBlock();
            if (!currentBlock.defaultBlockState().isAir()) {
                pos.setY(pos.getY() + 1);
                return pos;
            }
        }
        // SHOULD NOT happen! Entity gets teleported to where they were before.
        return entity.blockPosition();
    }
}
