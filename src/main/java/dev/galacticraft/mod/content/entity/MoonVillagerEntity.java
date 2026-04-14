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

import java.util.ArrayDeque;

import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.village.GCVillagerProfessions;
import dev.galacticraft.mod.village.MoonVillagerTypes;
import dev.galacticraft.mod.world.poi.GCPointOfInterestTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MoonVillagerEntity extends Villager {
    private static final int GC_MOON_JOB_SITE_RANGE = 16;
    private static final int GC_MOON_HOME_RANGE = 24;
    private static final int GC_MOON_HOME_SEARCH_RANGE = 48;
    private static final int GC_MOON_JOB_SITE_SEARCH_RANGE = 48;
    private static final int GC_HOME_REPATH_INTERVAL = 20;
    private static final int GC_DAYTIME_OUTDOOR_CHECK_INTERVAL = 80;
    private static final int GC_BELL_SEARCH_RANGE = 24;
    private static final int GC_BELL_VERTICAL_RANGE = 6;
    private static final int GC_DAYTIME_OUTDOOR_WANDER_ATTEMPTS = 5;
    private static final int GC_STATIONARY_TICKS_BEFORE_UNSTICK = 200;
    private static final double GC_MIN_MOVEMENT_DELTA_SQR = 0.0025D;
    private static final double GC_DAYTIME_HOME_LINGER_DISTANCE_SQR = 16.0D;
    private static final double GC_DAYTIME_OUTDOOR_TARGET_MIN_DISTANCE_SQR = 25.0D;
    private static final double GC_HOME_TARGET_DISTANCE_SQR = 9.0D;
    private static final int GC_MOON_RESTOCK_INTERVAL_TICKS = 6000;
    private static final int GC_MOON_RESTOCK_WINDOW_TICKS = 24000;
    private static final int GC_MOON_MAX_RESTOCKS_PER_WINDOW = 4;
    private static final int GC_MOON_RESTOCK_REPATH_INTERVAL = 20;
    private static final double GC_MOON_RESTOCK_JOB_SITE_DISTANCE_SQR = 9.0D;

    private Vec3 gc$lastMovementCheckPos = Vec3.ZERO;
    private int gc$stationaryTicks;
    private @Nullable BlockPos gc$pendingHomeTarget;
    private @Nullable BlockPos gc$pendingJobSiteTarget;
    private long gc$moonRestockReadyAt = -1L;
    private final ArrayDeque<Long> gc$moonRestockHistory = new ArrayDeque<>();

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, Level level) {
        super(entityType, level);
        this.gc$lastMovementCheckPos = this.position();
        // OpenDoorGoal allows villagers to open doors along their path.
        // DoorBlockMixin makes isWoodenDoor() return true for meteoric iron doors,
        // and WalkNodeEvaluatorMixin makes the pathfinder treat them as passable.
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.OpenDoorGoal(this, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.gc$ensureMoonProfession(false);
        this.gc$ensureMoonHome(false);
        this.gc$updateNightHomeBehavior();
        this.gc$updateDaytimeOutdoorBehavior();
        this.gc$unstickIfIdle();
        this.gc$updateMoonRestock();
    }

    @Override
    public boolean canBreed() {
        return super.canBreed() && this.gc$hasBreedingJobSite();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        this.gc$ensureMoonProfession(true);

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof SpawnEggItem)
                && this.isAlive()
                && !this.isTrading()
                && !this.isBaby()
                && !player.isSecondaryUseActive()
                && !this.gc$canUnderstandPlayer(player)) {
            if (!this.level().isClientSide) {
                player.displayClientMessage(Component.translatable(Translations.Chat.MOON_VILLAGER_NO_FREQUENCY_MODULE).withStyle(Constant.Text.RED_STYLE), true);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable Villager getBreedOffspring(ServerLevel serverLevel, AgeableMob otherParent) {
        MoonVillagerEntity child = GCEntityTypes.MOON_VILLAGER.create(serverLevel);
        if (child == null) {
            return null;
        }

        child.setVillagerData(child.getVillagerData()
                .setType(MoonVillagerTypes.MOON_HIGHLANDS)
                .setProfession(VillagerProfession.NONE)
                .setLevel(1));
        return child;
    }

    @Override
    protected Component getTypeName() {
        String descriptionId = this.getType().getDescriptionId();

        if (this.isBaby()) {
            return Component.translatable(descriptionId + ".baby");
        }

        VillagerProfession profession = this.getVillagerData().getProfession();
        if (profession == VillagerProfession.NONE) {
            return Component.translatable(descriptionId + ".none");
        }

        return Component.translatable(descriptionId + "." + BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getPath());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("MoonRestockReadyAt", this.gc$moonRestockReadyAt);
        if (!this.gc$moonRestockHistory.isEmpty()) {
            long[] restockHistory = new long[this.gc$moonRestockHistory.size()];
            int index = 0;

            for (Long restockTick : this.gc$moonRestockHistory) {
                restockHistory[index++] = restockTick.longValue();
            }

            compound.putLongArray("MoonRestockHistory", restockHistory);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.gc$moonRestockReadyAt = compound.contains("MoonRestockReadyAt") ? compound.getLong("MoonRestockReadyAt") : -1L;
        this.gc$moonRestockHistory.clear();

        if (compound.contains("MoonRestockHistory")) {
            for (long restockTick : compound.getLongArray("MoonRestockHistory")) {
                this.gc$moonRestockHistory.addLast(restockTick);
            }
        }
    }

    private void gc$ensureMoonProfession(boolean forceClaim) {
        if (this.level().isClientSide || this.isBaby()) {
            return;
        }

        var villagerData = this.getVillagerData();
        if (villagerData.getProfession() == VillagerProfession.NITWIT) {
            return;
        }

        GCVillagerProfessions.MoonProfessionData professionData = GCVillagerProfessions.getMoonProfessionData(villagerData.getProfession());
        boolean hasClaimedJobSite = professionData != null && this.gc$hasClaimedMoonJobSite(professionData);

        if (professionData != null && !hasClaimedJobSite && this.getVillagerXp() == 0 && !this.isTrading()) {
            this.gc$clearMoonJobSite();
            this.setVillagerData(villagerData.setProfession(VillagerProfession.NONE).setLevel(1));
            this.setVillagerXp(0);
            this.setOffers(new MerchantOffers());
            villagerData = this.getVillagerData();
            professionData = null;
            if (this.level() instanceof ServerLevel serverLevel) {
                this.refreshBrain(serverLevel);
            }
        } else if (professionData == null && villagerData.getProfession() == VillagerProfession.NONE) {
            this.gc$clearMoonJobSite();
        }

        if (professionData == null && villagerData.getProfession() == VillagerProfession.NONE && (forceClaim || this.tickCount % 20 == 0)) {
            professionData = this.gc$tryClaimMoonJobSite();
            if (professionData != null) {
                this.gc$pendingJobSiteTarget = null;
            } else if (!forceClaim) {
                this.gc$seekUnclaimedJobSite();
            }
        }

        if (professionData != null && villagerData.getProfession() == VillagerProfession.NONE) {
            this.setVillagerData(villagerData.setProfession(professionData.profession()).setLevel(Math.max(1, villagerData.getLevel())));
            villagerData = this.getVillagerData();
            if (this.level() instanceof ServerLevel serverLevel) {
                this.refreshBrain(serverLevel);
            }
        }

        if (professionData != null && this.getVillagerData().getProfession() == professionData.profession() && this.getOffers().isEmpty()) {
            this.updateTrades();
        }
    }

    private void gc$clearMoonJobSite() {
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
        this.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.gc$pendingJobSiteTarget = null;
    }

    private void gc$ensureMoonHome(boolean forceClaim) {
        if (this.level().isClientSide || this.isBaby()) {
            return;
        }

        if (this.gc$hasClaimedMoonHome()) {
            this.gc$pendingHomeTarget = null;
            return;
        }

        this.gc$clearMoonHome();
        if (forceClaim || this.tickCount % GC_HOME_REPATH_INTERVAL == 0) {
            if (this.gc$tryClaimMoonHome()) {
                this.gc$pendingHomeTarget = null;
                return;
            }
            if (!forceClaim) {
                this.gc$seekUnclaimedHome();
            }
        }
    }

    private void gc$clearMoonHome() {
        this.releasePoi(MemoryModuleType.HOME);
        this.getBrain().eraseMemory(MemoryModuleType.HOME);
        this.gc$pendingHomeTarget = null;
    }

    private @Nullable GCVillagerProfessions.MoonProfessionData gc$tryClaimMoonJobSite() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        serverLevel.getPoiManager().ensureLoadedAndValid(serverLevel, this.blockPosition(), GC_MOON_JOB_SITE_RANGE + 1);

        for (GCVillagerProfessions.MoonProfessionData professionData : GCVillagerProfessions.moonProfessions()) {
            boolean claimed = serverLevel.getPoiManager()
                    .take(holder -> holder.is(professionData.poiKey()),
                            (holder, pos) -> professionData.matches(this.level().getBlockState(pos)),
                            this.blockPosition(),
                            GC_MOON_JOB_SITE_RANGE)
                    .map(pos -> {
                        this.getBrain().setMemory(MemoryModuleType.JOB_SITE, GlobalPos.of(serverLevel.dimension(), pos));
                        this.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
                        return true;
                    })
                    .orElse(false);

            if (claimed) {
                return professionData;
            }
        }

        return null;
    }

    private boolean gc$tryClaimMoonHome() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        serverLevel.getPoiManager().ensureLoadedAndValid(serverLevel, this.blockPosition(), GC_MOON_HOME_RANGE + 1);

        return serverLevel.getPoiManager()
                .take(holder -> holder.is(GCPointOfInterestTypes.LUNAR_HOME),
                        (holder, pos) -> this.level().getBlockState(pos).is(GCBlocks.LUNAR_HOME_ANCHOR),
                        this.blockPosition(),
                        GC_MOON_HOME_RANGE)
                .map(pos -> {
                    this.getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(serverLevel.dimension(), pos));
                    return true;
                })
                .orElse(false);
    }

    private void gc$seekUnclaimedHome() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.gc$pendingHomeTarget != null) {
            if (serverLevel.getBlockState(this.gc$pendingHomeTarget).is(GCBlocks.LUNAR_HOME_ANCHOR)) {
                this.getNavigation().moveTo(
                        this.gc$pendingHomeTarget.getX() + 0.5D, this.gc$pendingHomeTarget.getY(),
                        this.gc$pendingHomeTarget.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
                return;
            }
            this.gc$pendingHomeTarget = null;
        }

        serverLevel.getPoiManager().ensureLoadedAndValid(serverLevel, this.blockPosition(), GC_MOON_HOME_SEARCH_RANGE + 1);
        serverLevel.getPoiManager()
                .findClosest(
                        holder -> holder.is(GCPointOfInterestTypes.LUNAR_HOME),
                        pos -> serverLevel.getBlockState(pos).is(GCBlocks.LUNAR_HOME_ANCHOR),
                        this.blockPosition(),
                        GC_MOON_HOME_SEARCH_RANGE,
                        PoiManager.Occupancy.HAS_SPACE)
                .ifPresent(pos -> {
                    this.gc$pendingHomeTarget = pos;
                    this.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
                });
    }

    private void gc$seekUnclaimedJobSite() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.gc$pendingJobSiteTarget != null) {
            boolean stillValid = false;
            for (GCVillagerProfessions.MoonProfessionData profData : GCVillagerProfessions.moonProfessions()) {
                if (profData.matches(serverLevel.getBlockState(this.gc$pendingJobSiteTarget))) {
                    stillValid = true;
                    break;
                }
            }
            if (stillValid) {
                this.getNavigation().moveTo(
                        this.gc$pendingJobSiteTarget.getX() + 0.5D, this.gc$pendingJobSiteTarget.getY(),
                        this.gc$pendingJobSiteTarget.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
                return;
            }
            this.gc$pendingJobSiteTarget = null;
        }

        serverLevel.getPoiManager().ensureLoadedAndValid(serverLevel, this.blockPosition(), GC_MOON_JOB_SITE_SEARCH_RANGE + 1);
        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;

        for (GCVillagerProfessions.MoonProfessionData profData : GCVillagerProfessions.moonProfessions()) {
            var found = serverLevel.getPoiManager().findClosest(
                    holder -> holder.is(profData.poiKey()),
                    pos -> profData.matches(serverLevel.getBlockState(pos)),
                    this.blockPosition(),
                    GC_MOON_JOB_SITE_SEARCH_RANGE,
                    PoiManager.Occupancy.HAS_SPACE);

            if (found.isPresent()) {
                double dist = found.get().distSqr(this.blockPosition());
                if (dist < bestDist) {
                    bestDist = dist;
                    bestPos = found.get();
                }
            }
        }

        if (bestPos != null) {
            this.gc$pendingJobSiteTarget = bestPos;
            this.getNavigation().moveTo(bestPos.getX() + 0.5D, bestPos.getY(), bestPos.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
        }
    }

    private boolean gc$hasClaimedMoonJobSite(GCVillagerProfessions.MoonProfessionData professionData) {
        return this.gc$getClaimedMoonJobSitePos(professionData) != null;
    }

    private boolean gc$hasClaimedMoonHome() {
        return this.getBrain().getMemory(MemoryModuleType.HOME)
                .filter(this::gc$isCurrentDimensionJobSite)
                .map(GlobalPos::pos)
                .map(pos -> this.level().getBlockState(pos))
                .filter(state -> state.is(GCBlocks.LUNAR_HOME_ANCHOR))
                .isPresent();
    }

    private boolean gc$hasBreedingJobSite() {
        GCVillagerProfessions.MoonProfessionData professionData = GCVillagerProfessions.getMoonProfessionData(this.getVillagerData().getProfession());
        return professionData != null && this.gc$hasClaimedMoonJobSite(professionData);
    }

    private boolean gc$isCurrentDimensionJobSite(GlobalPos jobSite) {
        return jobSite.dimension().equals(this.level().dimension());
    }

    private boolean gc$canUnderstandPlayer(Player player) {
        Container accessories = player.galacticraft$getAccessories();
        for (int slot = 0; slot < accessories.getContainerSize(); ++slot) {
            ItemStack stack = accessories.getItem(slot);
            if (stack.is(GCItemTags.FREQUENCY_MODULES)) {
                return true;
            }
            if (stack.getItem() instanceof Accessory accessory && accessory.enablesHearing()) {
                return true;
            }
        }

        return false;
    }

    private void gc$updateMoonRestock() {
        if (this.level().isClientSide || this.isBaby()) {
            return;
        }

        GCVillagerProfessions.MoonProfessionData professionData = GCVillagerProfessions.getMoonProfessionData(this.getVillagerData().getProfession());
        if (professionData == null) {
            this.gc$moonRestockReadyAt = -1L;
            return;
        }

        BlockPos jobSitePos = this.gc$getClaimedMoonJobSitePos(professionData);
        if (jobSitePos == null) {
            this.gc$moonRestockReadyAt = -1L;
            return;
        }

        long gameTime = this.level().getGameTime();
        this.gc$pruneMoonRestockHistory(gameTime);

        if (!this.gc$offersNeedMoonRestock()) {
            this.gc$moonRestockReadyAt = -1L;
            return;
        }

        if (this.gc$moonRestockHistory.size() >= GC_MOON_MAX_RESTOCKS_PER_WINDOW) {
            return;
        }

        if (this.gc$moonRestockReadyAt < 0L) {
            this.gc$moonRestockReadyAt = gameTime + GC_MOON_RESTOCK_INTERVAL_TICKS;
        }

        if (gameTime < this.gc$moonRestockReadyAt || this.isTrading() || this.isSleeping() || this.isPassenger() || this.isLeashed()) {
            return;
        }

        if (jobSitePos.distSqr(this.blockPosition()) > GC_MOON_RESTOCK_JOB_SITE_DISTANCE_SQR) {
            if (this.tickCount % GC_MOON_RESTOCK_REPATH_INTERVAL == 0 || this.getNavigation().isDone()) {
                this.getNavigation().moveTo(jobSitePos.getX() + 0.5D, jobSitePos.getY(), jobSitePos.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
            }
            return;
        }

        if (this.gc$performMoonRestock(gameTime)) {
            this.gc$pendingJobSiteTarget = null;
        }
    }

    private boolean gc$offersNeedMoonRestock() {
        MerchantOffers offers = this.getOffers();
        if (offers.isEmpty()) {
            return false;
        }

        for (MerchantOffer offer : offers) {
            if (offer.getUses() > 0) {
                return true;
            }
        }

        return false;
    }

    private void gc$pruneMoonRestockHistory(long gameTime) {
        while (!this.gc$moonRestockHistory.isEmpty() && gameTime - this.gc$moonRestockHistory.peekFirst() >= GC_MOON_RESTOCK_WINDOW_TICKS) {
            this.gc$moonRestockHistory.removeFirst();
        }
    }

    private boolean gc$performMoonRestock(long gameTime) {
        MerchantOffers offers = this.getOffers();
        if (offers.isEmpty() || !this.gc$offersNeedMoonRestock()) {
            this.gc$moonRestockReadyAt = -1L;
            return false;
        }

        super.restock();

        if (this.gc$offersNeedMoonRestock()) {
            for (MerchantOffer offer : offers) {
                if (offer.getUses() <= 0) {
                    continue;
                }

                offer.updateDemand();
                offer.resetUses();
            }
        }

        if (this.gc$offersNeedMoonRestock()) {
            return false;
        }

        this.gc$moonRestockHistory.addLast(gameTime);
        this.gc$moonRestockReadyAt = -1L;
        return true;
    }

    private void gc$updateNightHomeBehavior() {
        if (this.level().isClientSide || this.level().isDay() || this.isTrading() || this.isSleeping() || this.isPassenger() || this.isLeashed()) {
            return;
        }

        if (this.tickCount % GC_HOME_REPATH_INTERVAL != 0) {
            return;
        }

        this.gc$moveTowardNightHome();
    }

    private void gc$updateDaytimeOutdoorBehavior() {
        if (this.level().isClientSide || !this.level().isDay() || this.isTrading() || this.isSleeping() || this.isPassenger() || this.isLeashed()) {
            return;
        }

        if (this.gc$pendingJobSiteTarget != null || this.gc$pendingHomeTarget != null) {
            return;
        }

        if (this.tickCount % GC_DAYTIME_OUTDOOR_CHECK_INTERVAL != 0 || !this.getNavigation().isDone()) {
            return;
        }

        this.gc$moveTowardDaytimeOutdoorTarget();
    }

    private void gc$unstickIfIdle() {
        if (this.level().isClientSide || this.isTrading() || this.isSleeping() || this.isPassenger() || this.isLeashed()) {
            this.gc$resetIdleTracking();
            return;
        }

        if (!this.onGround() || !this.getNavigation().isDone()) {
            this.gc$resetIdleTracking();
            return;
        }

        Vec3 currentPos = this.position();
        if (currentPos.distanceToSqr(this.gc$lastMovementCheckPos) > GC_MIN_MOVEMENT_DELTA_SQR) {
            this.gc$lastMovementCheckPos = currentPos;
            this.gc$stationaryTicks = 0;
            return;
        }

        this.gc$stationaryTicks++;
        if (this.gc$stationaryTicks < GC_STATIONARY_TICKS_BEFORE_UNSTICK) {
            return;
        }

        this.gc$pendingHomeTarget = null;
        this.gc$pendingJobSiteTarget = null;

        if (this.level().isDay() && this.gc$moveTowardDaytimeOutdoorTarget()) {
            this.gc$resetIdleTracking();
            return;
        }

        if (!this.level().isDay() && this.gc$moveTowardNightHome()) {
            this.gc$resetIdleTracking();
            return;
        }

        if (this.gc$moveTowardClaimedJobSite()) {
            this.gc$resetIdleTracking();
            return;
        }

        Vec3 wanderTarget = LandRandomPos.getPos(this, 8, 4);
        if (wanderTarget != null) {
            this.getNavigation().moveTo(wanderTarget.x, wanderTarget.y, wanderTarget.z, this.isBaby() ? 0.6D : 0.5D);
        }

        this.gc$resetIdleTracking();
    }

    private boolean gc$moveTowardClaimedJobSite() {
        return this.getBrain().getMemory(MemoryModuleType.JOB_SITE)
                .filter(this::gc$isCurrentDimensionJobSite)
                .map(GlobalPos::pos)
                .filter(pos -> pos.distSqr(this.blockPosition()) > 9.0D)
                .map(pos -> this.getNavigation().moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.6D))
                .orElse(false);
    }

    private @Nullable BlockPos gc$getClaimedMoonJobSitePos(GCVillagerProfessions.MoonProfessionData professionData) {
        return this.getBrain().getMemory(MemoryModuleType.JOB_SITE)
                .filter(this::gc$isCurrentDimensionJobSite)
                .map(GlobalPos::pos)
                .filter(pos -> professionData.matches(this.level().getBlockState(pos)))
                .orElse(null);
    }

    private boolean gc$moveTowardNightHome() {
        BlockPos homeTarget = this.gc$getNightHomeTargetPos();
        if (homeTarget == null || homeTarget.distSqr(this.blockPosition()) <= GC_HOME_TARGET_DISTANCE_SQR) {
            return false;
        }

        return this.getNavigation().moveTo(homeTarget.getX() + 0.5D, homeTarget.getY(), homeTarget.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
    }

    private boolean gc$moveTowardDaytimeOutdoorTarget() {
        BlockPos homePos = this.gc$getClaimedHomePos();
        if (homePos == null || homePos.distSqr(this.blockPosition()) > GC_DAYTIME_HOME_LINGER_DISTANCE_SQR) {
            return false;
        }

        BlockPos bellPos = this.gc$findNearbyBell();
        if (bellPos != null && bellPos.distSqr(this.blockPosition()) > GC_HOME_TARGET_DISTANCE_SQR) {
            return this.getNavigation().moveTo(bellPos.getX() + 0.5D, bellPos.getY(), bellPos.getZ() + 0.5D, this.isBaby() ? 0.6D : 0.5D);
        }

        Vec3 homeCenter = Vec3.atBottomCenterOf(homePos);
        for (int attempt = 0; attempt < GC_DAYTIME_OUTDOOR_WANDER_ATTEMPTS; attempt++) {
            Vec3 wanderTarget = LandRandomPos.getPos(this, 12, 5);
            if (wanderTarget == null || wanderTarget.distanceToSqr(homeCenter) <= GC_DAYTIME_OUTDOOR_TARGET_MIN_DISTANCE_SQR) {
                continue;
            }

            return this.getNavigation().moveTo(wanderTarget.x, wanderTarget.y, wanderTarget.z, this.isBaby() ? 0.6D : 0.5D);
        }

        return false;
    }

    private @Nullable BlockPos gc$getNightHomeTargetPos() {
        BlockPos homePos = this.gc$getClaimedHomePos();
        return homePos != null ? homePos : this.gc$findNearbyBell();
    }

    private @Nullable BlockPos gc$getClaimedHomePos() {
        return this.getBrain().getMemory(MemoryModuleType.HOME)
                .filter(this::gc$isCurrentDimensionJobSite)
                .map(GlobalPos::pos)
                .filter(pos -> this.level().getBlockState(pos).is(GCBlocks.LUNAR_HOME_ANCHOR))
                .orElse(null);
    }

    private @Nullable BlockPos gc$findNearbyBell() {
        BlockPos origin = this.blockPosition();
        BlockPos min = origin.offset(-GC_BELL_SEARCH_RANGE, -GC_BELL_VERTICAL_RANGE, -GC_BELL_SEARCH_RANGE);
        BlockPos max = origin.offset(GC_BELL_SEARCH_RANGE, GC_BELL_VERTICAL_RANGE, GC_BELL_SEARCH_RANGE);
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!this.level().getBlockState(pos).is(Blocks.BELL)) {
                continue;
            }

            double distance = pos.distSqr(origin);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPos = pos.immutable();
            }
        }

        return bestPos;
    }

    private void gc$resetIdleTracking() {
        this.gc$lastMovementCheckPos = this.position();
        this.gc$stationaryTicks = 0;
    }
}
