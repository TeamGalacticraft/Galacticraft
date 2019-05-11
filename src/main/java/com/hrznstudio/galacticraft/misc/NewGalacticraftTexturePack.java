package com.hrznstudio.galacticraft.misc;

import net.minecraft.resource.DirectoryResourcePack;

import java.io.File;

public class NewGalacticraftTexturePack extends DirectoryResourcePack {

    public NewGalacticraftTexturePack(File file_1) {
        super(file_1);
    }

    @Override
    public String getName() {
        return "New Galacticraft Textures";
    }
}