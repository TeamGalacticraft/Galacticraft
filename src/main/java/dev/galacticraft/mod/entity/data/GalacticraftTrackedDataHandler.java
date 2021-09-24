/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class GalacticraftTrackedDataHandler {
    public static final TrackedDataHandler<LaunchStage> LAUNCH_STAGE = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, LaunchStage stage) {
            buf.writeEnumConstant(stage);
        }

        @Override
        public LaunchStage read(PacketByteBuf buf) {
            return buf.readEnumConstant(LaunchStage.class);
        }

        @Override
        public LaunchStage copy(LaunchStage stage) {
            return stage;
        }
    };

    public static final TrackedDataHandler<Double> DOUBLE = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, Double value) {
            buf.writeDouble(value);
        }

        @Override
        public Double read(PacketByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public Double copy(Double value) {
            return value;
        }
    };

    public static final TrackedDataHandler<Identifier[]> ROCKET_PART_IDS = new TrackedDataHandler<>() {
        @Override
        public void write(PacketByteBuf buf, Identifier[] parts) {
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                buf.writeBoolean(parts[i] != null);
                if (parts[i] != null) {
                    buf.writeIdentifier(parts[i]);
                }
            }
        }

        @Override
        public Identifier[] read(PacketByteBuf buf) {
            Identifier[] array = new Identifier[RocketPartType.values().length];
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                if (buf.readBoolean()) {
                    array[i] = buf.readIdentifier();
                }
            }
            return array;
        }

        @Override
        public Identifier[] copy(Identifier[] buf) {
            Identifier[] parts = new Identifier[RocketPartType.values().length];
            System.arraycopy(buf, 0, parts, 0, buf.length);
            return parts;
        }
    };

    public static void register() {
        TrackedDataHandlerRegistry.register(LAUNCH_STAGE);
        TrackedDataHandlerRegistry.register(DOUBLE);
        TrackedDataHandlerRegistry.register(ROCKET_PART_IDS);
    }
}
