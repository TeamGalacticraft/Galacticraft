package io.github.teamgalacticraft.galacticraft.container;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator.CoalGeneratorContainer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftContainers {

    public static final Identifier COAL_GENERATOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.COAL_GENERATOR_CONTAINER);
    public static final Identifier PLAYER_INVENTORY_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.PLAYER_INVENTORY_CONTAINER);

    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(COAL_GENERATOR_CONTAINER, (syncId, id, player, buf) -> new CoalGeneratorContainer(syncId, buf.readBlockPos(), player));
        ContainerProviderRegistry.INSTANCE.registerFactory(PLAYER_INVENTORY_CONTAINER, (syncId, identifier, playerEntity, packetByteBuf) -> new PlayerInventoryContainer(syncId, playerEntity));
    }
}
