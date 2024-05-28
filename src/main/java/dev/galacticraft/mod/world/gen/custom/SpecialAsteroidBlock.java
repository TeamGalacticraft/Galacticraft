package dev.galacticraft.mod.world.gen.custom;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
public class SpecialAsteroidBlock
{
    public Block block;
    public int probability;
    public double thickness; //Arbitrary scale from 0 to 1;
    public int index;
    public static ArrayList<SpecialAsteroidBlock> register = new ArrayList<>();

    public SpecialAsteroidBlock(Block block, int probability, double thickness)
    {
        this.block = block;
        this.probability = probability;
        this.thickness = thickness;
        this.index = register.size();
        register.add(this);
    }
}
