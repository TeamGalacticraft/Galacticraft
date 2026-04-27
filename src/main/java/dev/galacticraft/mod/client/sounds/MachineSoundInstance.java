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

package dev.galacticraft.mod.client.sounds;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
<<<<<<< Updated upstream
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.content.GCSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;


public class MachineSoundInstance extends AbstractTickableSoundInstance {
=======
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class MachineSoundInstance extends GCSoundInstance {
>>>>>>> Stashed changes
    private final MachineBlockEntity machine;

<<<<<<< Updated upstream
    public MachineSoundInstance(MachineBlockEntity machine) {
        super(GCSounds.MACHINE_BUZZ, SoundSource.BLOCKS,SoundInstance.createUnseededRandom());
=======
    public MachineSoundInstance(MachineBlockEntity machine, SoundEvent event, SoundInstanceCallback callback) {
        super(machine, event, SoundSource.BLOCKS, callback);
>>>>>>> Stashed changes
        this.machine = machine;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.00001F; // if it is 0 nothing will play
        this.pitch = 1.0F;
        this.delay = 0;
        this.x = machine.getBlockPos().getX();
        this.y = machine.getBlockPos().getY();
        this.z = machine.getBlockPos().getZ();
    }

    @Override
    public void tick() {
<<<<<<< Updated upstream
        if (!this.machine.isRemoved()) {
            status = this.machine.getState().getStatus();
            if (status != MachineStatuses.NOT_ENOUGH_ENERGY && status != null) {
                this.volume = 1.0F;
            } else {
                this.volume = 0.00001F;
            }
            if (this.machine.isActive()) {
                System.out.println("Active!");
            }
        } else {
            stop();
        }

    }
=======
        if (machine instanceof MachineBlockEntity blockEntity && blockEntity.isRemoved()) {
            this.end();
        }
        super.tick();
    }

    protected MachineStatus getStatus() {
        return this.machine.getState().getStatus();

    }

>>>>>>> Stashed changes
}
