/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.entity.data;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketPartType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;

public class GalacticraftTrackedDataHandler {
    public static final EntityDataSerializer<LaunchStage> LAUNCH_STAGE = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, LaunchStage stage) {
            buf.writeEnum(stage);
        }

        @Override
        public LaunchStage read(FriendlyByteBuf buf) {
            return buf.readEnum(LaunchStage.class);
        }

        @Override
        public LaunchStage copy(LaunchStage stage) {
            return stage;
        }
    };

    public static final EntityDataSerializer<Double> DOUBLE = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, Double value) {
            buf.writeDouble(value);
        }

        @Override
        public Double read(FriendlyByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public Double copy(Double value) {
            return value;
        }
    };

    public static final EntityDataSerializer<ResourceLocation[]> ROCKET_PART_IDS = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, ResourceLocation[] parts) {
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                buf.writeBoolean(parts[i] != null);
                if (parts[i] != null) {
                    buf.writeResourceLocation(parts[i]);
                }
            }
        }

        @Override
        public ResourceLocation[] read(FriendlyByteBuf buf) {
            ResourceLocation[] array = new ResourceLocation[RocketPartType.values().length];
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                if (buf.readBoolean()) {
                    array[i] = buf.readResourceLocation();
                }
            }
            return array;
        }

        @Override
        public ResourceLocation[] copy(ResourceLocation[] buf) {
            ResourceLocation[] parts = new ResourceLocation[RocketPartType.values().length];
            System.arraycopy(buf, 0, parts, 0, buf.length);
            return parts;
        }
    };

    public static void register() {
        EntityDataSerializers.registerSerializer(LAUNCH_STAGE);
        EntityDataSerializers.registerSerializer(DOUBLE);
        EntityDataSerializers.registerSerializer(ROCKET_PART_IDS);
    }
}
