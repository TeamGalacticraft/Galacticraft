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

package com.hrznstudio.galacticraft.network.packet;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPackets {
    public static void register() {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone_update"), ((context, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            String setting = buffer.readString();
            if (context.getPlayer().world.getBlockEntity(pos) == null) { //WHY
                for (BlockEntity blockEntity : context.getPlayer().world.blockEntities) { //uh-oh
                    if (blockEntity.getPos().equals(pos)) {
                        if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).redstoneOption = setting;
                        }
                        return;
                    }
                }
            } else if (context.getPlayer().world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).redstoneOption = setting;
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security_update"), ((context, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            String owner = buffer.readString();
            boolean isParty = false;
            boolean isPublic = false;
            if (owner.contains("_Public")) {
                owner = owner.replace("_Public", "");
                isPublic = true;
            } else if (owner.contains("_Party")) {
                owner = owner.replace("_Party", "");
                isParty = true;
            }
            String ownerUsername = buffer.readString();
            if (context.getPlayer().world.getBlockEntity(pos) == null) {
                for (BlockEntity blockEntity : context.getPlayer().world.blockEntities) { //uh-oh
                    if (blockEntity.getPos().equals(pos)) {
                        if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).owner = owner;
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).ownerUsername = ownerUsername;
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).isPublic = isPublic;
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).isParty = isParty;
                        }
                        return;
                    }
                }
            } else if (context.getPlayer().world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).owner = owner;
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).ownerUsername = ownerUsername;
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).isPublic = isPublic;
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).isParty = isParty;
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "side_config_update"), ((context, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            String data = buffer.readString();

            if (context.getPlayer().world.getBlockState(pos) != null) {
                if (context.getPlayer().world.getBlockState(pos).getBlock() instanceof ConfigurableElectricMachineBlock) {
                    context.getPlayer().world.setBlockState(pos, context.getPlayer().world.getBlockState(pos)
                            .with(EnumProperty.of(data.split(",")[0], SideOption.class, SideOption.getApplicableValuesForMachine(context.getPlayer().world.getBlockState(pos).getBlock())), SideOption.valueOf(data.split(",")[1])));
                }
            }
        }));
    }
}
