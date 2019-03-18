package io.github.teamgalacticraft.galacticraft.items;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftItems {

    public static ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP))
            .build();

    public static void init() {

    }
}
