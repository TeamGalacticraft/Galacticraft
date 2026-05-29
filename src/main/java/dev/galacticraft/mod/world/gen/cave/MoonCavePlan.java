package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MoonCavePlan {
    private final ResourceLocation definitionId;
    private final MoonCaveCellPos cell;
    private final double priority;
    private final MoonCaveStyle primaryStyle;
    private final MoonCaveShapeType shapeType;
    private final MoonCaveBounds bounds = new MoonCaveBounds();
    private final List<MoonCaveElement> elements = new ArrayList<>();

    public MoonCavePlan(ResourceLocation definitionId, MoonCaveCellPos cell, double priority, MoonCaveStyle primaryStyle, MoonCaveShapeType shapeType) {
        this.definitionId = definitionId;
        this.cell = cell;
        this.priority = priority;
        this.primaryStyle = primaryStyle;
        this.shapeType = shapeType;
    }

    public ResourceLocation definitionId() {
        return this.definitionId;
    }

    public MoonCaveCellPos cell() {
        return this.cell;
    }

    public double priority() {
        return this.priority;
    }

    public MoonCaveStyle primaryStyle() {
        return this.primaryStyle;
    }

    public MoonCaveShapeType shapeType() {
        return this.shapeType;
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