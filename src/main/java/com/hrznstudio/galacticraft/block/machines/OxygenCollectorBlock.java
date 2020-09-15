/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.screen.OxygenCollectorScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorBlock extends ConfigurableMachineBlock {
    public OxygenCollectorBlock(Settings settings) {
        super(settings, OxygenCollectorScreenHandler::new);
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, TooltipContext context) {
        return new TranslatableText("tooltip.galacticraft-rewoven.oxygen_collector").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView view) {
        return new OxygenCollectorBlockEntity();
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof OxygenCollectorBlockEntity)) {
            return;
        }

        OxygenCollectorBlockEntity collector = (OxygenCollectorBlockEntity) blockEntity;
        if (collector.collectionAmount > 0) {
            for (int particleCount = 0; particleCount < 10; particleCount++) {
                for (int int_1 = 0; int_1 < 32; ++int_1) {
                    world.addParticle(
                            new DustParticleEffect(0.9f, 0.9f, 1.0f, 1.0F),
                            pos.getX() + 0.5D,
                            (random.nextFloat() - 0.5D) * 0.5D + /*random.nextDouble() * 2.0D*/ 0.5D,
                            pos.getZ() + 0.5D,
                            random.nextGaussian(),
                            0.0D,
                            random.nextGaussian());
                }
            }
        }
    }
}