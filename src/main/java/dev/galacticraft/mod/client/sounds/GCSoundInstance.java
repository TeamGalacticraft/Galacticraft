package dev.galacticraft.mod.client.sounds;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.content.GCSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class GCSoundInstance extends AbstractTickableSoundInstance {
    protected final BlockEntity entity;
    protected final SoundInstanceCallback callback;

    public GCSoundInstance(BlockEntity entity, SoundEvent event, SoundSource source, SoundInstanceCallback callback) {
        super(event, source, SoundInstance.createUnseededRandom());

        // references to other objects
        this.entity = entity;
        this.callback = callback;

        // important behavior
        this.looping = true;
        this.delay = 0;

        // starting values
        this.volume = 0.00001F; // if it is 0 nothing will play
        this.pitch = 1.0F;
        this.setPosition();

    }


    @Override
    public void tick() {
        if (this.entity == null) {
            this.callback.onFinished(this);
        }

    }

    protected void setPosition() {
        this.x = entity.getBlockPos().getX();
        this.y = entity.getBlockPos().getY();
        this.z = entity.getBlockPos().getZ();
    }

    public void end() {
        this.callback.onFinished(this);
    }
}
