package com.hrznstudio.galacticraft.planetscreen;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class CelestialBodyDisplay {

    public final String name;
    public Block planetModel;
    public int size;
    public int rotation;

    public int x;
    public int y;

    public Identifier dimension;

    public CelestialBodyDisplay(Block planetModel, int size, int rotation, int x, int y, String name, Identifier dim) {
        this.planetModel = planetModel;
        this.size = size;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.name = name;
        this.dimension = dim;
    }

    public void tick(){
        //where we should move the planet on the screen
    }

}
