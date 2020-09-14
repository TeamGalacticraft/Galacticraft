package com.hrznstudio.galacticraft.api.block;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfiguredSideOption {
    @NotNull
    private SideOption option;
    private int value;
    private int size;

    public ConfiguredSideOption(@NotNull SideOption option, int size) {
        this(option, -1, size);
    }

    public ConfiguredSideOption(@NotNull SideOption option, int value, int size) {
        this.option = option;
        this.value = Math.max(value, -1);
        this.size = Math.max(size, 0);
    }

    public void setOption(@NotNull SideOption option, int size) {
        this.option = option;
        this.value = -1;
        this.size = Math.max(size, 0);
    }

    public @NotNull SideOption getOption() {
        return option;
    }

    public int getValue() {
        return value;
    }

    public boolean isWildcard() {
        return getValue() == -1;
    }

    public int increment() {
        if (++this.value == this.size) this.value = 0;
        return this.value;
    }
    public int decrement() {
        if (this.value-- == 0) this.value = this.size - 1;
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguredSideOption that = (ConfiguredSideOption) o;
        return getValue() == that.getValue() &&
                getOption() == that.getOption();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOption(), getValue());
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("option", option.name());
        tag.putInt("value", value);
        tag.putInt("size", size);
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        this.option = SideOption.valueOf(tag.getString("option"));
        this.value = tag.getInt("value");
        this.size = tag.getInt("size");
    }

}
