package dev.galacticraft.mod.structure.dungeon;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record DungeonPieceDefinition(
        ResourceLocation template,
        DungeonWing wing,
        DungeonPieceCategory category,
        int weight,
        int maxPerDungeon,
        int minimumDepth,
        int maximumDepth,
        boolean allowSameCategoryConsecutively
) {
    public DungeonPieceDefinition {
        Objects.requireNonNull(template, "template");
        Objects.requireNonNull(wing, "wing");
        Objects.requireNonNull(category, "category");

        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be > 0");
        }

        if (maxPerDungeon < 0) {
            throw new IllegalArgumentException("maxPerDungeon cannot be negative");
        }

        if (minimumDepth < 0) {
            throw new IllegalArgumentException("minimumDepth cannot be negative");
        }

        if (maximumDepth < minimumDepth) {
            throw new IllegalArgumentException(
                    "maximumDepth cannot be less than minimumDepth"
            );
        }
    }

    public boolean canGenerateAtDepth(int depth) {
        return depth >= this.minimumDepth && depth <= this.maximumDepth;
    }

    /**
     * A max of zero means unlimited.
     */
    public boolean hasGenerationLimit() {
        return this.maxPerDungeon > 0;
    }
}