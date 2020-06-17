package com.hrznstudio.galacticraft.planetscreen;

import net.minecraft.block.Block;

public class CelestialBodyDisplay {

    public final String name;
    public Block planetModel;
    public int size;
    public int rotation;

    public int x;
    public int y;

    public CelestialBodyDisplay(Block planetModel, int size, int rotation, int x, int y, String name) {
        this.planetModel = planetModel;
        this.size = size;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public void tick(){
        //where we should move the planet on the screen
    }

}
