package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.Objects;
import java.util.Random;

public class MoonFlowerBlockStateProvider extends BlockStateProvider {
   private static final BlockState[] mix1;
   private static final BlockState[] mix2;

   static {
      mix1 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState()};
      mix2 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState()};
   }

   public MoonFlowerBlockStateProvider() {
      super(GalacticraftBlockStateProviderTypes.MOON_FLOWER_PROVIDER);
   }

   public <T> MoonFlowerBlockStateProvider(Dynamic<T> configDeserializer) {
      this();
   }

   public BlockState getBlockState(Random random, BlockPos pos) {
      double d = Biome.FOLIAGE_NOISE.sample((double) pos.getX() / 200.0D, (double) pos.getZ() / 200.0D, false);
      if (d < -0.8D) {
         return mix1[random.nextInt(mix1.length)];
      } else {
         return random.nextInt(3) > 0 ? mix2[random.nextInt(mix2.length)] : GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState();
      }
   }

   public <T> T serialize(DynamicOps<T> ops) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(ops.createString("type"), ops.createString(Objects.requireNonNull(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider)).toString()));
      return (new Dynamic<>(ops, ops.createMap(builder.build()))).getValue();
   }
}
