/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public enum FootprintType {
    HUMAN(Constant.id("textures/misc/footprint.png"), 0.5F, 0.375F, 0.25D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0F, false, 0.0D, 0.0D, 0.35D),
    DOG(Constant.id("textures/misc/pawprint.png"), 0.09F, 0.07F, 0.0875D, 0.16D, -0.16D, 0.115D, -0.0875D, 0.0875D, -0.0875D, 0.24125D, 0.26875D, -0.43D, -0.43D, Mth.PI, true, 0.015D, 0.42D, 0.25D),
    CAT(Constant.id("textures/misc/pawprint.png"), 0.069609375F, 0.054140625F, 0.06759765625D, 0.14D, -0.14D, 0.057451171875D, -0.0496005859375D, 0.06615234375D, -0.0705029296875D, 0.219091796875D, 0.219091796875D, -0.329140625D, -0.3326953125D, Mth.PI, true, 0.012D, 0.36D, 0.2D);

    public static final StreamCodec<ByteBuf, FootprintType> STREAM_CODEC = StreamCodecs.ofEnum(values());

    private final ResourceLocation texture;
    private final float renderScale;
    private final float placementScale;
    private final double movingSideOffset;
    private final double movingLeftForwardOffset;
    private final double movingRightForwardOffset;
    private final double stopFrontLeftSideOffset;
    private final double stopFrontRightSideOffset;
    private final double stopBackLeftSideOffset;
    private final double stopBackRightSideOffset;
    private final double stopFrontLeftForwardOffset;
    private final double stopFrontRightForwardOffset;
    private final double stopBackLeftForwardOffset;
    private final double stopBackRightForwardOffset;
    private final float renderRotationOffset;
    private final boolean pet;
    private final double stopSpeedThreshold;
    private final double restartClearance;
    private final double stepDistance;

    FootprintType(
            ResourceLocation texture,
            float renderScale,
            float placementScale,
            double movingSideOffset,
            double movingLeftForwardOffset,
            double movingRightForwardOffset,
            double stopFrontLeftSideOffset,
            double stopFrontRightSideOffset,
            double stopBackLeftSideOffset,
            double stopBackRightSideOffset,
            double stopFrontLeftForwardOffset,
            double stopFrontRightForwardOffset,
            double stopBackLeftForwardOffset,
            double stopBackRightForwardOffset,
            float renderRotationOffset,
            boolean pet,
            double stopSpeedThreshold,
            double restartClearance,
            double stepDistance
    ) {
        this.texture = texture;
        this.renderScale = renderScale;
        this.placementScale = placementScale;
        this.movingSideOffset = movingSideOffset;
        this.movingLeftForwardOffset = movingLeftForwardOffset;
        this.movingRightForwardOffset = movingRightForwardOffset;
        this.stopFrontLeftSideOffset = stopFrontLeftSideOffset;
        this.stopFrontRightSideOffset = stopFrontRightSideOffset;
        this.stopBackLeftSideOffset = stopBackLeftSideOffset;
        this.stopBackRightSideOffset = stopBackRightSideOffset;
        this.stopFrontLeftForwardOffset = stopFrontLeftForwardOffset;
        this.stopFrontRightForwardOffset = stopFrontRightForwardOffset;
        this.stopBackLeftForwardOffset = stopBackLeftForwardOffset;
        this.stopBackRightForwardOffset = stopBackRightForwardOffset;
        this.renderRotationOffset = renderRotationOffset;
        this.pet = pet;
        this.stopSpeedThreshold = stopSpeedThreshold;
        this.restartClearance = restartClearance;
        this.stepDistance = stepDistance;
    }

    public ResourceLocation texture() {
        return this.texture;
    }

    public float renderScale() {
        return this.renderScale;
    }

    public float placementScale() {
        return this.placementScale;
    }

    public double movingSideOffset() {
        return this.movingSideOffset;
    }

    public double movingForwardOffset(int side) {
        return side > 0 ? this.movingLeftForwardOffset : this.movingRightForwardOffset;
    }

    public float renderRotationOffset() {
        return this.renderRotationOffset;
    }

    public boolean isPet() {
        return this.pet;
    }

    public double stopSpeedThreshold() {
        return this.stopSpeedThreshold;
    }

    public double restartClearanceSq() {
        return this.restartClearance * this.restartClearance;
    }

    public double cleanupRadiusSq() {
        double forwardRadius = Math.max(
                Math.max(Math.abs(this.stopFrontLeftForwardOffset), Math.abs(this.stopFrontRightForwardOffset)),
                Math.max(Math.abs(this.stopBackLeftForwardOffset), Math.abs(this.stopBackRightForwardOffset))
        );
        double sideRadius = Math.max(
                Math.max(Math.abs(this.stopFrontLeftSideOffset), Math.abs(this.stopFrontRightSideOffset)),
                Math.max(Math.abs(this.stopBackLeftSideOffset), Math.abs(this.stopBackRightSideOffset))
        );
        double radius = forwardRadius + Math.max(this.movingSideOffset, sideRadius) + this.renderScale;
        return radius * radius;
    }

    public double stopFrontLeftSideOffset() {
        return this.stopFrontLeftSideOffset;
    }

    public double stopFrontRightSideOffset() {
        return this.stopFrontRightSideOffset;
    }

    public double stopBackLeftSideOffset() {
        return this.stopBackLeftSideOffset;
    }

    public double stopBackRightSideOffset() {
        return this.stopBackRightSideOffset;
    }

    public double stopFrontLeftForwardOffset() {
        return this.stopFrontLeftForwardOffset;
    }

    public double stopFrontRightForwardOffset() {
        return this.stopFrontRightForwardOffset;
    }

    public double stopBackLeftForwardOffset() {
        return this.stopBackLeftForwardOffset;
    }

    public double stopBackRightForwardOffset() {
        return this.stopBackRightForwardOffset;
    }

    public double stepDistance() {
        return this.stepDistance;
    }
}
