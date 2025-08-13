package dev.galacticraft.mod.api.documentation.model;

public sealed interface Section permits SectionOverview {
    String type();
}