package com.hrznstudio.galacticraft.api.wire;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Wire {
    /**
     * Sets the {@link WireNetwork} associated with this wie
     * @param network The network to associate with
     */
    void setNetwork(WireNetwork network);

    /**
     * Returns the associated {@link WireNetwork}
     * @return The associated {@link WireNetwork}
     */
    @NotNull WireNetwork getNetwork();

    /**
     * Returns whether or not this wire is able to connect to another block on the specified face/direction
     * @param direction the direction offset to the block to check adjacency to
     * @return Whether or not this wire is able to connect to another block on the specified face/direction
     */
    @NotNull WireConnectionType getConnection(Direction direction, @Nullable BlockEntity entity);

    default boolean canConnect(Direction direction) {
        return true;
    }
}
