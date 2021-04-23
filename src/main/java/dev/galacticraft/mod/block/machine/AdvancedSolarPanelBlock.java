/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
 */

package dev.galacticraft.mod.block.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.block.entity.AdvancedSolarPanelBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class AdvancedSolarPanelBlock extends MachineBlock implements MultiBlockBase {
    public AdvancedSolarPanelBlock(Settings settings) {
        super(settings,
                AdvancedSolarPanelBlockEntity::new,
                new TranslatableText("tooltip.galacticraft.advanced_solar_panel")
                        .setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    @Override
    public List<BlockPos> getOtherParts(BlockState state, BlockPos pos) {
        return BasicSolarPanelBlock.genPartList(pos);
    }
}