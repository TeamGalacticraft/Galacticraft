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
import com.hrznstudio.galacticraft.block.entity.CoalGeneratorBlockEntity;
import com.hrznstudio.galacticraft.screen.CoalGeneratorScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorBlock extends ConfigurableMachineBlock {
    public CoalGeneratorBlock(Settings settings) {
        super(settings, CoalGeneratorScreenHandler::new);
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView view) {
        return new CoalGeneratorBlockEntity();
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView blockView, TooltipContext tooltipContext) {
        return new TranslatableText("tooltip.galacticraft-rewoven.coal_generator").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (world.getBlockEntity(pos) instanceof CoalGeneratorBlockEntity && ((CoalGeneratorBlockEntity) world.getBlockEntity(pos)).status == CoalGeneratorBlockEntity.CoalGeneratorStatus.ACTIVE || ((CoalGeneratorBlockEntity) world.getBlockEntity(pos)).status == CoalGeneratorBlockEntity.CoalGeneratorStatus.WARMING) {
            double x = (double) pos.getX() + 0.5D;
            double y = pos.getY();
            double z = (double) pos.getZ() + 0.5D;
            if (rand.nextDouble() < 0.1D) {
                world.playSound(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(Properties.HORIZONTAL_FACING);
            Direction.Axis axis = direction.getAxis();
            double d = rand.nextDouble() * 0.6D - 0.3D;
            double xo = axis == Direction.Axis.X ? (double) direction.getOffsetX() * 0.52D : d;
            double yo = rand.nextDouble() * 6.0D / 16.0D;
            double zo = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * 0.52D : d;
            world.addParticle(ParticleTypes.SMOKE, x + xo, y + yo, z + zo, 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, x + xo, y + yo, z + zo, 0.0D, 0.0D, 0.0D);

        }
    }
}
