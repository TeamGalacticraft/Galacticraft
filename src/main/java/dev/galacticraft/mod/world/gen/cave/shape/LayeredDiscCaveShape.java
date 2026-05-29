package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRoom;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class LayeredDiscCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private final int minLayers;
    private final int maxLayers;
    private final double minRadius;
    private final double maxRadius;
    private final double minThickness;
    private final double maxThickness;
    private final int minLayerGap;
    private final int maxLayerGap;

    public LayeredDiscCaveShape(
            int minLayers,
            int maxLayers,
            double minRadius,
            double maxRadius,
            double minThickness,
            double maxThickness,
            int minLayerGap,
            int maxLayerGap
    ) {
        this.minLayers = minLayers;
        this.maxLayers = maxLayers;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minThickness = minThickness;
        this.maxThickness = maxThickness;
        this.minLayerGap = minLayerGap;
        this.maxLayerGap = maxLayerGap;
    }

    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(
                context.cave(),
                context.cell(),
                random.nextDouble()
        );
        List<BlockPos> layerCenters = new ArrayList<>();

        int layers = this.minLayers + random.nextInt(this.maxLayers - this.minLayers + 1);
        BlockPos center = context.anchor();

        for (int i = 0; i < layers; i++) {
            int x = center.getX() - 12 + random.nextInt(25);
            int z = center.getZ() - 12 + random.nextInt(25);

            if (i > 0) {
                int gap = this.minLayerGap + random.nextInt(this.maxLayerGap - this.minLayerGap + 1);
                center = center.offset(-8 + random.nextInt(17), random.nextBoolean() ? gap : -gap, -8 + random.nextInt(17));
            }

            center = new BlockPos(x, context.clampY(center.getY()), z);
            layerCenters.add(center);

            double radiusX = randomBetween(random, this.minRadius, this.maxRadius);
            double radiusZ = randomBetween(random, this.minRadius, this.maxRadius);
            double radiusY = randomBetween(random, this.minThickness, this.maxThickness);

            plan.addRoom(new MoonCaveRoom(center, radiusX, radiusY, radiusZ, random.nextInt()));
        }

        for (int i = 1; i < layerCenters.size(); i++) {
            if (random.nextFloat() < 0.65F) {
                BlockPos from = layerCenters.get(i - 1);
                BlockPos to = layerCenters.get(i);

                plan.addTunnel(new MoonCaveTunnel(
                        from,
                        to,
                        1.5D + random.nextDouble() * 1.5D,
                        3.0D + random.nextDouble() * 5.0D,
                        random.nextInt()
                ));
            }
        }

        return plan;
    }

    private static double randomBetween(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}