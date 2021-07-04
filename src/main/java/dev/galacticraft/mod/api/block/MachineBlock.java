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

import alexiil.mc.lib.attributes.item.FixedItemInv;
import com.mojang.authlib.GameProfile;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.block.entity.MachineBlockEntityTicker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class MachineBlock<T extends MachineBlockEntity> extends BlockWithEntity {
    public static final BooleanProperty ARBITRARY_BOOLEAN_PROPERTY = BooleanProperty.of("update");
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    protected MachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING, ARBITRARY_BOOLEAN_PROPERTY, ACTIVE);
    }

    @Override
    public abstract T createBlockEntity(BlockPos pos, BlockState state);

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, context.getPlayerFacing().getOpposite()).with(ARBITRARY_BOOLEAN_PROPERTY, false).with(ACTIVE, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient && placer instanceof PlayerEntity player) {
            ((MachineBlockEntity) world.getBlockEntity(pos)).security().setOwner(/*((MinecraftServerTeamsGetter) world.getServer()).getSpaceRaceTeams(), */player); //todo: teams
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void appendTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext context) {
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
                        tooltip.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
                        builder = new StringBuilder();
                        continue;
                    }
                    builder.append(c);
                }
                tooltip.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
            } else {
                tooltip.add(new TranslatableText("tooltip.galacticraft.press_shift").setStyle(Constant.Text.DARK_GRAY_STYLE));
            }
        }

        if (stack != null && stack.getTag() != null && stack.getTag().contains(Constant.Nbt.BLOCK_ENTITY_TAG)) {
            NbtCompound tag = stack.getTag().getCompound(Constant.Nbt.BLOCK_ENTITY_TAG);
            tooltip.add(LiteralText.EMPTY);
            if (tag.contains(Constant.Nbt.ENERGY, NbtType.INT)) tooltip.add(new TranslatableText("ui.galacticraft.machine.current_energy", new LiteralText(String.valueOf(tag.getInt(Constant.Nbt.ENERGY))).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.GOLD_STYLE));
            if (tag.contains(Constant.Nbt.SECURITY, NbtType.COMPOUND)) {
                NbtCompound security = tag.getCompound(Constant.Nbt.SECURITY);
                if (security.contains(Constant.Nbt.OWNER, NbtType.COMPOUND)) {
                    GameProfile profile = NbtHelper.toGameProfile(security.getCompound(Constant.Nbt.OWNER));
                    MutableText text1 = new TranslatableText("ui.galacticraft.machine.security.owner", new LiteralText(profile.getName()).setStyle(Constant.Text.LIGHT_PURPLE_STYLE)).setStyle(Constant.Text.GRAY_STYLE);
                    if (Screen.hasControlDown()) {
                        text1.append(new LiteralText(" (" + profile.getId().toString() + ")").setStyle(Constant.Text.AQUA_STYLE));
                    }
                    tooltip.add(text1);
                    tooltip.add(new TranslatableText("ui.galacticraft.machine.security.accessibility", SecurityInfo.Accessibility.valueOf(security.getString(Constant.Nbt.ACCESSIBILITY)).getName()).setStyle(Constant.Text.GREEN_STYLE));
                }
            }
            tooltip.add(new TranslatableText("ui.galacticraft.machine.redstone.redstone", RedstoneInteractionType.fromTag(tag).getName()).setStyle(Constant.Text.DARK_RED_STYLE));
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public final ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof MachineBlockEntity machine) {
                SecurityInfo security = machine.security();
                if (security.getOwner() == null) security.setOwner(/*((MinecraftServerTeamsGetter) world.getServer()).getSpaceRaceTeams(), */player); //todo: teams
                if (security.isOwner(player.getGameProfile())) {
                    security.sendPacket(pos, (ServerPlayerEntity) player);
                    machine.redstoneInteraction().sendPacket(pos, (ServerPlayerEntity) player);
                    NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);

                    if (factory != null) {
                        player.openHandledScreen(factory);
                    }
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof MachineBlockEntity machine) {
            FixedItemInv inv = machine.itemInv();
            for (int i = 0; i < inv.getSlotCount(); i++) {
                ItemStack stack = inv.getInvStack(i);
                if (!stack.isEmpty()) {
                    world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                    inv.forceSetInvStack(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity entity = builder.get(LootContextParameters.BLOCK_ENTITY);
        if (entity.writeNbt(new NbtCompound()).getBoolean(Constant.Nbt.NO_DROP)) return Collections.emptyList();
        return super.getDroppedStacks(state, builder);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView view, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(view, pos, state);
        NbtCompound tag = (stack.getTag() != null ? stack.getTag() : new NbtCompound());
        if (view.getBlockEntity(pos) != null) {
            tag.put(Constant.Nbt.BLOCK_ENTITY_TAG, view.getBlockEntity(pos).writeNbt(new NbtCompound()));
        }

        stack.setTag(tag);
        return stack;
    }

    @Nullable
    @Override
    public <B extends BlockEntity> BlockEntityTicker<B> getTicker(World world, BlockState state, BlockEntityType<B> type) {
        return MachineBlockEntityTicker.getInstance();
    }

    public abstract Text machineInfo(ItemStack stack, BlockView view, boolean advanced);
}
