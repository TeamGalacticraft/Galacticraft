/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin.client;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.api.registry.RegistryUtil;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.accessor.SoundSystemAccessor;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements GearInventoryProvider {
    @Shadow @Final protected MinecraftClient client;
    private final @Unique FullFixedItemInv gearInv = createInv();

    private FullFixedItemInv createInv() {
        FullFixedItemInv inv = new FullFixedItemInv(12);
        inv.setOwnerListener((invView, slot, prev, current) -> {
            if (current.getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem).gc_updateAtmosphericMultiplier(1.0f);
            } else if (prev.getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                boolean hasFreqModule = false;
                for (int i = 0; i < invView.getSlotCount(); i++) {
                    if (i == slot) continue;
                    if (invView.getInvStack(i).getItem() == GalacticraftItem.FREQUENCY_MODULE) {
                        ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem).gc_updateAtmosphericMultiplier(1.0f);
                        hasFreqModule = true;
                        break;
                    }
                }
                if (!hasFreqModule) {
                    ((SoundSystemAccessor) MinecraftClient.getInstance().getSoundManager().soundSystem)
                            .gc_updateAtmosphericMultiplier(RegistryUtil.getCelestialBodyByDimension(this.client.world)
                                    .map(body -> body.type().atmosphere(body.config()).pressure()).orElse(1.0f));
                }
            }
        });
        return inv;
    }

    @Override
    public FullFixedItemInv getGearInv() {
        return this.gearInv;
    }

    @Override
    public NbtCompound writeGearToNbt(NbtCompound tag) {
        return this.getGearInv().toTag(tag);
    }

    @Override
    public void readGearFromNbt(NbtCompound tag) {
        this.getGearInv().fromTag(tag);
    }
}
