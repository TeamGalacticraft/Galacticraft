package dev.galacticraft.mod.rockets;

import dev.galacticraft.api.rocket.part.RocketEngine;
import dev.galacticraft.api.rocket.part.RocketFin;
import dev.galacticraft.api.rocket.part.type.RocketFinType;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

public class RocketParts {
    //LAUNCH PADS
    public static List<LaunchPadBlock> LAUNCH_PADS = new ArrayList<>();
    public static List<RocketNoseConeItem> NOSE_CONES = new ArrayList<>();
    public static List<RocketFinItem> ROCKET_FINS = new ArrayList<>();
    public static List<RocketEngineItem> ROCKET_ENGINES = new ArrayList<>();
    public static List<RocketPlatingItem> ROCKET_PLATINGS = new ArrayList<>();

    public static void register()
    {
        //iron launch pad
        LAUNCH_PADS.add(new LaunchPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F), "iron_", 500));
        //netherite launch pad... use as an example for more launch pads
        LAUNCH_PADS.add(new LaunchPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F), "netherite_", 1000));
        //iron nose cone
        NOSE_CONES.add(new RocketNoseConeItem(new FabricItemSettings(), "iron_"));

    }
}
