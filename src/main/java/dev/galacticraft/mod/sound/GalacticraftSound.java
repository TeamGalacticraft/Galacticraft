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

package dev.galacticraft.mod.sound;

import dev.galacticraft.mod.Constant;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftSound {

    public static final SoundEvent MUSIC_MOON = new SoundEvent(new Identifier(Constant.MOD_ID, "music.music_moon"));
    public static final SoundEvent MUSIC_CREDITS = new SoundEvent(new Identifier(Constant.MOD_ID, "music.music_credits"));
    public static final SoundEvent MUSIC_ORBIT = new SoundEvent(new Identifier(Constant.MOD_ID, "music.music_orbit"));
    public static final SoundEvent MUSIC_MARS = new SoundEvent(new Identifier(Constant.MOD_ID, "music.music_mars"));
    public static final SoundEvent PLAYER_UNLOCKCHEST = new SoundEvent(new Identifier(Constant.MOD_ID, "player.unlockchest"));
    public static final SoundEvent PLAYER_PARACHUTE = new SoundEvent(new Identifier(Constant.MOD_ID, "player.parachute"));
    public static final SoundEvent PLAYER_OPENAIRLOCK = new SoundEvent(new Identifier(Constant.MOD_ID, "player.openairlock"));
    public static final SoundEvent PLAYER_CLOSEAIRLOCK = new SoundEvent(new Identifier(Constant.MOD_ID, "player.closeairlock"));
    public static final SoundEvent ENTITY_SLIME_DEATH = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.slime_death"));
    public static final SoundEvent ENTITY_OOH = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.ooh"));
    public static final SoundEvent ENTITY_OUCH = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.ouch"));
    public static final SoundEvent ENTITY_BOSSLAUGH = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.bosslaugh"));
    public static final SoundEvent ENTITY_BOSSDEATH = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.bossdeath"));
    public static final SoundEvent ENTITY_ASTROMINER = new SoundEvent(new Identifier(Constant.MOD_ID, "entity.astrominer"));
    public static final SoundEvent AMBIENCE_SINGLEDRIP = new SoundEvent(new Identifier(Constant.MOD_ID, "ambience.singledrip"));
    public static final SoundEvent AMBIENCE_SCARYSCAPE = new SoundEvent(new Identifier(Constant.MOD_ID, "ambience.scaryscape"));
    public static final SoundEvent SHUTTLE_SHUTTLE = new SoundEvent(new Identifier(Constant.MOD_ID, "shuttle.shuttle"));

    // Legacy Discs
    public static final SoundEvent MUSIC_LEGACY_MARS = new SoundEvent(new Identifier(Constant.MOD_ID, "music.legacy.mars"));
    public static final SoundEvent MUSIC_LEGACY_MIMAS = new SoundEvent(new Identifier(Constant.MOD_ID, "music.legacy.mimas"));
    public static final SoundEvent MUSIC_LEGACY_ORBIT = new SoundEvent(new Identifier(Constant.MOD_ID, "music.legacy.orbit"));
    public static final SoundEvent MUSIC_LEGACY_SPACERACE = new SoundEvent(new Identifier(Constant.MOD_ID, "music.legacy.spacerace"));

    public static void register() {
        registerSound(MUSIC_MOON);
        registerSound(MUSIC_CREDITS);
        registerSound(MUSIC_ORBIT);
        registerSound(MUSIC_MARS);
        registerSound(PLAYER_UNLOCKCHEST);
        registerSound(PLAYER_PARACHUTE);
        registerSound(PLAYER_OPENAIRLOCK);
        registerSound(PLAYER_CLOSEAIRLOCK);
        registerSound(ENTITY_SLIME_DEATH);
        registerSound(ENTITY_OOH);
        registerSound(ENTITY_OUCH);
        registerSound(ENTITY_BOSSLAUGH);
        registerSound(ENTITY_BOSSDEATH);
        registerSound(ENTITY_ASTROMINER);
        registerSound(AMBIENCE_SINGLEDRIP);
        registerSound(AMBIENCE_SCARYSCAPE);
        registerSound(SHUTTLE_SHUTTLE);

        // Legacy Discs
        registerSound(MUSIC_LEGACY_MARS);
        registerSound(MUSIC_LEGACY_MIMAS);
        registerSound(MUSIC_LEGACY_ORBIT);
        registerSound(MUSIC_LEGACY_SPACERACE);
    }

    private static void registerSound(SoundEvent soundEvent) {
        Registry.register(Registry.SOUND_EVENT, soundEvent.id, soundEvent);
    }
}
