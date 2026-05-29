package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedBranchingCaveShape;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedLavaTubeCaveShape;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MoonCaveRegistry {
    private static final Map<MoonCaveStyle, List<MoonCaveDefinition>> DEFINITIONS = new EnumMap<>(MoonCaveStyle.class);

    static {
        register(new MoonCaveDefinition(
                Constant.id("olivine_branching_cave"),
                MoonCaveStyle.OLIVINE,
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        3, 5,
                        2, 4,
                        2, 5,
                        5.5D, 10.5D,
                        3.2D, 6.8D,
                        2.0D, 3.5D,
                        42, 92,
                        -60, -10
                ),
                100,
                0.22F,
                42,
                58,
                -60,
                62
        ));

        register(new MoonCaveDefinition(
                Constant.id("glacial_lava_tube_cave"),
                MoonCaveStyle.GLACIAL,
                MoonCaveShapeType.LAVA_TUBE,
                new PathSolvedLavaTubeCaveShape(
                        5, 8,
                        3, 6,
                        8, 16,
                        12, 34,
                        2.0D, 4.0D,
                        7.0D, 24.0D,
                        36, 96,
                        -10, 10
                ),
                100,
                0.42F,
                64,
                78,
                -16,
                82
        ));

        register(new MoonCaveDefinition(
                Constant.id("cheese_lava_tube_cave"),
                MoonCaveStyle.CHEESE,
                MoonCaveShapeType.LAVA_TUBE,
                new PathSolvedLavaTubeCaveShape(
                        5, 9,
                        3, 7,
                        10, 22,
                        22, 58,
                        2.2D, 4.5D,
                        7.0D, 26.0D,
                        70, 180,
                        -60, -50
                ),
                100,
                0.34F,
                16,
                26,
                -60,
                32
        ));

        register(new MoonCaveDefinition(
                Constant.id("cheese_branching_cave"),
                MoonCaveStyle.CHEESE,
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        4, 7,
                        2, 5,
                        5, 12,
                        4.2D, 8.5D,
                        3.0D, 6.0D,
                        2.0D, 3.8D,
                        70, 170,
                        -60, -50
                ),
                60,
                0.20F,
                -4,
                8,
                -60,
                24
        ));
    }

    private MoonCaveRegistry() {
    }

    public static void register(MoonCaveDefinition definition) {
        DEFINITIONS.computeIfAbsent(definition.style(), ignored -> new ArrayList<>()).add(definition);
    }

    public static MoonCaveDefinition pick(MoonCaveStyle style, RandomSource random) {
        List<MoonCaveDefinition> definitions = DEFINITIONS.get(style);

        if (definitions == null || definitions.isEmpty()) {
            return null;
        }

        List<MoonCaveDefinition> passedChance = new ArrayList<>();

        for (MoonCaveDefinition definition : definitions) {
            if (random.nextFloat() <= definition.spawnChance()) {
                passedChance.add(definition);
            }
        }

        if (passedChance.isEmpty()) {
            return null;
        }

        return weightedPick(passedChance, random);
    }

    public static boolean hasStyleForShapeType(MoonCaveShapeType shapeType, MoonCaveStyle style) {
        List<MoonCaveDefinition> definitions = DEFINITIONS.get(style);

        if (definitions == null) {
            return false;
        }

        for (MoonCaveDefinition definition : definitions) {
            if (definition.shapeType() == shapeType) {
                return true;
            }
        }

        return false;
    }

    private static MoonCaveDefinition weightedPick(List<MoonCaveDefinition> definitions, RandomSource random) {
        int totalWeight = 0;

        for (MoonCaveDefinition definition : definitions) {
            totalWeight += Math.max(0, definition.weight());
        }

        if (totalWeight <= 0) {
            return definitions.get(0);
        }

        int roll = random.nextInt(totalWeight);

        for (MoonCaveDefinition definition : definitions) {
            roll -= Math.max(0, definition.weight());

            if (roll < 0) {
                return definition;
            }
        }

        return definitions.get(definitions.size() - 1);
    }
}