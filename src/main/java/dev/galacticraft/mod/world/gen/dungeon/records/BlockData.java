package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;

// ===== Pack 12-bit local coords (x:0..3, y:4..7, z:8..11) + BlockState =====
public record BlockData(short packed, BlockState state) {
    // Create from local coords (0..15) within a section
    public static BlockData ofLocal(int lx, int ly, int lz, BlockState state) {
        return new BlockData(pack(lx, ly, lz), state);
    }

    // Create from world position + its section
    public static BlockData ofWorld(SectionPos section, BlockPos worldPos, BlockState state) {
        int lx = worldPos.getX() & 15;
        int ly = worldPos.getY() & 15;
        int lz = worldPos.getZ() & 15;
        return ofLocal(lx, ly, lz, state);
    }

    // Unpack helpers
    public int localX() { return  packed        & 0xF; }
    public int localY() { return (packed >>> 4) & 0xF; }
    public int localZ() { return (packed >>> 8) & 0xF; }

    // Rebuild absolute BlockPos when you have the section (map key)
    public BlockPos toBlockPos(SectionPos section) {
        return new BlockPos(
                section.minBlockX() + localX(),
                section.minBlockY() + localY(),
                section.minBlockZ() + localZ()
        );
    }

    public static short pack(int lx, int ly, int lz) {
        return (short)((lx & 0xF) | ((ly & 0xF) << 4) | ((lz & 0xF) << 8));
    }
}