package com.hrznstudio.galacticraft.structure;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftStructurePieceTypes {
    public static final StructurePieceType MOON_VILLAGE = Registry.register(Registry.STRUCTURE_PIECE, new Identifier(Constants.MOD_ID, "moon_village_piece"), MoonVillagePiece::new);

    public static void register() {

    }
}
