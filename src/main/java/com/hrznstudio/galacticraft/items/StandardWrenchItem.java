/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.util.Rotatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class StandardWrenchItem extends Item {

    public StandardWrenchItem(Settings settings) {
        super(settings);
        settings.maxDamage(256);
    }

    private static <T extends Comparable<T>> BlockState method_7758(BlockState state, Property<T> property, boolean sneaking) {
        return state.with(property, method_7760(property.getValues(), state.get(property), sneaking));
    }

    private static <T> T method_7760(Iterable<T> iterable_1, T object_1, boolean sneaking) {
        return sneaking ? SystemUtil.previous(iterable_1, object_1) : SystemUtil.next(iterable_1, object_1);
    }

    public boolean beforeBlockBreak(BlockState state, World world_1, BlockPos pos, PlayerEntity player) {
        if (!world_1.isClient) {
            this.method_7759(player, state, world_1, pos, player.getStackInHand(Hand.MAIN_HAND));
        }

        return false;
    }

    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        PlayerEntity player = itemUsageContext_1.getPlayer();
        World world_1 = itemUsageContext_1.getWorld();
        if (!world_1.isClient && player != null) {
            BlockPos pos = itemUsageContext_1.getBlockPos();
            this.method_7759(player, world_1.getBlockState(pos), world_1, pos, itemUsageContext_1.getStack());
        }

        return ActionResult.SUCCESS;
    }


    private void method_7759(PlayerEntity player, BlockState state, IWorld iWorld, BlockPos pos, ItemStack stack) {
        Block block = state.getBlock();
        if (block instanceof Rotatable) {
            StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
            Collection<Property<?>> collection = stateFactory.getProperties();
            String string_1 = Registry.BLOCK.getId(block).toString();
            if (!collection.isEmpty()) {
                CompoundTag compoundTag_1 = stack.getOrCreateSubTag("wrenchProp");
                String string_2 = compoundTag_1.getString(string_1);
                Property<?> property = stateFactory.getProperty(string_2);
                if (property == null) {
                    property = collection.iterator().next();
                }
                if (property.getName().equals("facing")) {
                    BlockState blockState_2 = method_7758(state, property, player.isSneaking());
                    iWorld.setBlockState(pos, blockState_2, 18);
                    stack.damage(2, player, (playerEntity) -> playerEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack_1, World world_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        if (Screen.hasShiftDown()) {
            list_1.add(new TranslatableText("tooltip.galacticraft-fabric.standard_wrench").setStyle(new Style().setColor(Formatting.GRAY)));
        } else {
            list_1.add(new TranslatableText("tooltip.galacticraft-fabric.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
    }
}
