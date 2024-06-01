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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "DataFlowIssue"})
@Environment(EnvType.CLIENT)
public class CelestialSelectionScreen extends CelestialScreen {
    protected static final int MAX_SPACE_STATION_NAME_LENGTH = 32;

    // String colours
    protected static final int WHITE = FastColor.ARGB32.color(255, 255, 255, 255);
    protected static final int GREY5 = FastColor.ARGB32.color(255, 150, 150, 150);
    protected static final int GREY4 = FastColor.ARGB32.color(255, 140, 140, 140);
    protected static final int GREY3 = FastColor.ARGB32.color(255, 120, 120, 120);
    protected static final int GREEN = FastColor.ARGB32.color(255, 0, 255, 0);
    protected static final int RED = FastColor.ARGB32.color(255, 255, 0, 0);
    protected static final int RED3 = FastColor.ARGB32.color(255, 255, 100, 100);

    // UI COLOURS
    protected static final int BLUE = FastColor.ARGB32.color(255, 0, 153, 255); // panel/button colour
    protected static final int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0); // galaxy/grandfather panel
    protected static final int GREY6 = FastColor.ARGB32.color(255, 165, 165, 165);

    private static final int SIDE_PANEL_U = 0;
    private static final int SIDE_PANEL_V = 0;
    private static final int SIDE_PANEL_WIDTH = 95;
    private static final int SIDE_PANEL_HEIGHT = 137;

    private static final int CATALOG_U = 0;
    private static final int CATALOG_V = 197;
    private static final int CATALOG_WIDTH = 74;
    private static final int CATALOG_HEIGHT = 11;

    private static final int ZOOM_INFO_TAB_U = 134;
    private static final int ZOOM_INFO_TAB_V = 67;
    private static final int ZOOM_INFO_TAB_WIDTH = 83;
    private static final int ZOOM_INFO_TAB_HEIGHT = 38;

    private static final int PROFILE_UPPER_TAB_U = 134;
    private static final int PROFILE_UPPER_TAB_V = 0;
    private static final int PROFILE_UPPER_TAB_WIDTH = 86;
    private static final int PROFILE_UPPER_TAB_HEIGHT = 15;

    private static final int CATALOG_BACKING_U = 0;
    private static final int CATALOG_BACKING_V = 221;
    private static final int CATALOG_BACKING_WIDTH = 83;
    private static final int CATALOG_BACKING_HEIGHT = 12;

    private static final int PARENT_LABEL_U = 134;
    private static final int PARENT_LABEL_V = 151;
    private static final int PARENT_LABEL_WIDTH = 95;
    private static final int PARENT_LABEL_HEIGHT = 41;

    private static final int GRANDPARENT_LABEL_U = 134;
    private static final int GRANDPARENT_LABEL_V = 193;
    private static final int GRANDPARENT_LABEL_WIDTH = 93;
    private static final int GRANDPARENT_LABEL_HEIGHT = 17;

    private static final int SIDE_BUTTON_U = 134;
    private static final int SIDE_BUTTON_V = 223;
    private static final int SIDE_BUTTON_WIDTH = 92;
    private static final int SIDE_BUTTON_HEIGHT = 12;

    private static final int TOP_RIGHT_ACTION_BUTTON_U = 134;
    private static final int TOP_RIGHT_ACTION_BUTTON_V = 211;
    private static final int TOP_RIGHT_ACTION_BUTTON_WIDTH = 74;
    private static final int TOP_RIGHT_ACTION_BUTTON_HEIGHT = 11;

    private static final int SIDE_BUTTON_GRADIENT_U = 0;
    private static final int SIDE_BUTTON_GRADIENT_V = 234;
    private static final int SIDE_BUTTON_GRADIENT_WIDTH = 86;
    private static final int SIDE_BUTTON_GRADIENTn_HEIGHT = 20;

    private static final int TOPBAR_U = 134;
    private static final int TOPBAR_V = 138;
    private static final int TOPBAR_WIDTH = 94;
    private static final int TOPBAR_HEIGHT = 12;

    private static final int TOPBAR_SUB_U = 0;
    private static final int TOPBAR_SUB_V = 209;
    private static final int TOPBAR_SUB_WIDTH = 94;
    private static final int TOPBAR_SUB_HEIGHT = 11;

    private static final int CREATE_SS_PANEL_U = 0;
    private static final int CREATE_SS_PANEL_V = 137;
    private static final int CREATE_SS_PANEL_WIDTH = 93;
    private static final int CREATE_SS_PANEL_HEIGHT = 47;
    private static final int CREATE_SS_PANEL_CAP_U = 0;
    private static final int CREATE_SS_PANEL_CAP_V = 185;
    private static final int CREATE_SS_PANEL_CAP_WIDTH = 61;
    private static final int CREATE_SS_PANEL_CAP_HEIGHT = 4;
    private static final int CREATE_SS_PANEL_BUTTON_U = 134;
    private static final int CREATE_SS_PANEL_BUTTON_V = 236;
    private static final int CREATE_SS_PANEL_BUTTON_WIDTH = 93;
    private static final int CREATE_SS_PANEL_BUTTON_HEIGHT = 12;

    protected static final ResourceLocation TEXTURE_0 = Constant.id("textures/gui/celestial_selection.png");
    protected static final ResourceLocation TEXTURE_1 = Constant.id("textures/gui/celestial_selection_1.png");
    public static final int GREEN1 = FastColor.ARGB32.color(255, 0, 255, 25);

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
        if (body == null) return I18n.get(Translations.Galaxy.MILKY_WAY); //fixme
        if (body.parent(manager) != null) {
            if (body.parent(manager).parent(manager) != null) {
                return I18n.get(((TranslatableContents)body.parent(manager).parent(manager).name().getContents()).getKey());
            } else {
                return I18n.get(((TranslatableContents) galaxies.get(body.parent(manager).galaxy()).name().getContents()).getKey());
            }
        } else {
            return I18n.get(((TranslatableContents) galaxies.get(body.galaxy()).name().getContents()).getKey());
        }
    }

    protected String parentName() {
        if (this.selectedBody == null) return I18n.get(Translations.CelestialBody.SOL); //fixme
        if (this.selectedBody.parent(manager) != null) return I18n.get(((TranslatableContents)this.selectedBody.parent(manager).name().getContents()).getKey());
        return I18n.get(((TranslatableContents) galaxies.get(this.selectedBody.galaxy()).name().getContents()).getKey());
    }

    protected List<CelestialBody<?, ?>> getChildren(CelestialBody<?, ?> celestialBody) {
        if (celestialBody != null) {
            List<CelestialBody<?, ?>> list = celestialBodies.stream().filter(celestialBodyType -> celestialBodyType.parent(manager) == celestialBody).collect(Collectors.toList());
            list.addAll(getVisibleSatellitesForCelestialBody(celestialBody));
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
        } else {
            if (key == GLFW.GLFW_KEY_ENTER) {
                // Keyboard shortcut - teleport to dimension by pressing 'Enter'
                this.teleportToSelectedBody();
                return true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (this.renamingSpaceStation && SharedConstants.isAllowedChatCharacter(character)) {
            this.renamingString = this.renamingString + character;
            this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), MAX_SPACE_STATION_NAME_LENGTH));
            return true;
        }

        return super.charTyped(character, modifiers);
    }

    public boolean isValid(String string) {
        return !string.isEmpty() && SharedConstants.isAllowedChatCharacter(string.charAt(string.length() - 1));
    }

    protected boolean canCreateSpaceStation(CelestialBody<?, ?> atBody) {
        if (!(atBody.type() instanceof Orbitable orbitable) || orbitable.satelliteRecipe(atBody.config()) == null) return false;
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
            if (type.parent(manager) == atBody) {
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
                    ClientPlayNetworking.send(Constant.Packet.PLANET_TP, PacketByteBufs.create().writeResourceLocation(celestialBodies.getKey(this.selectedBody)));
                    this.minecraft.setScreen(new SpaceTravelScreen(isSatellite(selectedBody) ? ((Satellite) this.selectedBody.type()).getCustomName(this.selectedBody.config()).getString() : ((TranslatableContents)this.selectedBody.name().getContents()).getKey(), ((Landable) this.selectedBody.type()).world(this.selectedBody.config())));
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
                if (this.selectedBody != null && this.selectedBody.type() instanceof Orbitable orbitable/* && this.selectedBody.getWorld() != null*/)
                {
                    SatelliteRecipe recipe = orbitable.satelliteRecipe(this.selectedBody.config());
                    if (recipe != null && this.canCreateSpaceStation(this.selectedBody))
                    {
                        assert this.minecraft != null;
                        assert this.minecraft.player != null;
                        if (recipe.test(this.minecraft.player.getInventory()) || this.minecraft.player.getAbilities().instabuild)
                        {
//                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_BIND_SPACE_STATION_ID, GCCoreUtil.getWorld(this.minecraft.level), new Object[]{this.selectedBody.getWorld()}));
                            ClientPlayNetworking.send(Constant.Packet.CREATE_SATELLITE, PacketByteBufs.create().writeResourceLocation(celestialBodies.getKey(this.selectedBody)));
                            //Zoom in on planet to show the new SpaceStation if not already zoomed
                            if (!this.isZoomed())
                            {
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
                        selectedSatellite.type().setCustomName(Component.translatable(this.renamingString), selectedSatellite.config());
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
//                this.blit(width / 2 - 47, LHS, 94, 11, 0, 414, 188, 22); //fixme - why are we blitting in mouse click??

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

        try {
            this.drawButtons(graphics, mouseX, mouseY);
        } catch (Exception e) {
            throw new RuntimeException("Problem identifying planet or dimension in an add on for Galacticraft!\n(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)", e);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.mapMode;
    }

    public void drawButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean handledSliderPos = false;

        String str;
        // Catalog:
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
        this.blit(LHS, LHS, 74, 11, CATALOG_U, CATALOG_V, CATALOG_WIDTH, CATALOG_HEIGHT, BLUE);
        str = I18n.get(Translations.CelestialSelection.CATALOG).toUpperCase();
        graphics.drawString(this.font, str, LHS + 40 - font.width(str) / 2, LHS + 1, WHITE, false);

        // Catalog wedge:
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
        this.blit(LHS + 4, LHS, 83, 12, CATALOG_BACKING_U, CATALOG_BACKING_V, CATALOG_BACKING_WIDTH, CATALOG_BACKING_HEIGHT, BLUE);

        int scale = (int) Math.min(95, this.ticksSinceMenuOpenF * 12.0F);
        boolean planetZoomedNotMoon = this.isZoomed() && !(isGrandchildBody(this.selectedParent));

        // Parent frame:
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
        this.blit(LHS - 95 + scale, LHS + 12, 95, 41, PARENT_LABEL_U, PARENT_LABEL_V, PARENT_LABEL_WIDTH, PARENT_LABEL_HEIGHT, BLUE);
        str = planetZoomedNotMoon ? I18n.get(((TranslatableContents) this.selectedBody.name().getContents()).getKey()) : this.parentName();
        graphics.drawString(this.font, str, LHS + 9 - 95 + scale, LHS + 34, WHITE, false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);

        // Grandparent frame:
        this.blit(LHS + 2 - 95 + scale, LHS + 14, 93, 17, GRANDPARENT_LABEL_U, GRANDPARENT_LABEL_V, GRANDPARENT_LABEL_WIDTH, GRANDPARENT_LABEL_HEIGHT, YELLOW);
        str = planetZoomedNotMoon ? this.parentName() : this.getGrandparentName();
        graphics.drawString(this.font, str, LHS + 7 - 95 + scale, LHS + 16, GREY3, false);

//            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
        List<CelestialBody<?, ?>> children = this.getChildren(/*planetZoomedNotMoon*/this.isZoomed() ? this.selectedBody : celestialBodies.get(Constant.id("sol")));
        this.drawChildButtons(graphics, children, 0, 0, true);

        if (this.mapMode) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            RenderSystem.enableBlend();
            this.blit(RHS - 74, LHS, 74, 11, TOP_RIGHT_ACTION_BUTTON_U, TOP_RIGHT_ACTION_BUTTON_V, TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_HEIGHT, RED, true, false);
            str = I18n.get(Translations.CelestialSelection.EXIT).toUpperCase();
            graphics.drawString(this.font, str, RHS - 40 - font.width(str) / 2, LHS + 1, WHITE, false);
        }

        if (this.selectedBody != null) {
            // Right-hand bar (basic selectionState info)

            if (isSatellite(this.selectedBody)) {
                this.drawSpaceStationDetails(graphics);
            } else {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                this.blit(RHS - 96, LHS, 96, 139, SIDE_PANEL_U, SIDE_PANEL_V, SIDE_PANEL_WIDTH, SIDE_PANEL_HEIGHT, BLUE);
            }

            this.drawSpaceStationCreationPrompt(graphics, mouseX, mouseY);

            // Top bar title:
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            int color = BLUE;
            if (isSatellite(this.selectedBody)) {
                if (this.selectedStationOwner.isEmpty() || !this.selectedStationOwner.equalsIgnoreCase(this.minecraft.player.getName().getString())) {
                    color = RED;
                } else {
                    color = GREEN;
                }
            }
            this.blit(width / 2 - 47, LHS, 94, 11, TOPBAR_U, TOPBAR_V, TOPBAR_WIDTH, TOPBAR_HEIGHT, color);

            if (this.selectedBody.type() instanceof Tiered tiered && tiered.accessWeight(this.selectedBody.config()) >= 0 && (!(isSatellite(this.selectedBody)))) {
                boolean canReach;
                if (this.data != null && !this.data.canTravel(manager, this.fromBody, this.selectedBody)) {
                    canReach = false;
                    color = RED;
                } else {
                    canReach = true;
                    color = GREEN;
                }
                this.blit(width / 2 - 30, LHS + 11, 30, 11, TOPBAR_U, TOPBAR_V, 30, TOPBAR_HEIGHT, color);
                this.blit(width / 2, LHS + 11, 30, 11, TOPBAR_U + TOPBAR_WIDTH - 30, TOPBAR_V, 30, TOPBAR_HEIGHT, color);
                str = I18n.get(Translations.CelestialSelection.TIER, tiered.accessWeight(this.selectedBody.config()) == -1 ? "?" : tiered.accessWeight(this.selectedBody.config()));
                graphics.drawString(this.font, str, width / 2 - this.font.width(str) / 2, LHS + 13, canReach ? GREY4 : RED3, false);
            }

            str = I18n.get(((TranslatableContents) this.selectedBody.name().getContents()).getKey());

            if (isSatellite(this.selectedBody)) {
                str = I18n.get(Translations.CelestialSelection.RENAME).toUpperCase();
            }

            graphics.drawString(this.font, str, width / 2 - this.font.width(str) / 2, LHS + 2, WHITE, false);

            if (!this.mapMode) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                if (this.data != null && !this.data.canTravel(manager, this.fromBody, this.selectedBody) || !(this.selectedBody.type() instanceof Landable) || isSatellite(this.selectedBody) && !((Satellite) this.selectedBody.type()).ownershipData(this.selectedBody.config()).canAccess(this.minecraft.player)) {
                    color = RED;
                } else {
                    color = GREEN;
                }

                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.enableBlend();
                this.blit(RHS - 74, LHS, 74, 11, TOP_RIGHT_ACTION_BUTTON_U, TOP_RIGHT_ACTION_BUTTON_V, TOP_RIGHT_ACTION_BUTTON_WIDTH, TOP_RIGHT_ACTION_BUTTON_HEIGHT, color, true, false);
                str = I18n.get(Translations.CelestialSelection.LAUNCH).toUpperCase();
                graphics.drawString(this.font, str, RHS - 40 - font.width(str) / 2, LHS + 2, WHITE, false);
            }

            if (this.selectionState == EnumSelection.SELECTED && !(isSatellite(this.selectedBody))) {
                handledSliderPos = true;

                int sliderPos = this.zoomTooltipPos;
                if (zoomTooltipPos != 38) {
                    sliderPos = Math.min((int) this.ticksSinceSelectionF * 2, 38);
                    this.zoomTooltipPos = sliderPos;
                }

                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                this.blit(RHS - 182, height - this.borderSize - this.borderEdgeSize - sliderPos, 83, 38, ZOOM_INFO_TAB_U, ZOOM_INFO_TAB_V, ZOOM_INFO_TAB_WIDTH, ZOOM_INFO_TAB_HEIGHT, BLUE, true, false);

                boolean flag0 = !getVisibleSatellitesForCelestialBody(this.selectedBody).isEmpty();
                boolean flag1 = isPlanet(this.selectedBody) && !getChildren(this.selectedBody).isEmpty();
                if (flag0 && flag1) {
                    this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CLICK_AGAIN_MOONS_AND_SATELLITES), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 2 - sliderPos, 79, GREY5, false, false);
                } else if (!flag0 && flag1) {
                    this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CLICK_AGAIN_MOONS), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 6 - sliderPos, 79, GREY5, false, false);
                } else if (flag0) {
                    this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CLICK_AGAIN_SATELLITES), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 6 - sliderPos, 79, GREY5, false, false);
                } else {
                    this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CLICK_AGAIN), RHS - 182 + 41, height - this.borderSize - this.borderEdgeSize + 11 - sliderPos, 79, GREY5, false, false);
                }
            }

            if (isSatellite(this.selectedBody) && renamingSpaceStation) {
//                    this.renderBackground(matrices);
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_1);
                this.blit5(width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, BLUE);
                this.blit5(width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, BLUE);
                this.blit5(width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, BLUE);
                this.blit5(width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, BLUE, true, false);
                this.blit5(width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, BLUE);
                str = I18n.get(Translations.CelestialSelection.ASSIGN_NAME);
                graphics.drawString(this.font, str, width / 2 - this.font.width(str) / 2, this.height / 2 - 35, WHITE, false);
                str = I18n.get(Translations.CelestialSelection.APPLY);
                graphics.drawString(this.font, str, width / 2 - this.font.width(str) / 2 - 36, this.height / 2 + 23, WHITE, false);
                str = I18n.get(Translations.CelestialSelection.CANCEL);
                graphics.drawString(this.font, str, width / 2 + 36 - this.font.width(str) / 2, this.height / 2 + 23, WHITE, false);

                if (this.renamingString == null) {
                    CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
                    String playerName = this.minecraft.player.getName().getString();
                    this.renamingString = selectedSatellite.type().getCustomName(selectedSatellite.config()).getString();
                    if (this.renamingString == null) {
                        this.renamingString = selectedSatellite.name().getString();
                    }
                    if (this.renamingString == null) {
                        this.renamingString = "";
                    }
                }

                str = this.renamingString;
                String str0 = this.renamingString;

                if ((this.ticksSinceMenuOpenF / 10) % 2 == 0) {
                    str0 += "_";
                }

                graphics.drawString(this.font, str0, width / 2 - this.font.width(str) / 2, this.height / 2 - 17, WHITE, false);
            }

//                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
//                RenderSystem.setShaderTexture(0, guiMain0);
//                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
        }

        if (!handledSliderPos) {
            this.zoomTooltipPos = 0;
        }
    }

    private void drawSpaceStationDetails(GuiGraphics graphics) {
        String str;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_1);
        CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
        int stationListSize = (int) ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().filter(s -> s.parent(manager) == this.selectedBody.parent(manager)).count();

        int max = Math.min((this.height / 2) / 14, stationListSize);
        this.blit5(RHS - 95, LHS, 95, 53, this.selectedStationOwner.isEmpty() ? 95 : 0, 186, 95, 53, BLUE);

        int color;
        if (this.spaceStationListOffset <= 0) {
            color = GREY6;
        } else {
            color = BLUE;
        }
        this.blit5(RHS - 85, LHS + 45, 61, 4, 0, 239, 61, 4, color);
        if (max + spaceStationListOffset >= stationListSize) {
            color = GREY6;
        } else {
            color = BLUE;
        }
        this.blit5(RHS - 85, LHS + 49 + max * 14, 61, 4, 0, 239, 61, 4, color, false, true);

//        RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);

        if (((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().noneMatch(s -> s.parent(manager) == this.selectedBody.parent(manager) && s.type().ownershipData(s.config()).canAccess(this.minecraft.player))) {
            str = I18n.get(Translations.CelestialSelection.SELECT_SS);
            this.drawSplitString(graphics, str, RHS - 47, LHS + 20, 91, WHITE, false, false);
        } else {
            str = I18n.get(Translations.CelestialSelection.SS_OWNER);
            graphics.drawString(this.font, str, RHS - 85, LHS + 18, WHITE, false);
            str = this.selectedStationOwner;
            graphics.drawString(this.font, str, RHS - 47 - this.font.width(str) / 2, LHS + 30, WHITE, false);
        }

        Iterator<CelestialBody<SatelliteConfig, SatelliteType>> it = ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values().stream().filter(s -> s.parent(manager) == this.selectedBody.parent(manager) && s.type().ownershipData(s.config()).canAccess(this.minecraft.player)).iterator();
        int i = 0;
        int j = 0;
        while (it.hasNext() && i < max) {
            CelestialBody<SatelliteConfig, SatelliteType> e = it.next();

            if (j >= this.spaceStationListOffset) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                int xOffset = 0;

                if (e.type().ownershipData(e.config()).username().equalsIgnoreCase(this.selectedStationOwner)) {
                    xOffset -= 5;
                }

                this.blit(RHS - 95 + xOffset, LHS + 50 + i * 14, 93, 12, SIDE_BUTTON_U, SIDE_BUTTON_V, SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, BLUE, true, false);
                str = "";
                String str0 = I18n.get(((TranslatableContents)e.name().getContents()).getKey());
                int point = 0;
                while (this.font.width(str) < 80 && point < str0.length()) {
                    str = str + str0.charAt(point);
                    point++;
                }
                if (this.font.width(str) >= 80) {
                    str = str.substring(0, str.length() - 3);
                    str = str + "...";
                }
                graphics.drawString(this.font, str, RHS - 88 + xOffset, LHS + 52 + i * 14, WHITE, false);
                i++;
            }
            j++;
        }
    }

    private void drawSpaceStationCreationPrompt(GuiGraphics graphics, int mousePosX, int mousePosY) {
        String str;
        if (this.canCreateSpaceStation(this.selectedBody) && (!(isSatellite(this.selectedBody)))) {
            int canCreateLength = Math.max(0, this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CAN_CREATE_SPACE_STATION), 0, 0, 91, 0, true, true) - 2);
            canCreateOffset = canCreateLength * this.font.lineHeight;
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, TEXTURE_0);

            this.blit(RHS - 79, LHS + 129, 61, 4, CREATE_SS_PANEL_CAP_U, CREATE_SS_PANEL_CAP_V, CREATE_SS_PANEL_CAP_WIDTH, CREATE_SS_PANEL_CAP_HEIGHT, BLUE);

            this.blit(RHS - 95, LHS + 134, 93, 4, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, 4, BLUE);
            for (int barY = 0; barY < canCreateLength; ++barY) {
                this.blit(RHS - 95, LHS + 138 + barY * this.font.lineHeight, 93, this.font.lineHeight, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, this.font.lineHeight, BLUE);
            }
            this.blit(RHS - 95, LHS + 138 + canCreateOffset, 93, 43, CREATE_SS_PANEL_U, CREATE_SS_PANEL_V, CREATE_SS_PANEL_WIDTH, CREATE_SS_PANEL_HEIGHT - 4, BLUE);

            SatelliteRecipe recipe = ((Orbitable) this.selectedBody.type()).satelliteRecipe(this.selectedBody.config());
            if (recipe != null) {
//                RenderSystem.setShaderColor(0.0F, 1.0F, 0.1F, 1);
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
                    graphics.renderItem(stack, xPos, yPos);
                    graphics.renderItemDecorations(font, stack, xPos, yPos, null);
                    Lighting.setupForFlatItems();
                    RenderSystem.enableBlend();

                    if (b) {
                        RenderSystem.depthMask(true);
                        RenderSystem.enableDepthTest();
                        graphics.pose().pushPose();
                        graphics.pose().translate(0, 0, 300);
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

                        int j1 = FastColor.ARGB32.color(190, 0, 153, 255);
                        graphics.fillGradient(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                        graphics.fillGradient(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                        graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                        graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                        graphics.fillGradient(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                        int k1 = FastColor.ARGB32.color(170, 0, 153, 255);
                        int l1 = (k1 & 0xfefefe) >> 1 | k1 & 0xff000000;
                        graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                        graphics.fillGradient(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                        graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                        graphics.fillGradient(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

                        graphics.drawString(this.font, stack.getHoverName(), j2, k2, WHITE, false);

                        graphics.pose().popPose();
                    }

                    str = "" + entry.getIntKey();
                    boolean valid = amount >= entry.getIntKey();
                    if (!valid && validInputMaterials) {
                        validInputMaterials = false;
                    }
                    int color = valid | this.minecraft.player.getAbilities().instabuild ? GREEN : RED;
                    graphics.drawString(this.font, str, xPos + 8 - this.font.width(str) / 2, LHS + 170 + canCreateOffset, color, false);

                    i++;
                }

                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                int color;
                if (validInputMaterials || this.minecraft.player.getAbilities().instabuild) {
                    color = GREEN1;
                } else {
                    color = RED;
                }

                RenderSystem.setShaderTexture(0, TEXTURE_0);

                if (!this.mapMode) {
                    if (mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= LHS + 182 + canCreateOffset && mousePosY <= LHS + 182 + 12 + canCreateOffset) {
                        this.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, CREATE_SS_PANEL_BUTTON_U, CREATE_SS_PANEL_BUTTON_V, CREATE_SS_PANEL_BUTTON_WIDTH, CREATE_SS_PANEL_BUTTON_HEIGHT, color);
                    }
                }

                this.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, CREATE_SS_PANEL_BUTTON_U, CREATE_SS_PANEL_BUTTON_V, CREATE_SS_PANEL_BUTTON_WIDTH, CREATE_SS_PANEL_BUTTON_HEIGHT, color);

                color = (int) ((Math.sin(this.ticksSinceMenuOpenF / 5.0) * 0.5 + 0.5) * 255);
                this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CAN_CREATE_SPACE_STATION), RHS - 48, LHS + 137, 91, FastColor.ARGB32.color(color, 255, color, 255), true, false);

                if (!mapMode) {
                    this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CREATE_SPACE_STATION).toUpperCase(), RHS - 48, LHS + 185 + canCreateOffset, 91, WHITE, false, false);
                }
            } else {
                this.drawSplitString(graphics, I18n.get(Translations.CelestialSelection.CANNOT_CREATE_SPACE_STATION), RHS - 48, LHS + 138, 91, WHITE, true, false);
            }
        }
    }

    private List<CelestialBody<SatelliteConfig, SatelliteType>> getVisibleSatellitesForCelestialBody(CelestialBody<?, ?> selectedBody) {
        if (selectedBody == null || selectedBody.type() instanceof Satellite) return Collections.emptyList();
        List<CelestialBody<SatelliteConfig, SatelliteType>> list = new LinkedList<>();
        for (CelestialBody<SatelliteConfig, SatelliteType> satellite : ((SatelliteAccessor) this.minecraft.getConnection()).galacticraft$getSatellites().values()) {
            if (satellite.parent(manager) == selectedBody && satellite.type().ownershipData(satellite.config()).canAccess(this.minecraft.player)) {
                list.add(satellite);
            }
        }
        return list;
    }

    /**
     * Draws child bodies (when appropriate) on the left-hand interface
     */
    protected int drawChildButtons(GuiGraphics graphics, List<CelestialBody<?, ?>> children, int xOffsetBase, int yOffsetPrior, boolean recursive) {
        xOffsetBase += this.borderSize + this.borderEdgeSize;
        final int yOffsetBase = this.borderSize + this.borderEdgeSize + 50 + yOffsetPrior;
        int yOffset = 0;
        for (int i = 0; i < children.size(); i++) {
            CelestialBody<?, ?> child = children.get(i);
            int xOffset = xOffsetBase + (child.equals(this.selectedBody) ? 5 : 0);
            final int scale = (int) Math.min(95.0F, Math.max(0.0F, (this.ticksSinceMenuOpenF * 25.0F) - 95 * i));

            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            RenderSystem.enableBlend();
            float brightness = child.equals(this.selectedBody) ? 0.2F : 0.0F;
            int color;
            if (child.type() instanceof Landable<?> && (this.data == null || this.fromBody == null || this.data.canTravel(manager, this.fromBody, child))) {
                color = FastColor.ARGB32.color((int) (scale / 95.0F) * 255, 0, (int) ((0.6F + brightness) * 255), 0);
            } else {
                color = FastColor.ARGB32.color((int) (scale / 95.0F) * 255, (int) ((0.6F + brightness) * 255), 0, 0);
            }
            this.blit(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, SIDE_BUTTON_GRADIENT_U, SIDE_BUTTON_GRADIENT_V, SIDE_BUTTON_GRADIENT_WIDTH, SIDE_BUTTON_GRADIENTn_HEIGHT, color);
//            RenderSystem.disableBlend();
            this.blit(2 + xOffset, yOffsetBase + yOffset, 93, 12, SIDE_BUTTON_U, SIDE_BUTTON_V, SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, FastColor.ARGB32.color((int) ((scale / 95.0F) * 255), (int) ((3 * brightness) * 255), (int) ((0.6F + 2 * brightness) * 255), 255));

            if (scale > 0) {
                color = 0xe0e0e0;
                graphics.drawString(this.font, I18n.get(((TranslatableContents)child.name().getContents()).getKey()), 7 + xOffset, yOffsetBase + yOffset + 2, color, false);
            }

            yOffset += 14;
            if (recursive && child.equals(this.selectedBody)) {
                List<CelestialBody<?, ?>> grandchildren = this.getChildren(child);
                if (!grandchildren.isEmpty()) {
                    if (this.animateGrandchildren == 14 * grandchildren.size()) {
                        yOffset += this.drawChildButtons(graphics, grandchildren, 10, yOffset, false);
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            List<CelestialBody<?, ?>> partial = new LinkedList<>();
                            for (int j = 0; j < this.animateGrandchildren / 14; j++) {
                                partial.add(grandchildren.get(j));
                            }
                            this.drawChildButtons(graphics, partial, 10, yOffset, false);
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

        for(int j = 0; j < Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).getInventory().getContainerSize(); ++j) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(j);
            if (ingredient.test(stack)) {
                i += stack.getCount();
            }
        }
        return i;
    }

    public int drawSplitString(GuiGraphics graphics, String string, int x, int y, int width, int color, boolean small, boolean simulate) {
        return this.renderSplitString(graphics, string, x, y, width, color, small, simulate);
    }

    protected int renderSplitString(GuiGraphics graphics, String str, int x, int y, int width, int color, boolean small, boolean simulate) {
        List<FormattedCharSequence> list = this.font.split(Component.translatable(str), width);

        for (Iterator<FormattedCharSequence> iterator = list.iterator(); iterator.hasNext(); y += this.font.lineHeight) {
            FormattedCharSequence line = iterator.next();
            if (!simulate) {
                this.renderStringAligned(graphics, line, x, y, width, color);
            }
        }

        return list.size();

    }

    protected void renderStringAligned(GuiGraphics graphics, FormattedCharSequence line, int x, int y, int width, int color) {
//        if (this.font.isBidirectional())//fixme
//        {
//            int i1 = this.font.width(this.bidiReorder(line));
//            x = x + width - i1;
//        }

        graphics.drawString(this.font, line, x - this.font.width(line) / 2, y, color, false);
    }

    protected String bidiReorder(String s) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(s), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException arabicshapingexception) {
            return s;
        }
    }

    public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color) {
        blit(x, y, width, height, u, v, uWidth, vHeight, 256, 256, color, false, false);
    }
    public void blit5(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color) {
        blit(x, y, width, height, u, v, uWidth, vHeight, 512, 512, color, false, false);
    }

    public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color, boolean invertX, boolean invertY) {
        blit(x, y, width, height, u, v, uWidth, vHeight, 256, 256, color, invertX, invertY);
    }
    public void blit5(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color, boolean invertX, boolean invertY) {
        blit(x, y, width, height, u, v, uWidth, vHeight, 512, 512, color, invertX, invertY);
    }

    private static void blit(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, float texSizeX, float texSizeY, int color, boolean invertX, boolean invertY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(x, y + height, 0).uv((u + width0) * texModX, (v + height0) * texModY).color(color).endVertex();
        buffer.vertex(x + width, y + height, 0).uv((u + width1) * texModX, (v + height0) * texModY).color(color).endVertex();
        buffer.vertex(x + width, y, 0).uv((u + width1) * texModX, (v + height1) * texModY).color(color).endVertex();
        buffer.vertex(x, y, 0).uv((u + width0) * texModX, (v + height1) * texModY).color(color).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }
}
