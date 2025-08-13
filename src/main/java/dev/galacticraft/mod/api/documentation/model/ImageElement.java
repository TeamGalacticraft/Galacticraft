package dev.galacticraft.mod.api.documentation.model;

public record ImageElement(
        String type, int x, int y, int w, int h,
        String texture, int u, int v, int texW, int texH
) implements Element {}