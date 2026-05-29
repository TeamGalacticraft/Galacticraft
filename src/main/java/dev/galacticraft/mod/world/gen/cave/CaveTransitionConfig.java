package dev.galacticraft.mod.world.gen.cave;

public record CaveTransitionConfig(
        boolean enabled,
        CaveTransitionStrength strength
) {
    public static CaveTransitionConfig none() {
        return new CaveTransitionConfig(false, CaveTransitionStrength.VERY_STRONG);
    }

    public static CaveTransitionConfig veryWeak() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.VERY_WEAK);
    }

    public static CaveTransitionConfig weak() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.WEAK);
    }

    public static CaveTransitionConfig medium() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.MEDIUM);
    }

    public static CaveTransitionConfig strong() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.STRONG);
    }

    public static CaveTransitionConfig veryStrong() {
        return new CaveTransitionConfig(true, CaveTransitionStrength.VERY_STRONG);
    }
}