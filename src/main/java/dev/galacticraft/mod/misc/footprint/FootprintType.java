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
    HUMAN(Constant.id("textures/misc/footprint.png"), 0.5F, 0.375F, 0.25D, 0.0D, 0.0F, false, false, 0.35D),
    DOG(Constant.id("textures/misc/pawprint.png"), 0.09F, 0.07F, 0.2D, 0.18D, Mth.PI, true, true, 0.25D),
    CAT(Constant.id("textures/misc/pawprint.png"), 0.0675F, 0.0525F, 0.16D, 0.14D, Mth.PI, true, true, 0.2D);

    public static final StreamCodec<ByteBuf, FootprintType> STREAM_CODEC = StreamCodecs.ofEnum(values());

    private final ResourceLocation texture;
    private final float renderScale;
    private final float placementScale;
    private final double sideOffset;
    private final double pairOffset;
    private final float renderRotationOffset;
    private final boolean paired;
    private final boolean linearStepDistance;
    private final double stepDistance;

    FootprintType(ResourceLocation texture, float renderScale, float placementScale, double sideOffset, double pairOffset, float renderRotationOffset, boolean paired, boolean linearStepDistance, double stepDistance) {
        this.texture = texture;
        this.renderScale = renderScale;
        this.placementScale = placementScale;
        this.sideOffset = sideOffset;
        this.pairOffset = pairOffset;
        this.renderRotationOffset = renderRotationOffset;
        this.paired = paired;
        this.linearStepDistance = linearStepDistance;
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

    public double sideOffset() {
        return this.sideOffset;
    }

    public double pairOffset() {
        return this.pairOffset;
    }

    public float renderRotationOffset() {
        return this.renderRotationOffset;
    }

    public boolean paired() {
        return this.paired;
    }

    public boolean linearStepDistance() {
        return this.linearStepDistance;
    }

    public double stepDistance() {
        return this.stepDistance;
    }
}
