package dev.galacticraft.mod.world.gen.dungeon;

import java.util.*;

final class CriticalSetPicker {
    private CriticalSetPicker() {}

    static final class Triplet {
        final java.util.List<RoomPlacer.Placed> path1;
        final java.util.List<RoomPlacer.Placed> path2;
        final java.util.List<RoomPlacer.Placed> path3;
        Triplet(List<RoomPlacer.Placed> a, List<RoomPlacer.Placed> b, List<RoomPlacer.Placed> c) {
            this.path1 = a; this.path2 = b; this.path3 = c;
        }
    }

    /**
     * From the pool of BASIC rooms inside the expanded bbox, pick 40% for critical paths and split 14/13/13.
     * Returns disjoint lists (sizes may differ if total not divisible).
     */
    static Triplet pickAndSplit(List<RoomPlacer.Placed> candidateBasics, Random rnd, int totalBasicsCount) {
        // 40% of usable basics
        int k = Math.max(0, Math.round(totalBasicsCount * 0.40f));
        k = Math.min(k, candidateBasics.size());

        Collections.shuffle(candidateBasics, rnd);
        List<RoomPlacer.Placed> chosen = new ArrayList<>(candidateBasics.subList(0, k));

        // Split 3 ways: ceil(k/3), ceil((k - ceil)/2), rest
        int a = (int)Math.ceil(k / 3.0);
        int b = (int)Math.ceil((k - a) / 2.0);
        int c = k - a - b;
        List<RoomPlacer.Placed> p1 = new ArrayList<>(chosen.subList(0, a));
        List<RoomPlacer.Placed> p2 = new ArrayList<>(chosen.subList(a, a + b));
        List<RoomPlacer.Placed> p3 = new ArrayList<>(chosen.subList(a + b, k));
        return new Triplet(p1, p2, p3);
    }
}