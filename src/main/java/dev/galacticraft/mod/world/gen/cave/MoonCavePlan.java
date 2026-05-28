package dev.galacticraft.mod.world.gen.cave;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MoonCavePlan {
    private static final int PADDING = 5;

    private final MoonCaveRegionPos region;
    private final double priority;
    private final EnumSet<MoonCaveStyle> styles;
    private final MoonCaveBounds bounds = new MoonCaveBounds();
    private final List<MoonCaveRoom> rooms = new ArrayList<>();
    private final List<MoonCaveTunnel> tunnels = new ArrayList<>();

    public MoonCavePlan(MoonCaveRegionPos region, double priority, MoonCaveStyle primaryStyle) {
        this.region = region;
        this.priority = priority;
        this.styles = EnumSet.of(primaryStyle);
    }

    public MoonCaveRegionPos region() {
        return this.region;
    }

    public double priority() {
        return this.priority;
    }

    public EnumSet<MoonCaveStyle> styles() {
        return this.styles;
    }

    public MoonCaveStyle primaryStyle() {
        return this.styles.iterator().next();
    }

    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    public void addStyle(MoonCaveStyle style) {
        this.styles.add(style);
    }

    public void addRoom(MoonCaveRoom room) {
        this.rooms.add(room);
        this.bounds.includeRoom(room.center(), room.radiusX(), room.radiusY(), room.radiusZ(), PADDING);
    }

    public void addTunnel(MoonCaveTunnel tunnel) {
        this.tunnels.add(tunnel);
        this.bounds.includeTunnel(tunnel.start(), tunnel.end(), tunnel.radius(), tunnel.curve(), PADDING);
    }

    public void mergeFrom(MoonCavePlan other) {
        this.styles.addAll(other.styles);
        other.rooms.forEach(this::addRoom);
        other.tunnels.forEach(this::addTunnel);
    }

    public boolean containsAir(double x, double y, double z) {
        for (MoonCaveRoom room : this.rooms) {
            if (room.contains(x, y, z)) {
                return true;
            }
        }

        for (MoonCaveTunnel tunnel : this.tunnels) {
            if (tunnel.contains(x, y, z)) {
                return true;
            }
        }

        return false;
    }

    public boolean innerShell(double x, double y, double z) {
        for (MoonCaveRoom room : this.rooms) {
            if (room.innerShell(x, y, z)) {
                return true;
            }
        }

        for (MoonCaveTunnel tunnel : this.tunnels) {
            if (tunnel.innerShell(x, y, z)) {
                return true;
            }
        }

        return false;
    }

    public boolean outerShell(double x, double y, double z) {
        for (MoonCaveRoom room : this.rooms) {
            if (room.outerShell(x, y, z)) {
                return true;
            }
        }

        for (MoonCaveTunnel tunnel : this.tunnels) {
            if (tunnel.outerShell(x, y, z)) {
                return true;
            }
        }

        return false;
    }
}