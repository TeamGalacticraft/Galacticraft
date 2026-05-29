package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class LavaTubeCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private final int minSegments;
    private final int maxSegments;
    private final int minSegmentLength;
    private final int maxSegmentLength;
    private final double minRadius;
    private final double maxRadius;
    private final double minCurve;
    private final double maxCurve;

    public LavaTubeCaveShape(
            int minSegments,
            int maxSegments,
            int minSegmentLength,
            int maxSegmentLength,
            double minRadius,
            double maxRadius,
            double minCurve,
            double maxCurve
    ) {
        this.minSegments = minSegments;
        this.maxSegments = maxSegments;
        this.minSegmentLength = minSegmentLength;
        this.maxSegmentLength = maxSegmentLength;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minCurve = minCurve;
        this.maxCurve = maxCurve;
    }

    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(context.definition().id(), context.cell(), random.nextDouble(), context.style());

        int segments = this.minSegments + random.nextInt(this.maxSegments - this.minSegments + 1);
        BlockPos current = context.anchor();
        double angle = random.nextDouble() * Math.PI * 2.0D;

        for (int i = 0; i < segments; i++) {
            angle += -0.65D + random.nextDouble() * 1.3D;

            int length = this.minSegmentLength + random.nextInt(this.maxSegmentLength - this.minSegmentLength + 1);
            int x = current.getX() + (int) Math.round(Math.cos(angle) * length);
            int z = current.getZ() + (int) Math.round(Math.sin(angle) * length);
            int y = context.clampY(current.getY() - 5 + random.nextInt(11));

            BlockPos next = new BlockPos(x, y, z);

            double radius = randomBetween(random, this.minRadius, this.maxRadius);
            double curve = randomBetween(random, this.minCurve, this.maxCurve);

            plan.addTunnel(new MoonCaveTunnel(current, next, radius, curve, random.nextInt()));
            current = next;
        }

        return plan;
    }

    private static double randomBetween(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}