package dev.galacticraft.mod.world.gen.cave;

public interface MoonCaveElement {
    MoonCaveBounds bounds();

    CaveZone zone(int x, int y, int z);
}