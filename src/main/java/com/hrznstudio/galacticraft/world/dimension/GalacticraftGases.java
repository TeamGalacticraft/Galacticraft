package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.api.addon.AddonRegistry;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftGases {

    public static final AtmosphericGas NITROGEN_OXIDE = register(
            new AtmosphericGas(
                    new Identifier("galacticraft-rewoven", "nitrogen_oxide"),
                    "ui.galacticraft-rewoven.nitrogen_oxide",
                    "NO"
            )
    );
    public static final AtmosphericGas HYDROGEN_DEUTERIUM_OXYGEN = register(
            new AtmosphericGas(
                    new Identifier("galacticraft-rewoven", "hydrogen_deuterium_oxygen"),
                    "ui.galacticraft-rewoven.hydrogen_deuterium_oxygen",
                    "HDO"
            )
    );
    private static AtmosphericGas register(AtmosphericGas gas) {
        return Registry.register(AddonRegistry.ATMOSPHERIC_GASES, gas.getId(), gas);
    }

    public static void init() {}
}
