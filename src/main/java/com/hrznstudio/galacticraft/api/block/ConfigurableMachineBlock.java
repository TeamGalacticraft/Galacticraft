/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.api.EnergyExtractable;
import com.hrznstudio.galacticraft.energy.api.EnergyInsertable;
import com.hrznstudio.galacticraft.misc.TriFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ConfigurableMachineBlock extends BaseEntityBlock implements AttributeProvider {
    public static final BooleanProperty ARBITRARY_BOOLEAN_PROPERTY = BooleanProperty.create("update");

    private final Function<BlockGetter, ? extends ConfigurableMachineBlockEntity> blockEntityFunc;
    private final TriFunction<ItemStack, BlockGetter, Boolean, Component> machineInfo;

    protected ConfigurableMachineBlock(Properties settings) {
        this(settings, (view) -> null, Constants.Misc.EMPTY_TEXT);
    }

    public ConfigurableMachineBlock(Properties settings, Function<BlockGetter, ? extends ConfigurableMachineBlockEntity> blockEntityFunc, TriFunction<ItemStack, BlockGetter, Boolean, Component> machineInfo) {
        super(settings);
        this.blockEntityFunc = blockEntityFunc;
        this.machineInfo = machineInfo;
    }

    public ConfigurableMachineBlock(Properties settings, Function<BlockGetter, ? extends ConfigurableMachineBlockEntity> blockEntityFunc, Component machineInfo) {
        this(settings, blockEntityFunc, (itemStack, blockView, tooltipContext) -> machineInfo);
    }

    public ConfigurableMachineBlock(Properties settings, Supplier<? extends ConfigurableMachineBlockEntity> blockEntitySupplier, TriFunction<ItemStack, BlockGetter, Boolean, Component> machineInfo) {
        this(settings, (view) -> blockEntitySupplier.get(), machineInfo);
    }

    public ConfigurableMachineBlock(Properties settings, Supplier<? extends ConfigurableMachineBlockEntity> blockEntitySupplier, Component machineInfo) {
        this(settings, blockEntitySupplier, (itemStack, blockView, tooltipContext) -> machineInfo);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING, ARBITRARY_BOOLEAN_PROPERTY);
    }

    @Override
    public ConfigurableMachineBlockEntity newBlockEntity(BlockGetter view) {
        return blockEntityFunc.apply(view);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()).setValue(ARBITRARY_BOOLEAN_PROPERTY, false);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (this instanceof MultiBlockBase) {
            ((MultiBlockBase) this).onMultiblockPlaced(world, pos, state);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void appendHoverText(ItemStack stack, BlockGetter view, List<Component> lines, TooltipFlag context) {
        Component text = machineInfo(stack, view, context.isAdvanced());
        if (text != null) {
            if (Screen.hasShiftDown()) {
                char[] line = text instanceof TranslatableComponent ? I18n.get(((TranslatableComponent) text).getKey()).toCharArray() : text.getString().toCharArray();
                int len = 0;
                final int maxLength = 175;
                StringBuilder builder = new StringBuilder();
                for (char c : line) {
                    len += Minecraft.getInstance().font.width(String.valueOf(c));
                    if (c == ' ' && len >= maxLength) {
                        len = 0;
                        lines.add(new TextComponent(builder.toString()).setStyle(text.getStyle()));
                        builder = new StringBuilder();
                        continue;
                    }
                    builder.append(c);
                }
                lines.add(new TextComponent(builder.toString()).setStyle(text.getStyle()));
            } else {
                lines.add(new TranslatableComponent("tooltip.galacticraft-rewoven.press_shift").setStyle(Constants.Styles.TOOLTIP_STYLE));
            }
        }

        if (stack != null && stack.getTag() != null && stack.getTag().contains(Constants.Nbt.BLOCK_ENTITY_TAG)) {
            CompoundTag tag = stack.getTag().getCompound(Constants.Nbt.BLOCK_ENTITY_TAG);
            lines.add(Constants.Misc.EMPTY_TEXT);
            lines.add(new TranslatableComponent("ui.galacticraft-rewoven.machine.current_energy", tag.getInt("Energy")).setStyle(Constants.Styles.AQUA_STYLE));
            lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.owner", tag.getString("OwnerUsername")).setStyle(Constants.Styles.BLUE_STYLE));
            if (tag.getBoolean("Public")) {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.public_2")
                        .setStyle(Constants.Styles.GREEN_STYLE)));

            } else if (tag.getBoolean("Party")) {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.space_race_2")
                        .setStyle(Constants.Styles.TOOLTIP_STYLE)));

            } else {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.security_config.private_2")
                        .setStyle(Constants.Styles.DARK_RED_STYLE)));

            }

            if (tag.getString("Redstone").equals("DISABLED")) {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore_2").setStyle(Constants.Styles.GRAY_STYLE)));
            } else if (tag.getString("Redstone").equals("OFF")) {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off_2").setStyle(Constants.Styles.DARK_RED_STYLE)));
            } else {
                lines.add(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableComponent("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on_2").setStyle(Constants.Styles.DARK_RED_STYLE)));
            }

        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public final InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider factory = state.getMenuProvider(world, pos);

            if (factory != null) {
                player.openMenu(factory);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(world, pos, state, player);
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ConfigurableMachineBlockEntity) {
            FullFixedItemInv inv = ((ConfigurableMachineBlockEntity) entity).getInventory();
            for (int i = 0; i < inv.getSlotCount(); i++) {
                ItemStack stack = inv.getInvStack(i);
                if (!stack.isEmpty()) {
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                    inv.forceSetInvStack(i, ItemStack.EMPTY);
                }
            }
        }

        if (this instanceof MultiBlockBase) {
            for (BlockPos otherPart : ((MultiBlockBase) this).getOtherParts(state, pos)) {
                world.setBlock(otherPart, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity entity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        if (entity.save(new CompoundTag()).getBoolean("NoDrop")) return Collections.emptyList();
        return super.getDrops(state, builder);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (this instanceof MultiBlockBase) {
            for (BlockPos otherPart : (((MultiBlockBase) this).getOtherParts(state, pos))) {
                if (!world.getBlockState(otherPart).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }
        return super.canSurvive(state, world, pos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter view, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(view, pos, state);
        CompoundTag tag = (stack.getTag() != null ? stack.getTag() : new CompoundTag());
        if (view.getBlockEntity(pos) != null) {
            tag.put(Constants.Nbt.BLOCK_ENTITY_TAG, view.getBlockEntity(pos).save(new CompoundTag()));
        }

        stack.setTag(tag);
        return stack;
    }

    public Component machineInfo(ItemStack stack, BlockGetter view, boolean context) {
        return machineInfo.apply(stack, view, context);
    }

    @Override
    public void addAllAttributes(Level world, BlockPos pos, BlockState blockState, AttributeList<?> attributeList) {
        Direction direction = attributeList.getSearchDirection() == null ? null : attributeList.getSearchDirection();
        ConfigurableMachineBlockEntity machine = (ConfigurableMachineBlockEntity) world.getBlockEntity(pos);
        assert machine != null;
        Object attribute = machine.getInventory(blockState, direction);
        attributeList.offer(attribute);
        attribute = machine.getFluidInsertable(blockState, direction);
        if (attribute != null) attributeList.offer(((FluidInsertable) attribute).getPureInsertable());
        attribute = machine.getFluidExtractable(blockState, direction);
        if (attribute != null) attributeList.offer(((FluidExtractable) attribute).getPureExtractable());
        attribute = machine.getEnergyExtractable(blockState, direction);
        if (attribute != null) attributeList.offer(((EnergyExtractable) attribute).asPureExtractable());
        attribute = machine.getEnergyInsertable(blockState, direction);
        if (attribute != null) attributeList.offer(((EnergyInsertable) attribute).asPureInsertable());
    }
}
