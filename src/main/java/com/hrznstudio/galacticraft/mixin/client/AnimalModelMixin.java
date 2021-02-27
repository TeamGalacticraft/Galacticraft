/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.accessor.AnimalModelGearAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Mixin(AnimalModel.class)
@Environment(EnvType.CLIENT)
public abstract class AnimalModelMixin<E extends Entity> extends EntityModel<E> implements AnimalModelGearAccessor {
    @Shadow protected abstract Iterable<ModelPart> getHeadParts();

    @Shadow @Final private float childHeadYOffset;
    @Shadow @Final private float childHeadZOffset;
    @Shadow @Final private float invertedChildHeadScale;
    @Shadow @Final private boolean headScaled;

    @Override
    public float getChildHeadYOffset() {
        return childHeadYOffset;
    }

    @Override
    public float getChildHeadZOffset() {
        return childHeadZOffset;
    }

    @Override
    public float getInvertedChildHeadScale() {
        return invertedChildHeadScale;
    }

    @Override
    public boolean isHeadScaled() {
        return headScaled;
    }

    @Override
    public Iterable<ModelPart> getAnimalHeadParts() {
        return this.getHeadParts();
    }
}
