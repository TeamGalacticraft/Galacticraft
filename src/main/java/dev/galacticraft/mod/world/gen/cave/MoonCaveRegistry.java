package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.cave.shape.BranchingCaveShape;
import dev.galacticraft.mod.world.gen.cave.shape.LayeredDiscCaveShape;
import dev.galacticraft.mod.world.gen.cave.shape.LavaTubeCaveShape;
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
                new BranchingCaveShape(12, 26, 5.5D, 11.0D, 3.5D, 7.0D, 1.8D, 3.2D, 12, 28),
                100,
                0.35F
        ));

        register(new MoonCaveDefinition(
                Constant.id("glacial_layered_cavern"),
                MoonCaveStyle.GLACIAL,
                new LayeredDiscCaveShape(3, 6, 24.0D, 56.0D, 3.0D, 7.0D, 8, 18),
                100,
                0.25F
        ));

        register(new MoonCaveDefinition(
                Constant.id("cheese_lava_tube_cave"),
                MoonCaveStyle.CHEESE,
                new LavaTubeCaveShape(4, 8, 24, 40, 2.0D, 4.2D, 4.0D, 18.0D),
                100,
                0.30F
        ));

        register(new MoonCaveDefinition(
                Constant.id("cheese_branching_cave"),
                MoonCaveStyle.CHEESE,
                new BranchingCaveShape(10, 22, 4.0D, 8.0D, 3.0D, 5.5D, 2.0D, 4.0D, 9, 22),
                60,
                0.22F
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