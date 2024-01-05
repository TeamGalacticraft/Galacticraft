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

package dev.galacticraft.impl.internal.mixin.client;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.accessor.SoundSystemAccessor;
import dev.galacticraft.impl.client.accessor.ClientResearchAccessor;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin implements ClientResearchAccessor, GearInventoryProvider {
    @Unique private final List<ResourceLocation> unlockedResearch = new ArrayList<>();
    @Shadow @Final public ClientLevel clientLevel;

    private final @Unique SimpleContainer gearInv = galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, 4, 5);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, 0, 1, 2, 3);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, 6, 7, 8, 9, 10, 11);

    @Unique
    private SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new GearInventory();
        inv.addListener((inventory) -> {
            float pressure = CelestialBody.getByDimension(this.clientLevel).map(body -> body.atmosphere().pressure()).orElse(1.0f);
            if (pressure != 1.0f) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (stack.getItem() instanceof Accessory accessory && accessory.enablesHearing()) {
                        ((SoundSystemAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundSystem())
                                .galacticraft$updateAtmosphericVolumeMultiplier(1.0f);
                        return;
                    } else {
                        ((SoundSystemAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundSystem())
                                .galacticraft$updateAtmosphericVolumeMultiplier(pressure);
                    }
                }
            } else {
                ((SoundSystemAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundSystem()).galacticraft$updateAtmosphericVolumeMultiplier(pressure);
            }
        });
        return inv;
    }

    @Override
    public void readChanges(FriendlyByteBuf buf) {
        byte size = buf.readByte();

        for (byte i = 0; i < size; i++) {
            if (buf.readBoolean()) {
                this.unlockedResearch.add(new ResourceLocation(buf.readUtf()));
            } else {
                this.unlockedResearch.remove(new ResourceLocation(buf.readUtf()));
            }
        }
    }

    @Override
    public boolean hasUnlockedResearch(ResourceLocation id) {
        return this.unlockedResearch.contains(id);
    }

    @Override
    public SimpleContainer galacticraft$getGearInv() {
        return this.gearInv;
    }

    @Override
    public Container galacticraft$getOxygenTanks() {
        return this.tankInv;
    }

    @Override
    public Container galacticraft$getThermalArmor() {
        return this.thermalArmorInv;
    }

    @Override
    public Container galacticraft$getAccessories() {
        return this.accessoryInv;
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.galacticraft$getGearInv().createTag());
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.galacticraft$getGearInv().fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND));
    }
}
