package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveCavern;
import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveSinkhole;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public final class GlacialCavernShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(context.cave(), context.cell(), random.nextDouble());

        int cavernCount = 2 + random.nextInt(3);
        List<BlockPos> caverns = new ArrayList<>();

        BlockPos first = context.anchor();
        caverns.add(first);

        for (int i = 1; i < cavernCount; i++) {
            BlockPos previous = caverns.get(i - 1);
            double angle = random.nextDouble() * Math.PI * 2.0D;
            int distance = 42 + random.nextInt(46);

            int x = previous.getX() + (int) Math.round(Math.cos(angle) * distance);
            int z = previous.getZ() + (int) Math.round(Math.sin(angle) * distance);
            int y = context.clampY(previous.getY() - 18 + random.nextInt(37));

            caverns.add(new BlockPos(x, y, z));
        }

        for (BlockPos cavern : caverns) {
            double radiusX = 30.0D + random.nextDouble() * 18.0D;
            double radiusY = 10.0D + random.nextDouble() * 7.0D;
            double radiusZ = 30.0D + random.nextDouble() * 18.0D;
            int pillars = 2 + random.nextInt(4);

            plan.addElement(new MoonCaveCavern(cavern, radiusX, radiusY, radiusZ, pillars, random.nextInt()));
        }

        for (int i = 1; i < caverns.size(); i++) {
            addTunnel(plan, caverns.get(i - 1), caverns.get(i), random, 4.0D, 7.5D, 14.0D);
        }

        int sideBranches = 5 + random.nextInt(9);

        for (int i = 0; i < sideBranches; i++) {
            BlockPos start = caverns.get(random.nextInt(caverns.size()));
            BlockPos end = randomBranchTarget(start, context, random);
            addTunnel(plan, start, end, random, 2.3D, 4.2D, 16.0D);
        }

        int sinkholes = 1 + random.nextInt(10);

        for (int i = 0; i < sinkholes; i++) {
            BlockPos cavern = caverns.get(random.nextInt(caverns.size()));
            BlockPos top = new BlockPos(
                    cavern.getX() - 24 + random.nextInt(49),
                    78 + random.nextInt(15),
                    cavern.getZ() - 24 + random.nextInt(49)
            );

            BlockPos bottom = new BlockPos(
                    cavern.getX() - 8 + random.nextInt(17),
                    cavern.getY() + 4 + random.nextInt(8),
                    cavern.getZ() - 8 + random.nextInt(17)
            );

            double radius = 3.5D + random.nextDouble() * 4.0D;
            plan.addElement(new MoonCaveSinkhole(top, bottom, radius, random.nextInt()));
        }

        return plan;
    }

    private static BlockPos randomBranchTarget(BlockPos start, MoonCaveContext context, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = 22 + random.nextInt(54);

        return new BlockPos(
                start.getX() + (int) Math.round(Math.cos(angle) * distance),
                context.clampY(start.getY() - 12 + random.nextInt(25)),
                start.getZ() + (int) Math.round(Math.sin(angle) * distance)
        );
    }

    private static void addTunnel(
            MoonCavePlan plan,
            BlockPos start,
            BlockPos end,
            RandomSource random,
            double minRadius,
            double maxRadius,
            double curve
    ) {
        double radius = minRadius + random.nextDouble() * (maxRadius - minRadius);
        plan.addTunnel(new MoonCaveTunnel(start, end, radius, curve, random.nextInt()));
    }
}