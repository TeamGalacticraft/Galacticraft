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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.impl.network.c2s.TeamNamePayload;
import dev.galacticraft.mod.client.gui.widget.ColorSlider;
import dev.galacticraft.mod.client.gui.widget.CustomizeFlagButton;
import dev.galacticraft.mod.client.gui.widget.TeamColorButton;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

final class SpaceRaceScreenMenus {
    private SpaceRaceScreenMenus() {
    }

    static void showMainMenu(SpaceRaceScreen screen) {
        clearSectionButtons(screen);

        screen.addButton(Component.translatable(Translations.SpaceRace.EXIT), screen.getLeft() + 5, screen.getTop() + 5, 40, 14, button -> screen.onClose());
        screen.addButton(Component.translatable(Translations.SpaceRace.ADD_PLAYERS), screen.getLeft() + 10, screen.getBottom() - 85, 100, 30, button -> screen.setMenu(SpaceRaceMenu.ADD_PLAYERS));
        screen.addButton(Component.translatable(Translations.SpaceRace.REMOVE_PLAYERS), screen.getLeft() + 10, screen.getBottom() - 45, 100, 30, button -> screen.setMenu(SpaceRaceMenu.REMOVE_PLAYERS));
        screen.addButton(Component.translatable(Translations.SpaceRace.SERVER_STATS), screen.getRight() - 100 - 10, screen.getBottom() - 85, 100, 30, button -> screen.setMenu(SpaceRaceMenu.SERVER_STATS));
        screen.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_STATS), screen.getRight() - 100 - 10, screen.getBottom() - 45, 100, 30, button -> screen.setMenu(SpaceRaceMenu.GLOBAL_STATS));

        int flagButtonWidth = 96;
        int flagButtonHeight = 64;
        int flagButtonX = screen.width / 2 - flagButtonWidth / 2;
        int flagButtonY = screen.getTop() + 10;
        screen.addScreenWidget(new CustomizeFlagButton(flagButtonX, flagButtonY, flagButtonWidth, flagButtonHeight, screen.teamFlag, () -> screen.setMenu(SpaceRaceMenu.TEAM_FLAG)));
        screen.addScreenWidget(new TeamColorButton(flagButtonX + flagButtonWidth + 10, flagButtonY + flagButtonHeight / 2 - 45 / 2, 45, 45, () -> screen.teamColor, () -> screen.setMenu(SpaceRaceMenu.TEAM_COLOR)));
        screen.addScreenWidget(screen.teamNameInput = new EditBox(screen.fontRenderer(), screen.getLeft() + (screen.backgroundWidth / 2) - 64, flagButtonY + 70, 128, 15, screen.teamNameInput, Component.empty()) {
            private String prevText;

            @Override
            public void setFocused(boolean focused) {
                if (this.isFocused() != focused) {
                    if (focused) {
                        this.prevText = this.getValue();
                    } else if (this.prevText == null || !this.prevText.equals(this.getValue())) {
                        ClientPlayNetworking.send(new TeamNamePayload(this.getValue()));
                    }
                }
                super.setFocused(focused);
            }
        });
    }

    static void showAddPlayersMenu(SpaceRaceScreen screen) {
        clearSectionButtons(screen);
        addBackButton(screen);
    }

    static void showRemovePlayersMenu(SpaceRaceScreen screen) {
        clearSectionButtons(screen);
        addBackButton(screen);
    }

    static void showTeamColorMenu(SpaceRaceScreen screen) {
        clearSectionButtons(screen);
        addBackButton(screen);

        screen.addScreenRenderable((graphics, mouseX, mouseY, delta) -> graphics.fill(screen.width / 2 - 50, screen.getTop() + 10, screen.width / 2 + 50, screen.getTop() + 110, screen.teamColor));

        int sliderWidth = 200;
        int sliderX = screen.width / 2 - sliderWidth / 2;
        screen.addScreenWidget(new ColorSlider(sliderX, screen.getBottom() - 80, sliderWidth, 20, Component.translatable(Translations.SpaceRace.RED), FastColor.ARGB32.red(screen.teamColor), value -> screen.teamColor = (screen.teamColor & 0xFF00FFFF) + (value << 16)));
        screen.addScreenWidget(new ColorSlider(sliderX, screen.getBottom() - 55, sliderWidth, 20, Component.translatable(Translations.SpaceRace.GREEN), FastColor.ARGB32.green(screen.teamColor), value -> screen.teamColor = (screen.teamColor & 0xFFFF00FF) + (value << 8)));
        screen.addScreenWidget(new ColorSlider(sliderX, screen.getBottom() - 30, sliderWidth, 20, Component.translatable(Translations.SpaceRace.BLUE), FastColor.ARGB32.blue(screen.teamColor), value -> screen.teamColor = (screen.teamColor & 0xFFFFFF00) + value));
    }

    static void showTeamFlagMenu(SpaceRaceScreen screen) {
        clearSectionButtons(screen);
        addBackButton(screen);
        screen.addScreenRenderable((graphics, mouseX, mouseY, delta) -> graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.DRAG_AND_DROP_FLAG), screen.width / 2, screen.height / 2 - screen.fontRenderer().lineHeight / 2, 0xFFFFFFFF));
    }

    static void addBackButton(SpaceRaceScreen screen) {
        screen.addButton(Component.translatable(Translations.SpaceRace.BACK), screen.getLeft() + 5, screen.getTop() + 5, 40, 14, button -> screen.setMenu(SpaceRaceMenu.MAIN));
    }

    static void clearSectionButtons(SpaceRaceScreen screen) {
        screen.serverVisibilityButton = null;
        screen.serverHideAdvancementsButton = null;
        screen.serverPlayerTabButton = null;
        screen.serverTeamTabButton = null;
        screen.serverTreeTabButton = null;
        screen.globalVisibilityButton = null;
        screen.globalGeneralTabButton = null;
        screen.globalItemsTabButton = null;
        screen.globalMobsTabButton = null;
    }
}
