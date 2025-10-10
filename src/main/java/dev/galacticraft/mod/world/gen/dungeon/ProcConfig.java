package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.util.RandomSource;

public final class ProcConfig {
    // -------- Room counts --------
    public int minRooms = 16;   // hard minimum before routing
    public int targetRooms = 32;   // preferred target
    public int maxRooms = 64;

    // -------- Composition knobs (NEW) --------
    /**
     * Fraction (0..1) of total placed rooms to budget as BASIC rooms used
     * along the 3 critical paths (Entrance -> BASICs -> Queen -> End).
     * Default: 0.40 (40%).
     */
    public float criticalBasicFraction = 0.40f;

    /**
     * Fraction (0..1) of non-fixed rooms (i.e., after Entrance/End/Queens) to convert
     * into treasure/branch_end rooms. Default: 0.20 (20%).
     */
    public float treasureFraction = 0.10f;

    // -------- Corridor & clearances (voxels) --------
    /**
     * Corridor half-width (radius). Tube aperture will be (2*rCorr+1).
     */
    public int rCorr = 2;
    /**
     * Extra safety padding added around corridors for routing.
     */
    public int cPad = 1;
    /**
     * Minimum gap between neighboring rooms (baked during placement).
     */
    public int cRoom = 4;

    // Floors for adaptive relaxation
    public int minRCorr = 2;
    public int minCPad = 1;
    public int minCRoom = 3;

    // -------- Attempts & sampling --------
    /**
     * Number of candidate anchors to try when scattering rooms.
     */
    public int anchorAttempts = 260;
    /**
     * Orientations/yaws to try per room.
     */
    public int orientTries = 16;
    /**
     * Per-room attempts at a given anchor/orientation.
     */
    public int perRoomTries = 40;

    // -------- Shell / bounds --------
    public float shellMin = 30f;
    public float shellMax = 76f;

    // -------- Relaxation strategy --------
    public int maxRelaxPasses = 3;
    /**
     * Each relax pass multiplies shell by this factor (>1.0).
     */
    public float relaxShellScale = 1.12f;

    // -------- Pathing / routing --------
    /**
     * Downsample stride for free-space backbone graph (lower = denser).
     */
    public int backboneStride = 2;
    /**
     * A* hard cap on route length in voxels.
     */
    public int maxRouteLen = 220;

    /**
     * How many disjoint entrance→end routes we try to reserve.
     */
    public int requiredMainPaths = 2;

    /**
     * Penalty applied to straight segments (set >0 to discourage long straights).
     */
    public float straightPenalty = 0.35f;

    /**
     * Random jitter factor (0..1) used by router to break ties / vary paths.
     */
    public float jitter = 0.22f;

    /**
     * Bias that prefers routing away from walls (higher = stronger centerline preference).
     */
    public float wallBias = 1.55f;

    /**
     * How many voxels near each portal to skip when rasterizing (to avoid punching into rooms).
     */
    public int skipEndpoint = 2;

    /**
     * Extra padding (world voxels) around (entrance,end) when sizing the mask grid.
     */
    public int gridPad = 28;

    // Penalize connecting portals that face the same way (we prefer opposite-facing)
    public float bendPenalty = 4.0f;

    // Small weight on squared Euclidean distance between portals
    // (use a small value because distSqr can be large, e.g., 40^2 = 1600)
    public float proxPenalty = 0.0015f;

    // -------- Fork controls (kept off by default) --------
    public float forkChance = 0.20f;     // chance to sprout a fork at a viable node
    public int forkMaxPerPath = 3;       // budget per critical path
    public float forkTurnHardness = 0.70f;  // 0..1: high = prefer 90° turns

    public float deadEndMinFraction = 0.10f;     // e.g., 10% of BASICs become dead-ends (minimum)
    public boolean enforceExitEntranceFlow = true; // force corridors: EXIT -> ENTRANCE
    // -------- Derived helpers --------

    public static ProcConfig fromDungeonConfig(DungeonConfig cfg, RandomSource rnd) {
        ProcConfig pc = new ProcConfig();

        // Map anything you’d like from cfg here; defaults above are safe.
        // Example: pc.targetRooms = Math.max(12, Math.min(22, cfg.maxPoints()/4));
        //
        // If you add these to DungeonConfig, wire them here:
        // pc.criticalBasicFraction = Mth.clamp(cfg.criticalBasicFraction(), 0.0f, 1.0f);
        // pc.treasureFraction      = Mth.clamp(cfg.treasureFraction(), 0.0f, 1.0f);
        return pc;
    }

    /**
     * Effective routing dilation radius: R = rCorr + cPad.
     */
    public int effectiveRadius() {
        int r = rCorr + cPad;
        return Math.max(1, r);
    }
}