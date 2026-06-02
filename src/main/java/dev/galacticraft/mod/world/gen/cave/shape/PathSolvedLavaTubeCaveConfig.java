package dev.galacticraft.mod.world.gen.cave.shape;

public record PathSolvedLavaTubeCaveConfig(
        int minMainAnchors,
        int maxMainAnchors,
        int minSegmentsBetweenAnchors,
        int maxSegmentsBetweenAnchors,
        int minSideBranches,
        int maxSideBranches,
        int minSegmentLength,
        int maxSegmentLength,
        double minRadius,
        double maxRadius,
        double minCurve,
        double maxCurve,
        int minAnchorDistance,
        int maxAnchorDistance,
        int minTargetY,
        int maxTargetY
) {
}