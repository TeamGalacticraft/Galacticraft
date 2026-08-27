package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.structure.dungeon.LunarDungeonPiece;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Locale;

public class GCStructurePieceTypes {
    public static final GCRegistry<StructurePieceType> PIECES =
            new GCRegistry<>(
                    BuiltInRegistries.STRUCTURE_PIECE
            );

    public static final StructurePieceType MOON_RUINS_PIECE =
            setTemplatePieceId(
                    MoonRuinsGenerator.Piece::new,
                    "moon_ruins_piece"
            );

    public static final StructurePieceType LUNAR_DUNGEON_PIECE =
            setTemplatePieceId(
                    LunarDungeonPiece::new,
                    "lunar_dungeon_piece"
            );

    public static void register() {
    }

    private static StructurePieceType setFullContextPieceId(
            StructurePieceType type,
            String id
    ) {
        return PIECES.register(
                id.toLowerCase(Locale.ROOT),
                type
        );
    }

    private static StructurePieceType setTemplatePieceId(
            StructurePieceType.StructureTemplateType type,
            String id
    ) {
        return setFullContextPieceId(
                type,
                id
        );
    }
}