package io.github.teamgalacticraft.galacticraft.client.render.block.entity;

import io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlockEntity;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBlockEntityRenderers {
    public static void register() {
        BlockEntityRendererRegistry.INSTANCE.register(BasicSolarPanelBlockEntity.class, new BasicSolarPanelBlockEntityRenderer());
    }
}
