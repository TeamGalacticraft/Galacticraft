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

package dev.galacticraft.mod.registries;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCSounds {
    public static final SoundEvent MUSIC_MOON = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.music_moon"));
    public static final SoundEvent MUSIC_CREDITS = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.music_credits"));
    public static final SoundEvent MUSIC_ORBIT = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.music_orbit"));
    public static final SoundEvent MUSIC_MARS = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.music_mars"));
    public static final SoundEvent PLAYER_UNLOCKCHEST = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "player.unlockchest"));
    public static final SoundEvent PLAYER_PARACHUTE = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "player.parachute"));
    public static final SoundEvent PLAYER_OPENAIRLOCK = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "player.openairlock"));
    public static final SoundEvent PLAYER_CLOSEAIRLOCK = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "player.closeairlock"));
    public static final SoundEvent ENTITY_SLIME_DEATH = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.slime_death"));
    public static final SoundEvent ENTITY_OOH = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.ooh"));
    public static final SoundEvent ENTITY_OUCH = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.ouch"));
    public static final SoundEvent ENTITY_BOSSLAUGH = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.bosslaugh"));
    public static final SoundEvent ENTITY_BOSSDEATH = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.bossdeath"));
    public static final SoundEvent ENTITY_ASTROMINER = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "entity.astrominer"));
    public static final SoundEvent AMBIENCE_SINGLEDRIP = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "ambience.singledrip"));
    public static final SoundEvent AMBIENCE_SCARYSCAPE = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "ambience.scaryscape"));
    public static final SoundEvent SHUTTLE_SHUTTLE = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "shuttle.shuttle"));

    // Legacy Discs
    public static final SoundEvent MUSIC_LEGACY_MARS = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.legacy.mars"));
    public static final SoundEvent MUSIC_LEGACY_MIMAS = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.legacy.mimas"));
    public static final SoundEvent MUSIC_LEGACY_ORBIT = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.legacy.orbit"));
    public static final SoundEvent MUSIC_LEGACY_SPACERACE = new SoundEvent(new ResourceLocation(Constant.MOD_ID, "music.legacy.spacerace"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, MUSIC_MOON.getLocation(), MUSIC_MOON);
        Registry.register(Registry.SOUND_EVENT, MUSIC_CREDITS.getLocation(), MUSIC_CREDITS);
        Registry.register(Registry.SOUND_EVENT, MUSIC_ORBIT.getLocation(), MUSIC_ORBIT);
        Registry.register(Registry.SOUND_EVENT, MUSIC_MARS.getLocation(), MUSIC_MARS);
        Registry.register(Registry.SOUND_EVENT, PLAYER_UNLOCKCHEST.getLocation(), PLAYER_UNLOCKCHEST);
        Registry.register(Registry.SOUND_EVENT, PLAYER_PARACHUTE.getLocation(), PLAYER_PARACHUTE);
        Registry.register(Registry.SOUND_EVENT, PLAYER_OPENAIRLOCK.getLocation(), PLAYER_OPENAIRLOCK);
        Registry.register(Registry.SOUND_EVENT, PLAYER_CLOSEAIRLOCK.getLocation(), PLAYER_CLOSEAIRLOCK);
        Registry.register(Registry.SOUND_EVENT, ENTITY_SLIME_DEATH.getLocation(), ENTITY_SLIME_DEATH);
        Registry.register(Registry.SOUND_EVENT, ENTITY_OOH.getLocation(), ENTITY_OOH);
        Registry.register(Registry.SOUND_EVENT, ENTITY_OUCH.getLocation(), ENTITY_OUCH);
        Registry.register(Registry.SOUND_EVENT, ENTITY_BOSSLAUGH.getLocation(), ENTITY_BOSSLAUGH);
        Registry.register(Registry.SOUND_EVENT, ENTITY_BOSSDEATH.getLocation(), ENTITY_BOSSDEATH);
        Registry.register(Registry.SOUND_EVENT, ENTITY_ASTROMINER.getLocation(), ENTITY_ASTROMINER);
        Registry.register(Registry.SOUND_EVENT, AMBIENCE_SINGLEDRIP.getLocation(), AMBIENCE_SINGLEDRIP);
        Registry.register(Registry.SOUND_EVENT, AMBIENCE_SCARYSCAPE.getLocation(), AMBIENCE_SCARYSCAPE);
        Registry.register(Registry.SOUND_EVENT, SHUTTLE_SHUTTLE.getLocation(), SHUTTLE_SHUTTLE);

        // Legacy Discs
        Registry.register(Registry.SOUND_EVENT, MUSIC_LEGACY_MARS.getLocation(), MUSIC_LEGACY_MARS);
        Registry.register(Registry.SOUND_EVENT, MUSIC_LEGACY_MIMAS.getLocation(), MUSIC_LEGACY_MIMAS);
        Registry.register(Registry.SOUND_EVENT, MUSIC_LEGACY_ORBIT.getLocation(), MUSIC_LEGACY_ORBIT);
        Registry.register(Registry.SOUND_EVENT, MUSIC_LEGACY_SPACERACE.getLocation(), MUSIC_LEGACY_SPACERACE);
    }
}
