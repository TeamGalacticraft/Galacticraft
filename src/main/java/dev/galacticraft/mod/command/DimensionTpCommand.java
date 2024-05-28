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
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.network.GCScreenType;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class DimensionTpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Constant.Command.DIMENSION_TP)
                .requires(stack -> stack.hasPermission(2))
                .executes(DimensionTpCommand::openCelestialScreen)
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> teleportToDimension(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), null))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(ctx -> teleportToDimension(ctx, EntityArgument.getPlayers(ctx, "players"), null))
                                .then(Commands.argument("pos", Vec3Argument.vec3(true))
                                        .executes(ctx -> teleportToDimension(ctx, EntityArgument.getPlayers(ctx, "players"), Vec3Argument.getVec3(ctx, "pos")))))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> teleportToDimension(ctx, Collections.singleton(ctx.getSource().getPlayerOrException()), Vec3Argument.getVec3(ctx, "pos")))
                        )
                )
        );

        dispatcher.register(Commands.literal(Constant.Command.DIMTP)
                .redirect(dispatcher.getRoot().getChild(Constant.Command.DIMENSION_TP))
        );
    }

    private static int teleportToDimension(CommandContext<CommandSourceStack> context, @NotNull Collection<ServerPlayer> players, @Nullable Vec3 pos) throws CommandSyntaxException {
        ServerLevel level = DimensionArgument.getDimension(context, "dimension");

        int success = 0;
        for (ServerPlayer player : players) {
            success += tryTeleport(level, player, pos) ? 1 : 0;
        }

        if (success > 0) {
            if (success == 1) {
                context.getSource().sendSuccess(() -> Component.translatable(Translations.DimensionTp.SUCCESS_SINGLE, level.dimension().location()), true);
            } else {
                int tmp = success;
                context.getSource().sendSuccess(() -> Component.translatable(Translations.DimensionTp.SUCCESS_MULTIPLE, tmp, level.dimension().location()), true);
            }
            return success;
        }
        return -1;
    }

    private static boolean tryTeleport(ServerLevel level, ServerPlayer player, @Nullable Vec3 pos) {
        if (pos == null) pos = getValidTeleportPos(level, player);
        player.teleportTo(level,
                pos.x,
                pos.y,
                pos.z,
                player.getYRot(),
                player.getXRot()
        );
        return true;
    }

    private static int openCelestialScreen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = context.getSource().getPlayerOrException();

        var buf = PacketByteBufs.create();
        buf.writeEnum(GCScreenType.CELESTIAL);
        buf.writeBoolean(false);
        ServerPlayNetworking.send(player, Constant.Packet.OPEN_SCREEN, buf);

        return Command.SINGLE_SUCCESS;
    }


    /**
     * Finds the highest solid block in the level to teleport to.
     * @param level The level.
     * @param entity The entity to teleport.
     * @return The highest valid position to teleport to.
     */
    static Vec3 getValidTeleportPos(ServerLevel level, Entity entity) {
        if (entity.level() == level) return entity.position();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(entity.blockPosition());

        for (int y = level.getMaxBuildHeight(); y > level.getMinBuildHeight(); y--) {
            pos.setY(y);
            if (!level.getBlockState(pos).isAir()) {
                return new Vec3(entity.getX(), y + 1.0, entity.getZ());
            }
        }
        return entity.position();
    }
}
