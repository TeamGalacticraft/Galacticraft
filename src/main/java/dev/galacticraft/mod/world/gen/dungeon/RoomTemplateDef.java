package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.resources.ResourceLocation;

/**
 * Only metadata you control; geometry & ports are scanned from NBT.
 */
public record RoomTemplateDef(
        ResourceLocation id,
        RoomType type,
        int pointsCost,
        int weight
) {
    public enum RoomType {ENTRANCE, BASIC, BRANCH_END, QUEEN, END}
}