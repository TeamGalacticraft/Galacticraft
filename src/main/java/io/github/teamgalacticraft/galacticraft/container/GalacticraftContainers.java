package io.github.teamgalacticraft.galacticraft.container;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorContainer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftContainers {

    public static final Identifier COAL_GENERATOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.COAL_GENERATOR_CONTAINER);

    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(COAL_GENERATOR_CONTAINER, (syncId, id, player, buf) -> new CoalGeneratorContainer(syncId, buf.readBlockPos(), player));
    }
}
