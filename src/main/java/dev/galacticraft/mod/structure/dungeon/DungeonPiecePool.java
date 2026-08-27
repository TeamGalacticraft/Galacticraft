package dev.galacticraft.mod.structure.dungeon;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public record DungeonPiecePool(List<DungeonPieceDefinition> pieces) {
    public DungeonPiecePool(List<DungeonPieceDefinition> pieces) {
        this.pieces = List.copyOf(pieces);
    }

    public static DungeonPiecePool of(DungeonPieceDefinition... pieces) {
        return new DungeonPiecePool(Arrays.asList(pieces));
    }

    public List<DungeonPieceDefinition> weightedOrder(
            RandomSource random,
            Predicate<DungeonPieceDefinition> predicate
    ) {
        List<DungeonPieceDefinition> remaining = new ArrayList<>();

        for (DungeonPieceDefinition definition : this.pieces) {
            if (predicate.test(definition)) {
                remaining.add(definition);
            }
        }

        List<DungeonPieceDefinition> result = new ArrayList<>(remaining.size());

        while (!remaining.isEmpty()) {
            int totalWeight = 0;

            for (DungeonPieceDefinition definition : remaining) {
                totalWeight += definition.weight();
            }

            int selected = random.nextInt(totalWeight);

            DungeonPieceDefinition chosen = null;

            for (DungeonPieceDefinition definition : remaining) {
                selected -= definition.weight();

                if (selected < 0) {
                    chosen = definition;
                    break;
                }
            }

            if (chosen == null) {
                throw new IllegalStateException("Failed weighted dungeon-piece selection");
            }

            result.add(chosen);
            remaining.remove(chosen);
        }

        return result;
    }
}