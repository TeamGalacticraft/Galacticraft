package dev.galacticraft.mod.world.gen.dungeon.plan;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.world.gen.dungeon.*;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;
import dev.galacticraft.mod.world.gen.dungeon.util.Bitmask;
import dev.galacticraft.mod.world.gen.dungeon.util.NegotiatedRouter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public final class DungeonPlanner {
    private static final java.util.concurrent.ExecutorService EXEC =
            new java.util.concurrent.ThreadPoolExecutor(
                    1, Math.max(2, Runtime.getRuntime().availableProcessors() / 3),
                    30L, java.util.concurrent.TimeUnit.SECONDS,
                    new java.util.concurrent.LinkedBlockingQueue<>(),
                    r -> {
                        Thread t = new Thread(r, "DungeonPlanner");
                        t.setDaemon(true);
                        t.setPriority(Thread.MIN_PRIORITY);
                        return t;
                    });

    public static <T> java.util.concurrent.CompletableFuture<T> supplyAsync(Supplier<T> s) {
        return java.util.concurrent.CompletableFuture.supplyAsync(s, EXEC);
    }
}