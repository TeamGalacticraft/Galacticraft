/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.network.GCScreenType;
import dev.galacticraft.mod.util.Translations;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.UUID;

public class GCCommands {
    private static final Object2IntMap<UUID> GC_HOUSTON_TIMERS = new Object2IntArrayMap<>();
    private static final int GC_HOUSTON_TIMER_LENGTH = 12 * 20; // seconds * tps

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, registryAccess, environment) -> {

            commandDispatcher.register(
                    Commands.literal(Constant.Command.HOUSTON)
                    .executes(GCCommands::teleportToEarth));

            LiteralCommandNode<CommandSourceStack> node = commandDispatcher.register(
                    Commands.literal(Constant.Command.DIMENSION_TP)
                    .requires(stack -> stack.hasPermission(2))
                    .then(Commands.argument("dimension", DimensionArgument.dimension())
                    .executes(GCCommands::teleport)
                            .then(Commands.argument("entities", EntityArgument.entities())
                                    .executes(((GCCommands::teleportMultiple))))
                            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(GCCommands::teleportToCoords))));
            commandDispatcher.register(Commands.literal(Constant.Command.DIMENSION_TP_ALIAS).redirect(node));

            commandDispatcher.register(
                    Commands.literal(Constant.Command.OPEN_CELESTIAL_SCREEN)
                        .requires(stack -> stack.hasPermission(2))
                            .then(Commands.argument("players", EntityArgument.players())
                                    .executes(context -> openCelestialScreenWithPlayer(context, false))
                                    .then(Commands.argument("mapMode", BoolArgumentType.bool())
                                            .executes(context -> openCelestialScreenWithPlayer(context, BoolArgumentType.getBool(context, "mapMode")))
                                    )
                            )
                        .executes(context -> openCelestialScreen(context, true)));
        });
    }

    private static int openCelestialScreen(CommandContext<CommandSourceStack> context, boolean mapMode) {
        var player = context.getSource().getPlayer();
        if (player != null) {
            var buf = PacketByteBufs.create();
            buf.writeEnum(GCScreenType.CELESTIAL);
            buf.writeBoolean(mapMode);
            player.galacticraft$openCelestialScreen(null);
            ServerPlayNetworking.send(player, Constant.Packet.OPEN_SCREEN, buf);
        } else {
            context.getSource().sendFailure(Component.translatable(Translations.OpenCelestialScreen.REQUIRES_PLAYER));
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int openCelestialScreenWithPlayer(CommandContext<CommandSourceStack> context, boolean mapMode) throws CommandSyntaxException {
        var buf = PacketByteBufs.create();
        buf.writeEnum(GCScreenType.CELESTIAL);
        buf.writeBoolean(mapMode);
        for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
            player.galacticraft$openCelestialScreen(null);
            ServerPlayNetworking.send(player, Constant.Packet.OPEN_SCREEN, buf);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int teleportToEarth(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        // Clear the expired timers
        for (UUID id : GC_HOUSTON_TIMERS.keySet()) {
            if (GC_HOUSTON_TIMERS.getInt(id) + GC_HOUSTON_TIMER_LENGTH < context.getSource().getServer().getTickCount()) {
                GC_HOUSTON_TIMERS.removeInt(id);
            }
        }
        context.getSource().getServer().execute(() -> {
            if (CelestialBody.getByDimension(context.getSource().registryAccess(), context.getSource().getLevel().dimension()).isEmpty()) {
                context.getSource().sendFailure(Component.translatable(Translations.GcHouston.IN_OTHER_DIMENSION).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            ServerPlayer player = context.getSource().getPlayer();
            if (player == null) {
                context.getSource().sendFailure(Component.translatable(Translations.GcHouston.MISSING_PLAYER).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            ServerLevel serverWorld = context.getSource().getServer().overworld();
            if (context.getSource().getLevel().equals(serverWorld)) {
                context.getSource().sendFailure(Component.translatable(Translations.GcHouston.IN_OVERWORLD).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            UUID playerID = context.getSource().getPlayer().getGameProfile().getId();
            if (!GC_HOUSTON_TIMERS.containsKey(playerID)) {
                GC_HOUSTON_TIMERS.put(playerID, context.getSource().getServer().getTickCount());
                context.getSource().sendSuccess(() -> Component.translatable(Translations.GcHouston.CONFIRMATION, serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE), false);
            } else if (GC_HOUSTON_TIMERS.getInt(playerID) + GC_HOUSTON_TIMER_LENGTH > context.getSource().getServer().getTickCount()) {
                GC_HOUSTON_TIMERS.removeInt(playerID);
                BlockPos pos = getValidTeleportPos(serverWorld, player);
                player.teleportTo(serverWorld,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.getYRot(),
                        player.getXRot());
                context.getSource().sendSuccess(() -> Component.translatable(Translations.GcHouston.SUCCESS, serverWorld.dimension().location()).setStyle(Constant.Text.Color.GREEN_STYLE), true);
            }
        });
        return retval[0];
    }

    private static int teleport(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
        context.getSource().getServer().execute(() -> {
            ServerPlayer player;
            player = context.getSource().getPlayer();
            if (context.getSource().getLevel().equals(serverWorld)) {
                context.getSource().sendFailure(Component.translatable(Translations.DimensionTp.ALREADY_IN_DIMENSION, serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            BlockPos pos = getValidTeleportPos(serverWorld, player);
            player.teleportTo(serverWorld,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    player.getYRot(),
                    player.getXRot());
            context.getSource().sendSuccess(() -> Component.translatable(Translations.DimensionTp.SUCCESS_SINGLE, serverWorld.dimension().location()), true);
        });
        return retval[0];
    }

    private static int teleportMultiple(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");

        context.getSource().getServer().execute(() -> {
            if (context.getSource().getLevel().equals(serverWorld)) {
                context.getSource().sendFailure(Component.translatable(Translations.DimensionTp.ALREADY_IN_DIMENSION, serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
                for (Entity entity : entities) {
                    BlockPos pos = getValidTeleportPos(serverWorld, entity);
                    if (entity instanceof ServerPlayer player) {
                        player.teleportTo(serverWorld,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                player.getYRot(),
                                player.getXRot());
                    } else {
                        entity = entity.changeDimension(serverWorld); //Entities are recreated upon dim change, not moved.
                        if (entity != null) {
                            entity.teleportToWithTicket(pos.getX(), pos.getY(), pos.getZ());
                        } else {
                            context.getSource().sendFailure(Component.translatable(Translations.DimensionTp.UNKNOWN_ENTITY).setStyle(Constant.Text.Color.RED_STYLE));
                        }
                    }
                }
                context.getSource().sendSuccess(() -> Component.translatable(Translations.DimensionTp.SUCCESS_MULTIPLE, entities.size(), serverWorld.dimension().location()), true);
        });
        return retval[0];
    }

    private static int teleportToCoords(CommandContext<CommandSourceStack> context) throws CommandSyntaxException{
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
        BlockPos pos = BlockPosArgument.getSpawnablePos(context, "pos");

        context.getSource().getServer().execute(() -> {
            if (context.getSource().getLevel().equals(serverWorld)) {
                context.getSource().sendFailure(Component.translatable(Translations.DimensionTp.ALREADY_IN_DIMENSION, serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            ServerPlayer player = context.getSource().getPlayer();
            player.teleportTo(serverWorld,
                    Mth.clamp(pos.getX(), -30000000, 30000000),
                    Mth.clamp(pos.getY(), 0, serverWorld.getHeight() - 1),
                    Mth.clamp(pos.getZ(), -30000000, 30000000),
                    player.getYRot(),
                    player.getXRot());
            context.getSource().sendSuccess(() -> Component.translatable(Translations.DimensionTp.SUCCESS_POSITION, serverWorld.dimension().location(), pos.getX(), pos.getY(), pos.getZ()), true);
        });
        return retval[0];
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

        for (int i = world.getHeight(); i > 0; i--) {
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
