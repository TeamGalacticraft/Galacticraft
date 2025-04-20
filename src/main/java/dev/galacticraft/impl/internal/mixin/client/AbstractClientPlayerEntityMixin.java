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

package dev.galacticraft.impl.internal.mixin.client;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.impl.accessor.SoundSystemAccessor;
import dev.galacticraft.impl.client.accessor.ClientResearchAccessor;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin implements ClientResearchAccessor, GearInventoryProvider {
    @Shadow
    @Final
    public ClientLevel clientLevel;

    @Shadow
    public abstract boolean isCreative();

    @Unique
    private final Set<ResourceLocation> unlockedResearch = new HashSet<>();

    private final @Unique SimpleContainer gearInv = galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, 6, 7);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, 0, 1, 2, 3);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, 4, 5, 8, 9, 10, 11);

    @Unique
    private SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new GearInventory();
        inv.addListener((inventory) -> {
            Holder<CelestialBody<?, ?>> holder = this.clientLevel.galacticraft$getCelestialBody();
            float volume = holder != null ? holder.value().atmosphere().pressure() : 1.0F;
            if (volume != 1.0F) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (stack.is(GCTags.FREQUENCY_MODULES) || (stack.getItem() instanceof Accessory accessory && accessory.enablesHearing())) {
                        volume = 1.0F;
                        break;
                    }
                }
            }
            
            ((SoundSystemAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundSystem()).galacticraft$updateAtmosphericVolumeMultiplier(volume);
        });
        return inv;
    }

    @Override
    public boolean galacticraft$isUnlocked(ResourceLocation id) {
        if (this.isCreative()) return true;
        return this.unlockedResearch.contains(id);
    }

    @Override
    public void galacticraft$updateResearch(boolean add, List<ResourceLocation> ids) {
        for (ResourceLocation id : ids) {
            if (add) {
                this.unlockedResearch.add(id);
            } else {
                this.unlockedResearch.remove(id);
            }
        }
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
        tag.put(Constant.Nbt.GEAR_INV, this.galacticraft$getGearInv().createTag(((Entity) (Object) this).registryAccess()));
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.galacticraft$getGearInv().fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), ((Entity) (Object) this).registryAccess());
    }
}
