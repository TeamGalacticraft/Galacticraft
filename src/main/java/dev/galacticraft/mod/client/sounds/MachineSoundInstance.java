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
import dev.galacticraft.mod.content.GCSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class MachineSoundInstance extends AbstractTickableSoundInstance {
    private final MachineBlockEntity machine;
    private long energyConsumptionRate;

    public MachineSoundInstance(MachineBlockEntity machine, long energyConsumption) {
        super(GCSounds.MACHINE_BUZZ, SoundSource.BLOCKS,SoundInstance.createUnseededRandom());
        this.machine = machine;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.00001F;
        this.pitch = 1.0F;
        this.delay = 0;
        this.energyConsumptionRate = energyConsumption;
        this.x = machine.getBlockPos().getX();
        this.y = machine.getBlockPos().getY();
        this.z = machine.getBlockPos().getZ();
        System.out.println("turning on!");
    }

    public void tick() {
        //System.out.println("Detecting Sounds!");
        if (!this.machine.isRemoved()) {
            if (this.machine.energyStorage().canExtract(energyConsumptionRate)) {
                this.volume = 1.0F;
                //System.out.println("playing sounds!");
                //System.out.println(this.volume);
            } else {
                this.volume = 0.00001F;
            }
        } else {
            System.out.println("stopping");
            stop();
        }

    }
}
