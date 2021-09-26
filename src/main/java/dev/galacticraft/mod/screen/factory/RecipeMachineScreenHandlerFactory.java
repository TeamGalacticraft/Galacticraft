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

import dev.galacticraft.mod.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.mod.screen.RecipeMachineScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class RecipeMachineScreenHandlerFactory<C extends Inventory, R extends Recipe<C>, T extends RecipeMachineBlockEntity<C, R>, S extends RecipeMachineScreenHandler<C, R, T>> extends MachineScreenHandlerFactory<T, S> {
    protected RecipeMachineScreenHandlerFactory(Supplier<ScreenHandlerType<S>> type, int invX, int invY) {
        super((syncId, player, machine) -> (S) RecipeMachineScreenHandler.create(syncId, player, machine, type::get, invX, invY));
    }

    public static <C extends Inventory, R extends Recipe<C>, B extends RecipeMachineBlockEntity<C, R>, T extends RecipeMachineScreenHandler<C, R, B>> RecipeMachineScreenHandlerFactory<C, R, B, T> create(Supplier<ScreenHandlerType<T>> type, int invX, int invY) {
        return new RecipeMachineScreenHandlerFactory<>(type, invX, invY);
    }

    public static <C extends Inventory, R extends Recipe<C>, B extends RecipeMachineBlockEntity<C, R>, T extends RecipeMachineScreenHandler<C, R, B>> RecipeMachineScreenHandlerFactory<C, R, B, T> create(Supplier<ScreenHandlerType<T>> type, int invY) {
        return create(type, 8, invY);
    }

    public static <C extends Inventory, R extends Recipe<C>, B extends RecipeMachineBlockEntity<C, R>, T extends RecipeMachineScreenHandler<C, R, B>> RecipeMachineScreenHandlerFactory<C, R, B, T> create(Supplier<ScreenHandlerType<T>> type) {
        return create(type, 84);
    }
}
