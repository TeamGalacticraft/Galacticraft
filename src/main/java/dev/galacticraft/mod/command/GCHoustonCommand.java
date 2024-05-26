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
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class GCHoustonCommand {
    private static final Object2IntMap<UUID> PROMPT_EXPIRY = new Object2IntArrayMap<>();
    private static final int GC_HOUSTON_TIMER_LENGTH = 10000/*ms*/;

    private static final SimpleCommandExceptionType IN_OTHER_DIMENSION = new SimpleCommandExceptionType(Component.translatable(Translations.GcHouston.IN_OTHER_DIMENSION));
    private static final SimpleCommandExceptionType IN_OVERWORLD = new SimpleCommandExceptionType(Component.translatable(Translations.GcHouston.IN_OVERWORLD));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Constant.Command.HOUSTON)
                .executes(GCHoustonCommand::teleportToEarth));
    }

    private static int teleportToEarth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        if (player.level() == context.getSource().getServer().overworld()) {
            throw IN_OVERWORLD.create();
        }

        if (CelestialBody.getByDimension(context.getSource().registryAccess(), context.getSource().getLevel().dimension()).isEmpty()) {
            throw IN_OTHER_DIMENSION.create();
        }

        UUID uuid = player.getUUID();
        int tickId = context.getSource().getServer().getTickCount();
        if (tickId < PROMPT_EXPIRY.getOrDefault(uuid, -1)) {
            PROMPT_EXPIRY.removeInt(uuid);

            ServerLevel overworld = context.getSource().getServer().overworld();
            BlockPos pos = player.getRespawnPosition();
            if (pos == null) pos = overworld.getSharedSpawnPos();

            player.teleportTo(overworld,
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot());

            context.getSource().sendSuccess(() -> Component.translatable(Translations.GcHouston.SUCCESS).setStyle(Constant.Text.Color.GREEN_STYLE), true);
        } else {
            PROMPT_EXPIRY.put(uuid, tickId + (int)(GC_HOUSTON_TIMER_LENGTH * context.getSource().getServer().tickRateManager().millisecondsPerTick()));
            context.getSource().sendSuccess(() -> Component.translatable(Translations.GcHouston.CONFIRMATION), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
