/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.misc.footprint;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.UUID;

public class Footprint {
    public static final StreamCodec<ByteBuf, Footprint> STREAM_CODEC = StreamCodecs.composite(
            ResourceLocation.STREAM_CODEC,
            f -> f.dimension,
            ByteBufCodecs.DOUBLE,
            f -> f.position.x,
            ByteBufCodecs.DOUBLE,
            f -> f.position.y,
            ByteBufCodecs.DOUBLE,
            f -> f.position.z,
            ByteBufCodecs.FLOAT,
            f -> f.rotation,
            ByteBufCodecs.SHORT,
            f -> f.age,
            UUIDUtil.STREAM_CODEC,
            f -> f.owner,
            (id, x, y, z, r, a, o) -> new Footprint(id, new Vector3d(x, y, z), r, a, o)
    );

    public static final short MAX_AGE = 3200;
    public final ResourceLocation dimension;
    public final float rotation;
    public final Vector3d position;
    public short age;
    public final UUID owner;

    public Footprint(ResourceLocation dimension, Vector3d position, float rotation, UUID ownerUUID) {
        this(dimension, position, rotation, (short) 0, ownerUUID);
    }

    public Footprint(ResourceLocation dimension, Vector3d position, float rotation, short age, UUID ownerUUID) {
        this.dimension = dimension;
        this.position = position;
        this.rotation = rotation;
        this.age = age;
        this.owner = ownerUUID;
    }

    public static Vector3d getFootprintPosition(Level level, float rotation, Vector3d startPosition, Vec3 playerCenter) {
        Vector3d position = new Vector3d(startPosition);
        float footprintScale = 0.375F;

        int mainPosX = Mth.floor(position.x());
        int mainPosY = Mth.floor(position.y());
        int mainPosZ = Mth.floor(position.z());

        double x0 = (Math.sin((45 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.x;
        double x1 = (Math.sin((135 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.x;
        double x2 = (Math.sin((225 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.x;
        double x3 = (Math.sin((315 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.x;
        double z0 = (Math.cos((45 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.z;
        double z1 = (Math.cos((135 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.z;
        double z2 = (Math.cos((225 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.z;
        double z3 = (Math.cos((315 - rotation) / Constant.RADIANS_TO_DEGREES) * footprintScale) + position.z;

        double xMin = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        double xMax = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        double zMin = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        double zMax = Math.max(Math.max(z0, z1), Math.max(z2, z3));

        if (xMin < mainPosX) {
            position.x += mainPosX - xMin;
        }

        if (xMax > mainPosX + 1) {
            position.x -= xMax - (mainPosX + 1);
        }

        if (zMin < mainPosZ) {
            position.z += mainPosZ - zMin;
        }

        if (zMax > mainPosZ + 1) {
            position.z -= zMax - (mainPosZ + 1);
        }

        return position;
    }
}
