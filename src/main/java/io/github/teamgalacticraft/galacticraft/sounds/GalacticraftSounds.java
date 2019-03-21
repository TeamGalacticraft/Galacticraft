package io.github.teamgalacticraft.galacticraft.sounds;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftSounds {

    public static final SoundEvent MUSIC_MUSIC_SPACE = new SoundEvent(new Identifier(Constants.MOD_ID, "music.music_space"));
    public static final SoundEvent PLAYER_UNLOCKCHEST = new SoundEvent(new Identifier(Constants.MOD_ID, "player.unlockchest"));
    public static final SoundEvent PLAYER_PARACHUTE = new SoundEvent(new Identifier(Constants.MOD_ID, "player.parachute"));
    public static final SoundEvent PLAYER_OPENAIRLOCK = new SoundEvent(new Identifier(Constants.MOD_ID, "player.openairlock"));
    public static final SoundEvent PLAYER_CLOSEAIRLOCK = new SoundEvent(new Identifier(Constants.MOD_ID, "player.closeairlock"));
    public static final SoundEvent ENTITY_SLIME_DEATH = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.slime_death"));
    public static final SoundEvent ENTITY_OOH = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.ooh"));
    public static final SoundEvent ENTITY_OUCH = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.ouch"));
    public static final SoundEvent ENTITY_BOSSLAUGH = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.bosslaugh"));
    public static final SoundEvent ENTITY_BOSSDEATH = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.bossdeath"));
    public static final SoundEvent ENTITY_ASTROMINER = new SoundEvent(new Identifier(Constants.MOD_ID, "entity.astrominer"));
    public static final SoundEvent AMBIENCE_SINGLEDRIP = new SoundEvent(new Identifier(Constants.MOD_ID, "ambience.singledrip"));
    public static final SoundEvent AMBIENCE_SCARYSCAPE = new SoundEvent(new Identifier(Constants.MOD_ID, "ambience.scaryscape"));
    public static final SoundEvent SHUTTLE_SHUTTLE = new SoundEvent(new Identifier(Constants.MOD_ID, "shuttle.shuttle"));

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, MUSIC_MUSIC_SPACE);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, PLAYER_UNLOCKCHEST);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, PLAYER_PARACHUTE);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, PLAYER_OPENAIRLOCK);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, PLAYER_CLOSEAIRLOCK);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_SLIME_DEATH);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_OOH);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_OUCH);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_BOSSLAUGH);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_BOSSDEATH);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, ENTITY_ASTROMINER);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, AMBIENCE_SINGLEDRIP);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, AMBIENCE_SCARYSCAPE);
        Registry.register(Registry.SOUND_EVENT, Constants.MOD_ID, SHUTTLE_SHUTTLE);
    }
}
