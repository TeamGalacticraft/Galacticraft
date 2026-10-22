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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config.DefaultCelestialTeleporterConfig;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.content.GCCelestialBodies;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.teleporters.OverworldCelestialTeleporterType;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ParachuteTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void parachuteOpensOnReentryTest(GameTestHelper helper) {
        ServerPlayer player = this.reenter(helper, true);
        helper.assertTrue(player.galacticraft$isParachuting(), "Expected the parachute to open on re-entry");
        for (int i = 0; i < 5; i++) {
            player.doTick();
        }
        helper.assertTrue(player.galacticraft$isParachuting(), "Expected the parachute to stay open while falling");
        player.galacticraft$getGearInv().clearContent();
        player.doTick();
        helper.assertFalse(player.galacticraft$isParachuting(), "Expected the parachute to close when taken off");
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void noParachuteWithoutItemTest(GameTestHelper helper) {
        ServerPlayer player = this.reenter(helper, false);
        helper.assertFalse(player.galacticraft$isParachuting(), "Expected no parachute without one equipped");
        helper.succeed();
    }

    private ServerPlayer reenter(GameTestHelper helper, boolean wearParachute) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        if (wearParachute) {
            player.galacticraft$getGearInv().setItem(GCAccessorySlots.ACCESSORY_SLOT_START, new ItemStack(GCItems.PARACHUTE));
        }
        CelestialBody<?, ?> earth = helper.getLevel().registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).getOrThrow(GCCelestialBodies.EARTH);
        OverworldCelestialTeleporterType.INSTANCE.onEnterAtmosphere(helper.getLevel(), player, earth, earth, DefaultCelestialTeleporterConfig.INSTANCE);
        return player;
    }
}
