/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.content.block.machine;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.api.block.entity.SpaceFillingAtmosphereProvider;
import dev.galacticraft.machinelib.api.block.MachineBlock;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class OxygenSealerBlock extends MachineBlock {
    private static final MapCodec<OxygenSealerBlock> CODEC = simpleCodec(OxygenSealerBlock::new);

    public OxygenSealerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable MachineBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OxygenSealerBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, level, pos, newState, moved);
        if (!newState.is(this)) {
            if (level.getBlockEntity(pos) instanceof OxygenSealerBlockEntity be) {
                be.destroySeal();
            }
        }
    }

    @Override
    public boolean galacticraft$hasAtmosphereListener() {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState blockState2, boolean bl) {
        super.onPlace(state, level, pos, blockState2, bl);
        OxygenSealerBlockEntity be = (OxygenSealerBlockEntity) level.getBlockEntity(pos);
        Iterator<AtmosphereProvider> iterator = level.galacticraft$getAtmosphericProviders(pos);
        assert be != null;
        while (iterator.hasNext()) {
            AtmosphereProvider next = iterator.next();
            if (!pos.equals(next.be().getBlockPos()) && next instanceof SpaceFillingAtmosphereProvider) {
                if (next.canBreathe(pos)) {
                    be.markContended(true);
                    return;
                }
            }
        }
        be.markContended(false);
    }

    @Override
    public void galacticraft$onAtmosphereChange(ServerLevel level, BlockPos pos, BlockState state, Iterator<AtmosphereProvider> iterator) {
        checkContention(level, pos, iterator);
    }

    private static void checkContention(ServerLevel level, BlockPos pos, Iterator<AtmosphereProvider> iterator) {
        OxygenSealerBlockEntity be = (OxygenSealerBlockEntity) level.getBlockEntity(pos);
        assert be != null;
        while (iterator.hasNext()) {
            AtmosphereProvider next = iterator.next();
            if (!pos.equals(next.be().getBlockPos()) && next instanceof SpaceFillingAtmosphereProvider) {
                if (next.canBreathe(pos)) {
                    be.markContended(true);
                    return;
                }
            }
        }
        be.markContended(false);
    }
}