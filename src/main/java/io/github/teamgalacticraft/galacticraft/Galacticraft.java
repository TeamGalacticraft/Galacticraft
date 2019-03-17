package io.github.teamgalacticraft.galacticraft;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer {

    public static Logger logger = LogManager.getLogger("Galacticraft");



    @Override
    public void onInitialize() {
        logger.info("Initializing Galacticraft");
        GalacticraftBlocks.init();
    }
}
