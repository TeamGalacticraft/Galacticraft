package dev.galacticraft.mod.accessor;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface BlockModelGeneratorsAccessor {
    Consumer<Item> getSkippedAutoModelsOutput();

    Map<Block, TexturedModel> getTexturedModels();

    void setTexturedModels(Map<Block, TexturedModel> map);

    Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> getFullBlockModelCustomGenerators();

    List<Block> getNonOrientableTrapdoor();
}
