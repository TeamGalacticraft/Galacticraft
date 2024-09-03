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

package dev.galacticraft.mod.machine;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.*;

public class SealerManager {
    public static final SealerManager INSTANCE = new SealerManager();

    private static HashMap<DimensionType, Map<BlockPos, SealerGroupings>> insideBlocks = new HashMap<>();
    private static HashMap<DimensionType, Map<BlockPos, SealerGroupings>> outsideBlocks = new HashMap<>();

    private SealerManager() {}

    static String RESET = "\u001B[0m";
    static String RED = "\u001B[31m";
    static String GREEN = "\u001B[32m";
    static String YELLOW = "\u001B[33m";


    public void onBlockChange(BlockPos pos, BlockState newState, ServerLevel world) {
        //creates a new solid check
        BlockCheck solidCheck = new SolidCheck();

        //gets the dimension type of the world
        DimensionType dimensionType = world.dimensionType();

        //gets the inside blocks from the dimension
        Map<BlockPos, SealerGroupings> localInsideBlocks = insideBlocks.get(dimensionType);
        //gets the outside blocks from the dimension
        Map<BlockPos, SealerGroupings> localOutsideBlocks = outsideBlocks.get(dimensionType);
        //checks if local inside blocks is null
        if (localInsideBlocks != null) {
            //checks for if the block is in a calculated area
            if (localInsideBlocks.containsKey(pos)) {
                //checks if the new state of the changed block is solid
                if (!solidCheck.checkSolid(pos, world)) {
                    //because block did not become air blocking block leave as is
                    System.out.println( YELLOW + "Block changed from non solid to non solid");
                    return;
                } else {
                    //check all sealers in the positions sealer group
                    for (OxygenSealerBlockEntity sealerBlockEntity : insideBlocks.get(dimensionType).get(pos).sealers)
                    {
                        //if sealer block is below placed block then remove sealer block from sealer group
                        if (sealerBlockEntity.getBlockPos().equals(pos.below()))
                        {
                            System.out.println( RED + "Block placed on sealer. Removing sealer from sealer group");
                            insideBlocks.get(dimensionType).get(pos).sealers.remove(sealerBlockEntity);
                            sealerBlockEntity.setBlocked(true);
                            //fixme: Recheck getSealed area smaller than sealers max sealing power
                        } else {
                            System.out.println("2");
                        }
                    }
                    //block placed is solid therefore should be removed from inside blocks
                    //change amount of total sealed blocks by 1
                    insideBlocks.get(dimensionType).get(pos).changeTotalSealedBlocks(-1);
                    insideBlocks.get(dimensionType).get(pos).removeCalculatedArea(pos);
                    //remove block from inside blocks
                    removeBlockFrom(pos, world, insideBlocks);
                    return;
                    //fixme: Check if block has blocked an unreachable room
                }
            } else if (positionAdjacent(pos, localInsideBlocks))
            {
                //block is adjacent to a local inside block but is not a part of inside blocks making it a wall block that was broken if its not solid
                if (!solidCheck.checkSolid(pos, world)) {
                    //block was broken
                    System.out.println(YELLOW + "Block broken adjacent to inside block at position: " + pos);
                    //do a flood search out from the broken block to check for empty gaps
                    Region region = FloodSearch.Flood(pos, world, solidCheck, new SealerGroupings());
                    if (region == null) {
                        System.out.println(RED + "SEALER ERROR CHECK CODE!!!!");
                    } else {
                        //gets the sealer grouping
                        SealerGroupings sealerGroupings = region.getSealerGroups();
                        addBlocksTo(sealerGroupings.calculatedArea, sealerGroupings, world, insideBlocks);
                        addBlocksTo(sealerGroupings.uncalculatedArea, sealerGroupings, world, outsideBlocks);
                        //for each sealer block entity get sealed area above and set its groupings
                        for (OxygenSealerBlockEntity sealerBlockEntity : sealerGroupings.sealers)
                        {
                            getInsideSealerGroupings(sealerBlockEntity.getBlockPos().above(), world.dimensionType()).set(sealerGroupings);
                        }
                        System.out.println(GREEN + "Replaced sealer grouping");
                    }
                } else {
                    //this should only occur when a block is placed adjacent to a wall position
                    System.out.println(GREEN + "Block placed adjacent to outside block at " + pos);
                    for (Direction direction : Direction.values())
                    {
                        BlockPos relativePos = pos.relative(direction);
                        if (localOutsideBlocks.containsKey(relativePos)) {
                            addBlockTo(relativePos, getOutsideSealerGroupings(relativePos, dimensionType), world, insideBlocks);
                            outsideBlocks.get(dimensionType).get(relativePos).changeTotalOutsideBlocks(-1);
                            outsideBlocks.get(dimensionType).get(relativePos).changeTotalSealedBlocks(1);
                            outsideBlocks.get(dimensionType).get(relativePos).removeUncalculatedArea(relativePos);
                            outsideBlocks.get(dimensionType).get(relativePos).addCalculatedArea(relativePos);
                            removeBlockFrom(relativePos, world, outsideBlocks);
                            //remove block from inside blocks
                        } else {
                            System.out.println("4");
                        }
                    }
                }
            } else {
                System.out.println("3");
            }
        } else {
            System.out.println("1");
        }
    }

    private boolean positionAdjacent(BlockPos pos, Map<BlockPos, SealerGroupings> array) {
        for (Direction direction : Direction.values())
        {
            if (array.containsKey(pos.relative(direction))) {
                return true;
            }
        }
        return false;
    }

    private boolean arrayContains(BlockPos pos, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        if (array.containsKey(world.dimensionType()))
        {
            return array.get(world.dimensionType()).containsKey(pos);
        }
        return false;
    }


    public static void addSealer(OxygenSealerBlockEntity sealer, ServerLevel world) {
        //gets the dimension type of the world
        DimensionType dimensionType = world.dimensionType();
        //gets the block pos above the sealer
        BlockPos aboveSealer = sealer.getBlockPos().above();
        //print to system that sealer is placed
        System.out.println(GREEN + "Sealer placed at " + RESET + sealer.getBlockPos() + GREEN + " in dimension " + RESET + dimensionType);
        //creates a new sealer group for flooding
        SealerGroupings sealerGroupings = new SealerGroupings();
        sealerGroupings.add(sealer);
        //performs a flood search
        Region region = FloodSearch.Flood(aboveSealer, world, new SolidCheck(), sealerGroupings);
        //makes sure region is not null
        if (region == null)
        {
            //region is null therefore the flood search was canceled due to starting position being blocked
            System.out.println(RED + "Sealer is blocked" + RESET);
            //set the sealer state to blocked
            sealer.setBlocked(true);
        } else
        {
            //region returned not null therefore the flood search returned with an area
            //gets the sealer group from the region
            SealerGroupings sealers = region.getSealerGroups();
            //prints to system to say the amount of inside blocks sealed
            System.out.println(YELLOW + "inside blocks " + RESET + sealers.totalSealedBlocks);
            //prints to system to say the amount of outside blocks sealed
            System.out.println(YELLOW + "outside blocks " + RESET + sealers.totalOutsideBlocks);
            //adds the outside and inside blocks to the sealer manager hashmaps
            addBlocksTo(region.getInsideBlocks(), sealers, world, insideBlocks);
            addBlocksTo(region.getOutsideBlocks(), sealers, world, outsideBlocks);
            //if the region is sealed then print sealable else not sealable
            if (region.isSealed())
            {
                System.out.println(GREEN + "Room calculated as sealable" + RESET);
            } else {
                System.out.println(RED + "Room calculated as unsealable" + RESET);
            }
        }
    }

    private static void addBlocksTo(Set<BlockPos> blocks, SealerGroupings sealerGroup, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        if (!array.containsKey(world.dimensionType())) {
            array.put(world.dimensionType(), new HashMap<>());
        }
        for (BlockPos pos : blocks) {
            array.get(world.dimensionType()).put(pos, sealerGroup);
        }
    }

    private void addBlockTo(BlockPos pos, SealerGroupings sealerGroup, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        if (array.containsKey(world.dimensionType())) {
            //dimension created
            array.get(world.dimensionType()).put(pos, sealerGroup);
        } else {
            //dimension hasn't been created
            array.put(world.dimensionType(), new HashMap<>());
            array.get(world.dimensionType()).put(pos, sealerGroup);
        }
    }

    private void removeBlockFrom(BlockPos pos, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        //dimension check to make sure it exists already
        if (array.containsKey(world.dimensionType())) {
            //dimension exists so remove entry
            array.get(world.dimensionType()).remove(pos);
        }
    }

    private void removeBlocksFrom(Set<BlockPos> blocks, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        if (array.containsKey(world.dimensionType())) {
            //dimension exists so remove entrys
            for (BlockPos pos : blocks) {
                //dimension check to make sure it exists already
                array.get(world.dimensionType()).remove(pos);
            }
        }
    }

    private SealerGroupings getSealersFrom(BlockPos pos, ServerLevel world, HashMap<DimensionType, Map<BlockPos, SealerGroupings>> array) {
        if (array.containsKey(world.dimensionType())) {
            return array.get(world.dimensionType()).get(pos);
        }
        return null;
    }

    public void removeSealer(OxygenSealerBlockEntity sealer, ServerLevel world) {
        DimensionType dimensionType = world.dimensionType();
        System.out.println(RED + "Sealer removed at " + sealer.getBlockPos() + " in dimension " + dimensionType);
    }

    public SealerGroupings getInsideSealerGroupings(BlockPos pos, DimensionType dimensionType)
    {
        if (insideBlocks.containsKey(dimensionType))
        {
            if (insideBlocks.get(dimensionType).containsKey(pos))
            {
                return insideBlocks.get(dimensionType).get(pos);
            }
        }
        return null;
    }

    public SealerGroupings getOutsideSealerGroupings(BlockPos pos, DimensionType dimensionType)
    {
        if (outsideBlocks.containsKey(dimensionType))
        {
            if (outsideBlocks.get(dimensionType).containsKey(pos))
            {
                return outsideBlocks.get(dimensionType).get(pos);
            }
        }
        return null;
    }




}
