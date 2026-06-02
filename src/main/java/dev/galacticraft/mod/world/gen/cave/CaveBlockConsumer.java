package dev.galacticraft.mod.world.gen.cave;

@FunctionalInterface
public interface CaveBlockConsumer {
    void accept(int x, int y, int z, CaveZone zone);
}