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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.client.GCKeyBinds;
import dev.galacticraft.mod.client.gui.overlay.CountdownOverlay;
import dev.galacticraft.mod.client.gui.overlay.LanderOverlay;
import dev.galacticraft.mod.client.gui.overlay.OxygenOverlay;
import dev.galacticraft.mod.client.gui.overlay.RocketOverlay;
import dev.galacticraft.mod.client.render.FootprintRenderer;
import dev.galacticraft.mod.client.sounds.RocketSound;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class ClientEventHandler {
    public static void init() {
        RocketEvents.STAGE_CHANGED.register(ClientEventHandler::rocketLaunchSound);
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandler::clientTick);
        WorldRenderEvents.LAST.register(FootprintRenderer::renderFootprints);
        ClientTickEvents.END_WORLD_TICK.register(ClientEventHandler::tickFootprints);
        HudRenderCallback.EVENT.register(OxygenOverlay::onHudRender);
        HudRenderCallback.EVENT.register(RocketOverlay::onHudRender);
        HudRenderCallback.EVENT.register(LanderOverlay::onRenderHud);
        HudRenderCallback.EVENT.register(CountdownOverlay::renderCountdown);
    }

    public static void rocketLaunchSound(Rocket rocket, LaunchStage oldStage) {
        if (rocket instanceof RocketEntity rocketEntity && rocket.getLaunchStage() == LaunchStage.IGNITED)
            Minecraft.getInstance().getSoundManager().play(new RocketSound(rocketEntity));
    }

    public static void clientTick(Minecraft client) {
        LanderOverlay.clientTick();
        GCKeyBinds.handleKeybinds(client);
    }

    public static void tickFootprints(ClientLevel level) {
        FootprintManager footprintManager = level.galacticraft$getFootprintManager();
        footprintManager.getFootprints().forEach((packedPos, footprints) -> {
            footprintManager.tick(level, packedPos);
        });
    }
}