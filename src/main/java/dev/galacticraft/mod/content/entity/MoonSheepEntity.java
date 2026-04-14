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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MoonSheepEntity extends Sheep {
    private static final String WOOL_REGROWTH_DELAY_TICKS_TAG = "MoonWoolRegrowthDelayTicks";
    private static final String MOON_GRAZE_TICKS_TAG = "MoonGrazeTicks";
    private static final int MIN_WOOL_REGROWTH_TICKS = 20 * 30;
    private static final int MAX_WOOL_REGROWTH_TICKS = 20 * 60;
    private static final int GRAZE_ANIMATION_TICKS = 40;
    private static final int GRAZE_REGROWTH_TICK = 4;

    private int woolRegrowthDelayTicks;
    private int moonGrazeTicks;

    public MoonSheepEntity(EntityType<? extends Sheep> entityType, Level level) {
        super(entityType, level);
        this.setColor(DyeColor.WHITE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes().add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D);
    }

    @Nullable
    @Override
    public MoonSheepEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        MoonSheepEntity child = GCEntityTypes.MOON_SHEEP.create(serverLevel);
        if (child != null) {
            child.setColor(DyeColor.WHITE);
        }
        return child;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DyeItem) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void setColor(DyeColor dyeColor) {
        super.setColor(DyeColor.WHITE);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.tickMoonWoolRegrowth();
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.woolRegrowthDelayTicks = tag.getInt(WOOL_REGROWTH_DELAY_TICKS_TAG);
        this.moonGrazeTicks = tag.getInt(MOON_GRAZE_TICKS_TAG);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(WOOL_REGROWTH_DELAY_TICKS_TAG, this.woolRegrowthDelayTicks);
        tag.putInt(MOON_GRAZE_TICKS_TAG, this.moonGrazeTicks);
    }

    @Override
    public void shear(SoundSource soundSource) {
        super.shear(soundSource);
        this.woolRegrowthDelayTicks = this.createMoonWoolRegrowthDelay();
        this.moonGrazeTicks = 0;
    }

    private void tickMoonWoolRegrowth() {
        if (!this.isSheared()) {
            this.woolRegrowthDelayTicks = 0;
            this.moonGrazeTicks = 0;
            return;
        }

        if (this.moonGrazeTicks > 0) {
            this.moonGrazeTicks--;
            if (this.moonGrazeTicks == GRAZE_REGROWTH_TICK) {
                this.ate();
            }
            return;
        }

        if (!this.level().getBlockState(this.blockPosition().below()).is(GCBlocks.MOON_TURF)) {
            return;
        }

        if (this.woolRegrowthDelayTicks <= 0) {
            this.woolRegrowthDelayTicks = this.createMoonWoolRegrowthDelay();
        }

        this.woolRegrowthDelayTicks--;
        if (this.woolRegrowthDelayTicks <= 0) {
            this.moonGrazeTicks = GRAZE_ANIMATION_TICKS;
            this.level().broadcastEntityEvent(this, (byte) 10);
        }
    }

    private int createMoonWoolRegrowthDelay() {
        return MIN_WOOL_REGROWTH_TICKS + this.random.nextInt(MAX_WOOL_REGROWTH_TICKS - MIN_WOOL_REGROWTH_TICKS + 1);
    }

    @Override
    public boolean galacticraft$hasMask() {
        return true;
    }

    @Override
    public boolean galacticraft$hasGear() {
        return true;
    }

    @Override
    public String galacticraft$tankSize(int i) {
        return Constant.Item.MEDIUM_OXYGEN_TANK;
    }
}