package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.world.gen.cave.impl.CheeseBranchingCave;
import dev.galacticraft.mod.world.gen.cave.impl.CheeseLavaTubeCave;
import dev.galacticraft.mod.world.gen.cave.impl.GlacialLavaTubeCave;
import dev.galacticraft.mod.world.gen.cave.impl.OlivineBranchingCave;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public final class MoonCaveRegistry {
    private static final List<PlanetCave> CAVES = new ArrayList<>();

    static {
        registerDefaults();
    }

    private MoonCaveRegistry() {
    }

    public static void registerDefaults() {
        OlivineBranchingCave.register();
        GlacialLavaTubeCave.register();
        CheeseLavaTubeCave.register();
        CheeseBranchingCave.register();
    }

    public static void register(PlanetCave cave) {
        CAVES.add(cave);
    }

    public static PlanetCave pickForBiome(Holder<Biome> biome, RandomSource random) {
        List<PlanetCave> candidates = new ArrayList<>();

        for (PlanetCave cave : CAVES) {
            if (cave.matchesBiome(biome) && random.nextFloat() <= cave.spawnChance()) {
                candidates.add(cave);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return weightedPick(candidates, random);
    }

    public static PlanetCave findTransitionCave(Holder<Biome> biome, MoonCaveShapeType shapeType) {
        for (PlanetCave cave : CAVES) {
            if (cave.shapeType() == shapeType && cave.matchesBiome(biome)) {
                return cave;
            }
        }

        return null;
    }

    public static boolean isKnownCaveBlock(BlockStateLikeAccess state) {
        for (PlanetCave cave : CAVES) {
            if (state.is(cave.innerWall(0, 0, 0).getBlock())
                    || state.is(cave.outerWall(0, 0, 0).getBlock())
                    || state.is(cave.accent(0, 0, 0).getBlock())) {
                return true;
            }
        }

        return false;
    }

    private static PlanetCave weightedPick(List<PlanetCave> caves, RandomSource random) {
        int totalWeight = 0;

        for (PlanetCave cave : caves) {
            totalWeight += Math.max(0, cave.weight());
        }

        if (totalWeight <= 0) {
            return caves.get(0);
        }

        int roll = random.nextInt(totalWeight);

        for (PlanetCave cave : caves) {
            roll -= Math.max(0, cave.weight());

            if (roll < 0) {
                return cave;
            }
        }

        return caves.get(caves.size() - 1);
    }

    public static PlanetCave firstForBiome(Holder<Biome> biome) {
        for (PlanetCave cave : CAVES) {
            if (cave.matchesBiome(biome)) {
                return cave;
            }
        }

        return null;
    }

    public interface BlockStateLikeAccess {
        boolean is(net.minecraft.world.level.block.Block block);
    }
}