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

package dev.galacticraft.mod.compat.rei.client.transfer;

import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.compat.rei.client.display.DefaultFabricationDisplay;
import me.shedaniel.rei.api.client.ClientHelper;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;

import net.minecraft.text.LiteralText;

public class DefaultTransferHandler implements TransferHandler {
    @Override
    public Result handle(Context context) {
        if (context.getDisplay() instanceof SimpleGridMenuDisplay && ClientHelper.getInstance().canUseMovePackets()) {
            return Result.createNotApplicable();
        } else {
            if(!(context.getMenu() instanceof MachineScreenHandler)) {
                return Result.createNotApplicable();
            }
            if(context.getDisplay() instanceof DefaultFabricationDisplay display) {
                if(display.getOptionalRecipe().isPresent()) {
                    return Result.createSuccessful();
                }
                return Result.createSuccessful();
            }
            return Result.createFailed(new LiteralText("Unimplemented"));
        }
    }
}
