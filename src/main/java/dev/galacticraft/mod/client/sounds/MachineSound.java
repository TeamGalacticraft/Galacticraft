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
import dev.galacticraft.machinelib.api.machine.MachineStatus.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;


public abstract class MachineSound extends GCSound {
    protected final MachineBlockEntity machine;
    protected final SoundEvent activeSound;
    private Type statustype;

    public MachineSound(MachineBlockEntity machine, SoundEvent event, SoundCallback callback, SoundEvent activeSound) {
        super(machine, event, SoundSource.BLOCKS, callback);
        
        this.machine = machine;
        this.statustype = this.getStatus().getType();
        this.activeSound = activeSound;
    }

    @Override
    public void tick() {

        if (this.machine instanceof MachineBlockEntity blockEntity && blockEntity.isRemoved()) {
            this.end();
        }
        super.tick();
        Type newstatus;
        newstatus = this.getStatus().getType();

        if (newstatus != this.statustype) {
            GCSound newsound;
            switch (newstatus) {
                case MISSING_ENERGY:
                    this.volume = 0.0F;
                    break;
                case WORKING:
                    newsound = new ActiveMachineSound(machine, activeSound, callback);
                    callback.onSwapped(this,newsound, this.machine);
                    break;
                case PARTIALLY_WORKING:
                    newsound = new ActiveMachineSound(machine, activeSound, callback);
                    callback.onSwapped(this,newsound, this.machine);
                    break;
                default:
                    newsound = new IdleMachineSound(machine, activeSound, callback);
                    callback.onSwapped(this,newsound, this.machine);
                    break;
            }
            this.statustype = newstatus;
        } else {
            this.volume = 1.0F;
        }
    }

    public MachineStatus getStatus() {
        return this.machine.getState().getStatus();
    }


}
