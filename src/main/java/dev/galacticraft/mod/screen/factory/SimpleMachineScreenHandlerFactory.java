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

package dev.galacticraft.mod.screen.factory;

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.screen.SimpleMachineScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SimpleMachineScreenHandlerFactory<B extends MachineBlockEntity, T extends MachineScreenHandler<B>> extends MachineScreenHandlerFactory<B, T> {
    protected SimpleMachineScreenHandlerFactory(Supplier<ScreenHandlerType<T>> type, int invX, int invY) {
        super((syncId, player, machine) -> (T) SimpleMachineScreenHandler.create(syncId, player, machine, () -> (ScreenHandlerType<MachineScreenHandler<B>>) type.get(), invX, invY));
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type, int invX, int invY) {
        return new SimpleMachineScreenHandlerFactory<>(type, invX, invY);
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type, int invY) {
        return create(type, 8, invY);
    }

    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> SimpleMachineScreenHandlerFactory<B, T> create(Supplier<ScreenHandlerType<T>> type) {
        return create(type, 84);
    }
}
