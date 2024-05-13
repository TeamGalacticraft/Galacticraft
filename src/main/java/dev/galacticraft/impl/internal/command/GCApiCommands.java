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

package dev.galacticraft.impl.internal.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.galacticraft.api.accessor.LevelOxygenAccessor;
import dev.galacticraft.impl.command.argument.RegistryArgumentType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GCApiCommands {
    public static void register() {
        SingletonArgumentInfo<ArgumentType<?>> serializer = SingletonArgumentInfo.contextFree(RegistryArgumentType::create);
        ArgumentTypesAccessor.fabric_getClassMap().put(RegistryArgumentType.class, serializer);
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Constant.id("registry"), serializer); // Blame fabric api generics for this
        CommandRegistrationCallback.EVENT.register((commandDispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(Constant.MOD_ID + ":debug")
                    .requires(serverCommandSource -> serverCommandSource.hasPermission(2));
            builder.then(Commands.literal("registry").then(Commands.argument("registry", RegistryArgumentType.create())
                    .then(Commands.literal("dump_ids").executes(context -> {
                        CommandSourceStack source = context.getSource();
                        Registry<?> registry = RegistryArgumentType.getRegistry(context, "registry");
                        source.sendSuccess(() -> Component.translatable(Translations.RegistryDebug.DUMP, registry.key().location().toString()), true);
                        for (ResourceLocation id : registry.keySet()) {
                            source.sendSuccess(() -> Component.literal(id.toString()), false);
                        }
                        return 1;
                    })).then(Commands.literal("get").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                        Registry<?> registry = RegistryArgumentType.getRegistry(context, "registry");
                        context.getSource().sendSuccess(() -> Component.translatable(Translations.RegistryDebug.ID, registry.key().location(), ResourceLocationArgument.getId(context, "id"), registry.get(ResourceLocationArgument.getId(context, "id"))), true);
                        return 1;
                    }))).then(Commands.literal("get_raw").then(Commands.argument("id", IntegerArgumentType.integer()).executes(context -> {
                        Registry<?> registry = RegistryArgumentType.getRegistry(context, "registry");
                        context.getSource().sendSuccess(() -> Component.translatable(Translations.RegistryDebug.ID, registry.key().location(), ResourceLocationArgument.getId(context, "id"), registry.byId(IntegerArgumentType.getInteger(context, "id"))), true);
                        return 1;
                    }))).then(Commands.literal("to_raw").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                        Registry<? super Object> registry = RegistryArgumentType.getRegistry(context, "registry");
                        Object o = registry.get(ResourceLocationArgument.getId(context, "id"));
                        context.getSource().sendSuccess(() -> Component.translatable(Translations.RegistryDebug.ID, registry.key().location(), o, registry.getId(o)), true);
                        return 1;
                    }))).then(Commands.literal("dump_values").then(Commands.argument("id", ResourceLocationArgument.id()).executes(context -> {
                        CommandSourceStack source = context.getSource();
                        Registry<?> registry = RegistryArgumentType.getRegistry(context, "registry");
                        source.sendSuccess(() -> Component.translatable(Translations.RegistryDebug.DUMP, registry.key().location().toString()), true);
                        for (ResourceLocation id : registry.keySet()) {
                            source.sendSuccess(() -> Component.literal(id.toString() + " - " + registry.get(id)), false);
                        }
                        return 1;
                    })))));
            commandDispatcher.register(builder);
            builder = Commands.literal(Constant.MOD_ID + ":oxygen").requires(source -> source.hasPermission(3));
            builder.then(Commands.literal("get").then(Commands.argument("start_pos", BlockPosArgument.blockPos()).executes(GCApiCommands::getOxygen).then(Commands.argument("end_pos", BlockPosArgument.blockPos()).executes(GCApiCommands::getOxygenArea))));
            builder.then(Commands.literal("set").requires(source -> source.hasPermission(4)).then(Commands.argument("start_pos", BlockPosArgument.blockPos()).then(Commands.argument("oxygen", BoolArgumentType.bool()).executes(GCApiCommands::setOxygen)).then(Commands.argument("end_pos", BlockPosArgument.blockPos()).then(Commands.argument("oxygen", BoolArgumentType.bool()).executes(GCApiCommands::setOxygenArea)))));
            commandDispatcher.register(builder);
        });
    }

    private static int setOxygen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "start_pos");
        boolean b = BoolArgumentType.getBool(context, "oxygen");
        context.getSource().getLevel().setBreathable(pos, b);
        context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.SUCCESS_SINGLE), true);
        return 1;
    }

    private static int setOxygenArea(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(context, "start_pos");
        BlockPos endPos = BlockPosArgument.getLoadedBlockPos(context, "end_pos");
        LevelOxygenAccessor accessor = context.getSource().getLevel();
        BoundingBox box = BoundingBox.fromCorners(startPos, endPos);
        boolean b = BoolArgumentType.getBool(context, "oxygen");
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minX(); y <= box.maxX(); y++) {
                for (int z = box.minX(); z <= box.maxX(); z++) {
                    accessor.setBreathable(mutable.set(x, y, z), b);
                }
            }
        }

        context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.SUCCESS_MULTIPLE), true);
        return 1;
    }

    private static int getOxygen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "start_pos");
        if (context.getSource().getLevel().isBreathable(pos)) {
            context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.OXYGEN_EXISTS), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.NO_OXYGEN_EXISTS), false);
        }
        return 1;
    }

    private static int getOxygenArea(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(context, "start_pos");
        BlockPos endPos = BlockPosArgument.getLoadedBlockPos(context, "end_pos");
        LevelOxygenAccessor accessor = context.getSource().getLevel();
        BoundingBox box = BoundingBox.fromCorners(startPos, endPos);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        boolean allOxygen = true;
        boolean hasSomeOxygen = false;
        boolean breathable;
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minX(); y <= box.maxX(); y++) {
                for (int z = box.minX(); z <= box.maxX(); z++) {
                    breathable = accessor.isBreathable(mutable.set(x, y, z));
                    hasSomeOxygen |= breathable;
                    allOxygen = allOxygen && breathable;
                }
            }
        }
        if (allOxygen) {
            context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.FULL_OXYGEN), false);
        } else if (hasSomeOxygen) {
            context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.PARTIAL_OXYGEN), false);
        } else {
            context.getSource().sendSuccess(() -> Component.translatable(Translations.SetOxygen.EMPTY_OXYGEN), false);
        }
        return 1;
    }
}
