package dev.galacticraft.mod.api.documentation.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HomeDoc(
        int schema,
        String titleKey,
        @Nullable TitlePos titlePos, // Optional
        @Nullable List<Element> elements, // Optional
        @Nullable List<ResourceLocation> links //Optional
) {
    public record TitlePos(int x, int y) {}
}