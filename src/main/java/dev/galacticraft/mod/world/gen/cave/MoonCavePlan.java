/*
 * Copyright (c) 2019-2026 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    public void addElement(MoonCaveElement element) {
        this.elements.add(element);
        MoonCaveBounds b = element.bounds();
        this.bounds.include(b.minX(), b.minY(), b.minZ(), b.maxX(), b.maxY(), b.maxZ());
    }
}