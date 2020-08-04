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
 *
 */

package com.hrznstudio.galacticraft.server.command;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.PlanetSelectScreen;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
                    .executes(context -> {
                        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
                            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                            ClientSidePacketRegistry.INSTANCE.sendToServer(new Identifier(Constants.MOD_ID, "planet_menu_open"), passedData);                        }
                        return 1;
                    })
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
                ServerWorld serverWorld = DimensionArgumentType.getDimensionArgument(context, "dimension");
                if (serverWorld == null) {
                    context.getSource().sendError(new TranslatableText("commands.galacticraft-rewoven.dimensiontp.failure.dimension").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    return;
                }

                context.getSource().getPlayer().moveToWorld(serverWorld);
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
}