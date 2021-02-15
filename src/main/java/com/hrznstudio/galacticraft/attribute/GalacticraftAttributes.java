package com.hrznstudio.galacticraft.attribute;

import alexiil.mc.lib.attributes.Attributes;
import alexiil.mc.lib.attributes.DefaultedAttribute;
import com.hrznstudio.galacticraft.attribute.oxygen.EmptyOxygenTank;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;

public class GalacticraftAttributes {
    public static final DefaultedAttribute<OxygenTank> OXYGEN_TANK_ATTRIBUTE = Attributes.createDefaulted(OxygenTank.class, EmptyOxygenTank.NULL);
}
