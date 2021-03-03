/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.MultiBlockBase;
import com.hrznstudio.galacticraft.block.entity.AdvancedSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.screen.AdvancedSolarPanelScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AdvancedSolarPanelBlock extends ConfigurableMachineBlock implements MultiBlockBase {
    public AdvancedSolarPanelBlock(Settings settings) {
        super(settings, AdvancedSolarPanelScreenHandler::new,
                AdvancedSolarPanelBlockEntity::new,
                new TranslatableText("tooltip.galacticraft-rewoven.advanced_solar_panel")
                        .setStyle(Constants.Styles.TOOLTIP_STYLE));
    }

    @Override
    public List<BlockPos> getOtherParts(BlockState state, BlockPos pos) {
        return BasicSolarPanelBlock.genPartList(pos);
    }
}