package com.hrznstudio.galacticraft.blocks.environment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GlowstoneTorchBlock extends TorchBlock {

    public GlowstoneTorchBlock(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
    }

    @Override
    public void buildTooltip(ItemStack itemStack, BlockView blockView, List<Component> list, TooltipContext tooltipContext) {
        if (Screen.hasShiftDown()) {
            list.add(new TranslatableComponent("tooltip.galacticraft-rewoven.glowstone_torch").setStyle(new Style().setColor(ChatFormat.GRAY)));
        } else {
            list.add(new TranslatableComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(ChatFormat.GRAY)));
        }
    }
}
