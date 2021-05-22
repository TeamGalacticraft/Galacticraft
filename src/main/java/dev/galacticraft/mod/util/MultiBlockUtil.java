package dev.galacticraft.mod.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiBlockUtil {
    private MultiBlockUtil() {}

    @NotNull
    public static List<BlockPos> generateSolarPanelParts() {
        ImmutableList.Builder<BlockPos> parts = ImmutableList.builder();
        BlockPos rod = new BlockPos(0, 1, 0);
        BlockPos mid = rod.up();
        BlockPos front = mid.north();
        BlockPos back = mid.south();

        BlockPos right = mid.east();
        BlockPos left = mid.west();

        BlockPos frontLeft = front.east();
        BlockPos frontRight = front.west();
        BlockPos backLeft = back.east();
        BlockPos backRight = back.west();

        parts.add(rod);
        parts.add(mid);
        parts.add(front);
        parts.add(back);

        parts.add(right);
        parts.add(left);

        parts.add(frontLeft);
        parts.add(frontRight);
        parts.add(backLeft);
        parts.add(backRight);

        return parts.build();
    }

}
