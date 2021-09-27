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

package dev.galacticraft.mod.screen.slot;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.AutomationType;
import dev.galacticraft.mod.util.ColorUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Objects;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SlotType implements StringIdentifiable {
    public static final Registry<SlotType> SLOT_TYPES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Constant.MOD_ID, "slot_type")), Lifecycle.experimental());

    public static final SlotType INPUT = new SlotType(new Identifier(Constant.MOD_ID, "input"), TextColor.fromRgb(ColorUtil.rgb(0, 160, 7)), new TranslatableText("ui.galacticraft.io_config.input"), AutomationType.ITEM_INPUT);
    public static final SlotType FLUID_TANK_IO = new SlotType(new Identifier(Constant.MOD_ID, "fluid_tank_io"), TextColor.fromRgb(ColorUtil.rgb(91, 156, 175)), new TranslatableText("ui.galacticraft.io_config.fluid_tank_io"), AutomationType.ITEM_IO);
    public static final SlotType OUTPUT = new SlotType(new Identifier(Constant.MOD_ID, "output"), TextColor.fromRgb(ColorUtil.rgb(187, 16, 18)), new TranslatableText("ui.galacticraft.io_config.output"), AutomationType.ITEM_OUTPUT);
    public static final SlotType CHARGE = new SlotType(new Identifier(Constant.MOD_ID, "charge"), TextColor.fromRgb(ColorUtil.rgb(220, 196, 57)), new TranslatableText("ui.galacticraft.io_config.charge"), AutomationType.ITEM_IO);
    public static final SlotType OXYGEN_TANK = new SlotType(new Identifier(Constant.MOD_ID, "oxygen_tank"), TextColor.fromRgb(ColorUtil.rgb(57, 119, 207)), new TranslatableText("ui.galacticraft.io_config.oxygen_tank"), AutomationType.ITEM_IO);
    public static final SlotType OTHER = new SlotType(new Identifier(Constant.MOD_ID, "other"), TextColor.fromRgb(ColorUtil.rgb(141, 50, 199)), new TranslatableText("ui.galacticraft.io_config.other"), AutomationType.ITEM_IO);

    public static final SlotType WILDCARD_ITEM = new SlotType(new Identifier(Constant.MOD_ID, "wildcard_item"), TextColor.fromRgb(ColorUtil.rgb(141, 50, 199)), new TranslatableText("ui.galacticraft.io_config.wildcard_item"), AutomationType.ITEM_IO);
    public static final SlotType WILDCARD_FLUID = new SlotType(new Identifier(Constant.MOD_ID, "wildcard_fluid"), TextColor.fromRgb(ColorUtil.rgb(141, 50, 199)), new TranslatableText("ui.galacticraft.io_config.wildcard_fluid"), AutomationType.ITEM_IO);

    public static final SlotType OIL_IN = new SlotType(new Identifier(Constant.MOD_ID, "oil"), TextColor.fromRgb(ColorUtil.rgb(40, 40, 40)), new TranslatableText("ui.galacticraft.io_config.oil"), AutomationType.FLUID_INPUT);
    public static final SlotType OXYGEN_IN = new SlotType(new Identifier(Constant.MOD_ID, "oxygen_in"), TextColor.fromRgb(ColorUtil.rgb(57, 119, 207)), new TranslatableText("ui.galacticraft.io_config.oxygen_in"), AutomationType.FLUID_INPUT);
    public static final SlotType OXYGEN_OUT = new SlotType(new Identifier(Constant.MOD_ID, "oxygen_out"), TextColor.fromRgb(ColorUtil.rgb(57, 119, 207)), new TranslatableText("ui.galacticraft.io_config.oxygen_out"), AutomationType.FLUID_OUTPUT);
    public static final SlotType OXYGEN = new SlotType(new Identifier(Constant.MOD_ID, "oxygen"), TextColor.fromRgb(ColorUtil.rgb(57, 119, 207)), new TranslatableText("ui.galacticraft.io_config.oxygen"), AutomationType.FLUID_IO);
    public static final SlotType FUEL_OUT = new SlotType(new Identifier(Constant.MOD_ID, "fuel"), TextColor.fromRgb(ColorUtil.rgb(70, 65, 11)), new TranslatableText("ui.galacticraft.io_config.fuel"), AutomationType.FLUID_OUTPUT);
    public static final SlotType SOLID_FUEL = new SlotType(new Identifier(Constant.MOD_ID, "solid_fuel"), TextColor.fromRgb(ColorUtil.rgb(40, 40, 40)), new TranslatableText("ui.galacticraft.io_config.solid_fuel"), AutomationType.ITEM_INPUT);
    public static final SlotType COAL = new SlotType(new Identifier(Constant.MOD_ID, "coal"), TextColor.fromRgb(ColorUtil.rgb(30, 30, 30)), new TranslatableText("ui.galacticraft.io_config.coal"), AutomationType.ITEM_INPUT);
    public static final SlotType NONE = new SlotType(new Identifier(Constant.MOD_ID, "none"), TextColor.fromRgb(ColorUtil.rgb(0, 0, 0)), new TranslatableText("ui.galacticraft.io_config.none"), AutomationType.NONE);

    private final Identifier id;
    private final TextColor color;
    private final Text name;
    private final AutomationType type;

    public SlotType(Identifier id, TextColor color, TranslatableText name, AutomationType type) {
        this.id = id;
        this.color = color;
        this.name = name.setStyle(Style.EMPTY.withColor(color));
        this.type = type;
    }

    public Identifier getId() {
        return id;
    }

    public TextColor getColor() {
        return color;
    }

    public Text getName() {
        return name;
    }

    public AutomationType getType() {
        return type;
    }

    @Override
    public String asString() {
        return this.id.toString();
    }

    @Override
    public String toString() {
        return "SlotType{" +
                "id=" + id +
                ", color=" + color +
                ", name=" + name +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotType slotType = (SlotType) o;
        return getId().equals(slotType.getId()) && getColor().equals(slotType.getColor()) && getName().equals(slotType.getName()) && getType() == slotType.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getColor(), getName(), getType());
    }

    static {
        Registry.register(SLOT_TYPES, WILDCARD_ITEM.getId(), WILDCARD_ITEM);
        Registry.register(SLOT_TYPES, WILDCARD_FLUID.getId(), WILDCARD_FLUID);
        Registry.register(SLOT_TYPES, INPUT.getId(), INPUT);
        Registry.register(SLOT_TYPES, FLUID_TANK_IO.getId(), FLUID_TANK_IO);
        Registry.register(SLOT_TYPES, OUTPUT.getId(), OUTPUT);
        Registry.register(SLOT_TYPES, OXYGEN_TANK.getId(), OXYGEN_TANK);
        Registry.register(SLOT_TYPES, CHARGE.getId(), CHARGE);
        Registry.register(SLOT_TYPES, OTHER.getId(), OTHER);
        Registry.register(SLOT_TYPES, OIL_IN.getId(), OIL_IN);
        Registry.register(SLOT_TYPES, FUEL_OUT.getId(), FUEL_OUT);
        Registry.register(SLOT_TYPES, SOLID_FUEL.getId(), SOLID_FUEL);
    }
}
