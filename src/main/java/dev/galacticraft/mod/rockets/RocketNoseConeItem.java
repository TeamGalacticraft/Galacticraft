package dev.galacticraft.mod.rockets;

import net.minecraft.world.item.Item;

public class RocketNoseConeItem extends Item {
    private final String type;
    public RocketNoseConeItem(Properties properties, String type) {
        super(properties);
        this.type = type;
    }

    public String getType()
    {
        return this.type + "nose_cone";
    }
}
