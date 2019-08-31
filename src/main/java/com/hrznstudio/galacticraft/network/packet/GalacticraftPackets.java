package com.hrznstudio.galacticraft.network.packet;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.entity.moonvillager.T1RocketEntity;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

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
            if (context.getPlayer().world.getBlockEntity(pos) == null) {
                for (BlockEntity blockEntity : context.getPlayer().world.blockEntities) { //uh-oh
                    if (blockEntity.getPos().equals(pos)) {
                        if (blockEntity instanceof ConfigurableElectricMachineBlockEntity) {
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).owner = owner;
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).isPublic = isPublic;
                            ((ConfigurableElectricMachineBlockEntity) blockEntity).isParty = isParty;
                        }
                        return;
                    }
                }
            } else if (context.getPlayer().world.getBlockEntity(pos) instanceof ConfigurableElectricMachineBlockEntity) {
                ((ConfigurableElectricMachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).owner = owner;
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

        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "t1_rocket_spawn"), ((context, byteBuf) -> {
            EntityType<T1RocketEntity> type = (EntityType<T1RocketEntity>) Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            int entityID = byteBuf.readVarInt();
            UUID entityUUID = byteBuf.readUuid();
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            float pitch = (byteBuf.readByte() * 360) / 256.0F;
            float yaw = (byteBuf.readByte() * 360) / 256.0F;
            Runnable spawn = () -> {
                T1RocketEntity entity = new T1RocketEntity(type, MinecraftClient.getInstance().world);
                entity.updateTrackedPosition(x, y, z);
                entity.x = x;
                entity.y = y;
                entity.z = z;
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityID);
                entity.setUuid(entityUUID);
                MinecraftClient.getInstance().world.addEntity(entityID, entity);
            };
            context.getTaskQueue().execute(spawn);
        }));
    }
}
