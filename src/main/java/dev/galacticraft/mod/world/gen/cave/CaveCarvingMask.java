package dev.galacticraft.mod.world.gen.cave;

public final class CaveCarvingMask {
    private static final byte NONE = 0;
    private static final byte OUTER = 1;
    private static final byte INNER = 2;
    private static final byte AIR = 3;

    private final int minY;
    private final int maxY;
    private final int height;
    private final byte[] zones;
    private final MoonCavePlan[] owners;

    public CaveCarvingMask(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        this.height = maxY - minY + 1;
        this.zones = new byte[16 * 16 * this.height];
        this.owners = new MoonCavePlan[this.zones.length];
    }

    public void set(int localX, int y, int localZ, CaveZone zone, MoonCavePlan owner) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return;
        }

        int index = this.index(localX, y, localZ);
        byte next = encode(zone);

        if (next >= this.zones[index]) {
            this.zones[index] = next;
            this.owners[index] = owner;
        }
    }

    public CaveZone get(int localX, int y, int localZ) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return CaveZone.NONE;
        }

        return decode(this.zones[this.index(localX, y, localZ)]);
    }

    public MoonCavePlan owner(int localX, int y, int localZ) {
        if (localX < 0 || localX > 15 || localZ < 0 || localZ > 15 || y < this.minY || y > this.maxY) {
            return null;
        }

        return this.owners[this.index(localX, y, localZ)];
    }

    private int index(int localX, int y, int localZ) {
        int localY = y - this.minY;
        return (localY * 16 + localZ) * 16 + localX;
    }

    private static byte encode(CaveZone zone) {
        return switch (zone) {
            case OUTER_SHELL -> OUTER;
            case INNER_SHELL -> INNER;
            case AIR -> AIR;
            default -> NONE;
        };
    }

    private static CaveZone decode(byte value) {
        return switch (value) {
            case OUTER -> CaveZone.OUTER_SHELL;
            case INNER -> CaveZone.INNER_SHELL;
            case AIR -> CaveZone.AIR;
            default -> CaveZone.NONE;
        };
    }
}