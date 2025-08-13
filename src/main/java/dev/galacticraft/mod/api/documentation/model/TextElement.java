package dev.galacticraft.mod.api.documentation.model;

import org.jetbrains.annotations.Nullable;

public record TextElement(
        String type, int minX, int minY, int maxX, int maxY,
        String textKey, @Nullable String align
) implements Element {}