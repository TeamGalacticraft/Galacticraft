package io.github.teamgalacticraft.galacticraft.blocks.ore;

import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Random;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class SiliconOreBlock extends OreBlock {

    public SiliconOreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView blockView, BlockPos pos, BlockState state) {
        int bonus = 0;
        //if (ConfigManagerCore.quickMode) {
        //    bonus = 1;
        //}

        return new ItemStack(GalacticraftItems.RAW_SILICON, new Random().nextInt(4) + 2 + bonus);

    }
}
