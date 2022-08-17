/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.item;

import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketItem extends Item {

    public RocketItem(Properties settings) {
        super(settings);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getLevel().getBlockState(context.getClickedPos()).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD
                && context.getLevel().getBlockState(context.getClickedPos()).getValue(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
            BlockPos pos = new BlockPos(context.getClickedPos()).offset(RocketLaunchPadBlock.partToCenterPos(context.getLevel().getBlockState(context.getClickedPos()).getValue(RocketLaunchPadBlock.PART)));
            assert context.getLevel().getBlockState(pos).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD;
            RocketLaunchPadBlockEntity blockEntity = (RocketLaunchPadBlockEntity) context.getLevel().getBlockEntity(pos);
            if (blockEntity.hasRocket()) return InteractionResult.FAIL;

            if (context.getLevel() instanceof ServerLevel) {
                RocketEntity rocket = new RocketEntity(GalacticraftEntityType.ROCKET, context.getLevel());
                RocketData data = RocketData.fromNbt(context.getPlayer().getItemInHand(context.getHand()).getTag());
                rocket.setParts(data.parts());
                rocket.setColor(data.color());
                rocket.setLinkedPad(pos);
                rocket.setOldPosAndRot();
                rocket.absMoveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                context.getLevel().addFreshEntity(rocket);

                if (!context.getPlayer().isCreative()) {
                    ItemStack stack = context.getPlayer().getItemInHand(context.getHand()).copy();
                    stack.shrink(1);
                    context.getPlayer().setItemInHand(context.getHand(), stack);
                }
                blockEntity.setRocketEntityUUID(rocket.getUUID());
                blockEntity.setRocketEntityId(rocket.getId());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
        if (allowedIn(group)) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = new CompoundTag();
//            tag.putInt("tier", 1);
            tag.putInt("color", 0xFFFFFFFF);
            for (RocketPartType type : RocketPartType.values()) {
                tag.putString(type.getSerializedName(), AddonRegistry.ROCKET_PART.getKey(GalacticraftRocketParts.getDefaultPartForType(type)).toString());
            }
            stack.setTag(tag);
            stacks.add(stack);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (Screen.hasShiftDown()) {
            if (tag.contains("color") && tag.contains("cone")) {
//                tooltip.add(Component.translatable("tooltip.galacticraft.tier", tag.getInt("tier")).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
                tooltip.add(Component.translatable("tooltip.galacticraft.color"));
                tooltip.add(Component.translatable("tooltip.galacticraft.red", tag.getInt("color") >> 16 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                tooltip.add(Component.translatable("tooltip.galacticraft.green", tag.getInt("color") >> 8 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                tooltip.add(Component.translatable("tooltip.galacticraft.blue", tag.getInt("color") & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tooltip.add(Component.translatable("tooltip.galacticraft.alpha", tag.getInt("color") >> 24 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("-----").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
                for (RocketPartType type : RocketPartType.values()) {
                    String s = new ResourceLocation(tag.getString(type.getSerializedName())).getPath();
                    if (!(Component.translatable("tooltip." + new ResourceLocation(tag.getString(type.getSerializedName())).getNamespace() + "." + new ResourceLocation(tag.getString(type.getSerializedName())).getPath() + ".name").getString()
                            .equals("tooltip." + new ResourceLocation(tag.getString(type.getSerializedName())).getNamespace() + "." + new ResourceLocation(tag.getString(type.getSerializedName())).getPath() + ".name"))) {
                        s = Component.translatable("tooltip." + new ResourceLocation(tag.getString(type.getSerializedName())).getNamespace() +
                                "." + new ResourceLocation(tag.getString(type.getSerializedName())).getPath() + ".name").getString();
                    }
                    tooltip.add(Component.translatable("tooltip.galacticraft.part_type." + type.getSerializedName(), s).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
            }
        } else {
            tooltip.add(Component.translatable("tooltip.galacticraft.press_shift").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
