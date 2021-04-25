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

package dev.galacticraft.mod.item;

import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        if (!context.getWorld().isClient && context.getWorld().getBlockState(context.getBlockPos()).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD
                && context.getWorld().getBlockState(context.getBlockPos()).get(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
            BlockPos pos = new BlockPos(context.getBlockPos()).add(RocketLaunchPadBlock.partToCenterPos(context.getWorld().getBlockState(context.getBlockPos()).get(RocketLaunchPadBlock.PART)));
            assert context.getWorld().getBlockState(pos).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD;
            RocketLaunchPadBlockEntity blockEntity = (RocketLaunchPadBlockEntity) context.getWorld().getBlockEntity(pos);
            if (blockEntity.hasRocket()) return ActionResult.FAIL;

            if (context.getWorld() instanceof ServerWorld) {
                RocketEntity rocket = new RocketEntity(GalacticraftEntityType.ROCKET, context.getWorld());
                RocketData data = RocketData.fromItem(context.getPlayer().getStackInHand(context.getHand()));
                rocket.setParts(data.getParts().toArray(new RocketPart[0]));
                rocket.setColor(data.getRed(), data.getGreen(), data.getBlue(), data.getAlpha());
                rocket.setLinkedPad(pos);
                rocket.resetPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                rocket.updatePosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                context.getWorld().spawnEntity(rocket);

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
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (isIn(group)) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = new CompoundTag();
            tag.putInt("tier", 1);
            tag.putInt("color", 0xFFFFFFFF);
            for (RocketPartType type : RocketPartType.values()) {
                tag.putString(type.asString(), AddonRegistry.ROCKET_PARTS.getId(GalacticraftRocketParts.getDefaultPartForType(type)).toString());
            }
            stack.setTag(tag);
            stacks.add(stack);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (Screen.hasShiftDown()) {
            if (tag.contains("color") && tag.contains("cone") && tag.contains("tier")) {
                tooltip.add(new TranslatableText("tooltip.galacticraft.tier", tag.getInt("tier")).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
                tooltip.add(new TranslatableText("tooltip.galacticraft.color"));
                tooltip.add(new TranslatableText("tooltip.galacticraft.red", tag.getInt("color") >> 16 & 0xFF).setStyle(Style.EMPTY.withColor(Formatting.RED)));
                tooltip.add(new TranslatableText("tooltip.galacticraft.green", tag.getInt("color") >> 8 & 0xFF).setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
                tooltip.add(new TranslatableText("tooltip.galacticraft.blue", tag.getInt("color") & 0xFF).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
                tooltip.add(new TranslatableText("tooltip.galacticraft.alpha", tag.getInt("color") >> 24 & 0xFF).setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
                tooltip.add(new LiteralText("-----").setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
                for (RocketPartType type : RocketPartType.values()) {
                    String s = new Identifier(tag.getString(type.asString())).getPath();
                    if (!(new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString()
                            .equals("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name"))) {
                        s = new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() +
                                "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString();
                    }
                    tooltip.add(new TranslatableText("tooltip.galacticraft.part_type." + type.asString(), s).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                }
            }
        } else {
            tooltip.add(new TranslatableText("tooltip.galacticraft.press_shift").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
    }
}
