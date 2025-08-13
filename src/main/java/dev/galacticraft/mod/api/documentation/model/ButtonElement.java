package dev.galacticraft.mod.api.documentation.model;

public record ButtonElement(
        String type, int x, int y, int w, int h,
        String textKey, String target
) implements Element {}