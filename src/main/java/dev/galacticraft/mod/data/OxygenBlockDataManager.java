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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.block.OxygenProvidingBlock;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines how much oxygen a block can produce.
 * Used for the oxygen collector
 */
public class OxygenBlockDataManager implements SimpleSynchronousResourceReloadListener {
    public static final OxygenBlockDataManager INSTANCE = new OxygenBlockDataManager();
    public static final ResourceLocation ID = Constant.id("oxygen_block_data_manager");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<BlockState, Float> blocks = new HashMap<>();

    public static float getOxygen(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof OxygenProvidingBlock oxygenProvidingBlock)
            return oxygenProvidingBlock.getOxygen(level, pos, state);
        return getOxygen(state);
    }

    public static float getOxygen(BlockState state) {
        boolean fallbackBehavior = true; // Probably make this a config value?
        if (!INSTANCE.blocks.containsKey(state) && fallbackBehavior) {
            if (state.getBlock() instanceof LeavesBlock && !state.getValue(LeavesBlock.PERSISTENT)) {
                return 1;
            } else if (state.getBlock() instanceof CropBlock) {
                return 0.75F;
            }

            return 0;
        }
        return INSTANCE.blocks.get(state);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        blocks.clear();
        for (String namespace : manager.getNamespaces()) {
            ResourceLocation path = ResourceLocation.fromNamespaceAndPath(namespace, "oxygen/blocks.json");
            List<Resource> resources = manager.getResourceStack(path);
            for (Resource resource : resources) {
                try {
                    Reader reader = resource.openAsReader();
                    try {
                        DataResult<List<OxygenData>> model = OxygenData.CODEC.listOf().parse(JsonOps.INSTANCE, GsonHelper.fromJson(GSON, reader, JsonElement.class));
                        if (model.error().isPresent())
                            continue;
                        List<OxygenData> data = model.getOrThrow(error -> new RuntimeException(String.format("Failed to load oxygen data for blocks: %s", error)));
                        for (OxygenData oxygenData : data) {
                            if (blocks.containsKey(oxygenData.state())) {
                                if (oxygenData.replace())
                                    blocks.replace(oxygenData.state(), oxygenData.amount());
                                else
                                    Constant.LOGGER.warn("Duplicate oxygen data for block {}", oxygenData.state());
                                continue;
                            }
                            blocks.put(oxygenData.state(), oxygenData.amount());
                        }
                    } catch (Throwable error) {
                        try {
                            reader.close();
                        } catch (Throwable nestedError) {
                            error.addSuppressed(nestedError);
                        }

                        throw error;
                    }
                } catch (Exception e) {
                    Constant.LOGGER.warn("Skipped oxygen data file: {}:{} ({})", namespace, resource.sourcePackId(), e.toString());
                }
            }

        }
    }

    public record OxygenData(BlockState state, float amount, boolean replace) {
        public static final Codec<OxygenData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("blockState").forGetter(OxygenData::state),
                Codec.FLOAT.fieldOf("amount").forGetter(OxygenData::amount),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(OxygenData::replace)
        ).apply(instance, OxygenData::new));
    }
}
