package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Collections;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensions {
    public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 30, "galacticraft-rewoven:moon", new GalacticraftDimensionType(30, "galacticraft-rewoven:moon", "DIM30", MoonDimension::new, true));
    public static final DimensionType MARS = Registry.register(Registry.DIMENSION, 31, "galacticraft-rewoven:mars", new GalacticraftDimensionType(31, "galacticraft-rewoven:mars", "DIM31", MarsDimension::new, true));


    public static void init() {
    }
}