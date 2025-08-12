package dev.galacticraft.mod.content;

public enum AirlockState {
    NONE,      // no frames sealed (all open)
    PARTIAL,   // at least one sealed, but not all
    ALL;       // all frames sealed
}