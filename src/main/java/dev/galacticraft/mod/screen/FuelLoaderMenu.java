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

package dev.galacticraft.mod.screen;

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.sync.MenuSyncHandler;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.FuelLoaderBlockEntity;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.RocketEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FuelLoaderMenu extends MachineMenu<FuelLoaderBlockEntity> {
    public @Nullable Fluid fluid = null;
    public long fluidAmount = 0;
    public long fluidCapacity = 0;

    public FuelLoaderMenu(int syncId, ServerPlayer player, FuelLoaderBlockEntity machine) {
        super(syncId, player, machine);
    }

    public FuelLoaderMenu(int syncId, Inventory inv, FriendlyByteBuf buf) {
        super(syncId, inv, buf, 8, 84, GCMachineTypes.FUEL_LOADER);
    }

    @Override
    public void registerSyncHandlers(Consumer<MenuSyncHandler> consumer) {
        super.registerSyncHandlers(consumer);
        consumer.accept(new MenuSyncHandler() { //fixme actually implement this
            private @Nullable Fluid fluidP = null;
            private long fluidAmountP = 0;
            private long fluidCapacityP = 0;

            @Override
            public boolean needsSyncing() {
                BlockPos connectionPos = machine.getConnectionPos();
                Fluid fluid1 = null;
                long amount = 0;
                long capacity = 0;
                if (connectionPos.closerThan(machine.getBlockPos(), 3.0)) {
                    if (machine.getLevel().getBlockEntity(connectionPos) instanceof RocketLaunchPadBlockEntity launchPad) {
                        if (launchPad.hasRocket()) {
                            if (machine.getLevel().getEntity(launchPad.getRocketEntityId()) instanceof RocketEntity rocket) {
//                                capacity = ;
                                if (!rocket.isTankEmpty()) {
//                                    fluid1 = ;
//                                    amount = ;
                                }
                            }
                        }
                    }
                }
                return fluid1 != fluidP || amount != fluidAmountP || capacity != fluidCapacityP;
            }

            @Override
            public void sync(@NotNull FriendlyByteBuf buf) {
                BlockPos connectionPos = machine.getConnectionPos();
                Fluid fluid1 = null;
                long amount = 0;
                long capacity = 0;
                if (connectionPos.closerThan(machine.getBlockPos(), 3.0)) {
                    if (machine.getLevel().getBlockEntity(connectionPos) instanceof RocketLaunchPadBlockEntity launchPad) {
                        if (launchPad.hasRocket()) {
                            if (machine.getLevel().getEntity(launchPad.getRocketEntityId()) instanceof RocketEntity rocket) {
//                                capacityP = ;
                                if (!rocket.isTankEmpty()) {
//                                    fluidAmountP = ;
//                                    fluidP = ;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void read(@NotNull FriendlyByteBuf buf) {
            }
        });
    }
}