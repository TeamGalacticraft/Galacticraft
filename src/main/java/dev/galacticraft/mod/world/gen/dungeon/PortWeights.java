package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

final class PortWeights {
    private PortWeights() {}

    static float directedWeight(RoomPlacer.Placed from, RoomPlacer.Placed to) {
        var sA = from.scan();
        var sB = to.scan();

        // exits of A, entrances of B
        var exitsA = sA.exits().isEmpty() ? sA.entrances() : sA.exits();
        var entB   = sB.entrances().isEmpty() ? sB.exits()    : sB.entrances();

        float best = Float.POSITIVE_INFINITY;

        for (var pa : exitsA) {
            // world port A:
            BlockPos wpa = Transforms.worldOfLocalMin(pa.localPos(), from.origin(), sA.size(), from.rot());
            Direction fa = Transforms.rotateFacingYaw(pa.facing(), from.rot());

            for (var pb : entB) {
                BlockPos wpb = Transforms.worldOfLocalMin(pb.localPos(), to.origin(), sB.size(), to.rot());
                Direction fb = Transforms.rotateFacingYaw(pb.facing(), to.rot());

                if (!pa.axisMatches(pb)) continue;
                if (!pa.apertureMatches(pb)) continue;

                // face directions must be opposite axes (corridor between them)
                if (!fa.getAxis().isHorizontal() || !fb.getAxis().isHorizontal()) continue;
                if (fa.getAxis() != fb.getAxis()) continue;
                if (fa == fb) continue; // need opposite

                // distance
                float dx = wpb.getX() - wpa.getX();
                float dy = wpb.getY() - wpa.getY();
                float dz = wpb.getZ() - wpa.getZ();
                float d  = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);

                if (d < best) best = d;
            }
        }
        return best;
    }
}