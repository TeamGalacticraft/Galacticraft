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
import net.minecraft.sounds.SoundEvent;

public class GCSounds {
    private static final GCRegistry<SoundEvent> SOUNDS = new GCRegistry<>(BuiltInRegistries.SOUND_EVENT);
    public static final Holder.Reference<SoundEvent> MUSIC_MOON = register("music.moon");
    public static final Holder.Reference<SoundEvent> MUSIC_CREDITS = register("music.credits");
    public static final Holder.Reference<SoundEvent> MUSIC_ORBIT = register("music.orbit");
    public static final Holder.Reference<SoundEvent> MUSIC_MARS = register("music.mars");
    public static final SoundEvent UNLOCK_CHEST = SoundEvent.createVariableRangeEvent(Constant.id("player.unlock_chest"));
    public static final SoundEvent PARACHUTE = SoundEvent.createVariableRangeEvent(Constant.id("player.parachute"));
    public static final SoundEvent SLIME_DEATH = SoundEvent.createVariableRangeEvent(Constant.id("entity.slime_death"));
    public static final SoundEvent OOH = SoundEvent.createVariableRangeEvent(Constant.id("entity.boss.ooh"));
    public static final SoundEvent OUCH = SoundEvent.createVariableRangeEvent(Constant.id("entity.boss.ouch"));
    public static final SoundEvent BOSS_LAUGH = SoundEvent.createVariableRangeEvent(Constant.id("entity.boss.laugh"));
    public static final SoundEvent BOSS_DEATH = SoundEvent.createVariableRangeEvent(Constant.id("entity.boss.death"));
    public static final SoundEvent ASTROMINER = SoundEvent.createVariableRangeEvent(Constant.id("entity.astrominer"));
    public static final SoundEvent SINGLE_DRIP = SoundEvent.createVariableRangeEvent(Constant.id("ambience.single_drip"));
    public static final SoundEvent SCARYSCAPE = SoundEvent.createVariableRangeEvent(Constant.id("ambience.scaryscape"));
    public static final SoundEvent SHUTTLE = SoundEvent.createVariableRangeEvent(Constant.id("shuttle.shuttle"));
    public static final SoundEvent METEOR_CHUNK_THROW = SoundEvent.createVariableRangeEvent(Constant.id("entity.throwable_meteor_chunk.throw"));

    // Machine Sounds
    public static final SoundEvent OPEN_AIRLOCK = SoundEvent.createVariableRangeEvent(Constant.id("machine.airlock.open"));
    public static final SoundEvent CLOSE_AIRLOCK = SoundEvent.createVariableRangeEvent(Constant.id("machine.airlock.close"));
    public static final SoundEvent CIRCUIT_SCRITCH = SoundEvent.createVariableRangeEvent(Constant.id("machine.circuit.scritch"));
    public static final SoundEvent MACHINE_BUZZ = SoundEvent.createVariableRangeEvent(Constant.id("machine.buzz"));
    public static final SoundEvent MACHINE_WHIR = SoundEvent.createVariableRangeEvent(Constant.id("machine.whir"));
    public static final SoundEvent OXYGEN_FAN = SoundEvent.createVariableRangeEvent(Constant.id("machine.oxygen.fan"));

    // Legacy Discs
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MARS = register("music_disc.legacy.mars");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_MIMAS = register("music_disc.legacy.mimas");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_ORBIT = register("music_disc.legacy.orbit");
    public static final Holder.Reference<SoundEvent> MUSIC_LEGACY_SPACERACE = register("music_disc.legacy.spacerace");

    public static Holder.Reference<SoundEvent> register(String id) {
        return SOUNDS.registerForHolder(id, SoundEvent.createVariableRangeEvent(Constant.id(id)));
    }

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, UNLOCK_CHEST.getLocation(), UNLOCK_CHEST);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PARACHUTE.getLocation(), PARACHUTE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SLIME_DEATH.getLocation(), SLIME_DEATH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OOH.getLocation(), OOH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OUCH.getLocation(), OUCH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BOSS_LAUGH.getLocation(), BOSS_LAUGH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, BOSS_DEATH.getLocation(), BOSS_DEATH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ASTROMINER.getLocation(), ASTROMINER);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SINGLE_DRIP.getLocation(), SINGLE_DRIP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SCARYSCAPE.getLocation(), SCARYSCAPE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, SHUTTLE.getLocation(), SHUTTLE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, METEOR_CHUNK_THROW.getLocation(), METEOR_CHUNK_THROW);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OPEN_AIRLOCK.getLocation(), OPEN_AIRLOCK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CLOSE_AIRLOCK.getLocation(), CLOSE_AIRLOCK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CIRCUIT_SCRITCH.getLocation(), CIRCUIT_SCRITCH);
        Registry.register(BuiltInRegistries.SOUND_EVENT, MACHINE_BUZZ.getLocation(), MACHINE_BUZZ);
        Registry.register(BuiltInRegistries.SOUND_EVENT, MACHINE_WHIR.getLocation(), MACHINE_WHIR);
        Registry.register(BuiltInRegistries.SOUND_EVENT, OXYGEN_FAN.getLocation(), OXYGEN_FAN);
    }
}
