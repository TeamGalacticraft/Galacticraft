package io.github.teamgalacticraft.galacticraft.blocks.configurable;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.api.blocks.configurable.BlockConfigurationType;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class PowerOutputConfigurationType implements BlockConfigurationType {
    
    @Override
    public Identifier getName() {
        return new Identifier(Constants.MOD_ID, "power_output");
    }
}
