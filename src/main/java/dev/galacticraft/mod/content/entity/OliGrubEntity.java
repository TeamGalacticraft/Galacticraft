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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.content.entity.goals.LatchWhenCloseGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.jetbrains.annotations.Nullable;

public class OliGrubEntity extends Animal {
    private static final int LATCH_DURATION_TICKS = 20 * 20; // 20 s
    private static final int DRAIN_INTERVAL_TICKS = 20 * 5;  // every 5s

    private static final EntityDataAccessor<Boolean> DATA_LATCHED =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> DATA_ANCHOR =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> DATA_OFFX =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFY =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFZ =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ROTX =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ROTY =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ROTZ =
            SynchedEntityData.defineId(OliGrubEntity.class, EntityDataSerializers.FLOAT);

    private int latchTicks;
    private int nextDrainTick;

    public OliGrubEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    /* ---------- Vanilla basics ---------- */

    @Override
    public boolean isFood(ItemStack stack) { return false; }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob partner) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D);
    }

    /* ---------- Synced state ---------- */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder compositeStateBuilder) {
        super.defineSynchedData(compositeStateBuilder);
        compositeStateBuilder.define(DATA_LATCHED, false);
        compositeStateBuilder.define(DATA_ANCHOR, (byte) AnchorPoint.BODY.ordinal());
        compositeStateBuilder.define(DATA_OFFX, 0f);
        compositeStateBuilder.define(DATA_OFFY, 0f);
        compositeStateBuilder.define(DATA_OFFZ, 0f);
        compositeStateBuilder.define(DATA_ROTX, 0f);
        compositeStateBuilder.define(DATA_ROTY, 0f);
        compositeStateBuilder.define(DATA_ROTZ, 0f);
    }

    public void setLocalRotation(float rx, float ry, float rz) {
        this.entityData.set(DATA_ROTX, rx);
        this.entityData.set(DATA_ROTY, ry);
        this.entityData.set(DATA_ROTZ, rz);
    }
    public float getLocalRotX() { return this.entityData.get(DATA_ROTX); }
    public float getLocalRotY() { return this.entityData.get(DATA_ROTY); }
    public float getLocalRotZ() { return this.entityData.get(DATA_ROTZ); }

    public boolean isLatched() {
        return this.entityData.get(DATA_LATCHED);
    }

    private void setLatched(boolean v) {
        this.entityData.set(DATA_LATCHED, v);
        this.setNoAi(v);
        this.setInvulnerable(v);
        this.setPersistenceRequired();
    }

    public AnchorPoint getAnchor() { return AnchorPoint.values()[this.entityData.get(DATA_ANCHOR)]; }
    public void setAnchor(AnchorPoint a) { this.entityData.set(DATA_ANCHOR, (byte) a.ordinal()); }
    public net.minecraft.world.phys.Vec3 getLocalOffset() {
        return new net.minecraft.world.phys.Vec3(this.entityData.get(DATA_OFFX), this.entityData.get(DATA_OFFY), this.entityData.get(DATA_OFFZ));
    }
    public void setLocalOffset(float x, float y, float z) {
        this.entityData.set(DATA_OFFX, x);
        this.entityData.set(DATA_OFFY, y);
        this.entityData.set(DATA_OFFZ, z);
    }

    //TODO test item for now but in future it will be something else
    private static boolean playerHoldsRedstone(Player p) {
        return p.getMainHandItem().is(Items.REDSTONE) || p.getOffhandItem().is(Items.REDSTONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));

        this.goalSelector.addGoal(3, new LatchWhenCloseGoal(this, 1.35, 1.6));
    }

    @Nullable private java.util.UUID pendingPassengersResendTo;
    private int pendingPassengersDelay;
    private boolean pendingDiscardToEgg;

    public void tryLatchOnto(Player player) {
        if (this.level().isClientSide || this.isLatched() || !this.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (playerHoldsRedstone(player)) return;

        java.util.EnumSet<AnchorPoint> occupied = java.util.EnumSet.noneOf(AnchorPoint.class);
        for (Entity e : player.getPassengers()) {
            if (e instanceof OliGrubEntity g && g.isLatched()) {
                occupied.add(g.getAnchor());
            }
        }

        java.util.List<AnchorPoint> available = new java.util.ArrayList<>();
        for (AnchorPoint a : AnchorPoint.values()) {
            if (!occupied.contains(a)) {
                available.add(a);
            }
        }
        if (available.isEmpty()) return;

        AnchorPoint chosen = available.get(this.getRandom().nextInt(available.size()));

        if (this.startRiding(player, true)) {
            this.latchTicks = 0;
            this.nextDrainTick = DRAIN_INTERVAL_TICKS;

            this.setAnchor(chosen);
            Pose p = randomPoseFor(chosen, this.getRandom());
            this.setLocalOffset(p.tx, p.ty, p.tz);
            this.setLocalRotation(p.rx, p.ry, p.rz);

            this.setLatched(true);
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.25, 0.0));
            this.pendingPassengersResendTo = player.getUUID();
            this.pendingPassengersDelay = 1;
        }
    }

    @Override
    public void rideTick() {
        if (this.getVehicle() instanceof Player mob) {
            if (playerHoldsRedstone(mob)) { unlatch(false); return; }

            latchTicks++;
            if (latchTicks >= nextDrainTick) {
                var src = this.level().damageSources().source(
                        GCDamageTypes.OLIGRUB_DRAIN,
                        this, this
                );
                mob.hurt(src, 1.0F);
                nextDrainTick += DRAIN_INTERVAL_TICKS;
            }
            if (latchTicks >= LATCH_DURATION_TICKS) {
                unlatch(true);
            }
        }
        super.rideTick();
    }

    private void unlatch(boolean convertToEgg) {
        if (this.level().isClientSide) return;

        Player vehiclePlayer = (this.getVehicle() instanceof Player p) ? p : null;

        this.stopRiding();
        this.setLatched(false);

        if (vehiclePlayer instanceof ServerPlayer sp) {
            this.pendingPassengersResendTo = sp.getUUID();
            this.pendingPassengersDelay = 1;
            this.pendingDiscardToEgg = convertToEgg;
        } else {
            if (convertToEgg) {
                var pos = BlockPos.containing(this.getX(), this.getY() + 0.1, this.getZ());
                FallingBlockEntity.fall(this.level(), pos, GCBlocks.OLI_FLY_EGG.defaultBlockState());
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.pendingPassengersResendTo != null && --this.pendingPassengersDelay <= 0) {
                ServerLevel sl = (ServerLevel) this.level();
                Player p = sl.getPlayerByUUID(this.pendingPassengersResendTo);
                if (p instanceof ServerPlayer sp) {
                    var pkt = new net.minecraft.network.protocol.game.ClientboundSetPassengersPacket(sp);
                    sp.connection.send(pkt);
                    for (var watcher : sl.getChunkSource().chunkMap.getPlayers(sp.chunkPosition(), false)) {
                        watcher.connection.send(pkt);
                    }
                }
                if (this.pendingDiscardToEgg) {
                    var pos = BlockPos.containing(this.getX(), this.getY() + 0.1, this.getZ());
                    FallingBlockEntity.fall(this.level(), pos, GCBlocks.OLI_FLY_EGG.defaultBlockState());
                    this.discard();
                }
                this.pendingPassengersResendTo = null;
                this.pendingDiscardToEgg = false;
            }
        }
    }

    @Override
    public boolean isPushable() { return !this.isLatched(); }

    @Override
    protected void doPush(Entity other) {
        if (!this.isLatched()) super.doPush(other);
    }

    public enum AnchorPoint {
        HEAD, BODY, RIGHT_ARM, LEFT_ARM, RIGHT_LEG, LEFT_LEG;
        public static AnchorPoint random(net.minecraft.util.RandomSource r) {
            return values()[r.nextInt(values().length)];
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Latched", this.isLatched());
        tag.putInt("LatchTicks", this.latchTicks);
        tag.putInt("NextDrain", this.nextDrainTick);
        tag.putByte("Anchor", (byte) this.getAnchor().ordinal());
        var off = this.getLocalOffset();
        tag.putFloat("OffX", (float) off.x);
        tag.putFloat("OffY", (float) off.y);
        tag.putFloat("OffZ", (float) off.z);
        tag.putFloat("RotX", this.getLocalRotX());
        tag.putFloat("RotY", this.getLocalRotY());
        tag.putFloat("RotZ", this.getLocalRotZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setLatched(tag.getBoolean("Latched"));
        this.latchTicks = tag.getInt("LatchTicks");
        this.nextDrainTick = tag.getInt("NextDrain");
        byte a = tag.getByte("Anchor");
        if (a >= 0 && a < AnchorPoint.values().length) this.setAnchor(AnchorPoint.values()[a]);
        this.setLocalOffset(tag.getFloat("OffX"), tag.getFloat("OffY"), tag.getFloat("OffZ"));
        this.setLocalRotation(tag.getFloat("RotX"), tag.getFloat("RotY"), tag.getFloat("RotZ"));
    }

    private record Pose(float tx, float ty, float tz, float rx, float ry, float rz) {}

    private static final Pose[] HEAD_POSES = new Pose[] {
            new Pose( 0f,  -1.4f,  0.5f,  0f,   0f,   0f),
            new Pose( 1.3f, -0.9f,  0f,   -90f,  45f,  90f),
            new Pose(-1.3f, -0.9f,  0f,  -90f, -45f,  -90f),
            new Pose( 0f,  -0.75f, -1.1f, 90f,   0f,  0f),
    };
    private static final Pose[] BODY_POSES = new Pose[] {
            new Pose( 0f,  -0.6f, -0.6f,  15f, 180f, 0f),
    };
    private static final Pose[] LEFT_ARM_POSES = new Pose[] {
            new Pose( 0.04f, -1f,   0.33f, 0f,   0f,   0f),
            new Pose( 0.40f, -0.8f, 0f,    0f,  90f,  0f),
    };
    private static final Pose[] RIGHT_ARM_POSES = new Pose[] {
            new Pose(-0.04f, -1f,   0.33f, 0f,   0f,   0f),
            new Pose(-0.40f, -0.8f, 0f,    0f, -90f,  0f),
    };
    private static final Pose[] LEFT_LEG_POSES = new Pose[] {
            new Pose( 0f,   -0.7f, 0.34f,  0f,   0f,   0f),
            new Pose( 0.34f, -0.9f, 0f,    0f,  90f,  0f),
    };
    private static final Pose[] RIGHT_LEG_POSES = new Pose[] {
            new Pose( 0f,   -0.7f, 0.34f,  0f,   0f,   0f),
            new Pose(-0.34f, -0.9f, 0f,    0f, -90f,  0f),
    };

    private static Pose randomPoseFor(AnchorPoint a, net.minecraft.util.RandomSource r) {
        Pose[] arr = switch (a) {
            case HEAD      -> HEAD_POSES;
            case BODY      -> BODY_POSES;
            case LEFT_ARM  -> LEFT_ARM_POSES;
            case RIGHT_ARM -> RIGHT_ARM_POSES;
            case LEFT_LEG  -> LEFT_LEG_POSES;
            case RIGHT_LEG -> RIGHT_LEG_POSES;
        };
        return arr[r.nextInt(arr.length)];
    }
}