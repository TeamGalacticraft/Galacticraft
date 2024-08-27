package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class FloodSearch {

    @Nullable
    public static Region Flood(BlockPos startPosition, ServerLevel world, BlockCheck checker, SealerGroupings initialSealerGroup)
    {
        Queue<BlockPos> outsideBlocks = new LinkedList<>();
        Queue<BlockPos> insideBlocks = new LinkedList<>();
        Queue<BlockPos> otherSealersInsideBlocks = new LinkedList<>();
        //checks if start position is solid
        if (!checker.checkSolid(startPosition, world))
        {
            //start position is not solid
            //checks if start position has any sealer group attached
            SealerGroupings startingPositionSealerGroup = checker.checkCalculated(startPosition, world);
            if (startingPositionSealerGroup != null)
            {
                //there is a sealer group attached to the block
                //make sure the current sealer group for the area does not contain the starting positions sealer group
                if (!initialSealerGroup.sealers.containsAll(startingPositionSealerGroup.sealers))
                {
                    //the initial starting sealers do not contain the starting positions sealers
                    //add them to the current floods sealer group
                    initialSealerGroup.add(startingPositionSealerGroup.sealers);
                    //the floods outside blocks is appended to by the leftover outside blocks of the other sealer group
                    outsideBlocks.addAll(startingPositionSealerGroup.getUncalculatedArea());
                    otherSealersInsideBlocks.addAll(startingPositionSealerGroup.getCalculatedArea());
                }
            } else {
                //there is no sealer group attached to the block
                //add the current start position to the inside blocks
                insideBlocks.add(startPosition);
                //iterate over all 6 directions
                for (Direction direction : Direction.values()) {
                    //check if relative direction block has a sealer group attached to it
                    SealerGroupings relativeSealerGroupings = checker.checkCalculated(startPosition.relative(direction), world);
                    if (relativeSealerGroupings != null)
                    {
                        //relative block has a sealer group attached to it
                        //make sure the current sealer group for the area does not contain the relative positions sealer group
                        if (!initialSealerGroup.sealers.containsAll(relativeSealerGroupings.sealers))
                        {
                            //the initial starting sealers do not contain the starting positions sealers
                            //add them to the current floods sealer group
                            initialSealerGroup.add(relativeSealerGroupings.sealers);
                            //the floods outside blocks is appended to by the leftover outside blocks of the other sealer group
                            outsideBlocks.addAll(relativeSealerGroupings.getUncalculatedArea());
                            otherSealersInsideBlocks.addAll(relativeSealerGroupings.getCalculatedArea());
                        }
                    } else {
                        //relative block does not have a sealer group attached to it
                        //check if relative block is solid or not
                        if (checker.checkSolid(startPosition.relative(direction), world))
                        {
                            //block in relative direction is solid so do nothing
                        }else {
                            //block in relative direction is not solid so check if its been calculated already
                            if (!insideBlocks.contains(startPosition.relative(direction)) && !outsideBlocks.contains(startPosition.relative(direction)) && !otherSealersInsideBlocks.contains(startPosition.relative(direction))) {
                                outsideBlocks.add(startPosition.relative(direction));
                            }
                        }
                    }
                }
            }
        } else
        {
            //start position blocked
            return null;
        }

        //make sure that the max sealing power isnt being exceeded already
        if (insideBlocks.size() + outsideBlocks.size() + otherSealersInsideBlocks.size() > initialSealerGroup.sealingPower)
        {
            insideBlocks.remove();
            for (Direction direction : Direction.values())
            {
                if (otherSealersInsideBlocks.contains(startPosition.relative(direction)))
                {
                    outsideBlocks.add(startPosition.relative(direction));
                    otherSealersInsideBlocks.remove(startPosition.relative(direction));
                }
            }
        }

        //while the outside blocks queue is not empty and the inside blocks queue + the outside blocks queue is smaller than the sealing power of the sealer manager
        while (!outsideBlocks.isEmpty() && insideBlocks.size() + outsideBlocks.size() + otherSealersInsideBlocks.size() < initialSealerGroup.sealingPower)
        {
            //poll the first most position from the outside block queue
            BlockPos position = outsideBlocks.poll();
            //add the current position to the inside blocks
            insideBlocks.add(position);
            //iterate over all 6 directions
            for (Direction direction : Direction.values()) {
                //check if relative direction block has a sealer group attached to it
                SealerGroupings relativeSealerGroupings = checker.checkCalculated(position.relative(direction), world);
                if (relativeSealerGroupings != null)
                {
                    //relative block has a sealer group attached to it
                    //make sure the current sealer group for the area does not contain the relative positions sealer group
                    if (!initialSealerGroup.sealers.containsAll(relativeSealerGroupings.sealers))
                    {
                        //the initial starting sealers do not contain the starting positions sealers
                        //add them to the current floods sealer group
                        initialSealerGroup.add(relativeSealerGroupings.sealers);
                        //the floods outside blocks is appended to by the leftover outside blocks of the other sealer group
                        outsideBlocks.addAll(relativeSealerGroupings.getUncalculatedArea());
                        otherSealersInsideBlocks.addAll(relativeSealerGroupings.getCalculatedArea());
                    }
                } else {
                    //relative block does not have a sealer group attached to it
                    //check if relative block is solid or not
                    if (checker.checkSolid(position.relative(direction), world))
                    {
                        //block in relative direction is solid so do nothing
                    }else {
                        //block in relative direction is not solid so check if its been calculated already
                        if (!insideBlocks.contains(position.relative(direction)) && !outsideBlocks.contains(position.relative(direction)) && !otherSealersInsideBlocks.contains(position.relative(direction))) {
                            outsideBlocks.add(position.relative(direction));
                        }
                    }
                }
            }
        }
        //sets total blocks for reference
        initialSealerGroup.setTotalSealedBlocks(insideBlocks.size() + otherSealersInsideBlocks.size());
        initialSealerGroup.setTotalOutsideBlocks(outsideBlocks.size());

        //sets sealer groups calculated blocks and uncalculated blocks
        initialSealerGroup.setCalculatedArea(new HashSet<>(insideBlocks));
        initialSealerGroup.addCalculatedArea(new HashSet<>(otherSealersInsideBlocks));
        initialSealerGroup.setUncalculatedArea(new HashSet<>(outsideBlocks));

        //sets sealed based of if outside blocks is empty or not
        initialSealerGroup.setSealed(outsideBlocks.isEmpty());

        //returns
        return new Region(initialSealerGroup.getCalculatedArea(), outsideBlocks, outsideBlocks.isEmpty(), initialSealerGroup);
    }
}
