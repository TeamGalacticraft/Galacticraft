package dev.galacticraft.mod.api.wire;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record NetworkId(UUID uuid, long throughput) {
    @Override
    public @NotNull String toString() {
        return this.uuid.toString() + " (" + this.throughput + ")";
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        NetworkId networkId = (NetworkId) o;
//        return this.uuid == networkId.uuid;
//    }
//
//    @Override
//    public int hashCode() {
//        return this.uuid.hashCode();
//    }
}
