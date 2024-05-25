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

package dev.galacticraft.mod.client;

import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class GCKeyBinds {
    public static final KeyMapping OPEN_CELESTIAL_SCREEN = new KeyMapping(Translations.Keybindings.OPEN_CELESTIAL_SCREEN, GLFW.GLFW_KEY_M, KeyMapping.CATEGORY_MISC);
    public static final KeyMapping OPEN_ROCKET_INVENTORY = new KeyMapping(Translations.Keybindings.ROCKET_INVENTORY, GLFW.GLFW_KEY_F, KeyMapping.CATEGORY_INVENTORY);

    public static void register() {
        KeyBindingHelper.registerKeyBinding(OPEN_CELESTIAL_SCREEN);
        KeyBindingHelper.registerKeyBinding(OPEN_ROCKET_INVENTORY);
    }

    public static void handleKeybinds(Minecraft client) {
        if (client.level != null && client.player != null) {
            if (client.screen == null) {
                while (OPEN_CELESTIAL_SCREEN.consumeClick()) {
                    client.setScreen(new CelestialSelectionScreen(true, null, false, null));
                }
            }
        }
    }
}
