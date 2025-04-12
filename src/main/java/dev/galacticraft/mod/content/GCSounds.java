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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class GCSounds {
    private static final GCRegistry<SoundEvent> SOUNDS = new GCRegistry<>(BuiltInRegistries.SOUND_EVENT);
    public static final Holder.Reference<SoundEvent> MUSIC_MOON = register("music.moon");
    public static final Holder.Reference<SoundEvent> MUSIC_CREDITS = register("music.credits");
    public static final Holder.Reference<SoundEvent> MUSIC_ORBIT = register("music.orbit");
    public static final Holder.Reference<SoundEvent> MUSIC_MARS = register("music.mars");
    public static final SoundEvent PLAYER_UNLOCKCHEST = SoundEvent.createVariableRangeEvent(Constant.id("player.unlockchest"));
    public static final SoundEvent PLAYER_PARACHUTE = SoundEvent.createVariableRangeEvent(Constant.id("player.parachute"));
    public static final SoundEvent PLAYER_OPENAIRLOCK = SoundEvent.createVariableRangeEvent(Constant.id("player.openairlock"));
    public static final SoundEvent PLAYER_CLOSEAIRLOCK = SoundEvent.createVariableRangeEvent(Constant.id("player.closeairlock"));
    public static final SoundEvent ENTITY_SLIME_DEATH = SoundEvent.createVariableRangeEvent(Constant.id("entity.slime_death"));
    public static final SoundEvent ENTITY_OOH = SoundEvent.createVariableRangeEvent(Constant.id("entity.ooh"));
    public static final SoundEvent ENTITY_OUCH = SoundEvent.createVariableRangeEvent(Constant.id("entity.ouch"));
    public static final SoundEvent ENTITY_BOSSLAUGH = SoundEvent.createVariableRangeEvent(Constant.id("entity.bosslaugh"));
    public static final SoundEvent ENTITY_BOSSDEATH = SoundEvent.createVariableRangeEvent(Constant.id("entity.bossdeath"));
    public static final SoundEvent ENTITY_ASTROMINER = SoundEvent.createVariableRangeEvent(Constant.id("entity.astrominer"));
    public static final SoundEvent AMBIENCE_SINGLEDRIP = SoundEvent.createVariableRangeEvent(Constant.id("ambience.singledrip"));
    public static final SoundEvent AMBIENCE_SCARYSCAPE = SoundEvent.createVariableRangeEvent(Constant.id("ambience.scaryscape"));
    public static final SoundEvent SHUTTLE_SHUTTLE = SoundEvent.createVariableRangeEvent(Constant.id("shuttle.shuttle"));
    public static final SoundEvent METEOR_CHUNK_THROW = SoundEvent.createVariableRangeEvent(Constant.id("entity.throwable_meteor_chunk.throw"));

    // Legacy Discs
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MARS = register("music_disc.legacy.mars");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MIMAS = register("music_disc.legacy.mimas");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_ORBIT = register("music_disc.legacy.orbit");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_SPACERACE = register("music_disc.legacy.spacerace");

    public static Holder.Reference<SoundEvent> register(String id) {
        return SOUNDS.registerForHolder(id, SoundEvent.createVariableRangeEvent(Constant.id(id)));
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_UNLOCKCHEST.getLocation(), PLAYER_UNLOCKCHEST);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_PARACHUTE.getLocation(), PLAYER_PARACHUTE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_OPENAIRLOCK.getLocation(), PLAYER_OPENAIRLOCK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PLAYER_CLOSEAIRLOCK.getLocation(), PLAYER_CLOSEAIRLOCK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_SLIME_DEATH.getLocation(), ENTITY_SLIME_DEATH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_OOH.getLocation(), ENTITY_OOH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_OUCH.getLocation(), ENTITY_OUCH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_BOSSLAUGH.getLocation(), ENTITY_BOSSLAUGH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_BOSSDEATH.getLocation(), ENTITY_BOSSDEATH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ENTITY_ASTROMINER.getLocation(), ENTITY_ASTROMINER);
        Registry.register(BuiltInRegistries.SOUND_EVENT, AMBIENCE_SINGLEDRIP.getLocation(), AMBIENCE_SINGLEDRIP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, AMBIENCE_SCARYSCAPE.getLocation(), AMBIENCE_SCARYSCAPE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SHUTTLE_SHUTTLE.getLocation(), SHUTTLE_SHUTTLE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, METEOR_CHUNK_THROW.getLocation(), METEOR_CHUNK_THROW);
    }
}
