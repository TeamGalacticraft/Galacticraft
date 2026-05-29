package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

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
        MoonCavePlan plan = new MoonCavePlan(
                context.definition().id(),
                context.cell(),
                random.nextDouble(),
                context.style(),
                context.definition().shapeType()
        );

        List<TubeNode> openEnds = new ArrayList<>();
        openEnds.add(new TubeNode(context.anchor(), random.nextDouble() * Math.PI * 2.0D));

        int totalSegments = this.minSegments + random.nextInt(this.maxSegments - this.minSegments + 1);

        for (int i = 0; i < totalSegments && !openEnds.isEmpty(); i++) {
            int index = random.nextInt(openEnds.size());
            TubeNode node = openEnds.remove(index);

            int branchCount = this.pickBranchCount(random, i);

            for (int branch = 0; branch < branchCount; branch++) {
                TubeNode next = this.extend(node, context, random, branch);

                double radius = randomBetween(random, this.minRadius, this.maxRadius);
                double curve = randomBetween(random, this.minCurve, this.maxCurve);

                plan.addTunnel(new MoonCaveTunnel(node.pos, next.pos, radius, curve, random.nextInt()));

                if (random.nextFloat() < 0.82F) {
                    openEnds.add(next);
                }
            }
        }

        return plan;
    }

    private int pickBranchCount(RandomSource random, int segmentIndex) {
        if (segmentIndex == 0) {
            return 2 + random.nextInt(2);
        }

        float roll = random.nextFloat();

        if (roll < 0.16F) {
            return 2;
        }

        if (roll < 0.78F) {
            return 1;
        }

        return 0;
    }

    private TubeNode extend(TubeNode node, MoonCaveContext context, RandomSource random, int branchIndex) {
        double angle = node.angle + randomBetween(random, -0.85D, 0.85D);

        if (branchIndex == 1) {
            angle += random.nextBoolean() ? 0.9D : -0.9D;
        } else if (branchIndex == 2) {
            angle += Math.PI + randomBetween(random, -0.4D, 0.4D);
        }

        int length = this.minSegmentLength + random.nextInt(this.maxSegmentLength - this.minSegmentLength + 1);
        int x = node.pos.getX() + (int) Math.round(Math.cos(angle) * length);
        int z = node.pos.getZ() + (int) Math.round(Math.sin(angle) * length);

        int downwardBias = random.nextFloat() < 0.58F ? random.nextInt(9) : 0;
        int y = context.clampY(node.pos.getY() - downwardBias - 3 + random.nextInt(9));

        return new TubeNode(new BlockPos(x, y, z), angle);
    }

    private static double randomBetween(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private record TubeNode(BlockPos pos, double angle) {
    }
}