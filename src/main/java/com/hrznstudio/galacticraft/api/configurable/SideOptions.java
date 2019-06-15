package com.hrznstudio.galacticraft.api.configurable;

import net.minecraft.nbt.CompoundTag;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum SideOptions {
    BLANK,
    POWER_INPUT,
    POWER_OUTPUT,
    OXYGEN_INPUT,
    OXYGEN_OUTPUT;

    public static SideOptions[] fromTag (CompoundTag tag) {
        String[] options = tag.getString("Config").split(",");
        SideOptions[] sideOptions = new SideOptions[6];
        sideOptions[0] = SideOptions.valueOf(options[0]);
        sideOptions[1] = SideOptions.valueOf(options[1]);
        sideOptions[2] = SideOptions.valueOf(options[2]);
        sideOptions[3] = SideOptions.valueOf(options[3]);
        sideOptions[4] = SideOptions.valueOf(options[4]);
        sideOptions[5] = SideOptions.valueOf(options[5]);
        return sideOptions;
    }
}
