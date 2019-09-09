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
