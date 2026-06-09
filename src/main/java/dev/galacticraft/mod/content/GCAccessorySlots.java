/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import java.util.HashMap;
import java.util.Map;

public class GCAccessorySlots {
    public static final int OXYGEN_MASK_SLOT = 0;
    public static final int OXYGEN_GEAR_SLOT = 1;
    public static final int OXYGEN_TANK_1_SLOT = 2;
    public static final int OXYGEN_TANK_2_SLOT = 3;
    public static final int ACCESSORY_SLOT_START = 4;
    public static final int ACCESSORY_SLOT_END = ACCESSORY_SLOT_START + 3;
    public static final int THERMAL_ARMOR_SLOT_START = 8;
    public static final int THERMAL_ARMOR_SLOT_END = THERMAL_ARMOR_SLOT_START + 3;
    public static final int PET_THERMAL_SLOT = 3;
    public static final Map<Integer, TagKey<Item>> SLOT_TAGS = new HashMap<Integer, TagKey<Item>>();
    public static final Map<Integer, ResourceLocation> SLOT_SPRITES = new HashMap<Integer, ResourceLocation>();

    public static void register() {
        SLOT_TAGS.put(OXYGEN_MASK_SLOT, GCItemTags.OXYGEN_MASKS);
        SLOT_TAGS.put(OXYGEN_GEAR_SLOT, GCItemTags.OXYGEN_GEAR);
        SLOT_SPRITES.put(OXYGEN_MASK_SLOT, Constant.SlotSprite.OXYGEN_MASK);
        SLOT_SPRITES.put(OXYGEN_GEAR_SLOT, Constant.SlotSprite.OXYGEN_GEAR);

        for (int slot = ACCESSORY_SLOT_START; slot <= ACCESSORY_SLOT_END; slot++) {
            SLOT_TAGS.put(slot, GCItemTags.ACCESSORIES);
            SLOT_SPRITES.put(slot, Constant.SlotSprite.GENERIC_ACCESSORY);
        }

        SLOT_TAGS.put(THERMAL_ARMOR_SLOT_START, GCItemTags.THERMAL_HEAD);
        SLOT_TAGS.put(THERMAL_ARMOR_SLOT_START + 1, GCItemTags.THERMAL_CHEST);
        SLOT_TAGS.put(THERMAL_ARMOR_SLOT_START + 2, GCItemTags.THERMAL_PANTS);
        SLOT_TAGS.put(THERMAL_ARMOR_SLOT_START + 3, GCItemTags.THERMAL_BOOTS);
        SLOT_SPRITES.put(THERMAL_ARMOR_SLOT_START, Constant.SlotSprite.THERMAL_HEAD);
        SLOT_SPRITES.put(THERMAL_ARMOR_SLOT_START + 1, Constant.SlotSprite.THERMAL_CHEST);
        SLOT_SPRITES.put(THERMAL_ARMOR_SLOT_START + 2, Constant.SlotSprite.THERMAL_PANTS);
        SLOT_SPRITES.put(THERMAL_ARMOR_SLOT_START + 3, Constant.SlotSprite.THERMAL_BOOTS);
    }
}