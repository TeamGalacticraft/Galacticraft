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

package dev.galacticraft.mod.content.block.machine.airlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.machinelib.api.block.SimpleMachineBlock;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.content.item.KeycardItem;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AirlockControllerBlock extends SimpleMachineBlock {
    private static final MapCodec<AirlockControllerBlock> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    propertiesCodec(),
                    ResourceKey.codec(Registries.BLOCK_ENTITY_TYPE)
                            .fieldOf("factory")
                            .forGetter(block -> block.blockEntityTypeKey),
                    Codec.BOOL
                            .optionalFieldOf("structure_managed", false)
                            .forGetter(block -> block.structureManagedByDefault)
            ).apply(instance, AirlockControllerBlock::new));

    private final ResourceKey<BlockEntityType<?>> blockEntityTypeKey;
    private final boolean structureManagedByDefault;

    public AirlockControllerBlock(
            Properties properties,
            ResourceLocation blockEntityType
    ) {
        this(
                properties,
                ResourceKey.create(
                        Registries.BLOCK_ENTITY_TYPE,
                        blockEntityType
                ),
                false
        );
    }

    public AirlockControllerBlock(
            Properties properties,
            ResourceLocation blockEntityType,
            boolean structureManagedByDefault
    ) {
        this(
                properties,
                ResourceKey.create(
                        Registries.BLOCK_ENTITY_TYPE,
                        blockEntityType
                ),
                structureManagedByDefault
        );
    }

    public AirlockControllerBlock(
            Properties properties,
            ResourceKey<BlockEntityType<?>> blockEntityTypeKey,
            boolean structureManagedByDefault
    ) {
        super(
                properties,
                blockEntityTypeKey
        );

        this.blockEntityTypeKey =
                blockEntityTypeKey;

        this.structureManagedByDefault =
                structureManagedByDefault;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable MachineBlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        MachineBlockEntity blockEntity =
                super.newBlockEntity(
                        pos,
                        state
                );

        if (this.structureManagedByDefault
                && blockEntity
                instanceof AirlockControllerBlockEntity airlock) {

            airlock.initializeStructureManagedDefaults();
        }

        return blockEntity;
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        if (this.structureManagedByDefault) {
            return;
        }

        super.setPlacedBy(
                level,
                pos,
                state,
                placer,
                stack
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!(stack.getItem() instanceof KeycardItem keycard)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide
                && level.getBlockEntity(pos)
                instanceof AirlockControllerBlockEntity airlock) {

            keycard.interactWithAirlock(
                    stack,
                    airlock,
                    player
            );
        }

        return ItemInteractionResult.sidedSuccess(
                level.isClientSide
        );
    }

    @Override
    public InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (level.getBlockEntity(pos)
                instanceof AirlockControllerBlockEntity airlock
                && (
                this.structureManagedByDefault
                        || airlock.isStructureManaged()
        )) {
            if (!level.isClientSide
                    && player instanceof ServerPlayer) {

                player.displayClientMessage(
                        Component.translatable(Translations.Chat.STRUCTURE_AIRLOCK_LOCKED),
                        true
                );
            }

            return InteractionResult.sidedSuccess(
                    level.isClientSide
            );
        }

        return super.useWithoutItem(
                state,
                level,
                pos,
                player,
                hit
        );
    }

    public boolean isStructureManagedByDefault() {
        return this.structureManagedByDefault;
    }
}