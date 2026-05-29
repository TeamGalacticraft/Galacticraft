package dev.galacticraft.mod.world.gen.cave;

public enum CaveTransitionStrength {
    VERY_WEAK(0.25D, 34),
    WEAK(0.45D, 26),
    MEDIUM(0.65D, 20),
    STRONG(0.85D, 12),
    VERY_STRONG(1.0D, 6);

    private final double dominance;
    private final int transitionRadius;

    CaveTransitionStrength(double dominance, int transitionRadius) {
        this.dominance = dominance;
        this.transitionRadius = transitionRadius;
    }

    public double dominance() {
        return this.dominance;
    }

    public int transitionRadius() {
        return this.transitionRadius;
    }
}