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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class GCSounds {
    private static final GCRegistry<SoundEvent> SOUNDS = new GCRegistry<>(BuiltInRegistries.SOUND_EVENT);
    // Ambient
    public static final SoundEvent AMBIENT_SCARYSCAPE = register("ambient.scaryscape");
    public static final SoundEvent AMBIENT_SINGLE_DRIP = register("ambient.single_drip");
    // Entity
    public static final SoundEvent ASTROMINER = register("entity.astrominer");
    public static final SoundEvent SKELETON_BOSS_DEATH = register("entity.skeleton_boss.death");
    public static final SoundEvent SKELETON_BOSS_LAUGH = register("entity.skeleton_boss.laugh");
    public static final SoundEvent SKELETON_BOSS_OOH = register("entity.skeleton_boss.ooh");
    public static final SoundEvent SKELETON_BOSS_OUCH = register("entity.skeleton_boss.ouch");
    public static final SoundEvent METEOR_THROW = register("entity.meteor_chunk.throw");
    public static final SoundEvent ROCKET_IGNITE = register("entity.rocket.ignite");
    public static final SoundEvent SLIMELING_DEATH = register("entity.slimeling.death");
    // Machine Sounds
    public static final SoundEvent AIRLOCK_OPEN = register("machine.airlock.open");
    public static final SoundEvent AIRLOCK_CLOSE = register("machine.airlock.close");
    public static final SoundEvent CIRCUIT_SCRITCH = register("machine.circuit.scritch");
    public static final SoundEvent MACHINE_BUZZ = register("machine.buzz");
    public static final SoundEvent MACHINE_WHIR = register("machine.whir");
    public static final SoundEvent OXYGEN_FAN = register("machine.oxygen.fan");
    // Music
    public static final Holder.Reference<SoundEvent> MUSIC_CREDITS = registerForHolder("music.credits");
    public static final Holder.Reference<SoundEvent> MUSIC_MARS = registerForHolder("music.mars");
    public static final Holder.Reference<SoundEvent> MUSIC_MOON = registerForHolder("music.moon");
    public static final Holder.Reference<SoundEvent> MUSIC_ORBIT = registerForHolder("music.orbit");
    // Legacy Discs
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MARS = registerForHolder("music_disc.legacy.mars");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MIMAS = registerForHolder("music_disc.legacy.mimas");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_ORBIT = registerForHolder("music_disc.legacy.orbit");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_SPACERACE = registerForHolder("music_disc.legacy.spacerace");
    // Player
    public static final SoundEvent CHEST_UNLOCK = register("player.chest.unlock");
    public static final SoundEvent PARACHUTE = register("player.parachute");

    private static Holder.Reference<SoundEvent> registerForHolder(String id) {
        return SOUNDS.registerForHolder(id, SoundEvent.createVariableRangeEvent(Constant.id(id)));
    }

    private static SoundEvent register(String id) {
        return register(Constant.id(id));
    }

    private static SoundEvent register(ResourceLocation id) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {}
}
