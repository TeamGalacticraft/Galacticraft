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

    public static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.values());

    public static EnumProperty<SideOption> getOptionForDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return SideOption.FRONT_SIDE_OPTION;
            case SOUTH:
                return SideOption.BACK_SIDE_OPTION;
            case EAST:
                return SideOption.RIGHT_SIDE_OPTION;
            case WEST:
                return SideOption.LEFT_SIDE_OPTION;
            case UP:
                return SideOption.TOP_SIDE_OPTION;
            case DOWN:
                return SideOption.BOTTOM_SIDE_OPTION;
        }
        return null;
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
}
