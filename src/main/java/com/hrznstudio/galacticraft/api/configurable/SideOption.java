package com.hrznstudio.galacticraft.api.configurable;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum SideOption implements StringIdentifiable {

    BLANK("default"),
    POWER_INPUT("powerin"),
    POWER_OUTPUT("powerout"),
    OXYGEN_INPUT("oxygenin"),
    OXYGEN_OUTPUT("oxygenout");

    private String name;

    SideOption(String name) {
        this.name = name;
    }

    public static List<SideOption> getApplicableValuesForMachine(Block block) {
        if (block instanceof ConfigurableElectricMachineBlock) {
            List<SideOption> options = new ArrayList<>();
            options.add(SideOption.BLANK);
            if (((ConfigurableElectricMachineBlock) block).consumesOxygen()) {
                options.add(SideOption.OXYGEN_INPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).generatesOxygen()) {
                options.add(SideOption.OXYGEN_OUTPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).consumesPower()) {
                options.add(SideOption.POWER_INPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).generatesPower()) {
                options.add(SideOption.POWER_OUTPUT);
            }
            return options;
        }
        return new ArrayList<>();
    }

    @Override
    public String asString() {
        return this.name;
    }

    public SideOption nextValidOption(Block block) {
        try {
            if (getApplicableValuesForMachine(block).contains(SideOption.values()[this.ordinal() + 1])) {
                return SideOption.values()[this.ordinal() + 1];
            } else {
                return SideOption.values()[this.ordinal() + 1].nextValidOption(block);
            }
        } catch (ArrayIndexOutOfBoundsException ignore) {
            if (getApplicableValuesForMachine(block).contains(SideOption.values()[0])) {
                return SideOption.values()[0];
            } else {
                return SideOption.values()[0].nextValidOption(block);
            }
        }
    }

    public String[] getFormattedName() {
        if (this == SideOption.BLANK) {
            return new String[] {"\u00a78Blank"};
        } else if (this == SideOption.OXYGEN_INPUT) {
            return new String[] {"\u00a7bOxygen \u00a7ain"};
        } else if (this == SideOption.OXYGEN_OUTPUT) {
            return new String[] {"\u00a78Oxygen \u00a74out"};
        } else if (this == SideOption.POWER_INPUT) {
            return new String[] {"\u00a7dPower \u00a7ain"};
        } else if (this == SideOption.POWER_OUTPUT) {
            return new String[] {"\u00a7dPower \u00a74out"};
        }
        return new String[] {""};
    }
}
