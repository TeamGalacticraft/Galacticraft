package dev.galacticraft.mod.block.environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class VolcanicRockBlock extends Block {
    private static final ItemPredicate HAS_SILK_TOUCH = ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))).build();

    public VolcanicRockBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (!HAS_SILK_TOUCH.test(stack)) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState());
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.@NotNull Builder builder) {
        ItemStack itemStack = builder.get(LootContextParameters.TOOL);
        if (itemStack != null) {
            if (HAS_SILK_TOUCH.test(itemStack)) {
                return super.getDroppedStacks(state, builder);
            }
        }
        return Collections.emptyList();
    }
}
