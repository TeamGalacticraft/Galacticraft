/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.server.command.LocateCommandGC;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCommands {

    public static void register() {
        CommandRegistry.INSTANCE.register(false, source -> source.register(
                LiteralArgumentBuilder.<ServerCommandSource>literal("dimensiontp")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(GalacticraftCommands::teleport)
                                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                                        .executes((GalacticraftCommands::teleport))))));
        // temp command
        CommandRegistry.INSTANCE.register(false, source -> source.register(
                LiteralArgumentBuilder.<ServerCommandSource>literal("gcr_listbodies")
                        .executes(context -> {
                            StringBuilder builder = new StringBuilder();
                            CelestialBodyType.getAll().forEach(celestialBodyType -> builder.append(celestialBodyType.getTranslationKey()).append("\n"));
                            context.getSource().sendFeedback(new LiteralText(builder.toString()), true);
                            return 1;
                        })
        ));

        CommandRegistry.INSTANCE.register(false, LocateCommandGC::register);
    }

    private static int teleport(CommandContext<ServerCommandSource> context) {
        try {
            DimensionType dimension = DimensionArgumentType.getDimensionArgument(context, "dimension");
            try {
                Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                entities.forEach((Consumer<Entity>) entity -> {
                    teleport(entity, context.getSource().getMinecraftServer().getWorld(dimension));
                    context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.multiple", entities.size(), dimension.toString()), true);
                });
                return entities.size();
            } catch (IllegalArgumentException ignore) {
                Entity entity = context.getSource().getEntity();
                teleport(entity, context.getSource().getMinecraftServer().getWorld(dimension));
                context.getSource().sendFeedback(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.success.single", dimension.toString()), true);
                return 1;
            } catch (CommandSyntaxException ignore) {
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                return -1;
            }
        } catch (Exception ignore) {
            return -1;
        }
    }

    private static void teleport(Entity entity, ServerWorld world) {
        BlockPos spawnPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnY(), world.getLevelProperties().getSpawnZ()));
        if (!world.getWorldBorder().contains(spawnPos)) {
            spawnPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, new BlockPos(world.getWorldBorder().getCenterX(), 0.0D, world.getWorldBorder().getCenterZ()));
        }
        double x = spawnPos.getX();
        double y = spawnPos.getY();
        double z = spawnPos.getZ();

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.stopRiding();
            if (player.isSleeping()) {
                player.wakeUp(true, true);
            }

            if (world == entity.world) {
                player.networkHandler.teleportRequest(x, y, z, 0, 0, Collections.emptySet());
            } else {
                player.teleport(world, x, y, z, 0, 0);
            }
        } else {
            if (world == entity.world) {
                entity.setPos(x, y, z);
            } else {
                entity.detach();
                entity.dimension = world.dimension.getType();
                Entity entity_2 = entity;
                entity = entity.getType().create(world);
                if (entity == null) {
                    return;
                }

                entity.copyFrom(entity_2);
                entity.setPos(x, y, z);
                world.onDimensionChanged(entity);
                entity_2.removed = true;
            }
        }

        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setVelocity(entity.getVelocity().multiply(1.0D, 0.0D, 1.0D));
            //entity.isOnGround() = true;
        }
    }
}