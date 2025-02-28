/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class OxygenBlockProvider implements DataProvider {
    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public OxygenBlockProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        List<OxygenBlockDataManager.OxygenData> data = new ArrayList<>();
        return registries.thenCompose(provider -> {
            addBlocks(data);

            return DataProvider.saveStable(writer, provider, OxygenBlockDataManager.OxygenData.CODEC.listOf(), data, this.output.createPathProvider(PackOutput.Target.DATA_PACK, "oxygen").json(ResourceLocation.fromNamespaceAndPath(output.getModId(), "blocks")));
        });
    }

    protected abstract void addBlocks(List<OxygenBlockDataManager.OxygenData> data);

    public void add(List<OxygenBlockDataManager.OxygenData> data, BlockState block, float amount, boolean replace) {
        data.add(new OxygenBlockDataManager.OxygenData(block, amount, replace));
    }

    public void add(List<OxygenBlockDataManager.OxygenData> data, Block block, float amount, boolean replace) {
        for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
            add(data, blockState, amount);
        }
    }

    public void add(List<OxygenBlockDataManager.OxygenData> data, BlockState block, float amount) {
        add(data, block, amount, false);
    }

    public void add(List<OxygenBlockDataManager.OxygenData> data, Block block, float amount) {
        add(data, block, amount, false);
    }
}
