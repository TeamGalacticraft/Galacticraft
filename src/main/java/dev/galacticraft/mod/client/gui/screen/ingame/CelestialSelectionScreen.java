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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.satellite.Satellite;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.Tiered;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.mod.client.util.Graphics;
import dev.galacticraft.mod.network.c2s.PlanetTeleportPayload;
import dev.galacticraft.mod.network.c2s.SatelliteCreationPayload;
import dev.galacticraft.mod.util.Translations;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

import static dev.galacticraft.mod.Constant.CelestialScreen.*;

@SuppressWarnings({"SpellCheckingInspection", "DataFlowIssue"})
@Environment(EnvType.CLIENT)
public class CelestialSelectionScreen extends CelestialScreen {
    protected int LHS = 0;
    protected int RHS = 0;
    protected int BOT = 0;

    public final boolean mapMode;
    private final @Nullable RocketData data;
    protected final CelestialBody<?, ?> fromBody;
    public final boolean canCreateStations;

    protected int canCreateOffset = 24;
    protected int zoomTooltipPos = 0;
    protected String selectedStationOwner = "";
    protected int spaceStationListOffset = 0;
    protected boolean renamingSpaceStation;
    protected String renamingString = "";

    public CelestialSelectionScreen(boolean mapMode, @Nullable RocketData data, boolean canCreateStations, CelestialBody<?, ?> fromBody) {
        super(Component.empty());
        this.mapMode = mapMode;
        this.data = data;
        this.canCreateStations = canCreateStations;
        this.fromBody = fromBody;
    }

    @Override
    public void init() {
        super.init();
        assert this.minecraft != null;

        this.LHS = this.borderSize + this.borderEdgeSize;
        this.RHS = this.width - this.LHS;
        this.BOT = this.height - this.LHS;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    protected String getGrandparentName() {
        CelestialBody<?, ?> body = this.selectedBody;
        if (body == null || body == celestialBodies.get(SOL)) {
            return I18n.get(Translations.Galaxy.MILKY_WAY); //fixme
        }
        if (body.parent().isPresent()) {
            if (body.parentValue(celestialBodies).parent().isPresent()) {
                return I18n.get(((TranslatableContents) body.parentValue(celestialBodies).parentValue(celestialBodies).name().getContents()).getKey());
            } else {
                return I18n.get(((TranslatableContents) body.galaxyValue(galaxies, celestialBodies).name().getContents()).getKey());
            }
        } else {
            return I18n.get(((TranslatableContents) body.galaxyValue(galaxies, celestialBodies).name().getContents()).getKey());
        }
    }

    protected String parentName() {
        if (this.selectedBody == null) return I18n.get(Translations.CelestialBody.SOL); //fixme
        if (this.selectedBody == celestialBodies.get(SOL))
            return I18n.get(Translations.CelestialBody.SOL);
        if (this.selectedBody.parent().isPresent())
            return I18n.get(((TranslatableContents) this.selectedBody.parentValue(celestialBodies).name().getContents()).getKey());
        return I18n.get(((TranslatableContents) this.selectedBody.galaxyValue(galaxies, celestialBodies).name().getContents()).getKey());
    }

    protected List<CelestialBody<?, ?>> getChildren(CelestialBody<?, ?> celestialBody) {
        if (celestialBody != null) {
            List<CelestialBody<?, ?>> list = celestialBodies.stream()
                    .filter(body -> !body.isSatellite() && body.parent().isPresent() && body.parentValue(celestialBodies) == celestialBody)
                    .collect(Collectors.toList());

            List<CelestialBody<SatelliteConfig, SatelliteType>> satellites = this.getVisibleSatellitesForCelestialBody(celestialBody);
            if (satellites.size() > 0) {
                list.add(satellites.get(0));
            }

            list.sort((o1, o2) -> Float.compare(o1.position().lineScale(), o2.position().lineScale()));
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (this.renamingSpaceStation) {
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (this.renamingString != null && !this.renamingString.isEmpty()) {
                    String toBeParsed = this.renamingString.substring(0, this.renamingString.length() - 1);

                    if (this.isValid(toBeParsed)) {
                        this.renamingString = toBeParsed;
//                        this.timeBackspacePressed = System.currentTimeMillis();
                    } else {
                        this.renamingString = "";
                    }
                }

                return true;
            } else if (Screen.isPaste(key)) {
                assert this.minecraft != null;
                String pastestring = this.minecraft.keyboardHandler.getClipboard();

                if (pastestring.isEmpty()) {
                    return false;
                }

                if (this.isValid(this.renamingString + pastestring)) {
                    this.renamingString = this.renamingString + pastestring;
                    this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), MAX_SPACE_STATION_NAME_LENGTH));
                }

                return true;
            }
        } else if (key == GLFW.GLFW_KEY_ENTER) {
            // Keyboard shortcut - teleport to dimension by pressing 'Enter'
            this.teleportToSelectedBody();
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (this.renamingSpaceStation && StringUtil.isAllowedChatCharacter(character)) {
            this.renamingString = this.renamingString + character;
            this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), MAX_SPACE_STATION_NAME_LENGTH));
            return true;
        }

        return super.charTyped(character, modifiers);
    }

    public boolean isValid(String string) {
        return !string.isEmpty() && StringUtil.isAllowedChatCharacter(string.charAt(string.length() - 1));
    }

    protected boolean canCreateSpaceStation(CelestialBody<?, ?> atBody) {
        if (!atBody.isOrbitable()) {
            return false;
        }
        SatelliteRecipe recipe = ((Orbitable) atBody.type()).satelliteRecipe(atBody.config());
        if (recipe == null) {
            return false;
        }
        if (this.mapMode/* || ConfigManagerCore.disableSpaceStationCreation.get()*/ || !this.canCreateStations) //todo SSconfig
        {
            return false;
        }

        if (this.data != null && !this.data.canTravel(manager, this.fromBody, atBody)) {
            // If parent body is unreachable, the satellite is also unreachable
            return false;
        }

        boolean foundSatellite = false;
        assert this.minecraft != null;
        assert this.minecraft.level != null;
        for (CelestialBody<SatelliteConfig, SatelliteType> type : ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values()) {
            if (type.parentValue(celestialBodies) == atBody) {
                assert this.minecraft.player != null;
                if (type.type().ownershipData(type.config()).owner().equals(this.minecraft.player.getUUID())) {
                    foundSatellite = true;
                    break;
                }
            }
        }

        return !foundSatellite;
    }

    @Override
    protected void unselectCelestialBody() {
        super.unselectCelestialBody();
        this.selectedStationOwner = "";
    }

    protected void teleportToSelectedBody() {
        assert !this.mapMode;
        if (this.selectedBody != null && this.selectedBody.type() instanceof Landable landable) {
            landable.world(this.selectedBody.config());
            if (this.data == null || this.data.canTravel(manager, this.fromBody, this.selectedBody)) {
                try {
                    assert this.minecraft != null;
                    String fromName;
                    if (this.selectedBody.isSatellite()) {
                        SatelliteConfig config = (SatelliteConfig) this.selectedBody.config();
                        ClientPlayNetworking.send(new PlanetTeleportPayload(config.getId()));

                        fromName = config.getCustomName();
                        if (fromName.length() == 0) {
                            fromName = Component.translatable(Translations.Ui.SPACE_STATION_NAME, config.getOwnershipData().username()).getString();
                        }
                    } else {
                        ClientPlayNetworking.send(new PlanetTeleportPayload(celestialBodies.getKey(this.selectedBody)));

                        fromName = I18n.get(((TranslatableContents) this.selectedBody.name().getContents()).getKey());
                    }
                    this.minecraft.setScreen(new SpaceTravelScreen(fromName, ((Landable) this.selectedBody.type()).world(this.selectedBody.config())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int activeButton, double dragX, double dragY) {
        return super.mouseDragged(x, y, activeButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean clickHandled = false;

        if (this.selectedBody != null && x > LHS && x < LHS + 88 && y > LHS && y < LHS + 13) {
            this.unselectCelestialBody();
            return true;
        }

        if (!this.mapMode) {
            if (x >= RHS - 95 && x < RHS && y > LHS + 181 + canCreateOffset && y < LHS + 182 + 12 + canCreateOffset) {
                if (this.selectedBody != null && this.selectedBody.type() instanceof Orbitable orbitable/* && this.selectedBody.getWorld() != null*/) {
                    SatelliteRecipe recipe = orbitable.satelliteRecipe(this.selectedBody.config());
                    if (recipe != null && this.canCreateSpaceStation(this.selectedBody)) {
                        assert this.minecraft != null;
                        assert this.minecraft.player != null;
                        if (recipe.test(this.minecraft.player.getInventory()) || this.minecraft.player.isCreative()) {
//                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_BIND_SPACE_STATION_ID, GCCoreUtil.getWorld(this.minecraft.level), new Object[]{this.selectedBody.getWorld()}));
                            SatelliteCreationPayload payload = new SatelliteCreationPayload(this.selectedBody.getKey(celestialBodies));
                            ClientPlayNetworking.send(payload);
                            //Zoom in on planet to show the new SpaceStation if not already zoomed
                            if (!this.isZoomed()) {
                                this.selectionState = EnumSelection.ZOOMED;
                                this.preSelectZoom = this.zoom;
                                this.preSelectPosition = this.position;
                                this.ticksSinceSelectionF = 0;
                                this.doneZooming = false;
                            }
                            return true;
                        }

                        clickHandled = true;
                    }
                }
            }
        }

        boolean a = x > RHS - 88 && x < RHS && y > LHS && y < LHS + 13;
        if (this.mapMode) {
            if (a) {
                assert this.minecraft != null;
                this.minecraft.setScreen(null);
                clickHandled = true;
            }
        }

        if (this.selectedBody != null && !this.mapMode) {
            if (a) {
                if (!(isSatellite(this.selectedBody)) || !this.selectedStationOwner.isEmpty()) {
                    this.teleportToSelectedBody();
                }
                clickHandled = true;
            }
        }

        if (isSatellite(this.selectedBody)) {
            if (this.renamingSpaceStation) {
                if (x >= width / 2f - 90 && x <= width / 2f + 90 && y >= this.height / 2f - 38 && y <= this.height / 2f + 38) {
                    // Apply
                    if (x >= width / 2f - 90 + 17 && x <= width / 2f - 90 + 17 + 72 && y >= this.height / 2f - 38 + 59 && y <= this.height / 2f - 38 + 59 + 12) {
                        assert this.minecraft != null;
                        assert this.minecraft.player != null;
                        String strName = this.minecraft.player.getName().getString();
//                        Integer spacestationID = this.spaceStationIDs.get(strName);
//                        if (spacestationID == null) spacestationID = this.spaceStationIDs.get(strName.toLowerCase());
                        CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
                        selectedSatellite.type().setCustomName(this.renamingString, selectedSatellite.config());
//                        RegistryKey<World> spacestationID = selectedSatellite.getWorld();
//                        this.spaceStationMap.get(getSatelliteParentID(selectedSatellite)).get(strName).setStationName(this.renamingString);
//	                    	this.spaceStationNames.put(strName, this.renamingString);
//                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_RENAME_SPACE_STATION, GCCoreUtil.getWorld(this.minecraft.level), new Object[]{this.renamingString, spacestationID})); //TODO SS ID PACKET
                        this.renamingSpaceStation = false;
                    }
                    // Cancel
                    if (x >= width / 2f && x <= width / 2f + 72 && y >= this.height / 2f - 38 + 59 && y <= this.height / 2f - 38 + 59 + 12) {
                        this.renamingSpaceStation = false;
                    }
                    clickHandled = true;
                }
            } else {
                if (x >= width / 2f - 47 && x <= width / 2f - 47 + 94 && y >= LHS && y <= LHS + 11) {
                    if (!this.selectedStationOwner.isEmpty()) {
                        assert this.minecraft != null;
                        if (this.selectedStationOwner.equalsIgnoreCase(this.minecraft.player.getName().getString())) {
                            this.renamingSpaceStation = true;
                            this.renamingString = null;
                            clickHandled = true;
                        }
                    }
                }

                CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
                assert this.minecraft != null;
                int stationListSize = ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().size();
                int max = Math.min((this.height / 2) / 14, stationListSize);

                int xPos;
                int yPos;

                // Up button
                xPos = RHS - 85;
                yPos = LHS + 45;

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (this.spaceStationListOffset > 0) {
                        this.spaceStationListOffset--;
                    }
                    clickHandled = true;
                }

                // Down button
                xPos = RHS - 85;
                yPos = LHS + 49 + max * 14;

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (max + spaceStationListOffset < stationListSize) {
                        this.spaceStationListOffset++;
                    }
                    clickHandled = true;
                }

                Iterator<CelestialBody<SatelliteConfig, SatelliteType>> it = ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().iterator();
                int i = 0;
                int j = 0;
                while (it.hasNext() && i < max) {
                    CelestialBody<SatelliteConfig, SatelliteType> satellite = it.next();
                    if (j >= this.spaceStationListOffset) {
                        int xOffset = 0;

                        if (satellite.type().ownershipData(satellite.config()).username().equalsIgnoreCase(this.selectedStationOwner)) {
                            xOffset -= 5;
                        }

                        xPos = RHS - 95 + xOffset;
                        yPos = LHS + 50 + i * 14;

                        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                            this.selectedStationOwner = satellite.type().ownershipData(satellite.config()).username();
                            clickHandled = true;
                        }
                        i++;
                    }
                    j++;
                }
            }
        }

        int xPos = LHS + 2;
        int yPos = LHS + 10;

        boolean planetZoomedMoon = this.isZoomed() && isPlanet(this.selectedParent);

        // Top yellow button e.g. Sol
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12 && this.selectedParent != null) {
            if (this.selectedBody == null) {
                this.preSelectZoom = this.zoom;
                this.preSelectPosition = this.position;
            }

            EnumSelection selectionCountOld = this.selectionState;

            if (this.isSelected()) {
                this.unselectCelestialBody();
            }

            if (selectionCountOld == EnumSelection.ZOOMED) {
                this.selectionState = EnumSelection.SELECTED;
            }

            this.selectedBody = this.selectedParent;
            this.ticksSinceSelectionF = 0;
            this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
            if (this.isZoomed() && !planetZoomedMoon) {
                this.ticksSinceMenuOpenF = 0;
            }
            clickHandled = true;
        }

        yPos += 22;

        // First blue button - normally the Selected Body (but it's the parent planet if this is a moon)
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
            if (planetZoomedMoon) {
                if (this.selectedBody == null) {
                    this.preSelectZoom = this.zoom;
                    this.preSelectPosition = this.position;
                }

                EnumSelection selectionCountOld = this.selectionState;
                if (this.isSelected()) {
                    this.unselectCelestialBody();
                }
                if (selectionCountOld == EnumSelection.ZOOMED) {
                    this.selectionState = EnumSelection.SELECTED;
                }

                this.selectedBody = this.selectedParent;
                this.ticksSinceSelectionF = 0;
                this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
            }
            clickHandled = true;
        }

        if (!clickHandled) {
            List<CelestialBody<?, ?>> children = this.getChildren(this.isZoomed() && !(isPlanet(this.selectedParent)) ? this.selectedBody : this.selectedParent);

            yPos = LHS + 50;
            for (CelestialBody<?, ?> child : children) {
                clickHandled = this.testClicked(child, child.equals(this.selectedBody) ? 5 : 0, yPos, x, y, false);
                yPos += 14;

                if (!clickHandled && !this.isZoomed() && child.equals(this.selectedBody)) {
                    List<CelestialBody<?, ?>> grandchildren = this.getChildren(child);
                    int gOffset = 0;
                    for (CelestialBody<?, ?> grandchild : grandchildren) {
                        if (gOffset + 14 > this.animateGrandchildren) {
                            break;
                        }
                        clickHandled = this.testClicked(grandchild, 10, yPos, x, y, true);
                        yPos += 14;
                        gOffset += 14;
                        if (clickHandled) {
                            break;
                        }
                    }
                    yPos += this.animateGrandchildren - gOffset;
                }

                if (clickHandled) {
                    break;
                }
            }
        }

        return clickHandled || super.mouseClicked(x, y, button);
    }

    protected boolean testClicked(CelestialBody<?, ?> body, int xOffset, int yPos, double x, double y, boolean grandchild) {
        int xPos = this.borderSize + this.borderEdgeSize + 2 + xOffset;
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
            if (this.selectedBody != body || !this.isZoomed()) {
                if (this.selectedBody == null) {
                    this.preSelectZoom = this.zoom;
                    this.preSelectPosition = this.position;
                }

                EnumSelection selectionCountOld = this.selectionState;

                if (selectionCountOld == EnumSelection.ZOOMED) {
                    this.selectionState = EnumSelection.SELECTED;
                }

                this.doneZooming = false;
                this.planetZoom = 0.0F;

                if (body != this.selectedBody) {
                    // Selecting a different body
                    this.lastSelectedBody = this.selectedBody;
                    this.selectionState = EnumSelection.SELECTED;
                } else {
                    // Selecting the same body e.g. double-clicking
                    this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];
                }

                this.selectedBody = body;
                this.ticksSinceSelectionF = 0;
                if (grandchild) {
                    this.selectionState = EnumSelection.ZOOMED;
                }
                if (this.isZoomed()) {
                    this.ticksSinceMenuOpenF = 0;
                }
                this.animateGrandchildren = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        this.drawButtons(graphics, mouseX, mouseY);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.mapMode;
    }

    public void drawButtons(GuiGraphics gui, int mouseX, int mouseY) {
        boolean handledSliderPos = false;
        String str;

        try (Graphics graphics = Graphics.managed(gui, this.font)) {
            try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
                // Catalog:
                texture.blit(LHS, LHS, 74, 11, CATALOG_U, CATALOG_V, CATALOG_WIDTH, CATALOG_HEIGHT, BLUE);
                str = I18n.get(Translations.CelestialSelection.CATALOG).toUpperCase();
                texture.drawCenteredText(str, LHS + 40, LHS + 1, WHITE);

                // Catalog wedge:
                texture.blit(LHS + 4, LHS, 83, 12, CATALOG_BACKING_U, CATALOG_BACKING_V, CATALOG_BACKING_WIDTH, CATALOG_BACKING_HEIGHT, BLUE);

                int scale = (int) Math.min(95, this.ticksSinceMenuOpenF * 12.0F);
                boolean planetZoomedNotMoon = this.isZoomed() && !(isGrandchildBody(this.selectedParent));

                // Parent frame:
                texture.blit(LHS - 95 + scale, LHS + 12, 95, 41, PARENT_LABEL_U, PARENT_LABEL_V, PARENT_LABEL_WIDTH, PARENT_LABEL_HEIGHT, BLUE);
                str = planetZoomedNotMoon ? I18n.get(((TranslatableContents) this.selectedBody.name().getContents()).getKey()) : this.parentName();
                texture.drawText(str, LHS + 9 - 95 + scale, LHS + 34, WHITE, false);

                // Grandparent frame:
                texture.blit(LHS + 2 - 95 + scale, LHS + 14, 93, 17, GRANDPARENT_LABEL_U, GRANDPARENT_LABEL_V, GRANDPARENT_LABEL_WIDTH, GRANDPARENT_LABEL_HEIGHT, YELLOW);
                if (this.isZoomed() && this.selectedBody == celestialBodies.get(SOL)) {
                    str = this.getGrandparentName();
                } else {
                    str = planetZoomedNotMoon ? this.parentName() : this.getGrandparentName();
                }
                texture.drawText(str, LHS + 7 - 95 + scale, LHS + 16, GREY3, false);

                List<CelestialBody<?, ?>> children = this.getChildren(/*planetZoomedNotMoon*/this.isZoomed() ? this.selectedBody : celestialBodies.get(SOL));
                this.drawChildButtons(texture, children, 0, 0, true);

                if (this.mapMode) {
                    texture.blit(RHS - 74, LHS, 74, 11, TOP_RIGHT_ACTION_BUTTON_U + TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_V, -TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_HEIGHT, RED);
                    str = I18n.get(Translations.CelestialSelection.EXIT).toUpperCase();
                    texture.drawText(str, RHS - 40 - font.width(str) / 2, LHS + 1, WHITE, false);
                }
            }


            if (this.selectedBody != null) {
                // Right-hand bar (basic selectionState info)

                if (isSatellite(this.selectedBody)) {
                    this.drawSpaceStationDetails(graphics);
                } else {
                    try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
                        texture.blit(RHS - 96, LHS, 96, 139, SIDE_PANEL_U, SIDE_PANEL_V, SIDE_PANEL_WIDTH, SIDE_PANEL_HEIGHT, BLUE);
                    }
                }

                this.drawSpaceStationCreationPrompt(gui, graphics, mouseX, mouseY);

                try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
                    // Top bar title:
                    int color = BLUE;
                    if (isSatellite(this.selectedBody)) {
                        if (this.selectedStationOwner.isEmpty() || !this.selectedStationOwner.equalsIgnoreCase(this.minecraft.player.getName().getString())) {
                            color = RED;
                        } else {
                            color = GREEN;
                        }
                    }
                    texture.blit(width / 2 - 47, LHS, 94, 11, TOPBAR_U, TOPBAR_V, TOPBAR_WIDTH, TOPBAR_HEIGHT, color);

                    if (this.selectedBody.type() instanceof Tiered tiered && tiered.accessWeight(this.selectedBody.config()) >= 0 && (!(isSatellite(this.selectedBody)))) {
                        boolean canReach;
                        if (this.data != null && !this.data.canTravel(manager, this.fromBody, this.selectedBody)) {
                            canReach = false;
                            color = RED;
                        } else {
                            canReach = true;
                            color = GREEN;
                        }
                        texture.blit(width / 2 - 30, LHS + 11, 30, 11, TOPBAR_U, TOPBAR_V, 30, TOPBAR_HEIGHT, color);
                        texture.blit(width / 2, LHS + 11, 30, 11, TOPBAR_U + TOPBAR_WIDTH - 30, TOPBAR_V, 30, TOPBAR_HEIGHT, color);
                        str = I18n.get(Translations.CelestialSelection.TIER, tiered.accessWeight(this.selectedBody.config()) == -1 ? "?" : tiered.accessWeight(this.selectedBody.config()));
                        texture.drawText(str, width / 2 - this.font.width(str) / 2, LHS + 13, canReach ? GREY4 : RED3, false);
                    }

                    str = I18n.get(((TranslatableContents) this.selectedBody.name().getContents()).getKey());

                    if (isSatellite(this.selectedBody)) {
                        str = I18n.get(Translations.CelestialSelection.RENAME).toUpperCase();
                    }

                    texture.drawText(str, width / 2 - this.font.width(str) / 2, LHS + 2, WHITE, false);

                    if (!this.mapMode) {
                        if (this.data != null && !this.data.canTravel(manager, this.fromBody, this.selectedBody) || !(this.selectedBody.type() instanceof Landable) || isSatellite(this.selectedBody) && !((Satellite) this.selectedBody.type()).ownershipData(this.selectedBody.config()).canAccess(this.minecraft.player)) {
                            color = RED;
                        } else {
                            color = GREEN;
                        }

                        texture.blit(RHS - 74, LHS, 74, 11, TOP_RIGHT_ACTION_BUTTON_U + TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_V, -TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_HEIGHT, color);
                        str = I18n.get(Translations.CelestialSelection.LAUNCH).toUpperCase();
                        texture.drawText(str, RHS - 40 - font.width(str) / 2, LHS + 2, WHITE, false);
                    }

                    if (this.selectionState == EnumSelection.SELECTED && !(isSatellite(this.selectedBody))) {
                        handledSliderPos = true;

                        int sliderPos = this.zoomTooltipPos;
                        if (zoomTooltipPos != 38) {
                            sliderPos = Math.min((int) this.ticksSinceSelectionF * 2, 38);
                            this.zoomTooltipPos = sliderPos;
                        }

                        texture.blit(RHS - 182, height - this.borderSize - this.borderEdgeSize - sliderPos, 83, 38, ZOOM_INFO_TAB_U + ZOOM_INFO_TAB_WIDTH, ZOOM_INFO_TAB_V, -ZOOM_INFO_TAB_WIDTH, ZOOM_INFO_TAB_HEIGHT, BLUE);

                        boolean flag0 = !getVisibleSatellitesForCelestialBody(this.selectedBody).isEmpty();
                        boolean flag1 = isPlanet(this.selectedBody) && !getChildren(this.selectedBody).isEmpty();
                        if (flag0 && flag1) {
                            texture.drawSplitText(I18n.get(Translations.CelestialSelection.CLICK_AGAIN_MOONS_AND_SATELLITES), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 2 - sliderPos, 79, GREY5);
                        } else if (!flag0 && flag1) {
                            texture.drawSplitText(I18n.get(Translations.CelestialSelection.CLICK_AGAIN_MOONS), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 6 - sliderPos, 79, GREY5);
                        } else if (flag0) {
                            texture.drawSplitText(I18n.get(Translations.CelestialSelection.CLICK_AGAIN_SATELLITES), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 6 - sliderPos, 79, GREY5);
                        } else {
                            texture.drawSplitText(I18n.get(Translations.CelestialSelection.CLICK_AGAIN), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 11 - sliderPos, 79, GREY5);
                        }
                    }
                }

                if (isSatellite(this.selectedBody) && renamingSpaceStation) {
                    try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION_1, 512, 512)) {
                        texture.blit(width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, BLUE);
                        texture.blit(width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, BLUE);
                        texture.blit(width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, BLUE);
                        texture.blit(width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159 + 72, 80, -72, 12, BLUE);
                        texture.blit(width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, BLUE);
                        str = I18n.get(Translations.CelestialSelection.ASSIGN_NAME);
                        texture.drawCenteredText(str, width / 2, this.height / 2 - 35, WHITE);
                        str = I18n.get(Translations.CelestialSelection.APPLY);
                        texture.drawCenteredText(str, width / 2 - 36, this.height / 2 + 23, WHITE);
                        str = I18n.get(Translations.CelestialSelection.CANCEL);
                        texture.drawCenteredText(str, width / 2 + 36, this.height / 2 + 23, WHITE);

                        if (this.renamingString == null) {
                            CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
                            String playerName = this.minecraft.player.getName().getString();
                            this.renamingString = selectedSatellite.type().getCustomName(selectedSatellite.config());
                        }

                        str = this.renamingString;
                        String str0 = this.renamingString;

                        if ((this.ticksSinceMenuOpenF / 10) % 2 == 0) {
                            str0 += "_";
                        }

                        texture.drawCenteredText(str0, width / 2, this.height / 2 - 17, WHITE, false);
                    }
                }
            }

            if (!handledSliderPos) {
                this.zoomTooltipPos = 0;
            }
        }
    }

    private void drawSpaceStationDetails(Graphics graphics) {
        String str;
        int max;

        try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION_1, 512)) {
            CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
            int stationListSize = (int) ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().filter(s -> s.parentValue(celestialBodies) == this.selectedBody.parentValue(celestialBodies)).count();

            max = Math.min((this.height / 2) / 14, stationListSize);
            texture.blit(RHS - 95, LHS, 95, 53, this.selectedStationOwner.isEmpty() ? 95 : 0, 186, 95, 53, BLUE);

            int color;
            if (this.spaceStationListOffset <= 0) {
                color = GREY6;
            } else {
                color = BLUE;
            }
            texture.blit(RHS - 85, LHS + 45, 61, 4, 0, 239, 61, 4, color);
            if (max + spaceStationListOffset >= stationListSize) {
                color = GREY6;
            } else {
                color = BLUE;
            }
            texture.blit(RHS - 85, LHS + 49 + max * 14, 61, 4, 0, 239 + 4, 61, -4, color);

            if (((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().noneMatch(s -> s.parent() == this.selectedBody.parent() && s.type().ownershipData(s.config()).canAccess(this.minecraft.player))) {
                str = I18n.get(Translations.CelestialSelection.SELECT_SS);
                texture.drawSplitText(str, RHS - 47, LHS + 20, 91, WHITE);
            } else {
                str = I18n.get(Translations.CelestialSelection.SS_OWNER);
                texture.drawText(str, RHS - 85, LHS + 18, WHITE, false);
                str = this.selectedStationOwner;
                texture.drawCenteredText(str, RHS - 47, LHS + 30, WHITE, false);
            }
        }

        try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
            Iterator<CelestialBody<SatelliteConfig, SatelliteType>> it = ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().filter(s -> s.parent() == this.selectedBody.parent() && s.type().ownershipData(s.config()).canAccess(this.minecraft.player)).iterator();
            int i = 0;
            int j = 0;
            while (it.hasNext() && i < max) {
                CelestialBody<SatelliteConfig, SatelliteType> e = it.next();

                if (j >= this.spaceStationListOffset) {
                    int xOffset = 0;

                    if (e.type().ownershipData(e.config()).username().equalsIgnoreCase(this.selectedStationOwner)) {
                        xOffset -= 5;
                    }

                    texture.blit(RHS - 95 + xOffset, LHS + 50 + i * 14, 93, 12, SIDE_BUTTON_U + SIDE_BUTTON_WIDTH, SIDE_BUTTON_V, -SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, BLUE);
                    str = "";
                    String str0 = I18n.get(((TranslatableContents) e.name().getContents()).getKey());
                    int point = 0;
                    while (this.font.width(str) < 80 && point < str0.length()) {
                        str = str + str0.charAt(point);
                        point++;
                    }
                    if (this.font.width(str) >= 80) {
                        str = str.substring(0, str.length() - 3);
                        str = str + "...";
                    }
                    texture.drawText(str, RHS - 88 + xOffset, LHS + 52 + i * 14, WHITE, false);
                    i++;
                }
                j++;
            }
        }
    }

    private void drawSpaceStationCreationPrompt(GuiGraphics gui, Graphics graphics, int mousePosX, int mousePosY) {
        String str;
        if (this.canCreateSpaceStation(this.selectedBody) && (!(isSatellite(this.selectedBody)))) {
            try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
                int canCreateLength = Math.max(0, texture.getSplitStringLines(I18n.get(Translations.CelestialSelection.CAN_CREATE_SPACE_STATION), 91) - 2);
                canCreateOffset = canCreateLength * this.font.lineHeight;

                texture.blit(RHS - 79, LHS + 129, 61, 4, CREATE_SS_PANEL_CAP_U, CREATE_SS_PANEL_CAP_V, CREATE_SS_PANEL_CAP_WIDTH, CREATE_SS_PANEL_CAP_HEIGHT, BLUE);

                texture.blit(RHS - 95, LHS + 134, 93, 4, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, 4, BLUE);
                for (int barY = 0; barY < canCreateLength; ++barY) {
                    texture.blit(RHS - 95, LHS + 138 + barY * this.font.lineHeight, 93, this.font.lineHeight, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, this.font.lineHeight, BLUE);
                }
                texture.blit(RHS - 95, LHS + 138 + canCreateOffset, 93, 43, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, CREATE_SS_PANEL_HEIGHT - 4, BLUE);
            }

            SatelliteRecipe recipe = ((Orbitable) this.selectedBody.type()).satelliteRecipe(this.selectedBody.config());
            if (recipe != null) {
                boolean validInputMaterials = true;

                int i = 0;
                for (Int2ObjectMap.Entry<Ingredient> entry : recipe.ingredients().int2ObjectEntrySet()) {
                    Ingredient ingredient = entry.getValue();
                    int xPos = (int) (RHS - 95 + i * 93 / (double) recipe.ingredients().size() + 5);
                    int yPos = LHS + 154 + canCreateOffset;

                    boolean b = mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16;
                    int amount = getAmountInInventory(ingredient);
                    Lighting.setupFor3DItems();
                    ItemStack stack = ingredient.getItems()[(int) (minecraft.level.getGameTime() % (20 * ingredient.getItems().length) / 20)];

                    graphics.cleanupState();
                    gui.renderItem(stack, xPos, yPos);
                    gui.renderItemDecorations(font, stack, xPos, yPos, null);
                    Lighting.setupForFlatItems();
                    RenderSystem.enableBlend();

                    if (b) {
                        RenderSystem.depthMask(true);
                        RenderSystem.enableDepthTest();
                        gui.pose().pushPose();
                        gui.pose().translate(0, 0, 300);
                        int k = this.font.width(stack.getHoverName());
                        int j2 = mousePosX - k / 2;
                        int k2 = mousePosY - 12;
                        int i1 = 8;

                        if (j2 + k > this.width) {
                            j2 -= (j2 - this.width + k);
                        }

                        if (k2 + i1 + 6 > this.height) {
                            k2 = this.height - i1 - 6;
                        }

                        try (Graphics.Fill fill = graphics.fill()) {
                            int j1 = FastColor.ARGB32.color(190, 0, 153, 255);
                            fill.fillGradientRaw(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                            fill.fillGradientRaw(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                            fill.fillGradientRaw(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                            fill.fillGradientRaw(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                            fill.fillGradientRaw(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                            int k1 = FastColor.ARGB32.color(170, 0, 153, 255);
                            int l1 = (k1 & 0xfefefe) >> 1 | k1 & 0xff000000;
                            fill.fillGradientRaw(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                            fill.fillGradientRaw(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                            fill.fillGradientRaw(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                            fill.fillGradientRaw(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
                        }

                        graphics.cleanupState();
                        gui.drawString(this.font, stack.getHoverName(), j2, k2, WHITE, false);
                        gui.pose().popPose();
                    }

                    str = "" + entry.getIntKey();
                    boolean valid = amount >= entry.getIntKey();
                    if (!valid && validInputMaterials) {
                        validInputMaterials = false;
                    }
                    int color = valid | this.minecraft.player.getAbilities().instabuild ? GREEN : RED;
                    gui.drawString(this.font, str, xPos + 8 - this.font.width(str) / 2, LHS + 170 + canCreateOffset, color, false);

                    i++;
                }

                try (Graphics.TextureColor texture = graphics.textureColor(CELESTIAL_SELECTION)) {
                    int color;
                    if (validInputMaterials || this.minecraft.player.getAbilities().instabuild) {
                        color = GREEN1;
                    } else {
                        color = RED;
                    }

                    if (!this.mapMode) {
                        if (mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= LHS + 182 + canCreateOffset && mousePosY <= LHS + 182 + 12 + canCreateOffset) {
                            texture.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, CREATE_SS_PANEL_BUTTON_U, CREATE_SS_PANEL_BUTTON_V, CREATE_SS_PANEL_BUTTON_WIDTH, CREATE_SS_PANEL_BUTTON_HEIGHT, color);
                        }
                    }

                    texture.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, CREATE_SS_PANEL_BUTTON_U, CREATE_SS_PANEL_BUTTON_V, CREATE_SS_PANEL_BUTTON_WIDTH, CREATE_SS_PANEL_BUTTON_HEIGHT, color);

                    color = (int) ((Math.sin(this.ticksSinceMenuOpenF / 5.0) * 0.5 + 0.5) * 255);
                    texture.drawSplitText(I18n.get(Translations.CelestialSelection.CAN_CREATE_SPACE_STATION), RHS - 48, LHS + 137, 91, FastColor.ARGB32.color(255, color, 255, color));

                    if (!mapMode) {
                        texture.drawSplitText(I18n.get(Translations.CelestialSelection.CREATE_SPACE_STATION).toUpperCase(), RHS - 48, LHS + 185 + canCreateOffset, 91, WHITE);
                    }
                }
            } else {
                try (Graphics.Text text = graphics.text()) {
                    text.drawSplitText(I18n.get(Translations.CelestialSelection.CANNOT_CREATE_SPACE_STATION), RHS - 48, LHS + 138, 91, WHITE);
                }
            }
        }
    }

    private List<CelestialBody<SatelliteConfig, SatelliteType>> getVisibleSatellitesForCelestialBody(CelestialBody<?, ?> selectedBody) {
        if (selectedBody == null || selectedBody.type() instanceof Satellite) return Collections.emptyList();
        List<CelestialBody<SatelliteConfig, SatelliteType>> list = new LinkedList<>();
        for (CelestialBody<SatelliteConfig, SatelliteType> satellite : ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values()) {
            if (satellite.parentValue(celestialBodies) == selectedBody && satellite.type().ownershipData(satellite.config()).canAccess(this.minecraft.player)) {
                list.add(satellite);
            }
        }
        return list;
    }

    /**
     * Draws child bodies (when appropriate) on the left-hand interface
     */
    protected int drawChildButtons(Graphics.TextureColor texture, List<CelestialBody<?, ?>> children, int xOffsetBase, int yOffsetPrior, boolean recursive) {
        xOffsetBase += this.borderSize + this.borderEdgeSize;
        final int yOffsetBase = this.borderSize + this.borderEdgeSize + 50 + yOffsetPrior;
        int yOffset = 0;
        for (int i = 0; i < children.size(); i++) {
            CelestialBody<?, ?> child = children.get(i);
            int xOffset = xOffsetBase + (child.equals(this.selectedBody) ? 5 : 0);
            final int scale = (int) Math.min(95.0F, Math.max(0.0F, (this.ticksSinceMenuOpenF * 25.0F) - 95 * i));

            float brightness = child.equals(this.selectedBody) ? 0.2F : 0.0F;
            int color;
            if (child.type() instanceof Landable<?> && (this.data == null || this.fromBody == null || this.data.canTravel(manager, this.fromBody, child))) {
                color = FastColor.ARGB32.color((int) (scale / 95.0F) * 255, 0, (int) ((0.6F + brightness) * 255), 0);
            } else {
                color = FastColor.ARGB32.color((int) (scale / 95.0F) * 255, (int) ((0.6F + brightness) * 255), 0, 0);
            }
            texture.blit(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, SIDE_BUTTON_GRADIENT_U, SIDE_BUTTON_GRADIENT_V, SIDE_BUTTON_GRADIENT_WIDTH, SIDE_BUTTON_GRADIENTn_HEIGHT, color);
            texture.blit(2 + xOffset, yOffsetBase + yOffset, 93, 12, SIDE_BUTTON_U, SIDE_BUTTON_V, SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, FastColor.ARGB32.color((int) ((scale / 95.0F) * 255), (int) ((3 * brightness) * 255), (int) ((0.6F + 2 * brightness) * 255), 255));

            if (scale > 0) {
                color = 0xe0e0e0;
                texture.drawText(I18n.get(((TranslatableContents) child.name().getContents()).getKey()), 7 + xOffset, yOffsetBase + yOffset + 2, color, false);
            }

            yOffset += 14;
            if (recursive && child.equals(this.selectedBody)) {
                List<CelestialBody<?, ?>> grandchildren = this.getChildren(child);
                if (!grandchildren.isEmpty()) {
                    if (this.animateGrandchildren == 14 * grandchildren.size()) {
                        yOffset += this.drawChildButtons(texture, grandchildren, 10, yOffset, false);
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            List<CelestialBody<?, ?>> partial = new LinkedList<>();
                            for (int j = 0; j < this.animateGrandchildren / 14; j++) {
                                partial.add(grandchildren.get(j));
                            }
                            this.drawChildButtons(texture, partial, 10, yOffset, false);
                        }
                        yOffset += this.animateGrandchildren;
                        this.animateGrandchildren += 2;
                    }
                }
            }
        }
        return yOffset;
    }

    protected int getAmountInInventory(Ingredient ingredient) {
        int i = 0;

        for (int j = 0; j < Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).getInventory().getContainerSize(); ++j) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(j);
            if (ingredient.test(stack)) {
                i += stack.getCount();
            }
        }
        return i;
    }
}
