package io.github.teamgalacticraft.galacticraft.tag;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class GalacticraftTags {

    public static final Tag<Block> WALLS = register(new Identifier("minecraft", "walls"));

    private static Tag<Block> register(Identifier id) {
        return TagRegistry.block(id);
    }

}
