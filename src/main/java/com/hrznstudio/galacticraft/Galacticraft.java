package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.config.ConfigHandler;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.misc.Capes;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.source.GalacticraftBiomeSourceTypes;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.hrznstudio.galacticraft.world.gen.WorldGenerator;
import com.hrznstudio.galacticraft.world.gen.chunk.GalacticraftChunkGeneratorTypes;
import com.hrznstudio.galacticraft.world.gen.decorator.GalacticraftDecorators;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.ModUpdateChecker;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.ModUpdateListener;
import io.github.teamgalacticraft.tgcutils.api.updatechecker.UpdateInfo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Galacticraft implements ModInitializer, ModUpdateListener {

    public static final Logger logger = LogManager.getLogger("Galacticraft-Rewoven");
    private static final Marker GALACTICRAFT = MarkerManager.getMarker("Galacticraft");

    public static ConfigHandler configHandler = new ConfigHandler();
    private ModUpdateChecker modUpdateChecker = new ModUpdateChecker(
            Constants.MOD_ID,
            "https://raw.githubusercontent.com/StellarHorizons/Galacticraft-Rewoven/master/updates.json",
            true
    );

    @Override
    public void onInitialize() {
        logger.info(GALACTICRAFT, "[Galacticraft] Initializing...");
        GalacticraftFluids.register();
        GalacticraftBlocks.register();
        GalacticraftItems.register();
        GalacticraftRecipes.register();
        GalacticraftSounds.register();
        GalacticraftEnergy.register();
        GalacticraftEntityTypes.register();
        GalacticraftContainers.register();
        GalacticraftCommands.register();
        GalacticraftBlockEntities.init();
        GalacticraftChunkGeneratorTypes.init();
        GalacticraftFeatures.init();
        GalacticraftDecorators.init();
        GalacticraftBiomes.init();
        GalacticraftBiomeSourceTypes.init();
        GalacticraftDimensions.init();
        GalacticraftSurfaceBuilders.init();
        WorldGenerator.register();
        Capes.updateCapeList();

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "redstone_update"), ((context, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            String setting = buffer.readString();
            System.out.println("Received packet");
            if (context.getPlayer().world.getBlockEntity(pos) == null) {
                for (BlockEntity blockEntity : context.getPlayer().world.blockEntities) {
                    if (blockEntity.getPos().equals(pos)) {
                        if (blockEntity instanceof MachineBlockEntity) {
                            ((MachineBlockEntity) blockEntity).redstoneOption = setting;
                            System.out.println("Set to: " + setting);
                        }
                        return;
                    }
                }
            } else if (context.getPlayer().world.getBlockEntity(pos) instanceof MachineBlockEntity) {
                ((MachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).redstoneOption = setting;
                System.out.println("Set to: " + setting);
            } else {
                System.out.println("Failed to find blockentity!");
            }
        }));

        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "security_update"), ((context, buffer) -> {
            BlockPos pos = buffer.readBlockPos();
            String owner = buffer.readString();
            boolean isParty = false;
            boolean isPublic = false;
            System.out.println("Received packet");
            if (owner.contains("_Public")) {
                owner = owner.replace("_Public", "");
                isPublic = true;
            } else if (owner.contains("_Party")) {
                owner = owner.replace("_Party", "");
                isParty = true;
            }
            if (context.getPlayer().world.getBlockEntity(pos) == null) {
                for (BlockEntity blockEntity : context.getPlayer().world.blockEntities) {
                    if (blockEntity.getPos().equals(pos)) {
                        if (blockEntity instanceof MachineBlockEntity) {
                            ((MachineBlockEntity) blockEntity).owner = owner;
                            ((MachineBlockEntity) blockEntity).isPublic = isPublic;
                            ((MachineBlockEntity) blockEntity).isParty = isParty;
                            System.out.println("The owner is: " + owner + " Status: " + isParty + " " + isPublic);
                        }
                        return;
                    }
                }
            } else if (context.getPlayer().world.getBlockEntity(pos) instanceof MachineBlockEntity) {
                ((MachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).owner = owner;
                ((MachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).isPublic = isPublic;
                ((MachineBlockEntity) context.getPlayer().world.getBlockEntity(pos)).isParty = isParty;
                System.out.println("The owner is: " + owner + " " + isParty + " " + isPublic);
            }
        }));

        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class<?> clazz = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
                Method method = clazz.getMethod("addConfigOverride", String.class, Runnable.class);
                method.invoke(null, Constants.MOD_ID, (Runnable) () -> configHandler.openConfigScreen());
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                logger.error("[Galacticraft] Failed to add modmenu config override. {1}", e);
            }
        }
        modUpdateChecker.register(this::onUpdate);
    }

    @Override
    public void onUpdate(UpdateInfo updateInfo) {
        if (updateInfo.getStatus() == UpdateInfo.VersionStatus.OUTDATED) {
            logger.info("Galacticraft: Rewoven is outdated.");
        }
    }
}
