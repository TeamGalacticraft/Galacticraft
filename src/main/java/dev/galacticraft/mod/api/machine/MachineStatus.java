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

package dev.galacticraft.mod.api.machine;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface MachineStatus {
    MachineStatus NULL = new MachineStatus() {
        @Override
        public @NotNull Text getName() {
            return LiteralText.EMPTY;
        }

        @Override
        public @NotNull StatusType getType() {
            return StatusType.OTHER;
        }

        @Override
        public int getIndex() {
            return 0;
        }
    };

    @NotNull Text getName();

    @NotNull StatusType getType();

    int getIndex();

    enum StatusType {
        /**
         * The machine is active
         */
        WORKING(true),
        /**
         * The machine is active, but at reduced efficiency.
         */
        PARTIALLY_WORKING(true),
        /**
         * The machine is missing a resource it needs to function.
         * Should not be an item, fluid or energy.
         *
         * @see #MISSING_ENERGY
         * @see #MISSING_FLUIDS
         * @see #MISSING_ITEMS
         */
        MISSING_RESOURCE(false),
        /**
         * The machine is missing a fluid it needs to function.
         * Should be preferred over {@link #MISSING_RESOURCE}
         */
        MISSING_FLUIDS(false),
        /**
         * The machine does not have the amount of energy needed to function.
         * Should be preferred over {@link #MISSING_RESOURCE}
         */
        MISSING_ENERGY(false),
        /**
         * The machine does not have the items needed to function.
         * Should be preferred over {@link #MISSING_RESOURCE}
         */
        MISSING_ITEMS(false),
        /**
         * The machine's output is blocked/full.
         */
        OUTPUT_FULL(false),
        /**
         * Everything else
         */
        OTHER(false);

        private final boolean active;

        StatusType(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }
    }
}
