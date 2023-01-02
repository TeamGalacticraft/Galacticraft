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

package dev.galacticraft.mod.data;

import dev.galacticraft.mod.mixin.BlockFamilyProviderAccessor;
import dev.galacticraft.mod.mixin.BlockModelGeneratorsAccessor;
import net.minecraft.core.Registry;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

// Yes this entire class is for 1 method blame mojang not me
public class GCBlockModelGenerators extends BlockModelGenerators {

    private final BlockModelGenerators parent;

    public GCBlockModelGenerators(BlockModelGenerators generator) {
        super(generator.blockStateOutput, generator.modelOutput, ((BlockModelGeneratorsAccessor)generator).getSkippedAutoModelsOutput());
        this.parent = generator;
    }

    public final BlockModelGenerators.BlockFamilyProvider family(Block block, TexturedModel.Provider provider) {
        TexturedModel texturedModel = ((BlockModelGeneratorsAccessor)parent).getTexturedModels().getOrDefault(block, TexturedModel.CUBE.get(block));
        return new GCBlocksFamilyProvider(parent, texturedModel.getMapping(), provider).fullBlock(block, texturedModel.getTemplate());
    }

    public class GCBlocksFamilyProvider extends BlockFamilyProvider {

        private final BlockModelGenerators parent;
        private final TexturedModel.Provider provider;

        public GCBlocksFamilyProvider(BlockModelGenerators parent, TextureMapping textureMapping, TexturedModel.Provider provider) {
            super(textureMapping);
            this.parent = parent;
            this.provider = provider;
        }

        public BlockModelGenerators.BlockFamilyProvider fullBlock(Block block, ModelTemplate modelTemplate) {
            if (Registry.BLOCK.getKey(block).getPath().contains("detailed"))
                ((BlockFamilyProviderAccessor)this).setFullBlock(provider.get(block).getTemplate().create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput));
            else
                ((BlockFamilyProviderAccessor)this).setFullBlock(modelTemplate.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput));
            if (((BlockModelGeneratorsAccessor)parent).getFullBlockModelCustomGenerators().containsKey(block)) {
                parent.blockStateOutput
                        .accept(
                                ((BlockModelGeneratorsAccessor)parent).getFullBlockModelCustomGenerators().get(block)
                                        .create(block, ((BlockFamilyProviderAccessor)this).getFullBlock(), ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput)
                        );
            } else {
                parent.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, ((BlockFamilyProviderAccessor)this).getFullBlock()));
            }

            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider fullBlockCopies(Block... blocks) {
            if (((BlockFamilyProviderAccessor)this).getFullBlock() == null) {
                throw new IllegalStateException("Full block not generated yet");
            } else {
                for(Block block : blocks) {
                    parent.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, ((BlockFamilyProviderAccessor)this).getFullBlock()));
                    parent.delegateItemModel(block, ((BlockFamilyProviderAccessor)this).getFullBlock());
                }

                return this;
            }
        }

        public BlockModelGenerators.BlockFamilyProvider button(Block block) {
            ResourceLocation resourceLocation = ModelTemplates.BUTTON.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.BUTTON_PRESSED.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.blockStateOutput.accept(BlockModelGenerators.createButton(block, resourceLocation, resourceLocation2));
            ResourceLocation resourceLocation3 = ModelTemplates.BUTTON_INVENTORY.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.delegateItemModel(block, resourceLocation3);
            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider wall(Block block) {
            ResourceLocation resourceLocation = ModelTemplates.WALL_POST.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.WALL_LOW_SIDE.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation3 = ModelTemplates.WALL_TALL_SIDE.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.blockStateOutput.accept(BlockModelGenerators.createWall(block, resourceLocation, resourceLocation2, resourceLocation3));
            ResourceLocation resourceLocation4 = ModelTemplates.WALL_INVENTORY.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.delegateItemModel(block, resourceLocation4);
            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider fence(Block block) {
            ResourceLocation resourceLocation = ModelTemplates.FENCE_POST.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.FENCE_SIDE.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.blockStateOutput.accept(BlockModelGenerators.createFence(block, resourceLocation, resourceLocation2));
            ResourceLocation resourceLocation3 = ModelTemplates.FENCE_INVENTORY.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.delegateItemModel(block, resourceLocation3);
            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider fenceGate(Block block) {
            ResourceLocation resourceLocation = ModelTemplates.FENCE_GATE_OPEN.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.FENCE_GATE_CLOSED.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation3 = ModelTemplates.FENCE_GATE_WALL_OPEN.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation4 = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.blockStateOutput
                    .accept(BlockModelGenerators.createFenceGate(block, resourceLocation, resourceLocation2, resourceLocation3, resourceLocation4));
            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider pressurePlate(Block block) {
            ResourceLocation resourceLocation = ModelTemplates.PRESSURE_PLATE_UP.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.PRESSURE_PLATE_DOWN.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
            parent.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(block, resourceLocation, resourceLocation2));
            return this;
        }

        public BlockModelGenerators.BlockFamilyProvider sign(Block block) {
            if (((BlockFamilyProviderAccessor)this).getFamily() == null) {
                throw new IllegalStateException("Family not defined");
            } else {
                Block block2 = ((BlockFamilyProviderAccessor)this).getFamily().getVariants().get(BlockFamily.Variant.WALL_SIGN);
                ResourceLocation resourceLocation = ModelTemplates.PARTICLE_ONLY.create(block, ((BlockFamilyProviderAccessor)this).getMapping(), parent.modelOutput);
                parent.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourceLocation));
                parent.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block2, resourceLocation));
                parent.createSimpleFlatItemModel(block.asItem());
                parent.skipAutoItemBlock(block2);
                return this;
            }
        }

        public BlockModelGenerators.BlockFamilyProvider slab(Block block) {
            if (((BlockFamilyProviderAccessor)this).getFullBlock() == null) {
                throw new IllegalStateException("Full block not generated yet");
            } else {
                ResourceLocation resourceLocation = ((BlockFamilyProviderAccessor)this).callGetOrCreateModel(ModelTemplates.SLAB_BOTTOM, block);
                ResourceLocation resourceLocation2 = ((BlockFamilyProviderAccessor)this).callGetOrCreateModel(ModelTemplates.SLAB_TOP, block);
                parent.blockStateOutput.accept(BlockModelGenerators.createSlab(block, resourceLocation, resourceLocation2, ((BlockFamilyProviderAccessor)this).getFullBlock()));
                parent.delegateItemModel(block, resourceLocation);
                return this;
            }
        }

        public BlockModelGenerators.BlockFamilyProvider stairs(Block block) {
            ResourceLocation resourceLocation = ((BlockFamilyProviderAccessor)this).callGetOrCreateModel(ModelTemplates.STAIRS_INNER, block);
            ResourceLocation resourceLocation2 = ((BlockFamilyProviderAccessor)this).callGetOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, block);
            ResourceLocation resourceLocation3 = ((BlockFamilyProviderAccessor)this).callGetOrCreateModel(ModelTemplates.STAIRS_OUTER, block);
            parent.blockStateOutput.accept(BlockModelGenerators.createStairs(block, resourceLocation, resourceLocation2, resourceLocation3));
            parent.delegateItemModel(block, resourceLocation2);
            return this;
        }

        private BlockModelGenerators.BlockFamilyProvider fullBlockVariant(Block block) {
            TexturedModel texturedModel = ((BlockModelGeneratorsAccessor)parent).getTexturedModels().getOrDefault(block, TexturedModel.CUBE.get(block));
            parent.blockStateOutput
                    .accept(BlockModelGenerators.createSimpleBlock(block, texturedModel.create(block, parent.modelOutput)));
            return this;
        }

        private BlockModelGenerators.BlockFamilyProvider door(Block block) {
            parent.createDoor(block);
            return this;
        }

        private void trapdoor(Block block) {
            if (((BlockModelGeneratorsAccessor)parent).getNonOrientableTrapdoor().contains(block)) {
                parent.createTrapdoor(block);
            } else {
                parent.createOrientableTrapdoor(block);
            }

        }
    }
}
