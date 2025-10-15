package dev.galacticraft.mod.world.gen.dungeon.records;

public record TemplateMeta(
        int sizeX, int sizeY, int sizeZ,
        PortDef[] entrances,
        PortDef[] exits
) {
}