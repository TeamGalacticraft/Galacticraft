/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.api.EnergyExtractable;
import com.hrznstudio.galacticraft.energy.api.EnergyInsertable;
import dev.galacticraft.mod.misc.TriFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConfigurableMachineBlock extends BlockWithEntity implements AttributeProvider {
    public static final BooleanProperty ARBITRARY_BOOLEAN_PROPERTY = BooleanProperty.of("update");

    private final Function<BlockView, ? extends ConfigurableMachineBlockEntity> blockEntityFunc;
    private final TriFunction<ItemStack, BlockView, Boolean, Text> machineInfo;

    protected ConfigurableMachineBlock(Settings settings) {
        this(settings, (view) -> null, Constants.Misc.EMPTY_TEXT);
    }

    public ConfigurableMachineBlock(Settings settings, Function<BlockView, ? extends ConfigurableMachineBlockEntity> blockEntityFunc, TriFunction<ItemStack, BlockView, Boolean, Text> machineInfo) {
        super(settings);
        this.blockEntityFunc = blockEntityFunc;
        this.machineInfo = machineInfo;
    }

    public ConfigurableMachineBlock(Settings settings, Function<BlockView, ? extends ConfigurableMachineBlockEntity> blockEntityFunc, Text machineInfo) {
        this(settings, blockEntityFunc, (itemStack, blockView, tooltipContext) -> machineInfo);
    }

    public ConfigurableMachineBlock(Settings settings, Supplier<? extends ConfigurableMachineBlockEntity> blockEntitySupplier, TriFunction<ItemStack, BlockView, Boolean, Text> machineInfo) {
        this(settings, (view) -> blockEntitySupplier.get(), machineInfo);
    }

    public ConfigurableMachineBlock(Settings settings, Supplier<? extends ConfigurableMachineBlockEntity> blockEntitySupplier, Text machineInfo) {
        this(settings, blockEntitySupplier, (itemStack, blockView, tooltipContext) -> machineInfo);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING, ARBITRARY_BOOLEAN_PROPERTY);
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView view) {
        return blockEntityFunc.apply(view);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, context.getPlayerFacing().getOpposite()).with(ARBITRARY_BOOLEAN_PROPERTY, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (this instanceof MultiBlockBase) {
            ((MultiBlockBase) this).onMultiblockPlaced(world, pos, state);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void appendTooltip(ItemStack stack, BlockView view, List<Text> lines, TooltipContext context) {
        Text text = machineInfo(stack, view, context.isAdvanced());
        if (text != null) {
            if (Screen.hasShiftDown()) {
                char[] line = text instanceof TranslatableText ? I18n.translate(((TranslatableText) text).getKey()).toCharArray() : text.getString().toCharArray();
                int len = 0;
                final int maxLength = 175;
                StringBuilder builder = new StringBuilder();
                for (char c : line) {
                    len += MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(c));
                    if (c == ' ' && len >= maxLength) {
                        len = 0;
                        lines.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
                        builder = new StringBuilder();
                        continue;
                    }
                    builder.append(c);
                }
                lines.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
            } else {
                lines.add(new TranslatableText("tooltip.galacticraft.press_shift").setStyle(Constants.Styles.TOOLTIP_STYLE));
            }
        }

        if (stack != null && stack.getTag() != null && stack.getTag().contains(Constants.Nbt.BLOCK_ENTITY_TAG)) {
            CompoundTag tag = stack.getTag().getCompound(Constants.Nbt.BLOCK_ENTITY_TAG);
            lines.add(Constants.Misc.EMPTY_TEXT);
            lines.add(new TranslatableText("ui.galacticraft.machine.current_energy", tag.getInt("Energy")).setStyle(Constants.Styles.AQUA_STYLE));
            lines.add(new TranslatableText("ui.galacticraft.tabs.security_config.owner", tag.getString("OwnerUsername")).setStyle(Constants.Styles.BLUE_STYLE));
            if (tag.getBoolean("Public")) {
                lines.add(new TranslatableText("ui.galacticraft.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableText("ui.galacticraft.tabs.security_config.public_2")
                        .setStyle(Constants.Styles.GREEN_STYLE)));

            } else if (tag.getBoolean("Party")) {
                lines.add(new TranslatableText("ui.galacticraft.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableText("ui.galacticraft.tabs.security_config.space_race_2")
                        .setStyle(Constants.Styles.TOOLTIP_STYLE)));

            } else {
                lines.add(new TranslatableText("ui.galacticraft.tabs.security_config_2").setStyle(Constants.Styles.GRAY_STYLE)
                        .append(new TranslatableText("ui.galacticraft.tabs.security_config.private_2")
                        .setStyle(Constants.Styles.DARK_RED_STYLE)));

            }

            if (tag.getString("Redstone").equals("DISABLED")) {
                lines.add(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config.ignore_2").setStyle(Constants.Styles.GRAY_STYLE)));
            } else if (tag.getString("Redstone").equals("OFF")) {
                lines.add(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config.redstone_means_off_2").setStyle(Constants.Styles.DARK_RED_STYLE)));
            } else {
                lines.add(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config_2").setStyle(Constants.Styles.RED_STYLE).append(new TranslatableText("ui.galacticraft.tabs.redstone_activation_config.redstone_means_on_2").setStyle(Constants.Styles.DARK_RED_STYLE)));
            }

        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public final ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);

            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ConfigurableMachineBlockEntity) {
            FullFixedItemInv inv = ((ConfigurableMachineBlockEntity) entity).getInventory();
            for (int i = 0; i < inv.getSlotCount(); i++) {
                ItemStack stack = inv.getInvStack(i);
                if (!stack.isEmpty()) {
                    world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                    inv.forceSetInvStack(i, ItemStack.EMPTY);
                }
            }
        }

        if (this instanceof MultiBlockBase) {
            for (BlockPos otherPart : ((MultiBlockBase) this).getOtherParts(state, pos)) {
                world.setBlockState(otherPart, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity entity = builder.get(LootContextParameters.BLOCK_ENTITY);
        if (entity.toTag(new CompoundTag()).getBoolean("NoDrop")) return Collections.emptyList();
        return super.getDroppedStacks(state, builder);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (this instanceof MultiBlockBase) {
            for (BlockPos otherPart : (((MultiBlockBase) this).getOtherParts(state, pos))) {
                if (!world.getBlockState(otherPart).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView view, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(view, pos, state);
        CompoundTag tag = (stack.getTag() != null ? stack.getTag() : new CompoundTag());
        if (view.getBlockEntity(pos) != null) {
            tag.put(Constants.Nbt.BLOCK_ENTITY_TAG, view.getBlockEntity(pos).toTag(new CompoundTag()));
        }

        stack.setTag(tag);
        return stack;
    }

    public Text machineInfo(ItemStack stack, BlockView view, boolean context) {
        return machineInfo.apply(stack, view, context);
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState blockState, AttributeList<?> attributeList) {
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
