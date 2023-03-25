/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.accessor.BlockModelGeneratorsAccessor;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(BlockModelGenerators.class)
public abstract class BlockModelGeneratorsMixin implements BlockModelGeneratorsAccessor {
    @Shadow @Final private Consumer<Item> skippedAutoModelsOutput;

    @Mutable
    @Shadow @Final Map<Block, TexturedModel> texturedModels;

    @Shadow @Final Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> fullBlockModelCustomGenerators;

    @Shadow @Final List<Block> nonOrientableTrapdoor;

    @Override
    public Consumer<Item> getSkippedAutoModelsOutput() {
        return this.skippedAutoModelsOutput;
    }

    @Override
    public Map<Block, TexturedModel> getTexturedModels() {
        return this.texturedModels;
    }

    @Override
    public void setTexturedModels(Map<Block, TexturedModel> map) {
        this.texturedModels = map;
    }

    @Override
    public Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> getFullBlockModelCustomGenerators() {
        return this.fullBlockModelCustomGenerators;
    }

    @Override
    public List<Block> getNonOrientableTrapdoor() {
        return this.nonOrientableTrapdoor;
    }
}
