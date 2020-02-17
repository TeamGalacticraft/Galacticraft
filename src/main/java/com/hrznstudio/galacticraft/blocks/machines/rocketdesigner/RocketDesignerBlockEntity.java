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

package com.hrznstudio.galacticraft.blocks.machines.rocketdesigner;

import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.api.rocket.RocketParts;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketDesignerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final int SCHEMATIC_OUTPUT_SLOT = 0;

    protected int red = 0;
    protected int green = 0;
    protected int blue = 0;
    protected int alpha = 0;

    private RocketPart cone = RocketParts.DEFAULT_CONE;
    private RocketPart body = RocketParts.DEFAULT_BODY;
    private RocketPart fin = RocketParts.DEFAULT_FIN;
    private RocketPart booster = RocketParts.NO_BOOSTER;
    private RocketPart bottom = RocketParts.DEFAULT_BOTTOM;

    private final FullFixedItemInv inventory = new FullFixedItemInv(2) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return getFilterForSlot(slot).matches(item);
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            return (itemStack -> false);
        }
    };

    public RocketDesignerBlockEntity() {
        this(GalacticraftBlockEntities.ROCKET_DESIGNER_TYPE);
    }

    public RocketDesignerBlockEntity(BlockEntityType<?> rocketDesignerType) {
        super(rocketDesignerType);
    }

    public FullFixedItemInv getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("r", red);
        tag.putInt("g", green);
        tag.putInt("b", blue);
        tag.putInt("a", alpha);

        tag.putString("cone", Galacticraft.ROCKET_PARTS.getId(cone).toString());
        tag.putString("body", Galacticraft.ROCKET_PARTS.getId(body).toString());
        tag.putString("fin", Galacticraft.ROCKET_PARTS.getId(fin).toString());
        tag.putString("booster", Galacticraft.ROCKET_PARTS.getId(booster).toString());
        tag.putString("bottom", Galacticraft.ROCKET_PARTS.getId(bottom).toString());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        if (tag.containsKey("r") && tag.containsKey("cone")) {
            red = tag.getInt("r");
            green = tag.getInt("g");
            blue = tag.getInt("b");
            alpha = tag.getInt("a");

            cone = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("cone")));
            body = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("body")));
            fin = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("fin")));
            booster = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("booster")));
            bottom = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("bottom")));
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    public RocketPart getPart(RocketPartType type) {
        switch (type) {
            case BOOSTER:
                return booster;
            case BOTTOM:
                return bottom;
            case CONE:
                return cone;
            case BODY:
                return body;
            case FIN:
                return fin;
            default:
                return null;
        }
    }

    public void setPart(RocketPart part) {
        switch (part.getType()) {
            case BOOSTER:
                booster = part;
                break;
            case BOTTOM:
                bottom = part;
                break;
            case CONE:
                cone = part;
                break;
            case BODY:
                body = part;
                break;
            case FIN:
                fin = part;
                break;
        }
    }
}
