package dev.galacticraft.mod.misc.cape;


public enum CapeRole {
    NONE,
    PATRON,
    DEVELOPER;

    public boolean atLeast(CapeRole other) {
        return this.ordinal() >= other.ordinal();
    }
}