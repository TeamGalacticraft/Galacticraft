package com.hrznstudio.galacticraft.api.machine;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

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
         * THe machine is active, but at reduced efficiency.
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
         *
         */
        OTHER(false);

        final boolean active;

        StatusType(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }
    }
}
