package dev.galacticraft.mod.world.gen.cave;

import java.util.ArrayList;
import java.util.List;

public class MoonCavePlan {
    private final PlanetCave cave;
    private final MoonCaveCellPos cell;
    private final double priority;
    private final MoonCaveBounds bounds = new MoonCaveBounds();
    private final List<MoonCaveElement> elements = new ArrayList<>();

    public MoonCavePlan(PlanetCave cave, MoonCaveCellPos cell, double priority) {
        this.cave = cave;
        this.cell = cell;
        this.priority = priority;
    }

    public PlanetCave cave() {
        return this.cave;
    }

    public MoonCaveCellPos cell() {
        return this.cell;
    }

    public double priority() {
        return this.priority;
    }

    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    public List<MoonCaveElement> elements() {
        return this.elements;
    }

    public void addRoom(MoonCaveRoom room) {
        this.addElement(room);
    }

    public void addTunnel(MoonCaveTunnel tunnel) {
        this.addElement(tunnel);
    }

    private void addElement(MoonCaveElement element) {
        this.elements.add(element);
        MoonCaveBounds b = element.bounds();
        this.bounds.include(b.minX(), b.minY(), b.minZ(), b.maxX(), b.maxY(), b.maxZ());
    }
}