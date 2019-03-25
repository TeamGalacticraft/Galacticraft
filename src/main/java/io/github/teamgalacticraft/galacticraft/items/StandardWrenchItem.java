package io.github.teamgalacticraft.galacticraft.items;

import com.sun.istack.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Collection;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class StandardWrenchItem extends Item {

    public StandardWrenchItem(Settings settings) {
        super(settings);
        settings.durability(256);
    }

    public boolean beforeBlockBreak(BlockState state, World world_1, BlockPos pos, PlayerEntity player) {
        if (!world_1.isClient) {
            this.method_7759(player, state, world_1, pos, player.getStackInHand(Hand.MAIN));
        }

        return false;
    }

    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        PlayerEntity player = itemUsageContext_1.getPlayer();
        World world_1 = itemUsageContext_1.getWorld();
        if (!world_1.isClient && player != null) {
            BlockPos pos = itemUsageContext_1.getBlockPos();
            this.method_7759(player, world_1.getBlockState(pos), world_1, pos, itemUsageContext_1.getItemStack());
        }

        return ActionResult.SUCCESS;
    }

    private void method_7759(PlayerEntity player, BlockState state, IWorld iWorld, BlockPos pos, ItemStack stack) {
        Block block = state.getBlock();
        StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
        Collection<Property<?>> collection = stateFactory.getProperties();
        String string_1 = Registry.BLOCK.getId(block).toString();
        if (!collection.isEmpty()) {
            CompoundTag compoundTag_1 = stack.getOrCreateSubCompoundTag("wrenchProp");
            String string_2 = compoundTag_1.getString(string_1);
            Property<?> property = stateFactory.getProperty(string_2);
            if (property == null) {
                property = collection.iterator().next();
            }
            if (property.getName().equals("facing")) {
                BlockState blockState_2 = method_7758(state, property, player.isSneaking());
                iWorld.setBlockState(pos, blockState_2, 18);
                stack.applyDamage(1, player);
            }
        }
    }

    private static <T extends Comparable<T>> BlockState method_7758(BlockState state, Property<T> property, boolean sneaking) {
        return state.with(property, method_7760(property.getValues(), state.get(property), sneaking));
    }

    private static <T> T method_7760(Iterable<T> iterable_1, @Nullable T object_1, boolean sneaking) {
        return sneaking ? SystemUtil.method_645(iterable_1, object_1) : SystemUtil.method_660(iterable_1, object_1);
    }
}
