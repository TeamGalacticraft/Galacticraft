package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlockEntity;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBlockEntityRenderers {
    public static void register() {
        BlockEntityRendererRegistry.INSTANCE.register(BasicSolarPanelBlockEntity.class, new BasicSolarPanelBlockEntityRenderer());
    }
}
