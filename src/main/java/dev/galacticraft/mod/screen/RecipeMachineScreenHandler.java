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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.block.entity.RecipeMachineBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class RecipeMachineScreenHandler<T extends RecipeMachineBlockEntity<?, ?>> extends MachineScreenHandler<T> {
    private final Supplier<ScreenHandlerType<? extends MachineScreenHandler<T>>> type;

    public final Property progress = new Property() {
        @Override
        public int get() {
            return RecipeMachineScreenHandler.this.machine.progress();
        }

        @Override
        public void set(int value) {
            RecipeMachineScreenHandler.this.machine.progress(value);
        }
    };

    public final Property maxProgress = new Property() {
        @Override
        public int get() {
            return RecipeMachineScreenHandler.this.machine.maxProgress();
        }

        @Override
        public void set(int value) {
            RecipeMachineScreenHandler.this.machine.maxProgress(value);
        }
    };

    protected RecipeMachineScreenHandler(int syncId, PlayerEntity player, T machine, Supplier<ScreenHandlerType<? extends MachineScreenHandler<T>>> type, int invX, int invY) {
        super(syncId, player, machine, null);
        this.type = type;
        this.addProperty(this.progress);
        this.addProperty(this.maxProgress);
        this.addPlayerInventorySlots(invX, invY);
    }

    public static <T extends RecipeMachineBlockEntity<?, ?>> RecipeMachineScreenHandler<T> create(int syncId, PlayerEntity playerEntity, T machine, Supplier<ScreenHandlerType<? extends MachineScreenHandler<T>>> handlerType, int invX, int invY) {
        return new RecipeMachineScreenHandler<>(syncId, playerEntity, machine, handlerType, invX, invY);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return this.type.get();
    }
}
