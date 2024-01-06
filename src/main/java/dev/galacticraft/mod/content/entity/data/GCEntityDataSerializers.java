/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.entity.data;

import dev.galacticraft.api.rocket.LaunchStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class GCEntityDataSerializers {
    public static final EntityDataSerializer<LaunchStage> LAUNCH_STAGE = EntityDataSerializer.simpleEnum(LaunchStage.class);
    public static final EntityDataSerializer<ResourceLocation> IDENTIFIER = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buffer, ResourceLocation value) {
            if (value == null) {
                buffer.writeBoolean(false);
            } else {
                buffer.writeBoolean(true);
                buffer.writeResourceLocation(value);
            }
        }

        @Override
        public ResourceLocation read(FriendlyByteBuf buffer) {
            if (buffer.readBoolean()) {
                return buffer.readResourceLocation();
            } else {
                return null;
            }
        }

        @Override
        public ResourceLocation copy(ResourceLocation value) {
            return value;
        }
    };

    public static final EntityDataAccessor<Boolean> IS_IN_CRYO_SLEEP_ID = SynchedEntityData.defineId(
            Player.class, EntityDataSerializers.BOOLEAN
    );

    public static void register() {
        EntityDataSerializers.registerSerializer(LAUNCH_STAGE);
        EntityDataSerializers.registerSerializer(IDENTIFIER);
    }
}
