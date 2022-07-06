/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.server.command.Commands;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.UUID;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftCommand {
    private static final Object2IntMap<UUID> GC_HOUSTON_TIMERS = new Object2IntArrayMap<>();
    private static final int GC_HOUSTON_TIMER_LENGTH = 12 * 20; // seconds * tps

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, registryAccess, environment) -> {

            commandDispatcher.register(
                    Commands.literal("gchouston")
                    .executes(GalacticraftCommand::teleportToEarth));

            LiteralCommandNode<ServerCommandSource> node = commandDispatcher.register(
                    Commands.literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(Commands.argument("dimension", DimensionArgumentType.dimension())
                    .executes(GalacticraftCommand::teleport)
                            .then(Commands.argument("entities", EntityArgumentType.entities())
                                    .executes(((GalacticraftCommand::teleportMultiple))))
                            .then(Commands.argument("pos", BlockPosArgumentType.blockPos())
                                    .executes(GalacticraftCommand::teleportToCoords))));
            commandDispatcher.register(Commands.literal("dimtp").redirect(node));
                    Commands.literal("gchouston")
        });
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
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.gchouston.cannot_detect_signal").setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            ServerPlayer player = context.getSource().getPlayer();
            ServerLevel serverWorld = context.getSource().getServer().getLevel(Level.OVERWORLD);
            if (serverWorld == null) {
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            } else if (context.getSource().getLevel().equals(serverWorld)) {
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.gchouston.on_earth_already").setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
                return;
            }
            UUID playerID = context.getSource().getPlayer().getGameProfile().getId();
            if (!GC_HOUSTON_TIMERS.containsKey(playerID)) {
                GC_HOUSTON_TIMERS.put(playerID, context.getSource().getServer().getTickCount());
                context.getSource().sendSuccess(Component.translatable("commands.galacticraft.gchouston.confirm", serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE), false);
            } else if (GC_HOUSTON_TIMERS.getInt(playerID) + GC_HOUSTON_TIMER_LENGTH > context.getSource().getServer().getTickCount()) {
                GC_HOUSTON_TIMERS.removeInt(playerID);
                BlockPos pos = getValidTeleportPos(serverWorld, player);
                player.teleportTo(serverWorld,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        player.getYRot(),
                        player.getXRot());
                context.getSource().sendSuccess(Component.translatable("commands.galacticraft.gchouston.success", serverWorld.dimension().location()).setStyle(Constant.Text.Color.GREEN_STYLE), true);
            }
        });
        return retval[0];
    }

    private static int teleport(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getServer().execute(() -> {
            ServerPlayer player;
            player = context.getSource().getPlayer();
            try {
                ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.already_in_dimension", serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
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
                context.getSource().sendSuccess(Component.translatable("commands.galacticraft.dimensiontp.success.single", serverWorld.dimension().location()), true);
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int teleportMultiple(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getServer().execute(() -> {
            try {
                ServerLevel serverWorld = DimensionArgument.getDimension(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.already_in_dimension", serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
                Collection<? extends Entity> entities = EntityArgument.getEntities(context, "entities");
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
                            context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.entity").setStyle(Constant.Text.Color.RED_STYLE));
                        }
                    }
                }
                context.getSource().sendSuccess(Component.translatable("commands.galacticraft.dimensiontp.success.multiple", entities.size(), serverWorld.dimension().location()), true);
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.entity").setStyle(Constant.Text.Color.RED_STYLE));
                retval[0] = -1;
            }
        });
        return retval[0];
    }

    private static int teleportToCoords(CommandContext<CommandSourceStack> context) {
        final int[] retval = new int[]{Command.SINGLE_SUCCESS};
        context.getSource().getServer().execute(() -> {
            ServerLevel serverWorld;
            BlockPos pos;
            try {
                serverWorld = DimensionArgument.getDimension(context, "dimension");
                pos = BlockPosArgument.getSpawnablePos(context, "pos");
                if (serverWorld == null || pos == null) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
                    retval[0] = -1;
                    return;
                } else if (context.getSource().getLevel().equals(serverWorld)) {
                    context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.already_in_dimension", serverWorld.dimension().location()).setStyle(Constant.Text.Color.RED_STYLE));
                    retval[0] = -1;
                    return;
                }
            } catch (CommandSyntaxException e) {
                context.getSource().sendFailure(Component.translatable("commands.galacticraft.dimensiontp.failure.dimension").setStyle(Constant.Text.Color.RED_STYLE));
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
            context.getSource().sendSuccess(Component.translatable("commands.galacticraft.dimensiontp.success.pos", serverWorld.dimension().location(), pos.getX(), pos.getY(), pos.getZ()), true);
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
