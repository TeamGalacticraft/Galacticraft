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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;

public class GCJukeboxSongs {
    public static final ResourceKey<JukeboxSong> MARS = create("legacy_mars");
    public static final ResourceKey<JukeboxSong> MIMAS = create("legacy_mimas");
    public static final ResourceKey<JukeboxSong> ORBIT = create("legacy_orbit");
    public static final ResourceKey<JukeboxSong> SPACERACE = create("legacy_spacerace");

    private static ResourceKey<JukeboxSong> create(String string) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Constant.id(string));
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> resourceKey, Holder.Reference<SoundEvent> reference, int n, int n2) {
        context.register(resourceKey, new JukeboxSong(reference, Component.translatable(Util.makeDescriptionId("jukebox_song", resourceKey.location())), n, n2));
    }

    public static void bootstrapRegistries(BootstrapContext<JukeboxSong> context) {
        register(context, MARS, GCSounds.MUSIC_LEGACY_MARS, 124, 1);
        register(context, MIMAS, GCSounds.MUSIC_LEGACY_MIMAS, 130, 2);
        register(context, ORBIT, GCSounds.MUSIC_LEGACY_ORBIT, 172, 3);
        register(context, SPACERACE, GCSounds.MUSIC_LEGACY_SPACERACE, 120, 4);
    }
}