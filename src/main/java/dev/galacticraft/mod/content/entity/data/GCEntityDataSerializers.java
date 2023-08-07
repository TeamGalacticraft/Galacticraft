/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketCone;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCEntityDataSerializers {
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

    public static final EntityDataSerializer<ResourceLocation> ROCKET_PART = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, ResourceLocation id) {
            buf.writeResourceLocation(id);
        }

        @Override
        public @NotNull ResourceLocation read(FriendlyByteBuf buf) {
            return buf.readResourceLocation();
        }

        @Contract(value = "_ -> param1", pure = true)
        @Override
        public @NotNull ResourceLocation copy(ResourceLocation id) {
            return id;
        }
    };

    public static final EntityDataAccessor<Boolean> IS_IN_CRYO_SLEEP_ID = SynchedEntityData.defineId(
            Player.class, EntityDataSerializers.BOOLEAN
    );
    public static final EntityDataSerializer<ResourceLocation[]> ROCKET_UPGRADES = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, ResourceLocation[] ids) {
            buf.writeVarInt(ids.length);
            for (ResourceLocation id : ids) {
                buf.writeResourceLocation(id);
            }
        }

        @Override
        public ResourceLocation @NotNull [] read(FriendlyByteBuf buf) {
            int s = buf.readVarInt();
            ResourceLocation[] ids = new ResourceLocation[s];
            for (int i = 0; i < s; i++) {
                ids[i] = buf.readResourceLocation();
            }
            return ids;
        }

        @Contract(value = "_ -> param1", pure = true)
        @Override
        public @NotNull ResourceLocation[] copy(ResourceLocation[] id) {
            ResourceLocation[] temp = new ResourceLocation[id.length];
            System.arraycopy(id, 0, temp, 0, id.length);
            return temp;
        }
    };

    public static void register() {
        EntityDataSerializers.registerSerializer(LAUNCH_STAGE);
        EntityDataSerializers.registerSerializer(ROCKET_PART);
        EntityDataSerializers.registerSerializer(ROCKET_UPGRADES);
    }
}
