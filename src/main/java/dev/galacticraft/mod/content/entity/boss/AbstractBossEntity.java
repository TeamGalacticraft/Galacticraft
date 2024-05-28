/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.entity.boss;

import dev.galacticraft.mod.content.block.entity.DungeonSpawnerBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public abstract class AbstractBossEntity extends Monster {
    protected DungeonSpawnerBlockEntity spawner;

    public int entitiesWithin;
    public int entitiesWithinLast;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), getHealthBarColor(), BossEvent.BossBarOverlay.PROGRESS);

    public AbstractBossEntity(EntityType<? extends AbstractBossEntity> entityType, Level level) {
        super(entityType, level);
    }

    public abstract int getChestTier();

    public abstract ItemStack getGuaranteedLoot(Random rand);

    public abstract void dropKey();

    public abstract BossEvent.BossBarColor getHealthBarColor();

    @Override
    protected void customServerAiStep() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

//        if (this.deathTime >= 180 && this.deathTime <= 200) {
//            final float x = (this.random.nextFloat() - 0.5F) * this.width;
//            final float y = (this.random.nextFloat() - 0.5F) * (this.height / 2.0F);
//            final float z = (this.random.nextFloat() - 0.5F) * this.width;
//            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + x, this.posY + 2.0D + y, this.posZ + z, 0.0D, 0.0D, 0.0D);
//        }
//
//        int i;
//        int j;
//
//        if (!this.world.isRemote) {
//            if (this.deathTime >= 180 && this.deathTime % 5 == 0) {
//                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(PacketSimple.EnumSimplePacket.C_PLAY_SOUND_EXPLODE, GCCoreUtil.getDimensionID(this.world), new Object[]
//                        {}), new NetworkRegistry.TargetPoint(GCCoreUtil.getDimensionID(this.world), this.posX, this.posY, this.posZ, 40.0D));
//            }
//
//            if (this.deathTime > 150 && this.deathTime % 5 == 0) {
//                i = 30;
//
//                while (i > 0) {
//                    j = EntityXPOrb.getXPSplit(i);
//                    i -= j;
//                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
//                }
//            }
//        }
//
//        if (this.deathTime == 200 && !this.world.isRemote) {
//            i = 20;
//
//            while (i > 0) {
//                j = EntityXPOrb.getXPSplit(i);
//                i -= j;
//                this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
//            }
//
//            TileEntityTreasureChest chest = null;
//
//            if (this.spawner != null && this.spawner.getChestPos() != null) {
//                TileEntity chestTest = this.world.getTileEntity(this.spawner.getChestPos());
//
//                if (chestTest != null && chestTest instanceof TileEntityTreasureChest) {
//                    chest = (TileEntityTreasureChest) chestTest;
//                }
//            }
//
//            if (chest == null) {
//                // Fallback to finding closest chest
//                chest = TileEntityTreasureChest.findClosest(this, this.getChestTier());
//            }
//
//            if (chest != null) {
//                double dist = this.getDistanceSq(chest.getPos().getX() + 0.5, chest.getPos().getY() + 0.5, chest.getPos().getZ() + 0.5);
//                if (dist < 1000 * 1000) {
//                    if (!chest.locked) {
//                        chest.locked = true;
//                    }
//
//                    for (int k = 0; k < chest.getSizeInventory(); k++) {
//                        chest.setInventorySlotContents(k, ItemStack.EMPTY);
//                    }
//
//                    chest.fillWithLoot(null);
//
////                    ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
////
////                    // Generate twice, since it's an extra special chest
////                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
////                    WeightedRandomChestContent.generateChestContents(this.rand, info.getItems(this.rand), chest, info.getCount(this.rand));
//
//                    ItemStack schematic = this.getGuaranteedLoot(this.rand);
//                    int slot = this.rand.nextInt(chest.getSizeInventory());
//                    chest.setInventorySlotContents(slot, schematic);
//                }
//            }
//
//            this.dropKey();
//
//            super.setDead();
//
//            if (this.spawner != null) {
//                // Note: spawner.isBossDefeated is true, so it's properly dead
//                this.spawner.isBossDefeated = true;
//                this.spawner.boss = null;
//                this.spawner.spawned = false;
//
//                if (!this.world.isRemote) {
//                    this.spawner.lastKillTime = MinecraftServer.getCurrentTimeMillis();
//                }
//            }
//        }
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.setHealth(this.getHealth());
        }

        if (this.spawner != null) {
            List<Player> playersWithin = this.level().getEntitiesOfClass(Player.class, this.spawner.getRangeBounds());

            this.entitiesWithin = playersWithin.size();

            if (this.entitiesWithin == 0 && this.entitiesWithinLast != 0) {
                List<Player> entitiesWithin2 = this.level().getEntitiesOfClass(Player.class, this.spawner.getRangeBoundsPlus11());

                for (Player p : entitiesWithin2) {
                    p.sendSystemMessage(Component.translatable("gui.skeleton_boss.message"));
                }

                this.remove(RemovalReason.DISCARDED);
                // Note: spawner.isBossDefeated is false, so the boss will
                // respawn if any player comes back inside the room

                return;
            }

            this.entitiesWithinLast = this.entitiesWithin;
        }

        super.aiStep();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.spawner != null && reason == RemovalReason.DISCARDED) {
            this.spawner.isBossDefeated = false;
            this.spawner.boss = null;
            this.spawner.spawned = false;
        }

        super.remove(reason);
    }

    public void onBossSpawned(DungeonSpawnerBlockEntity spawner) {
        this.spawner = spawner;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}
