package dev.galacticraft.mod.api.documentation.model;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record SubDoc(
        int schema,
        ResourceLocation id,
        @Nullable ResourceLocation parent, // Optional
        String titleKey,
        List<ResourceLocation> bind,
        List<String> tags,
        List<Section> sections,
        @Nullable HomeDoc.TitlePos titlePos, // Optional
        @Nullable List<Element> elements // Optional
) {}