/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class GCAccessorySlots {
    public static final int OXYGEN_MASK_SLOT = 4;
    public static final int OXYGEN_GEAR_SLOT = 5;
    public static final int OXYGEN_TANK_1_SLOT = 6;
    public static final int OXYGEN_TANK_2_SLOT = 7;
    public static final List<TagKey<Item>> SLOT_TAGS = new ArrayList<TagKey<Item>>();
    public static final List<ResourceLocation> SLOT_SPRITES = new ArrayList<ResourceLocation>();

    public static void register() {
        SLOT_TAGS.add(GCItemTags.THERMAL_HEAD);
        SLOT_TAGS.add(GCItemTags.THERMAL_CHEST);
        SLOT_TAGS.add(GCItemTags.THERMAL_PANTS);
        SLOT_TAGS.add(GCItemTags.THERMAL_BOOTS);
        SLOT_TAGS.add(GCItemTags.OXYGEN_MASKS);
        SLOT_TAGS.add(GCItemTags.OXYGEN_GEAR);
        SLOT_TAGS.add(GCItemTags.OXYGEN_TANKS);
        SLOT_TAGS.add(GCItemTags.OXYGEN_TANKS);
        SLOT_TAGS.add(GCItemTags.ACCESSORIES);
        SLOT_TAGS.add(GCItemTags.ACCESSORIES);
        SLOT_TAGS.add(GCItemTags.ACCESSORIES);
        SLOT_TAGS.add(GCItemTags.ACCESSORIES);

        SLOT_SPRITES.add(Constant.SlotSprite.THERMAL_HEAD);
        SLOT_SPRITES.add(Constant.SlotSprite.THERMAL_CHEST);
        SLOT_SPRITES.add(Constant.SlotSprite.THERMAL_PANTS);
        SLOT_SPRITES.add(Constant.SlotSprite.THERMAL_BOOTS);
        SLOT_SPRITES.add(Constant.SlotSprite.OXYGEN_MASK);
        SLOT_SPRITES.add(Constant.SlotSprite.OXYGEN_GEAR);
        SLOT_SPRITES.add(Constant.SlotSprite.OXYGEN_TANK);
        SLOT_SPRITES.add(Constant.SlotSprite.OXYGEN_TANK);
        SLOT_SPRITES.add(Constant.SlotSprite.GENERIC_ACCESSORY);
        SLOT_SPRITES.add(Constant.SlotSprite.GENERIC_ACCESSORY);
        SLOT_SPRITES.add(Constant.SlotSprite.GENERIC_ACCESSORY);
        SLOT_SPRITES.add(Constant.SlotSprite.GENERIC_ACCESSORY);
    }
}