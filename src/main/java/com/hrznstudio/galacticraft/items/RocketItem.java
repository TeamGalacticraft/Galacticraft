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

import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.special.rocketlaunchpad.RocketLaunchPadBlock;
import com.hrznstudio.galacticraft.blocks.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketItem extends Item {

    public RocketItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient && context.getWorld().getBlockState(context.getBlockPos()).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                && context.getWorld().getBlockState(context.getBlockPos()).get(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
            BlockPos pos = new BlockPos(context.getBlockPos()).add(RocketLaunchPadBlock.partToCenterPos(context.getWorld().getBlockState(context.getBlockPos()).get(RocketLaunchPadBlock.PART)));
            assert context.getWorld().getBlockState(pos).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD;
            RocketLaunchPadBlockEntity blockEntity = (RocketLaunchPadBlockEntity) context.getWorld().getBlockEntity(pos);
            if (blockEntity.hasRocket()) return ActionResult.FAIL;

            if (context.getWorld() instanceof ServerWorld) {
                RocketEntity rocket = new RocketEntity(GalacticraftEntityTypes.ROCKET, context.getWorld());
                RocketData data = RocketData.fromItem(context.getPlayer().getStackInHand(context.getHand()));
                rocket.setParts(data.getParts());
                rocket.setColor(data.getRed(), data.getGreen(), data.getBlue(), data.getAlpha());
                rocket.setLinkedPad(pos);
                rocket.onGround = true; //
                rocket.resetPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                rocket.updatePosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

                context.getWorld().spawnEntity(rocket);
                rocket.resetPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                rocket.updatePosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

                if (!context.getPlayer().isCreative()) {
                    ItemStack stack = context.getPlayer().getStackInHand(context.getHand()).copy();
                    stack.decrement(1);
                    context.getPlayer().setStackInHand(context.getHand(), stack);
                }
                blockEntity.setRocketEntityUUID(rocket.getUuid());
                blockEntity.setRocketEntityId(rocket.getEntityId());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (Screen.hasShiftDown()) {
            if (tag.contains("red") && tag.contains("cone") && tag.contains("tier")) {
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.tier", tag.getInt("tier")).setStyle(new Style().setColor(Formatting.DARK_GRAY)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.color"));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.red", tag.getInt("red")).setStyle(new Style().setColor(Formatting.RED)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.green", tag.getInt("green")).setStyle(new Style().setColor(Formatting.GREEN)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.blue", tag.getInt("blue")).setStyle(new Style().setColor(Formatting.BLUE)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.alpha", tag.getInt("alpha")).setStyle(new Style().setColor(Formatting.WHITE)));
                tooltip.add(new LiteralText("-----").setStyle(new Style().setColor(Formatting.AQUA)));
                for (RocketPartType type : RocketPartType.values()) {
                    String s = new Identifier(tag.getString(type.asString())).getPath();
                    if (!(new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString()
                            .equals("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name"))) {
                        s = new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() +
                                "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString();
                    }
                    tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.part_type." + type.asString(), s).setStyle(new Style().setColor(Formatting.GRAY)));
                }
            }
        } else {
            tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
    }
}
