package dev.galacticraft.mod.world.gen.dungeon.records;

public record Constraints(
        int minY,               // inclusive; -INF = Integer.MIN_VALUE
        int maxY,               // inclusive; +INF = Integer.MAX_VALUE
        int maxPerDungeon,      // 0 = unlimited
        boolean unique,         // true = at most 1 in dungeon
        String[] requireTags,   // must include all
        String[] forbidTags     // must include none
) {
    public static Constraints any() {
        return new Constraints(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, false, new String[0], new String[0]);
    }
}