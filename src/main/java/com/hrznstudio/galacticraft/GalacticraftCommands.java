package com.hrznstudio.galacticraft;

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
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
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
                        .then(CommandManager.argument("dimension", DimensionArgumentType.create())
                                .executes(GalacticraftCommands::teleport)
                                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                                        .executes((GalacticraftCommands::teleport))))));
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
                context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.entity").setStyle(new Style().setColor(Formatting.RED)));
                return -1;
            }
        } catch (Exception ignore) {
            return -1;
        }
    }

    private static void teleport(Entity entity, ServerWorld world) {
        BlockPos spawnPos = world.getSpawnPos();
        double x = spawnPos.getX();
        double y = spawnPos.getY();
        double z = spawnPos.getZ();

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            player.stopRiding();
            if (player.isSleeping()) {
                player.wakeUp(true, true, false);
            }

            if (world == entity.world) {
                player.networkHandler.teleportRequest(x, y, z, 0, 0, Collections.emptySet());
            } else {
                player.teleport(world, x, y, z, 0, 0);
            }
        } else {
            if (world == entity.world) {
                entity.setPosition(x, y, z);
            } else {
                entity.detach();
                entity.dimension = world.dimension.getType();
                Entity entity_2 = entity;
                entity = entity.getType().create(world);
                if (entity == null) {
                    return;
                }

                entity.method_5878(entity_2);
                entity.setPosition(x, y, z);
                world.method_18769(entity);
                entity_2.removed = true;
            }
        }

        if (!(entity instanceof LivingEntity) || !((LivingEntity) entity).isFallFlying()) {
            entity.setVelocity(entity.getVelocity().multiply(1.0D, 0.0D, 1.0D));
            entity.onGround = true;
        }
    }
}