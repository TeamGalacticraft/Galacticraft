/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AirlockControllerBlockEntity extends BlockEntity implements MenuProvider {
    public boolean redstoneActivation;
    public boolean playerDistanceActivation = true;
    public int playerDistanceSelection;
    public boolean playerNameMatches;
    public String playerToOpenFor = "";
    public boolean invertSelection;
    public boolean horizontalModeEnabled;
    public boolean lastHorizontalModeEnabled;
    public String ownerName = "";

    public boolean active;
    public boolean lastActive;
    private int otherAirLocks;
    private int lastOtherAirLocks;
    private AirLockProtocol protocol;
    private AirLockProtocol lastProtocol;
    public int ticks = 0;

    public AirlockControllerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.AIRLOCK_CONTROLLER, blockPos, blockState);
    }

    
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AirlockControllerBlockEntity blockEntity) {
        blockEntity.tick();
    }

    public void tick() {
        ticks++;
        if (!this.getLevel().isClientSide())
        {
            this.active = false;

            if (this.redstoneActivation)
            {
                this.active = this.getLevel().getBestNeighborSignal(this.getBlockPos()) > 0;
            }

            if ((this.active || !this.redstoneActivation) && this.playerDistanceActivation)
            {
                double distance = 0D;

                switch (this.playerDistanceSelection)
                {
                    case 0:
                        distance = 1.0D;
                        break;
                    case 1:
                        distance = 2.0D;
                        break;
                    case 2:
                        distance = 5.0D;
                        break;
                    case 3:
                        distance = 10.0D;
                        break;
                }

                Vec3 minPos = new Vec3(getBlockPos().getX() + 0.5D - distance, getBlockPos().getY() + 0.5D - distance, getBlockPos().getZ() + 0.5D - distance);
                Vec3 maxPos = new Vec3(getBlockPos().getX() + 0.5D + distance, getBlockPos().getY() + 0.5D + distance, getBlockPos().getZ() + 0.5D + distance);
                AABB matchingRegion = new AABB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z);
                List<Player> playersWithin = this.getLevel().getEntitiesOfClass(Player.class, matchingRegion);

                if (this.playerNameMatches)
                {
                    boolean foundPlayer = false;
                    for (Player p : playersWithin)
                    {
                        if (p.getUUID().equals(this.playerToOpenFor))
                        {
                            foundPlayer = true;
                            break;
                        }
                    }
                    this.active = foundPlayer;
                } else
                {
                    this.active = !playersWithin.isEmpty();
                }
            }

            if (!this.invertSelection)
            {
                this.active = !this.active;
            }

            if (this.protocol == null)
            {
                this.protocol = this.lastProtocol = new AirLockProtocol(this);
            }

            if (this.ticks % 5 == 0)
            {
                if (this.horizontalModeEnabled != this.lastHorizontalModeEnabled)
                {
                    this.unsealAirLock();
                } else if (this.active || this.lastActive)
                {
                    this.lastOtherAirLocks = this.otherAirLocks;
                    this.otherAirLocks = this.protocol.calculate(this.horizontalModeEnabled);

                    if (this.active)
                    {
                        if (this.otherAirLocks != this.lastOtherAirLocks || !this.lastActive)
                        {
                            this.unsealAirLock();
                            if (this.otherAirLocks >= 0)
                            {
                                this.sealAirLock();
                            }
                        }
                    } else
                    {
                        if (this.lastActive)
                        {
                            this.unsealAirLock();
                        }
                    }
                }

                if (this.active != this.lastActive)
                {
                    BlockState state = this.getLevel().getBlockState(this.getBlockPos());
                    this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
                }

                this.lastActive = this.active;
                this.lastProtocol = this.protocol;
                this.lastHorizontalModeEnabled = this.horizontalModeEnabled;
            }
        }
    }

    private void sealAirLock()
    {
        int x = (this.lastProtocol.maxX + this.lastProtocol.minX) / 2;
        int y = (this.lastProtocol.maxY + this.lastProtocol.minY) / 2;
        int z = (this.lastProtocol.maxZ + this.lastProtocol.minZ) / 2;

        if (!this.getLevel().getBlockState(new BlockPos(x, y, z)).is(GCBlocks.AIR_LOCK_SEAL))
        {
//            this.getLevel().playSound(null, x, y, z, GCSounds.openAirLock, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (this.horizontalModeEnabled)
        {
            if (this.protocol.minY == this.protocol.maxY && this.protocol.minX != this.protocol.maxX && this.protocol.minZ != this.protocol.maxZ)
            {
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; x++)
                {
                    for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; z++)
                    {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).isAir())
                        {
                            this.getLevel().setBlock(pos, GCBlocks.AIR_LOCK_SEAL.defaultBlockState(), 3);
                        }
                    }
                }
            }
        } else
        {
            if (this.protocol.minX != this.protocol.maxX)
            {
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; x++)
                {
                    for (y = this.protocol.minY + 1; y <= this.protocol.maxY - 1; y++)
                    {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).isAir())
                        {
                            this.getLevel().setBlock(pos, GCBlocks.AIR_LOCK_SEAL.defaultBlockState(), 3);
                        }
                    }
                }
            } else if (this.protocol.minZ != this.protocol.maxZ)
            {
                for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; z++)
                {
                    for (y = this.protocol.minY + 1; y <= this.protocol.maxY - 1; y++)
                    {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).isAir())
                        {
                            this.getLevel().setBlock(pos, GCBlocks.AIR_LOCK_SEAL.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public void unsealAirLock()
    {
        if (this.lastProtocol == null)
        {
            return;
        }

        int x = this.lastProtocol.minX + (this.lastProtocol.maxX - this.lastProtocol.minX) / 2;
        int y = this.lastProtocol.minY + (this.lastProtocol.maxY - this.lastProtocol.minY) / 2;
        int z = this.lastProtocol.minZ + (this.lastProtocol.maxZ - this.lastProtocol.minZ) / 2;

        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = this.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof AirBlock)) {
//            this.getLevel().playSound(null, x, y, z, GCSounds.closeAirLock, SoundSource.BLOCKS, 1.0F, 1.0F); TODO: Sound
        }

        boolean sealedSide = false;
        boolean breathable;
        if (this.lastHorizontalModeEnabled)
        {
            if (this.protocol.minY == this.protocol.maxY && this.protocol.minX != this.protocol.maxX && this.protocol.minZ != this.protocol.maxZ)
            {
                // First test if there is sealed air to either side
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; x++)
                {
                    for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; z++)
                    {
                        pos = new BlockPos(x, y, z);
                        breathable = this.getLevel().isBreathable(pos.above());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).getBlock() == GCBlocks.AIR_LOCK_SEAL)
                            {
                                sealedSide = true;
                                break;
                            }
                            continue;
                        }
                        breathable = this.getLevel().isBreathable(pos.below());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).getBlock() == GCBlocks.AIR_LOCK_SEAL)
                            {
                                sealedSide = true;
                                break;
                            }
                        }
                    }
                    if (sealedSide)
                        break;
                }
                // Now replace the airlock blocks with either air, or sealed air
                for (x = this.protocol.minX + 1; x <= this.protocol.maxX - 1; x++)
                {
                    for (z = this.protocol.minZ + 1; z <= this.protocol.maxZ - 1; z++)
                    {
                        pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).getBlock() == GCBlocks.AIR_LOCK_SEAL)
                        {
                            if (sealedSide)
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            else
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        } else
        {
            if (this.lastProtocol.minX != this.lastProtocol.maxX)
            {
                // First test if there is sealed air to either side
                for (x = this.lastProtocol.minX + 1; x <= this.lastProtocol.maxX - 1; x++)
                {
                    for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; y++)
                    {
                        pos = new BlockPos(x, y, z);
                        breathable = this.getLevel().isBreathable(pos.north());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                            {
                                sealedSide = true;
                                break;
                            }
                            continue;
                        }
                        breathable = this.getLevel().isBreathable(pos.south());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                            {
                                sealedSide = true;
                                break;
                            }
                        }
                    }
                    if (sealedSide)
                        break;
                }
                // Now replace the airlock blocks with either air, or sealed air
                for (x = this.lastProtocol.minX + 1; x <= this.lastProtocol.maxX - 1; x++)
                {
                    for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; y++)
                    {
                        pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                        {
                            if (sealedSide)
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            else
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            } else if (this.lastProtocol.minZ != this.lastProtocol.maxZ)
            {
                // First test if there is sealed air to either side
                for (z = this.lastProtocol.minZ + 1; z <= this.lastProtocol.maxZ - 1; z++)
                {
                    for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; y++)
                    {
                        pos = new BlockPos(x, y, z);
                        breathable = this.getLevel().isBreathable(pos.west());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                            {
                                sealedSide = true;
                                break;
                            }
                            continue;
                        }
                        breathable = this.getLevel().isBreathable(pos.east());
                        if (breathable)
                        {
                            if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                            {
                                sealedSide = true;
                                break;
                            }
                        }
                    }
                    if (sealedSide)
                        break;
                }
                // Now replace the airlock blocks with either air, or sealed air
                for (z = this.lastProtocol.minZ + 1; z <= this.lastProtocol.maxZ - 1; z++)
                {
                    for (y = this.lastProtocol.minY + 1; y <= this.lastProtocol.maxY - 1; y++)
                    {
                        pos = new BlockPos(x, y, z);
                        if (this.getLevel().getBlockState(pos).is(GCBlocks.AIR_LOCK_SEAL))
                        {
                            if (sealedSide)
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            else
                                this.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(ownerName + "'s Air Lock Controller");
    }

    @Override
    public AirlockControllerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AirlockControllerMenu(syncId, inventory);
    }
}
