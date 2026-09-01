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

package dev.galacticraft.api.registry;

import dev.galacticraft.impl.internal.registry.ExtinguishableBlockRegistryImpl;
import net.fabricmc.fabric.api.util.Block2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ExtinguishableBlockRegistry extends Block2ObjectMap<ExtinguishableBlockRegistry.Entry> {
    ExtinguishableBlockRegistry INSTANCE = new ExtinguishableBlockRegistryImpl();

    default void add(Block block, BlockState transform) {
        this.add(block, state -> transform);
    }

    default void add(Block block, Function<BlockState, BlockState> transform) {
        this.add(block, new Entry(transform));
    }

    default void add(Block block, BlockState transform, Consumer<Context> callback) {
        this.add(block, state -> transform, callback);
    }

    default void add(Block block, Function<BlockState, BlockState> transform, Consumer<Context> callback) {
        this.add(block, new Entry(transform, callback));
    }

    default void add(TagKey<Block> tag, BlockState transform) {
        this.add(tag, state -> transform);
    }

    default void add(TagKey<Block> tag, Function<BlockState, BlockState> transform) {
        this.add(tag, new Entry(transform));
    }

    default void add(TagKey<Block> tag, BlockState transform, Consumer<Context> callback) {
        this.add(tag, state -> transform, callback);
    }

    default void add(TagKey<Block> tag, Function<BlockState, BlockState> transform, Consumer<Context> callback) {
        this.add(tag, new Entry(transform, callback));
    }

    default @Nullable BlockState transform(BlockState oldState) {
        Entry entry = this.get(oldState.getBlock());
        if (entry != null) {
            BlockState newState = entry.transform(oldState);
            return oldState != newState ? newState : null;
        }
        return null;
    }

    final class Entry {
        private final Function<BlockState, BlockState> transform;
        private final Consumer<Context> callback;

        public Entry(Function<BlockState, BlockState> transform) {
            this.transform = transform;
            this.callback = context -> {};
        }

        public Entry(Function<BlockState, BlockState> transform, Consumer<Context> callback) {
            this.transform = transform;
            this.callback = callback;
        }

        public Function<BlockState, BlockState> getTransform() {
            return this.transform;
        }

        public BlockState transform(BlockState state) {
            return this.transform.apply(state);
        }

        public Consumer<Context> getCallback() {
            return this.callback;
        }

        public void callback(Context context) {
            this.callback.accept(context);
        }
    }

    public record Context(Level level, BlockPos pos, BlockState state) {}
}
