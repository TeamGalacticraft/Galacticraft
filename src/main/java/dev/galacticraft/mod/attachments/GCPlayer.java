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

package dev.galacticraft.mod.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.rocket.RocketData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class GCPlayer {
    private RocketData rocketData;
    public NonNullList<ItemStack> stacks = NonNullList.withSize(2, ItemStack.EMPTY);
    public long fuel;
    private boolean buildingHyperloop = false;
    private BlockPos startLinkPos = null;

    public static final Codec<GCPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketData.CODEC.optionalFieldOf("rocket_data").forGetter(o -> Optional.ofNullable(o.rocketData)),
            Codec.LONG.fieldOf("fuel").forGetter(GCPlayer::getFuel),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("rocket_stacks").forGetter(GCPlayer::getRocketStacks)
    ).apply(instance, (data, fuel, stacks) -> new GCPlayer(data.orElse(null), fuel, stacks)));

    public static GCPlayer get(Player player) {
        return player.getAttachedOrCreate(GCAttachments.PLAYER, GCPlayer::new);
    }

    public GCPlayer() {}

    public GCPlayer(RocketData data, long fuel, List<ItemStack> stacks) {
        this.rocketData = data;
        this.fuel = fuel;
        this.stacks = NonNullList.of(ItemStack.EMPTY, stacks.toArray(ItemStack[]::new));
    }

    public RocketData getRocketData() {
        return rocketData;
    }

    public void setRocketData(RocketData rocketData) {
        this.rocketData = rocketData;
    }

    public NonNullList<ItemStack> getRocketStacks() {
        return this.stacks;
    }

    public void setRocketStacks(NonNullList<ItemStack> rocketStacks) {
        this.stacks = rocketStacks;
    }

    public long getFuel() {
        return fuel;
    }

    public void setFuel(long fuel) {
        this.fuel = fuel;
    }

    public void setRocketItem(ItemStack rocketItem) {
        for (int stack = 0; stack < getRocketStacks().size(); stack++)
            if (getRocketStacks().get(stack).isEmpty())
                if (stack == getRocketStacks().size() - 1)
                        getRocketStacks().set(stack, rocketItem);
    }

    public void setLaunchpadStack(ItemStack launchpad) {
        for (int stack = 0; stack < getRocketStacks().size(); stack++)
            if (getRocketStacks().get(stack).isEmpty())
                if (stack == getRocketStacks().size() - 2)
                    getRocketStacks().set(stack, launchpad == null ? ItemStack.EMPTY : launchpad);
    }

    public boolean isBuildingHyperloop() {
        return buildingHyperloop;
    }

    public void setBuildingHyperloop(boolean buildingHyperloop) {
        this.buildingHyperloop = buildingHyperloop;
    }

    public BlockPos getStartLinkPos() {
        return startLinkPos;
    }

    public void setStartLinkPos(BlockPos startLinkPos) {
        this.startLinkPos = startLinkPos;
    }
}
