package dev.galacticraft.mod.client.sounds;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.entity.RocketEntity;
import dev.galacticraft.mod.sound.GCSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;

public class RocketSound extends AbstractTickableSoundInstance {
    private final RocketEntity rocket;
    private boolean ignition = false;

    public RocketSound(RocketEntity rocket) {
        super(GCSounds.SHUTTLE_SHUTTLE, SoundSource.NEUTRAL, rocket.getLevel().getRandom());
        this.rocket = rocket;
        this.x = rocket.getX();
        this.y = rocket.getY();
        this.z = rocket.getZ();
    }

    @Override
    public void tick() {
        if (this.rocket.getStage() == LaunchStage.IGNITED) {
            if (!ignition) {
                this.pitch = 0.0F;
                ignition = true;
            }
            if (this.rocket.getTimeBeforeLaunch() < 400)
            {
                if (this.pitch < 1.0F)
                {
                    this.pitch += 0.0025F;
                }

                if (this.pitch > 1.0F)
                {
                    this.pitch = 1.0F;
                }
            }
        } else
        {
            this.pitch = 1.0F;
        }

        if (this.rocket.getStage().ordinal() >= LaunchStage.IGNITED.ordinal())
        {
            if (this.rocket.getY() > 1000)
            {
                this.volume = 0F;
            } else if (this.rocket.getY() > 200)
            {
                this.volume = 1.0F - (float) ((this.rocket.getY() - 200) / (1000.0 - 200));
            } else
            {
                this.volume = 1.0F;
            }
        }

        this.x = rocket.getX();
        this.y = rocket.getY();
        this.z = rocket.getZ();
    }
}
