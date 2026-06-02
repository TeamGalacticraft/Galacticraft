package dev.galacticraft.mod.world.gen.cave.shape;

public record PathSolvedBranchingCaveConfig(
        int minMainAnchors,
        int maxMainAnchors,
        int minRoomsBetweenAnchors,
        int maxRoomsBetweenAnchors,
        int minSideBranches,
        int maxSideBranches,
        double minRadius,
        double maxRadius,
        double minHeightRadius,
        double maxHeightRadius,
        double minTunnelRadius,
        double maxTunnelRadius,
        int minAnchorDistance,
        int maxAnchorDistance,
        int minTargetY,
        int maxTargetY
) {
}