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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) -> {
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                            .executes(GalacticraftCommands::teleport)
                            .then(CommandManager.argument("entities", EntityArgumentType.entities())
                                    .executes(((GalacticraftCommands::teleportMultiple))))));
            commandDispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("gcr_listbodies")
                    .executes(context -> {
                        StringBuilder builder = new StringBuilder();
                        CelestialBodyType.getAll().forEach(celestialBodyType -> builder.append(celestialBodyType.getTranslationKey()).append("\n"));
                        context.getSource().sendFeedback(new LiteralText(builder.toString()), true);
                        return 1;
                    }));
        });
    }

    private static int teleport(CommandContext<ServerCommandSource> context) {
        context.getSource().getMinecraftServer().execute(() -> {
            try {
                ServerPlayerEntity player = context.getSource().getPlayer();
                ServerWorld world = context.getSource().getWorld();

                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    return;
                }

                player.moveToWorld(serverWorld);
                player.teleport(player.getX(),
                        getTopBlockY(world, player),
                        player.getZ()); // there's actually a method that takes in target world too, might be a good thing to look into.
                                        // I haven't tested
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
                    entity.moveToWorld(serverWorld);
                    context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.multiple", entities.size(), serverWorld.getRegistryKey().getValue()), true);
                });
            } catch (CommandSyntaxException ignore) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
            }
        });
        return -1;
    }

    // Hey it prob works
    private static double getTopBlockY(ServerWorld world, ServerPlayerEntity player) {
        int playerX = (int) player.getX();
        int playerZ = (int) player.getZ();

        for (int i = world.getHeight(); i > 0; i-- ) {
            BlockPos pos = new BlockPos(new Vec3d(playerX, i, playerZ));
            Block currentBlock = world.getBlockState(pos).getBlock();
            if (currentBlock != Blocks.VOID_AIR) {
                if (currentBlock != Blocks.AIR) {
                    System.out.println(pos);
                    System.out.println(world.getBlockState(pos).getBlock());
                    return pos.getY() + 1;
                }
            }
        }
        return player.getY(); // This SHOULD NOT happen! However if it does, player gets teleported to where they were before.
    }
}