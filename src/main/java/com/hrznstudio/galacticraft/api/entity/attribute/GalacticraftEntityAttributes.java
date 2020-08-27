package com.hrznstudio.galacticraft.api.entity.attribute;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftEntityAttributes {
    public static final EntityAttribute CAN_BREATHE_IN_SPACE = Registry.register(Registry.ATTRIBUTE, new Identifier(Constants.MOD_ID, "can_breathe_in_space"), (new ClampedEntityAttribute("galacticraft-rewoven.attribute.name.can_breathe_in_space", 0.0D, 0.0D, 1.0D)).setTracked(true));
}
