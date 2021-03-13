/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialObjectType;
import com.hrznstudio.galacticraft.api.celestialbodies.SolarSystemType;
import com.hrznstudio.galacticraft.api.celestialbodies.satellite.Satellite;
import com.hrznstudio.galacticraft.api.celestialbodies.satellite.SatelliteRecipe;
import com.hrznstudio.galacticraft.api.internal.accessor.ClientSatelliteAccessor;
import com.hrznstudio.galacticraft.api.internal.accessor.SatelliteAccessor;
import com.hrznstudio.galacticraft.api.math.Matrix4;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.hrznstudio.galacticraft.util.ColorUtils;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
@Environment(EnvType.CLIENT)
public class PlanetSelectScreen extends Screen {
    protected static final int MAX_SPACE_STATION_NAME_LENGTH = 32;
    // String colours
    protected static final int WHITE = ColorUtils.to32BitColor(255, 255, 255, 255);
    protected static final int GREY5 = ColorUtils.to32BitColor(255, 150, 150, 150);
    protected static final int GREY4 = ColorUtils.to32BitColor(255, 140, 140, 140);
    protected static final int GREY3 = ColorUtils.to32BitColor(255, 120, 120, 120);
    protected static final int GREY2 = ColorUtils.to32BitColor(255, 100, 100, 100);
    protected static final int GREY1 = ColorUtils.to32BitColor(255, 80, 80, 80);
    protected static final int GREY0 = ColorUtils.to32BitColor(255, 40, 40, 40);
    protected static final int GREEN = ColorUtils.to32BitColor(255, 0, 255, 0);
    protected static final int RED = ColorUtils.to32BitColor(255, 255, 0, 0);
    protected static final int RED3 = ColorUtils.to32BitColor(255, 255, 100, 100);
    protected static final int CYAN = ColorUtils.to32BitColor(255, 150, 200, 255);
    protected static Identifier guiMain0 = new Identifier(Constants.MOD_ID, "textures/gui/celestialselection.png");
    protected static Identifier guiMain1 = new Identifier(Constants.MOD_ID, "textures/gui/celestialselection1.png");
    protected static int BORDER_SIZE = 0;
    protected static int BORDER_EDGE_SIZE = 0;
    protected final boolean mapMode;
    private final int tier;
    public boolean canCreateStations;
    protected float zoom = 0.0F;
    protected float planetZoom = 0.0F;
    protected boolean doneZooming = false;
    protected float preSelectZoom = 0.0F;
    protected Vec2f preSelectPosition = new Vec2f(0, 0);
    protected float ticksSinceSelectionF = 0;
    protected float ticksSinceUnselectionF = -1;
    protected float ticksSinceMenuOpenF = 0;
    protected float ticksTotalF = 0;
    @Deprecated
    protected int ticksSinceUnselection = -1;
    protected int animateGrandchildren = 0;
    protected Vec2f position = new Vec2f(0, 0);
    protected Map<CelestialBodyType, Vector3d> planetPosMap = new HashMap<>();
    @Deprecated
    protected Map<CelestialBodyType, Integer> celestialBodyTicks = new HashMap<>();
    protected CelestialBodyType selectedBody = CelestialBodyType.THE_SUN;
    protected CelestialBodyType lastSelectedBody;
    protected int canCreateOffset = 24;
    protected EnumView viewState = EnumView.PREVIEW;
    protected EnumSelection selectionState = EnumSelection.UNSELECTED;
    protected int zoomTooltipPos = 0;
    protected CelestialBodyType selectedParent = CelestialBodyType.THE_SUN;
    protected String selectedStationOwner = "";
    protected int spaceStationListOffset = 0;
    protected boolean renamingSpaceStation;
    protected String renamingString = "";
    protected Vec2f translation = new Vec2f(0.0F, 0.0F);
    protected boolean mouseDragging = false;
    protected double lastMovePosX = -1;
    protected double lastMovePosY = -1;
    protected boolean errorLogged = false;
    protected List<CelestialBodyType> bodiesToRender = new ArrayList<>();
    private final ClientSatelliteAccessor.SatelliteListener listener = (satellite, b) -> {
        if (!b) {
            bodiesToRender.remove(satellite);
        } else {
            bodiesToRender.add(satellite);
        }
    };

    public PlanetSelectScreen(boolean mapMode, int tier, boolean canCreateStations) {
        super(LiteralText.EMPTY);
        this.mapMode = mapMode;
        this.tier = tier;
        this.canCreateStations = canCreateStations;
        ((ClientSatelliteAccessor) MinecraftClient.getInstance().getNetworkHandler()).addListener(this.listener);
    }

    protected static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }

    protected static Vec2f lerpVec2(Vec2f v0, Vec2f v1, float t) {
        return new Vec2f(v0.x + t * (v1.x - v0.x), v0.y + t * (v1.y - v0.y));
    }

    @Override
    public void init() {
        PlanetSelectScreen.BORDER_SIZE = this.width / 65;
        PlanetSelectScreen.BORDER_EDGE_SIZE = PlanetSelectScreen.BORDER_SIZE / 4;
        this.bodiesToRender.clear();
        this.bodiesToRender.addAll(CelestialBodyType.getAll(this.client.getNetworkHandler().getRegistryManager()).stream().collect(Collectors.toList()));
        this.bodiesToRender.addAll(((ClientSatelliteAccessor) this.client.getNetworkHandler()).getSatellites());
    }

    @Override
    public void onClose() {
        super.onClose();
        ((ClientSatelliteAccessor) this.client.getNetworkHandler()).removeListener(this.listener);
    }

    protected String getGrandparentName() {
        CelestialBodyType parent = this.selectedBody;
        if (parent == null) return "null";
        for (int i = 0; i < 2; i++) {
            if (parent.getParent() != null) {
                parent = parent.getParent();
            } else {
                if (i == 1) {
                    return I18n.translate(parent.getParentSystem().getTranslationKey());
                } else {
                    return I18n.translate(parent.getParentSystem().getGalaxyTranslationKey());
                }
            }

        }
        return I18n.translate(parent.getTranslationKey());
    }

    private boolean isChildBody(CelestialBodyType type) {
        return type != null && (type.getType() == CelestialObjectType.MOON || type.getType() == CelestialObjectType.SATELLITE);
    }

    private boolean isPlanet(CelestialBodyType type) {
        return type != null && type.getType() == CelestialObjectType.PLANET;
    }

    private boolean isStar(CelestialBodyType type) {
        return type != null && type.getType() == CelestialObjectType.STAR;
    }

    protected RegistryKey<World> getSatelliteParentID(Satellite satellite) {
        return satellite.getParent().getWorld();
    }

    protected String getParentName() {
        if (this.selectedBody == null) return "null";
        if (this.selectedBody.getParent() != null) return I18n.translate(this.selectedBody.getParent().getTranslationKey());
        return I18n.translate(this.selectedBody.getParentSystem().getTranslationKey());
    }

    protected float getScale(CelestialBodyType celestialBody) {
        return 3.0F * celestialBody.getDisplayInfo().getRelativeDistance() * (isPlanet(celestialBody) ? 25.0F : 1.0F / 5.0F);
    }

    protected List<CelestialBodyType> getSiblings(CelestialBodyType celestialBody) {
        List<CelestialBodyType> bodyList = Lists.newArrayList();

        if (isPlanet(celestialBody)) {
            SolarSystemType system = celestialBody.getParentSystem();

            for (CelestialBodyType planet : AddonRegistry.CELESTIAL_BODIES) {
                if (isPlanet(planet)) {
                    SolarSystemType system1 = planet.getParentSystem();

                    if (system.equals(system1)) {
                        bodyList.add(planet);
                    }
                }
            }
        } else if (isChildBody(celestialBody)) {
            CelestialBodyType planet = celestialBody.getParent();

            for (CelestialBodyType moon : AddonRegistry.CELESTIAL_BODIES) {
                if (planet.equals(moon.getParent())) {
                    bodyList.add(moon);
                }
            }
        }

        return bodyList;
    }

    protected List<CelestialBodyType> getChildren(CelestialBodyType celestialBody) {
        if (celestialBody != null) {
            List<CelestialBodyType> list = CelestialBodyType.getAll(this.client.getNetworkHandler().getRegistryManager()).stream().filter(celestialBodyType -> celestialBodyType.getParent() == celestialBody).collect(Collectors.toList());
            list.addAll(getVisibleSatellitesForCelestialBodyType(celestialBody));
            return list;
        }
        return Collections.emptyList();
    }

    protected float getZoomAdvanced() {
        if (this.ticksTotalF < 30) {
            float scale = Math.max(0.0F, Math.min(this.ticksTotalF / 30.0F, 1.0F));
            return lerp(-0.75F, 0.0F, (float) Math.pow(scale, 0.5F));
        }

        if (this.selectedBody == null || this.selectionState != EnumSelection.ZOOMED) {
            if (!this.doneZooming) {
                float unselectScale = lerp(this.zoom, this.preSelectZoom, Math.max(0.0F, Math.min(this.ticksSinceUnselectionF / 100.0F, 1.0F)));

                if (unselectScale <= this.preSelectZoom + 0.05F) {
                    this.zoom = this.preSelectZoom;
//                    this.preSelectZoom = 0.0F;
                    this.ticksSinceUnselectionF = -1;
                    this.ticksSinceUnselection = -1;
                    this.doneZooming = true;
                }

                return unselectScale;
            }

            return this.zoom;
        }

        if (!this.doneZooming) {
            float f = lerp(this.zoom, 12, Math.max(0.0F, Math.min((this.ticksSinceSelectionF - 20) / 40.0F, 1.0F)));

            if (f >= 11.95F) {
                this.doneZooming = true;
            }

            return f;
        }

        return 12 + this.planetZoom;
    }

    protected Vec2f getTranslationAdvanced(float partialTicks) {
        if (this.selectedBody == null) {
            if (this.ticksSinceUnselectionF > 0) {
                float f0 = Math.max(0.0F, Math.min(this.ticksSinceUnselectionF / 100.0F, 1.0F));
                if (f0 >= 0.999999F) {
                    this.ticksSinceUnselectionF = 0;
                    this.ticksSinceUnselection = 0;
                }
                return lerpVec2(this.position, this.preSelectPosition, f0);
            }

            return new Vec2f(this.position.x + translation.x, this.position.y + translation.y);
        }

        if (!this.isZoomed()) {
            if (isChildBody(this.selectedBody)) {
                Vector3f posVec = this.getCelestialBodyTypePosition(this.selectedBody.getParent());
                return new Vec2f(posVec.getX(), posVec.getY());
            }

            return new Vec2f(this.position.x + translation.x, this.position.y + translation.y);
        }

//        if (this.selectedBody instanceof Planet && this.lastSelectedBody instanceof IChildBody && ((IChildBody) this.lastSelectedBody).getParent() == this.selectedBody)
//        {
//            Vector3f posVec = this.getCelestialBodyTypePosition(this.selectedBody);
//            return new Vec2f(posVec.x, posVec.y);
//        }


        if (this.lastSelectedBody != null) {
            Vector3f pos3 = this.getCelestialBodyTypePosition(this.lastSelectedBody);
            this.position = new Vec2f(pos3.getX(), pos3.getY());
        }

        Vector3f posVec = this.getCelestialBodyTypePosition(this.selectedBody);
        return lerpVec2(this.position, new Vec2f(posVec.getX(), posVec.getY()), Math.max(0.0F, Math.min((this.ticksSinceSelectionF - 18) / 7.5F, 1.0F)));
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        // Override and do nothing, so it isn't possible to exit the GUI
        if (this.mapMode) {
            return super.keyPressed(key, scanCode, modifiers);
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.selectedBody != null) {
                this.unselectCelestialBodyType();
            }

            return true;
        }

        if (this.renamingSpaceStation) {
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (this.renamingString != null && this.renamingString.length() > 0) {
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
                String pastestring = this.client.keyboard.getClipboard();

                if (pastestring == null || pastestring.isEmpty()) {
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

        return false;
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (renamingSpaceStation && SharedConstants.isValidChar(character)) {
            this.renamingString = this.renamingString + character;
            this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), MAX_SPACE_STATION_NAME_LENGTH));

            return true;
        } else {
            return false;
        }
    }

    public boolean isValid(String string) {
        return string.length() > 0 && SharedConstants.isValidChar(string.charAt(string.length() - 1));

    }

    protected boolean canCreateSpaceStation(CelestialBodyType atBody) {
        if (this.mapMode/* || ConfigManagerCore.disableSpaceStationCreation.get()*/ || !this.canCreateStations) //todo SSconfig
        {
            return false;
        }

        if (atBody.getAccessWeight() > this.tier) {
            // If parent body is unreachable, the satellite is also unreachable
            return false;
        }

        boolean foundSatellite = false;
        assert client != null;
        assert client.world != null;
        for (Satellite type : ((SatelliteAccessor) client.getNetworkHandler()).getSatellites()) {
            if (type.getParent() == atBody && type.getOwnershipData().getOwner().equals(this.client.player.getUuid())) {
                foundSatellite = true;
                break;
            }
        }

        if (foundSatellite) {
            return false;
        }
        return atBody.getSatelliteRecipe() != null;
    }

    protected void unselectCelestialBodyType() {
        this.selectionState = EnumSelection.UNSELECTED;
        this.ticksSinceUnselectionF = 0;
        this.ticksSinceUnselection = 0;
        this.lastSelectedBody = this.selectedBody;
        this.selectedBody = CelestialBodyType.THE_SUN;
        this.doneZooming = false;
        this.selectedStationOwner = "";
        this.animateGrandchildren = 0;
    }

    @Override
    public void tick() {
        if (this.selectedBody == null && this.ticksSinceUnselection >= 0) {
            this.ticksSinceUnselection++;
        }

        if (!this.renamingSpaceStation && (this.selectedBody == null || !this.isZoomed())) {
            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT)) {
                translation = new Vec2f(translation.x - 2, translation.y - 2);
            }

            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT)) {
                translation = new Vec2f(translation.x + 2, translation.y + 2);
            }

            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_UP)) {
                translation = new Vec2f(translation.x + 2, translation.y - 2);
            }

            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_DOWN)) {
                translation = new Vec2f(translation.x - 2, translation.y + 2);
            }
        }
    }

    protected void teleportToSelectedBody() {
        if (this.selectedBody != null && this.selectedBody.getWorld() != null) {
            if (this.selectedBody.getAccessWeight() <= this.tier) {
                try {
                    client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "planet_tp"), new PacketByteBuf(Unpooled.buffer()).writeIdentifier(this.selectedBody.getId())));
                    client.openScreen(new SpaceTravelScreen(isSatellite(selectedBody) ? ((Satellite) this.selectedBody).getName() : this.selectedBody.getTranslationKey(), this.selectedBody.getWorld()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int activeButton, double relOffsetX, double relOffsetY) {
        if (mouseDragging && lastMovePosX != -1 && activeButton == 0) {
            double deltaX = x - lastMovePosX;
            double deltaY = y - lastMovePosY;
            float scrollMultiplier = -Math.abs(this.zoom);

            if (this.zoom == -1.0F) {
                scrollMultiplier = -1.5F;
            }

            if (this.zoom >= -0.25F && this.zoom <= 0.15F) {
                scrollMultiplier = -0.2F;
            }

            if (this.zoom >= 0.15F) {
                scrollMultiplier = -0.15F;
            }
            translation = new Vec2f(translation.x + (float) (deltaX - deltaY) * scrollMultiplier * 0.2F, translation.y + (float) (deltaY + deltaX) * scrollMultiplier * 0.2F);
        }

        lastMovePosX = x;
        lastMovePosY = y;
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);

        mouseDragging = false;
        lastMovePosX = -1;
        lastMovePosY = -1;

        this.translation = Vec2f.ZERO;
        return true;
    }

//    @Override
//    public void handleInput() throws IOException
//    {
//        this.translation.x = 0.0F;
//        this.translation.y = 0.0F;
//        super.handleInput();
//    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        super.mouseClicked(x, y, button);
        boolean clickHandled = false;

        final int LHS = PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE;
        final int RHS = width - LHS;
        final int TOP = LHS;

        if (this.selectedBody != null && x > LHS && x < LHS + 88 && y > TOP && y < TOP + 13) {
            this.unselectCelestialBodyType();
            return true;
        }

        if (!this.mapMode) {
            if (x >= RHS - 95 && x < RHS && y > TOP + 181 + canCreateOffset && y < TOP + 182 + 12 + canCreateOffset) {
                if (this.selectedBody != null && this.selectedBody.getWorld() != null)
                {
                    SatelliteRecipe recipe = this.selectedBody.getSatelliteRecipe();
                    if (recipe != null && this.canCreateSpaceStation(this.selectedBody))
                    {
                        if (recipe.test(this.client.player.inventory) || this.client.player.abilities.creativeMode)
                        {
//                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_BIND_SPACE_STATION_ID, GCCoreUtil.getWorld(this.client.world), new Object[]{this.selectedBody.getWorld()}));
                            ClientPlayNetworking.send(new Identifier(Constants.MOD_ID, "create_satellite"), new PacketByteBuf(Unpooled.buffer()).writeIdentifier(this.selectedBody.getId()));
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

        boolean a = x > RHS - 88 && x < RHS && y > TOP && y < TOP + 13;
        if (this.mapMode) {
            if (a) {
                this.client.openScreen(null);
                clickHandled = true;
            }
        }

        if (this.selectedBody != null && !this.mapMode) {
            if (a) {
                if (!(isSatellite(this.selectedBody)) || !this.selectedStationOwner.equals("")) {
                    this.teleportToSelectedBody();
                }
                clickHandled = true;
            }
        }

        // Need unscaled mouse coords
//        int mouseX = Mouse.getX();
//        int mouseY = Mouse.getY() * -1 + this.client.displayHeight - 1;
//        double mouseX = (x / (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth());
//        double mouseY = (y / (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight());

        if (isSatellite(this.selectedBody)) {
            if (this.renamingSpaceStation) {
                if (x >= width / 2f - 90 && x <= width / 2f + 90 && y >= this.height / 2f - 38 && y <= this.height / 2f + 38) {
                    // Apply
                    if (x >= width / 2f - 90 + 17 && x <= width / 2f - 90 + 17 + 72 && y >= this.height / 2f - 38 + 59 && y <= this.height / 2f - 38 + 59 + 12) {
                        String strName = this.client.player.getName().getString();
//                        Integer spacestationID = this.spaceStationIDs.get(strName);
//                        if (spacestationID == null) spacestationID = this.spaceStationIDs.get(strName.toLowerCase());
                        Satellite selectedSatellite = (Satellite) this.selectedBody;
                        selectedSatellite.setName(this.renamingString);
//                        RegistryKey<World> spacestationID = selectedSatellite.getWorld();
//                        this.spaceStationMap.get(getSatelliteParentID(selectedSatellite)).get(strName).setStationName(this.renamingString);
//	                    	this.spaceStationNames.put(strName, this.renamingString);
//                            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_RENAME_SPACE_STATION, GCCoreUtil.getWorld(this.client.world), new Object[]{this.renamingString, spacestationID})); //TODO SS ID PACKET
                        this.renamingSpaceStation = false;
                    }
                    // Cancel
                    if (x >= width / 2f && x <= width / 2f + 72 && y >= this.height / 2f - 38 + 59 && y <= this.height / 2f - 38 + 59 + 12) {
                        this.renamingSpaceStation = false;
                    }
                    clickHandled = true;
                }
            } else {
                this.blit(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);

                if (x >= width / 2f - 47 && x <= width / 2f - 47 + 94 && y >= TOP && y <= TOP + 11) {
                    if (this.selectedStationOwner.length() != 0 && this.selectedStationOwner.equalsIgnoreCase(this.client.player.getName().getString())) {
                        this.renamingSpaceStation = true;
                        this.renamingString = null;
                        clickHandled = true;
                    }
                }

                Satellite selectedSatellite = (Satellite) this.selectedBody;
                int stationListSize = ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().size();
                int max = Math.min((this.height / 2) / 14, stationListSize);

                int xPos;
                int yPos;

                // Up button
                xPos = RHS - 85;
                yPos = TOP + 45;

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (this.spaceStationListOffset > 0) {
                        this.spaceStationListOffset--;
                    }
                    clickHandled = true;
                }

                // Down button
                xPos = RHS - 85;
                yPos = TOP + 49 + max * 14;

                if (x >= xPos && x <= xPos + 61 && y >= yPos && y <= yPos + 4) {
                    if (max + spaceStationListOffset < stationListSize) {
                        this.spaceStationListOffset++;
                    }
                    clickHandled = true;
                }

                Iterator<Satellite> it = ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().iterator();
                int i = 0;
                int j = 0;
                while (it.hasNext() && i < max) {
                    Satellite satellite = it.next();
                    if (j >= this.spaceStationListOffset) {
                        int xOffset = 0;

                        if (satellite.getOwnershipData().getUsername().equalsIgnoreCase(this.selectedStationOwner)) {
                            xOffset -= 5;
                        }

                        xPos = RHS - 95 + xOffset;
                        yPos = TOP + 50 + i * 14;

                        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12) {
                            this.selectedStationOwner = satellite.getOwnershipData().getUsername();
                            clickHandled = true;
                        }
                        i++;
                    }
                    j++;
                }
            }
        }

        int xPos = LHS + 2;
        int yPos = TOP + 10;

        boolean planetZoomedMoon = this.isZoomed() && isPlanet(this.selectedParent);

        // Top yellow button e.g. Sol
        if (x >= xPos && x <= xPos + 93 && y >= yPos && y <= yPos + 12 && this.selectedParent != null) {
            if (this.selectedBody == null) {
                this.preSelectZoom = this.zoom;
                this.preSelectPosition = this.position;
            }

            EnumSelection selectionCountOld = this.selectionState;

            if (this.isSelected()) {
                this.unselectCelestialBodyType();
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
                    this.unselectCelestialBodyType();
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

        double mouseX = (x / (this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth()));
        double mouseY = (y / (this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight()));

        if (!clickHandled) {
            List<CelestialBodyType> children = this.getChildren(this.isZoomed() && !(isPlanet(this.selectedParent)) ? this.selectedBody : this.selectedParent);

            yPos = TOP + 50;
            for (CelestialBodyType child : children) {
                clickHandled = this.testClicked(child, child.equals(this.selectedBody) ? 5 : 0, yPos, x, y, false);
                yPos += 14;

                if (!clickHandled && !this.isZoomed() && child.equals(this.selectedBody)) {
                    List<CelestialBodyType> grandchildren = this.getChildren(child);
                    int gOffset = 0;
                    for (CelestialBodyType grandchild : grandchildren) {
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

        if (!clickHandled) {
            for (Map.Entry<CelestialBodyType, Vector3d> e : this.planetPosMap.entrySet()) {
                CelestialBodyType bodyClicked = e.getKey();
                if (this.selectedBody == null && isChildBody(bodyClicked)) {
                    continue;
                }

                double iconSize = e.getValue().z; // Z value holds size on-screen

                if (x >= e.getValue().x - iconSize && x <= e.getValue().x + iconSize && y >= e.getValue().y - iconSize && y <= e.getValue().y + iconSize) {
                    if (this.selectedBody != bodyClicked || !this.isZoomed()) {
                        if (this.isSelected() && this.selectedBody != bodyClicked) {
                            /*if (!(this.selectedBody instanceof IChildBody) || ((IChildBody) this.selectedBody).getParent() != bodyClicked)
                            {
//                                this.unselectCelestialBodyType();
                            }
                            else */
                            if (this.isZoomed()) {
                                this.selectionState = EnumSelection.SELECTED;
                            }
                        }

                        if (bodyClicked != this.selectedBody) {
                            this.lastSelectedBody = this.selectedBody;
                            this.animateGrandchildren = 0;
                            if (!(isChildBody(this.selectedBody)) || this.selectedBody.getParent() != bodyClicked) {
                                // Only unzoom if the new selected body is not the child of the previously selected body
                                this.selectionState = EnumSelection.UNSELECTED;
                            }
                        } else {
                            this.doneZooming = false;
                            this.planetZoom = 0.0F;
                        }

                        this.selectedBody = bodyClicked;
                        this.ticksSinceSelectionF = 0;
                        this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];

                        if (isChildBody(bodyClicked)) {
                            this.selectionState = EnumSelection.ZOOMED;
                        }

                        if (this.isZoomed()) {
                            this.ticksSinceMenuOpenF = 0;
                        }

                        //Auto select if it's a spacestation and there is only a single entry
                        if (isSatellite(this.selectedBody) && ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().stream().filter(s -> s.getParent() == this.selectedBody.getParent()).count() == 1) {
                            this.selectedStationOwner = ((Satellite) this.selectedBody).getOwnershipData().getUsername();
                        }

                        clickHandled = true;
                        break;
                    }
                }
            }
        }

        if (!clickHandled) {
            if (this.selectedBody != null) {
                this.unselectCelestialBodyType();
                this.planetZoom = 0.0F;
            }

            mouseDragging = true;
        }

        CelestialBodyType selectedParent = this.selectedParent;

        if (isChildBody(this.selectedBody)  || isSatellite(this.selectedBody) || isPlanet(this.selectedBody)) {
            selectedParent = this.selectedBody.getParent();
//        } else if (isPlanet(this.selectedBody)) {
//            selectedParent = this.selectedBody.getParentSystem();
        } else if (this.selectedBody == null) {
            selectedParent = CelestialBodyType.THE_SUN; //SOL
        }

        if (this.selectedParent != selectedParent) {
            this.selectedParent = selectedParent;
        }

        return true;
    }

//    @Override
//    protected void mouseClickMove(int x, int y, int lastButtonClicked, long timeSinceMouseClick)
//    {
//        super.mouseClickMove(x, y, lastButtonClicked, timeSinceMouseClick);
//
//        if (mouseDragging && lastMovePosX != -1 && lastButtonClicked == 0)
//        {
//            int deltaX = x - lastMovePosX;
//            int deltaY = y - lastMovePosY;
//            float scollMultiplier = -Math.abs(this.zoom);
//
//            if (this.zoom == -1.0F)
//            {
//                scollMultiplier = -1.5F;
//            }
//
//            if (this.zoom >= -0.25F && this.zoom <= 0.15F)
//            {
//                scollMultiplier = -0.2F;
//            }
//
//            if (this.zoom >= 0.15F)
//            {
//                scollMultiplier = -0.15F;
//            }
//
//            translation.x += (deltaX - deltaY) * scollMultiplier * (ConfigManagerCore.invertMapMouseScroll.get() ? -1.0F : 1.0F) * ConfigManagerCore.mapMouseScrollSensitivity.get() * 0.2F;
//            translation.y += (deltaY + deltaX) * scollMultiplier * (ConfigManagerCore.invertMapMouseScroll.get() ? -1.0F : 1.0F) * ConfigManagerCore.mapMouseScrollSensitivity.get() * 0.2F;
//        }
//
//        lastMovePosX = x;
//        lastMovePosY = y;
//    }

    protected boolean testClicked(CelestialBodyType body, int xOffset, int yPos, double x, double y, boolean grandchild) {
        int xPos = PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE + 2 + xOffset;
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double wheel = amount / (this.selectedBody == null ? 500.0 : 250.0);

        if (wheel != 0) {
            if (this.selectedBody == null || (this.viewState == EnumView.PREVIEW && !this.isZoomed())) {
                //Minimum zoom increased from 0.55F to 1F to allow zoom out to see other solar systems
                this.zoom = (float) Math.min(Math.max(this.zoom + wheel * ((this.zoom + 2.0)) / 10.0, -1.0), 3);
            } else {
                this.planetZoom = (float) Math.min(Math.max(this.planetZoom + wheel, -4.9), 5);
            }
            return true;
        }
        return false;
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.ticksSinceMenuOpenF += delta;
        this.ticksTotalF += delta;

        if (this.selectedBody != null) {
            this.ticksSinceSelectionF += delta;
        }

        if (this.selectedBody == null && this.ticksSinceUnselectionF >= 0) {
            this.ticksSinceUnselectionF += delta;
        }

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();

        Matrix4 camMatrix = new Matrix4();
        Matrix4.translate(new Vector3f(0.0F, 0.0F, -9000.0F), camMatrix, camMatrix); // See EntityRenderer.java:setupOverlayRendering
        Matrix4 viewMatrix = new Matrix4();
        viewMatrix.m00 = 2.0F / width;
        viewMatrix.m11 = 2.0F / -height;
        viewMatrix.m22 = -2.0F / 9000.0F;
        viewMatrix.m30 = -1.0F;
        viewMatrix.m31 = 1.0F;
        viewMatrix.m32 = -2.0F;

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.loadIdentity();
        FloatBuffer fb = BufferUtils.createFloatBuffer(16 * Float.SIZE);
        fb.rewind();
        viewMatrix.store(fb);
        fb.flip();
        GL11.glMultMatrixf(fb);
//        fb.clear();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.loadIdentity();
        fb.rewind();
        camMatrix.store(fb);
        fb.flip();
        fb.clear();
        GL11.glMultMatrixf(fb);

        this.setBlackBackground();

        RenderSystem.pushMatrix();
        Matrix4 worldMatrix = this.setIsometric(delta);
        float gridSize = 7000F; //194.4F;
        //TODO: Add dynamic map sizing, to allow the map to be small by default and expand when more distant solar systems are added.
        this.drawGrid(gridSize, height / 3f / 3.5F);
        this.drawCircles();
        RenderSystem.popMatrix();

        HashMap<CelestialBodyType, Matrix4> matrixMap = this.drawCelestialBodies(worldMatrix);

        this.planetPosMap.clear();

        for (Map.Entry<CelestialBodyType, Matrix4> e : matrixMap.entrySet()) {
            Matrix4 planetMatrix = e.getValue();
            Matrix4 matrix0 = Matrix4.mul(viewMatrix, planetMatrix, planetMatrix);
            int x = (int) Math.floor((matrix0.m30 * 0.5 + 0.5) * client.getWindow().getWidth());
            int y = (int) Math.floor(client.getWindow().getHeight() - (matrix0.m31 * 0.5 + 0.5) * client.getWindow().getHeight());
            double mx = (x * (this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth()));
            double my = (y * (this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight()));
            Vec2f vec = new Vec2f((float) mx, (float) my);

            Matrix4 scaleVec = new Matrix4();
            scaleVec.m00 = matrix0.m00;
            scaleVec.m11 = matrix0.m11;
            scaleVec.m22 = matrix0.m22;
            Vector4f newVec = Matrix4.transform(scaleVec, new Vector4f(2, -2, 0, 0));
            float iconSize = (newVec.getY() * (client.getWindow().getHeight() / 2.0F)) * (isStar(e.getKey()) ? 2 : 1) * (e.getKey() == this.selectedBody ? 1.5F : 1.0F);

            this.planetPosMap.put(e.getKey(), new Vector3d(vec.x, vec.y, iconSize)); // Store size on-screen in Z-value for ease
        }

        this.drawSelectionCursor(fb, worldMatrix);

        try {
            this.drawButtons(matrices, mouseX, mouseY);
        } catch (Exception e) {
            throw new RuntimeException("Problem identifying planet or dimension in an add on for Galacticraft!\n(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)", e);
        }

        this.drawBorder(matrices);
        RenderSystem.popMatrix();

//        RenderSystem.matrixMode(GL11.GL_PROJECTION);
//        RenderSystem.loadIdentity();
//        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//        RenderSystem.loadIdentity();
    }

    protected void drawSelectionCursor(FloatBuffer fb, Matrix4 worldMatrix) {
        RenderSystem.pushMatrix();
        switch (this.selectionState) {
            case SELECTED:
                if (this.selectedBody != null) {
//                Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);
//                Matrix4f.translate(this.getCelestialBodyTypePosition(this.selectedBody), worldMatrix0, worldMatrix0);
//                Matrix4f worldMatrix1 = new Matrix4f();
//                Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix1, worldMatrix1);
//                Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix1, worldMatrix1);
//                worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1);
//                fb.rewind();
//                worldMatrix1.store(fb);
//                fb.flip();
//                GL11.glMultMatrixf(fb);
                    setupMatrix(this.selectedBody, worldMatrix, fb);
                    fb.clear();
                    RenderSystem.scalef(1 / 15.0F, 1 / 15.0F, 1);
                    this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                    float colMod = this.getZoomAdvanced() < 4.9F ? (float) (Math.sin(this.ticksSinceSelectionF / 2.0F) * 0.5F + 0.5F) : 1.0F;
                    RenderSystem.color4f(1.0F, 1.0F, 0.0F, 1 * colMod);
                    int width = (int) Math.floor((getWidthForCelestialBodyType(this.selectedBody) / 2.0) * (isChildBody(this.selectedBody) ? 9.0 : 30.0));

                    this.blit(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
                }
                break;
            case ZOOMED:
                if (this.selectedBody != null) {
//                Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);
//                Matrix4f.translate(this.getCelestialBodyTypePosition(this.selectedBody), worldMatrix0, worldMatrix0);
//                Matrix4f worldMatrix1 = new Matrix4f();
//                Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix1, worldMatrix1);
//                Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix1, worldMatrix1);
//                worldMatrix1 = Matrix4f.mul(worldMatrix0, worldMatrix1, worldMatrix1);
//                fb.rewind();
//                worldMatrix1.store(fb);
//                fb.flip();
//                GL11.glMultMatrixf(fb);
                    setupMatrix(this.selectedBody, worldMatrix, fb);
                    fb.clear();
                    float div = (this.zoom + 1.0F - this.planetZoom);
                    float scale = Math.max(0.3F, 1.5F / (this.ticksSinceSelectionF / 5.0F)) * 2.0F / div;
                    RenderSystem.scalef(scale, scale, 1);
                    this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                    float colMod = this.getZoomAdvanced() < 4.9F ? (float) (Math.sin(this.ticksSinceSelectionF) * 0.5F + 0.5F) : 1.0F;
                    RenderSystem.color4f(0.4F, 0.8F, 1.0F, 1 * colMod);
                    int width = getWidthForCelestialBodyType(this.selectedBody) * 13;
                    this.blit(-width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
                }
                break;
            default:
                break;
        }
        RenderSystem.popMatrix();
    }

    protected Vector3f getCelestialBodyTypePosition(CelestialBodyType cBody) {
        if (cBody == null) {
            return new Vector3f(0, 0, 0);
        }
        if (isStar(cBody)) {
            if (cBody == CelestialBodyType.THE_SUN) {
                return new Vector3f(0, 0, 0);
            }
            return new Vector3f(cBody.getParentSystem().getX(), cBody.getParentSystem().getY(), 0);
        }

        float timeScale = isPlanet(cBody) ? 200.0F : 2.0F;
        float distanceFromCenter = this.getScale(cBody); // / 24000d because it needs to be relative to overworld //todo phaseshift?!
        Vector3f cBodyPos = new Vector3f((float) Math.sin(ticksTotalF / (timeScale * (cBody.getDisplayInfo().getOrbitTime() / 24000d)) + /*cBody.getPhaseShift()*/ Math.PI) * distanceFromCenter, (float) Math.cos(ticksTotalF / (timeScale * (cBody.getDisplayInfo().getOrbitTime() / 24000d)) + /*cBody.getPhaseShift()*/Math.PI) * distanceFromCenter, 0);

        if (cBody.getParent() != null) {
            cBodyPos.add(this.getCelestialBodyTypePosition(cBody.getParent()));
        }
        return cBodyPos;
    }

    public int getWidthForCelestialBodyType(CelestialBodyType celestialBody) {
        boolean zoomed = celestialBody == this.selectedBody && this.selectionState == EnumSelection.SELECTED;
        return isStar(celestialBody) ? (zoomed ? 12 : 8) :
                isPlanet(celestialBody) ? (zoomed ? 6 : 4) :
                        isChildBody(celestialBody) ? (zoomed ? 6 : 4) : 2;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.mapMode;
    }

    public HashMap<CelestialBodyType, Matrix4> drawCelestialBodies(Matrix4 worldMatrix) {
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        FloatBuffer fb = BufferUtils.createFloatBuffer(16 * Float.SIZE);
        HashMap<CelestialBodyType, Matrix4> matrixMap = new HashMap<>();

        for (CelestialBodyType body : bodiesToRender) {
            boolean hasParent = isChildBody(body);

            float alpha = getAlpha(body);

            if (alpha > 0.0F) {
                GlStateManager.pushMatrix();
                Matrix4 worldMatrixLocal = setupMatrix(body, worldMatrix, fb, hasParent ? 0.25F : 1.0F);
//                CelestialBodyTypeRenderEvent.Pre preEvent = new CelestialBodyTypeRenderEvent.Pre(body, body.getBodyIcon(), 16); //todo render event
//                MinecraftForge.EVENT_BUS.post(preEvent);

                RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
//                if (preEvent.celestialBodyTexture != null)
//                {
                this.client.getTextureManager().bindTexture(/*preEvent.celestialBodyTexture*/new Identifier(body.getDisplayInfo().getIconTexture().getNamespace(), "textures/" + body.getDisplayInfo().getIconTexture().getPath() + ".png"));
//                }

//                if (!preEvent.isCanceled())
//                {
                int size = getWidthForCelestialBodyType(body);
                this.blit(-size / 2f, -size / 2f, size, size, body.getDisplayInfo().getIconX(), body.getDisplayInfo().getIconY(), body.getDisplayInfo().getIconW(), body.getDisplayInfo().getIconH(), false, false, GlStateManager.getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH), GlStateManager.getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT));
                matrixMap.put(body, worldMatrixLocal);
//                }

//                CelestialBodyTypeRenderEvent.Post postEvent = new CelestialBodyTypeRenderEvent.Post(body);
//                MinecraftForge.EVENT_BUS.post(postEvent);
                GlStateManager.popMatrix();
            }
        }

        return matrixMap;
    }

    /**
     * Draws gray border around outside of gui
     */
    public void drawBorder(MatrixStack matrices) {
        fill(matrices, 0, 0, PlanetSelectScreen.BORDER_SIZE, height, GREY2);
        fill(matrices, width - PlanetSelectScreen.BORDER_SIZE, 0, width, height, GREY2);
        fill(matrices, 0, 0, width, PlanetSelectScreen.BORDER_SIZE, GREY2);
        fill(matrices, 0, height - PlanetSelectScreen.BORDER_SIZE, width, height, GREY2);
        fill(matrices, PlanetSelectScreen.BORDER_SIZE, PlanetSelectScreen.BORDER_SIZE, PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE, height - PlanetSelectScreen.BORDER_SIZE, GREY0);
        fill(matrices, PlanetSelectScreen.BORDER_SIZE, PlanetSelectScreen.BORDER_SIZE, width - PlanetSelectScreen.BORDER_SIZE, PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE, GREY0);
        fill(matrices, width - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE, PlanetSelectScreen.BORDER_SIZE, width - PlanetSelectScreen.BORDER_SIZE, height - PlanetSelectScreen.BORDER_SIZE, GREY1);
        fill(matrices, PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE, width - PlanetSelectScreen.BORDER_SIZE, height - PlanetSelectScreen.BORDER_SIZE, GREY1);
    }

    public void drawButtons(MatrixStack matrices, int mousePosX, int mousePosY) {
        this.setZOffset(0);
        boolean handledSliderPos = false;

        final int LHS = PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE;
        final int RHS = width - LHS;
        final int TOP = LHS;
        final int BOT = height - LHS;

        if (this.viewState == EnumView.PROFILE) {
            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
            RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
            this.blit(width / 2 - 43, TOP, 86, 15, 266, 0, 172, 29, false, false);
            String str = I18n.translate("gui.message.catalog").toUpperCase();
            this.textRenderer.draw(matrices, str, width / 2f - this.textRenderer.getWidth(str) / 2f, TOP + this.textRenderer.fontHeight / 2f, WHITE);

            if (this.selectedBody != null) {
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);

                if (mousePosX > LHS && mousePosX < LHS + 88 && mousePosY > TOP && mousePosY < TOP + 13) {
                    RenderSystem.color3f(3.0F, 0.0F, 0.0F);
                } else {
                    RenderSystem.color3f(0.9F, 0.2F, 0.2F);
                }

                this.blit(LHS, TOP, 88, 13, 0, 392, 148, 22, false, false);
                str = I18n.translate("gui.message.back").toUpperCase();
                this.textRenderer.draw(matrices, str, LHS + 45 - this.textRenderer.getWidth(str) / 2f, TOP + this.textRenderer.fontHeight / 2f - 2, WHITE);

                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                if (mousePosX > RHS - 88 && mousePosX < RHS && mousePosY > TOP && mousePosY < TOP + 13) {
                    RenderSystem.color3f(0.0F, 3.0F, 0.0F);
                } else {
                    RenderSystem.color3f(0.2F, 0.9F, 0.2F);
                }

                this.blit(RHS - 88, TOP, 88, 13, 0, 392, 148, 22, true, false);

                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                this.blit(LHS, BOT - 13, 88, 13, 0, 392, 148, 22, false, true);
                this.blit(RHS - 88, BOT - 13, 88, 13, 0, 392, 148, 22, true, true);
                int menuTopLeft = TOP - 115 + height / 2 - 4;
                int posX = LHS + Math.min((int) this.ticksSinceSelectionF * 10, 133) - 134;
                int posX2 = (int) (LHS + Math.min(this.ticksSinceSelectionF * 1.25F, 15) - 15);
                int textRendererPosY = menuTopLeft + PlanetSelectScreen.BORDER_EDGE_SIZE + this.textRenderer.fontHeight / 2 - 2;
                this.blit(posX, menuTopLeft + 12, 133, 196, 0, 0, 266, 392, false, false);

//			str = this.selectedBody.getLocalizedName();
//			this.textRenderer.draw(matrices, str, posX + 20, textRendererPosY, GCCoreUtil.to32BitColor(255, 255, 255, 255));

                str = I18n.translate("gui.message.daynightcycle") + ":";
                this.textRenderer.draw(matrices, str, posX + 5, textRendererPosY + 14, CYAN);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".daynightcycle.0");
                this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 25, WHITE);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".daynightcycle.1");
                if (!str.isEmpty()) {
                    this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 36, WHITE);
                }

                str = I18n.translate("gui.message.surfacegravity") + ":";
                this.textRenderer.draw(matrices, str, posX + 5, textRendererPosY + 50, CYAN);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".surfacegravity.0");
                this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 61, WHITE);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".surfacegravity.1");
                if (!str.isEmpty()) {
                    this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 72, WHITE);
                }

                str = I18n.translate("gui.message.surfacecomposition") + ":";
                this.textRenderer.draw(matrices, str, posX + 5, textRendererPosY + 88, CYAN);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".surfacecomposition.0");
                this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 99, WHITE);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".surfacecomposition.1");
                if (!str.isEmpty()) {
                    this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 110, WHITE);
                }

                str = I18n.translate("gui.message.atmosphere") + ":";
                this.textRenderer.draw(matrices, str, posX + 5, textRendererPosY + 126, CYAN);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".atmosphere.0");
                this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 137, WHITE);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".atmosphere.1");
                if (!str.isEmpty()) {
                    this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 148, WHITE);
                }

                str = I18n.translate("gui.message.meansurfacetemp") + ":";
                this.textRenderer.draw(matrices, str, posX + 5, textRendererPosY + 165, CYAN);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".meansurfacetemp.0");
                this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 176, WHITE);
                str = I18n.translate("gui.message." + this.selectedBody.getTranslationKey() + ".meansurfacetemp.1");
                if (!str.isEmpty()) {
                    this.textRenderer.draw(matrices, str, posX + 10, textRendererPosY + 187, WHITE);
                }

                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                this.blit(posX2, menuTopLeft + 12, 17, 199, 439, 0, 32, 399, false, false);
//			this.drawRectD(posX2 + 16.5, menuTopLeft + 13, posX + 131, menuTopLeft + 14, GCCoreUtil.to32BitColor(120, 0, (int) (0.6F * 255), 255));
            }
        } else {
            String str;
            // Catalog:
            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
            RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
            this.blit(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false);
            str = I18n.translate("gui.message.catalog").toUpperCase();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.textRenderer.draw(matrices, str, LHS + 40 - textRenderer.getWidth(str) / 2, TOP + 1, WHITE);

            int scale = (int) Math.min(95, this.ticksSinceMenuOpenF * 12.0F);
            boolean planetZoomedNotMoon = this.isZoomed() && !(isChildBody(this.selectedParent));

            // Parent frame:
            RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
            this.blit(LHS - 95 + scale, TOP + 12, 95, 41, 0, 436, 95, 41, false, false);
            str = /*planetZoomedNotMoon ? I18n.translate(this.selectedBody.getTranslationKey()) :*/ this.getParentName();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.textRenderer.draw(matrices, str, LHS + 9 - 95 + scale, TOP + 34, WHITE);
            RenderSystem.color4f(1, 1, 0, 1);
            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);

            // Grandparent frame:
            this.blit(LHS + 2 - 95 + scale, TOP + 14, 93, 17, 95, 436, 93, 17, false, false);
            str = /*planetZoomedNotMoon ? this.getParentName() :*/ this.getGrandparentName();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.textRenderer.draw(matrices, str, LHS + 7 - 95 + scale, TOP + 16, GREY3);
            RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);

            List<CelestialBodyType> children = this.getChildren(/*planetZoomedNotMoon*/this.isZoomed() ? this.selectedBody : this.selectedParent);
            drawChildren(matrices, children, 0, 0, true);

            if (this.mapMode) {
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1);
                this.blit(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false);
                str = I18n.translate("gui.message.exit").toUpperCase();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.textRenderer.draw(matrices, str, RHS - 40 - textRenderer.getWidth(str) / 2, TOP + 1, WHITE);
            }

            if (this.selectedBody != null) {
                // Right-hand bar (basic selectionState info)
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain1);
                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);

                if (isSatellite(this.selectedBody)) {
                    Satellite selectedSatellite = (Satellite) this.selectedBody;
                    int stationListSize = (int) ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().stream().filter(s -> s.getParent() == this.selectedBody.getParent()).count();

                    int max = Math.min((this.height / 2) / 14, stationListSize);
                    this.blit(RHS - 95, TOP, 95, 53, this.selectedStationOwner.length() == 0 ? 95 : 0, 186, 95, 53, false, false);
                    if (this.spaceStationListOffset <= 0) {
                        RenderSystem.color4f(0.65F, 0.65F, 0.65F, 1);
                    } else {
                        RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                    }
                    this.blit(RHS - 85, TOP + 45, 61, 4, 0, 239, 61, 4, false, false);
                    if (max + spaceStationListOffset >= stationListSize) {
                        RenderSystem.color4f(0.65F, 0.65F, 0.65F, 1);
                    } else {
                        RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                    }
                    this.blit(RHS - 85, TOP + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true);
                    RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);

                    if (!((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().stream().anyMatch(s -> s.getParent() == this.selectedBody.getParent() && s.getOwnershipData().canAccess(client.player))) {
                        str = I18n.translate("gui.message.select_ss");
                        this.drawSplitString(matrices, str, RHS - 47, TOP + 20, 91, WHITE, false, false);
                    } else {
                        str = I18n.translate("gui.message.ss_owner");
                        this.textRenderer.draw(matrices, str, RHS - 85, TOP + 18, WHITE);
                        str = this.selectedStationOwner;
                        this.textRenderer.draw(matrices, str, RHS - 47 - this.textRenderer.getWidth(str) / 2, TOP + 30, WHITE);
                    }

                    Iterator<Satellite> it = ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites().stream().filter(s -> s.getParent() == this.selectedBody.getParent() && s.getOwnershipData().canAccess(client.player)).iterator();
                    int i = 0;
                    int j = 0;
                    while (it.hasNext() && i < max) {
                        Satellite e = it.next();

                        if (j >= this.spaceStationListOffset) {
                            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                            RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                            int xOffset = 0;

                            if (e.getOwnershipData().getUsername().equalsIgnoreCase(this.selectedStationOwner)) {
                                xOffset -= 5;
                            }

                            this.blit(RHS - 95 + xOffset, TOP + 50 + i * 14, 93, 12, 95, 464, 93, 12, true, false);
                            str = "";
                            String str0 = e.getName();
                            int point = 0;
                            while (this.textRenderer.getWidth(str) < 80 && point < str0.length()) {
                                str = str + str0.charAt(point);
                                point++;
                            }
                            if (this.textRenderer.getWidth(str) >= 80) {
                                str = str.substring(0, str.length() - 3);
                                str = str + "...";
                            }
                            this.textRenderer.draw(matrices, str, RHS - 88 + xOffset, TOP + 52 + i * 14, WHITE);
                            i++;
                        }
                        j++;
                    }
                } else {
                    this.blit(RHS - 96, TOP, 96, 139, 63, 0, 96, 139, false, false);
                }

                if (this.canCreateSpaceStation(this.selectedBody) && (!(isSatellite(this.selectedBody))))
                {
                    RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                    this.client.getTextureManager().bindTexture(guiMain1);
                    int canCreateLength = Math.max(0, this.drawSplitString(matrices, I18n.translate("gui.message.can_create_space_station"), 0, 0, 91, 0, true, true) - 2);
                    canCreateOffset = canCreateLength * this.textRenderer.fontHeight;

                    this.blit(RHS - 95, TOP + 134, 93, 4, 159, 102, 93, 4, false, false);
                    for (int barY = 0; barY < canCreateLength; ++barY)
                    {
                        this.blit(RHS - 95, TOP + 138 + barY * this.textRenderer.fontHeight, 93, this.textRenderer.fontHeight, 159, 106, 93, this.textRenderer.fontHeight, false, false);
                    }
                    this.blit(RHS - 95, TOP + 138 + canCreateOffset, 93, 43, 159, 106, 93, 43, false, false);
                    this.blit(RHS - 79, TOP + 129, 61, 4, 0, 170, 61, 4, false, false);

                    SatelliteRecipe recipe = this.selectedBody.getSatelliteRecipe();
                    if (recipe != null)
                    {
                        RenderSystem.color4f(0.0F, 1.0F, 0.1F, 1);
                        boolean validInputMaterials = true;

                        int i = 0;
                        for (ItemStack ingredient : recipe.getIngredients())
                        {
                            int xPos = (int) (RHS - 95 + i * 93 / (double) recipe.getIngredients().size() + 5);
                            int yPos = TOP + 154 + canCreateOffset;

                            boolean b = mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16;
                            int amount = getAmountInInventory(ingredient);
                            DiffuseLighting.enable();
                            this.itemRenderer.renderGuiItemIcon(ingredient, xPos, yPos);
                            this.itemRenderer.renderGuiItemOverlay(textRenderer, ingredient, xPos, yPos, null);
                            DiffuseLighting.disable();
                            RenderSystem.enableBlend();

                            if (b) {
                                RenderSystem.depthMask(true);
                                RenderSystem.enableDepthTest();
                                RenderSystem.pushMatrix();
                                RenderSystem.translatef(0, 0, 300);
                                int k = this.textRenderer.getWidth(ingredient.getName());
                                int j2 = mousePosX - k / 2;
                                int k2 = mousePosY - 12;
                                int i1 = 8;

                                if (j2 + k > this.width)
                                {
                                    j2 -= (j2 - this.width + k);
                                }

                                if (k2 + i1 + 6 > this.height)
                                {
                                    k2 = this.height - i1 - 6;
                                }

                                int j1 = ColorUtils.to32BitColor(190, 0, 153, 255);
                                this.fillGradient(matrices, j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                                this.fillGradient(matrices, j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                                this.fillGradient(matrices, j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                                this.fillGradient(matrices, j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                                this.fillGradient(matrices, j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                                int k1 = ColorUtils.to32BitColor(170, 0, 153, 255);
                                int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
                                this.fillGradient(matrices, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                                this.fillGradient(matrices, j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                                this.fillGradient(matrices, j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                                this.fillGradient(matrices, j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

                                this.textRenderer.draw(matrices, ingredient.getName(), j2, k2, WHITE);

                                RenderSystem.popMatrix();
                            }

                            str = "" + ingredient.getCount();
                            boolean valid = amount >= ingredient.getCount();
                            if (!valid && validInputMaterials) {
                                validInputMaterials = false;
                            }
                            int color = valid | this.client.player.abilities.creativeMode ? GREEN : RED;
                            this.textRenderer.draw(matrices, str, xPos + 8 - this.textRenderer.getWidth(str) / 2f, TOP + 170 + canCreateOffset, color);

                            i++;
                        }

                        if (validInputMaterials || this.client.player.abilities.creativeMode)
                        {
                            RenderSystem.color4f(0.0F, 1.0F, 0.1F, 1);
                        }
                        else
                        {
                            RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1);
                        }

                        this.client.getTextureManager().bindTexture(guiMain1);

                        if (!this.mapMode)
                        {
                            if (mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= TOP + 182 + canCreateOffset && mousePosY <= TOP + 182 + 12 + canCreateOffset)
                            {
                                this.blit(RHS - 95, TOP + 182 + canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                            }
                        }

                        this.blit(RHS - 95, TOP + 182 + canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);

                        int color = (int) ((Math.sin(this.ticksSinceMenuOpenF / 5.0) * 0.5 + 0.5) * 255);
                        this.drawSplitString(matrices, I18n.translate("gui.message.can_create_space_station"), RHS - 48, TOP + 137, 91, ColorUtils.to32BitColor(255, color, 255, color), true, false);

                        if (!mapMode)
                        {
                            this.drawSplitString(matrices, I18n.translate("gui.message.create_ss").toUpperCase(), RHS - 48, TOP + 185 + canCreateOffset, 91, WHITE, false, false);
                        }
                    }
                    else
                    {
                        this.drawSplitString(matrices, I18n.translate("gui.message.cannot_create_space_station"), RHS - 48, TOP + 138, 91, WHITE, true, false);
                    }
                }

                // Catalog overlay
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.3F - Math.min(0.3F, this.ticksSinceSelectionF / 50.0F));
                this.blit(LHS, TOP, 74, 11, 0, 392, 148, 22, false, false);
                str = I18n.translate("gui.message.catalog").toUpperCase();
                this.textRenderer.draw(matrices, str, LHS + 40 - textRenderer.getWidth(str) / 2f, TOP + 1, WHITE);

                // Top bar title:
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                if (isSatellite(this.selectedBody)) {
                    if (this.selectedStationOwner.length() == 0 || !this.selectedStationOwner.equalsIgnoreCase(this.client.player.getName().getString())) {
                        RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1);
                    } else {
                        RenderSystem.color4f(0.0F, 1.0F, 0.0F, 1);
                    }
                    this.blit(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);
                } else {
                    this.blit(width / 2 - 47, TOP, 94, 11, 0, 414, 188, 22, false, false);
                }
                if (this.selectedBody.getAccessWeight() >= 0 && (!(isSatellite(this.selectedBody)))) {
                    boolean canReach;
                    if (!(this.selectedBody.getAccessWeight() <= this.tier) || this.selectedBody.getWorld() == null) {
                        canReach = false;
                        RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1);
                    } else {
                        canReach = true;
                        RenderSystem.color4f(0.0F, 1.0F, 0.0F, 1);
                    }
                    this.blit(width / 2 - 30, TOP + 11, 30, 11, 0, 414, 60, 22, false, false);
                    this.blit(width / 2, TOP + 11, 30, 11, 128, 414, 60, 22, false, false);
                    str = I18n.translate("gui.message.tier", this.selectedBody.getAccessWeight() == 0 ? "?" : this.selectedBody.getAccessWeight());
                    this.textRenderer.draw(matrices, str, width / 2f - this.textRenderer.getWidth(str) / 2f, TOP + 13, canReach ? GREY4 : RED3);
                }

                str = I18n.translate(this.selectedBody.getTranslationKey());

                if (isSatellite(this.selectedBody)) {
                    str = I18n.translate("gui.message.r").toUpperCase();
                }

                this.textRenderer.draw(matrices, str, width / 2f - this.textRenderer.getWidth(str) / 2f, TOP + 2, WHITE);

                // Catalog wedge:
                this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                this.blit(LHS + 4, TOP, 83, 12, 0, 477, 83, 12, false, false);

                if (!this.mapMode) {
                    if (!(this.selectedBody.getAccessWeight() <= this.tier) || this.selectedBody.getWorld() == null || (isSatellite(this.selectedBody) && !((Satellite) this.selectedBody).getOwnershipData().canAccess(this.client.player)))
                    {
                        RenderSystem.color4f(1.0F, 0.0F, 0.0F, 1);
                    } else {
                        RenderSystem.color4f(0.0F, 1.0F, 0.0F, 1);
                    }

                    this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                    this.blit(RHS - 74, TOP, 74, 11, 0, 392, 148, 22, true, false);
                    str = I18n.translate("gui.message.launch").toUpperCase();
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.textRenderer.draw(matrices, str, RHS - 40 - textRenderer.getWidth(str) / 2f, TOP + 2, WHITE);
                }

                if (this.selectionState == EnumSelection.SELECTED && !(isSatellite(this.selectedBody))) {
                    handledSliderPos = true;

                    int sliderPos = this.zoomTooltipPos;
                    if (zoomTooltipPos != 38) {
                        sliderPos = Math.min((int) this.ticksSinceSelectionF * 2, 38);
                        this.zoomTooltipPos = sliderPos;
                    }

                    RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                    this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
                    this.blit(RHS - 182, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE - sliderPos, 83, 38, 512 - 166, 512 - 76, 166, 76, true, false);

                    boolean flag0 = getVisibleSatellitesForCelestialBodyType(this.selectedBody).size() > 0;
                    boolean flag1 = isPlanet(this.selectedBody) && getChildren(this.selectedBody).size() > 0;
                    if (flag0 && flag1) {
                        this.drawSplitString(matrices, I18n.translate("gui.message.click_again.0"), RHS - 182 + 41, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE + 2 - sliderPos, 79, GREY5, false, false);
                    } else if (!flag0 && flag1) {
                        this.drawSplitString(matrices, I18n.translate("gui.message.click_again.1"), RHS - 182 + 41, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE + 6 - sliderPos, 79, GREY5, false, false);
                    } else if (flag0) {
                        this.drawSplitString(matrices, I18n.translate("gui.message.click_again.2"), RHS - 182 + 41, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE + 6 - sliderPos, 79, GREY5, false, false);
                    } else {
                        this.drawSplitString(matrices, I18n.translate("gui.message.click_again.3"), RHS - 182 + 41, height - PlanetSelectScreen.BORDER_SIZE - PlanetSelectScreen.BORDER_EDGE_SIZE + 11 - sliderPos, 79, GREY5, false, false);
                    }
                }

                if (isSatellite(this.selectedBody) && renamingSpaceStation) {
                    this.renderBackground(matrices);
                    RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
                    this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain1);
                    this.blit(width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, false, false);
                    this.blit(width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, false, false);
                    this.blit(width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, false, false);
                    this.blit(width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, true, false);
                    this.blit(width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, false, false);
                    str = I18n.translate("gui.message.assign");
                    this.textRenderer.draw(matrices, str, width / 2f - this.textRenderer.getWidth(str) / 2f, this.height / 2f - 35, WHITE);
                    str = I18n.translate("gui.message.apply");
                    this.textRenderer.draw(matrices, str, width / 2f - this.textRenderer.getWidth(str) / 2f - 36, this.height / 2f + 23, WHITE);
                    str = I18n.translate("gui.message.cancel");
                    this.textRenderer.draw(matrices, str, width / 2f + 36 - this.textRenderer.getWidth(str) / 2f, this.height / 2f + 23, WHITE);

                    if (this.renamingString == null) {
                        Satellite selectedSatellite = (Satellite) this.selectedBody;
                        String playerName = this.client.player.getName().getString();
                        this.renamingString = selectedSatellite.getName();
                        if (this.renamingString == null) {
                            this.renamingString = selectedSatellite.getName();
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

                    this.textRenderer.draw(matrices, str0, width / 2f - this.textRenderer.getWidth(str) / 2f, this.height / 2f - 17, WHITE);
                }

//                this.client.getTextureManager().bindTexture(guiMain0);
//                RenderSystem.color4f(0.0F, 0.6F, 1.0F, 1);
            }
        }

        if (!handledSliderPos) {
            this.zoomTooltipPos = 0;
        }
    }

    private List<Satellite> getVisibleSatellitesForCelestialBodyType(CelestialBodyType selectedBody) {
        if (selectedBody == null || selectedBody.getType() == CelestialObjectType.SATELLITE) return Collections.emptyList();
        List<Satellite> list = new LinkedList<>();
        for (Satellite satellite : ((SatelliteAccessor) this.client.getNetworkHandler()).getSatellites()) {
            if (satellite.getParent() == selectedBody && satellite.getOwnershipData().canAccess(this.client.player)) {
                list.add(satellite);
            }
        }
        return list;
    }

    private boolean isSatellite(CelestialBodyType selectedBody) {
        return selectedBody != null && selectedBody.getType() == CelestialObjectType.SATELLITE;
    }

    /**
     * Draws child bodies (when appropriate) on the left-hand interface
     */
    protected int drawChildren(MatrixStack matrices, List<CelestialBodyType> children, int xOffsetBase, int yOffsetPrior, boolean recursive) {
        xOffsetBase += PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE;
        final int yOffsetBase = PlanetSelectScreen.BORDER_SIZE + PlanetSelectScreen.BORDER_EDGE_SIZE + 50 + yOffsetPrior;
        int yOffset = 0;
        for (int i = 0; i < children.size(); i++) {
            CelestialBodyType child = children.get(i);
            int xOffset = xOffsetBase + (child.equals(this.selectedBody) ? 5 : 0);
            final int scale = (int) Math.min(95.0F, Math.max(0.0F, (this.ticksSinceMenuOpenF * 25.0F) - 95 * i));

            this.client.getTextureManager().bindTexture(PlanetSelectScreen.guiMain0);
            float brightness = child.equals(this.selectedBody) ? 0.2F : 0.0F;
            if (child.getAccessWeight() <= this.tier && child.getWorld() != null) {
                RenderSystem.color4f(0.0F, 0.6F + brightness, 0.0F, scale / 95.0F);
            } else {
                RenderSystem.color4f(0.6F + brightness, 0.0F, 0.0F, scale / 95.0F);
            }
            this.blit(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, 0, 489, 86, 10, false, false);
//            RenderSystem.color4f(5 * brightness, 0.6F + 2 * brightness, 1.0F - 4 * brightness, scale / 95.0F);
            RenderSystem.color4f(3 * brightness, 0.6F + 2 * brightness, 1.0F, scale / 95.0F);
            this.blit(2 + xOffset, yOffsetBase + yOffset, 93, 12, 95, 464, 93, 12, false, false);

            if (scale > 0) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int color = 14737632;
                this.textRenderer.draw(matrices, I18n.translate(child.getTranslationKey()), 7 + xOffset, yOffsetBase + yOffset + 2, color);
            }

            yOffset += 14;
            if (recursive && child.equals(this.selectedBody)) {
                List<CelestialBodyType> grandchildren = this.getChildren(child);
                if (grandchildren.size() > 0) {
                    if (this.animateGrandchildren == 14 * grandchildren.size()) {
                        yOffset += drawChildren(matrices, grandchildren, 10, yOffset, false);
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            List<CelestialBodyType> partial = new LinkedList<>();
                            for (int j = 0; j < this.animateGrandchildren / 14; j++) {
                                partial.add(grandchildren.get(j));
                            }
                            drawChildren(matrices, partial, 10, yOffset, false);
                        }
                        yOffset += this.animateGrandchildren;
                        this.animateGrandchildren += 2;
                    }
                }
            }
        }
        return yOffset;
    }

    protected int getAmountInInventory(ItemStack ingredient) {
        int i = 0;

        for(int j = 0; j < this.client.player.inventory.size(); ++j) {
            ItemStack stack = this.client.player.inventory.getStack(j);
            if (ingredient.isItemEqual(stack)) {
                i += stack.getCount();
            }
        }
        return i;
    }

    public int drawSplitString(MatrixStack matrices, String par1Str, int par2, int par3, int par4, int par5, boolean small, boolean simulate) {
        return this.renderSplitString(matrices, par1Str, par2, par3, par4, par5, small, simulate);
    }

    protected int renderSplitString(MatrixStack matrices, String par1Str, int par2, int par3, int par4, int par6, boolean small, boolean simulate) {
        List<OrderedText> list = this.textRenderer.wrapLines(new TranslatableText(par1Str), par4);

        for (Iterator<OrderedText> iterator = list.iterator(); iterator.hasNext(); par3 += this.textRenderer.fontHeight) {
            OrderedText s1 = iterator.next();
            if (!simulate) {
                this.renderStringAligned(matrices, s1, par2, par3, par4, par6);
            }
        }

        return list.size();

    }

    protected void renderStringAligned(MatrixStack matrices, OrderedText par1Str, int par2, int par3, int par4, int par5) {
//        if (this.textRenderer.getBidiFlag())//fixme
//        {
//            int i1 = this.textRenderer.getWidth(this.bidiReorder(par1Str));
//            par2 = par2 + par4 - i1;
//        }

        this.textRenderer.draw(matrices, par1Str, par2 - this.textRenderer.getWidth(par1Str) / 2f, par3, par5);
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

    public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, boolean invertX, boolean invertY) {
        this.blit(x, y, width, height, u, v, uWidth, vHeight, invertX, invertY, 512, 512);
    }

    public void blit(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, boolean invertX, boolean invertY, float texSizeX, float texSizeY) {
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        worldRenderer.vertex(x, y + height, this.getZOffset()).texture((u + width0) * texModX, (v + height0) * texModY).next();
        worldRenderer.vertex(x + width, y + height, this.getZOffset()).texture((u + width1) * texModX, (v + height0) * texModY).next();
        worldRenderer.vertex(x + width, y, this.getZOffset()).texture((u + width1) * texModX, (v + height1) * texModY).next();
        worldRenderer.vertex(x, y, this.getZOffset()).texture((u + width0) * texModX, (v + height1) * texModY).next();
        tessellator.draw();
    }

    public void setBlackBackground() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
        worldRenderer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
        worldRenderer.vertex(0.0D, height, -90.0D).next();
        worldRenderer.vertex(width, height, -90.0D).next();
        worldRenderer.vertex(width, 0.0D, -90.0D).next();
        worldRenderer.vertex(0.0D, 0.0D, -90.0D).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Rotates/translates/scales to appropriate values before drawing celestial bodies
     */
    public Matrix4 setIsometric(float partialTicks) {
        Matrix4 mat0 = new Matrix4();
        Matrix4.translate(new Vector3f(width / 2.0F, height / 2, 0), mat0, mat0);
        Matrix4.rotate((float) Math.toRadians(55), new Vector3f(1, 0, 0), mat0, mat0);
        Matrix4.rotate((float) Math.toRadians(-45), new Vector3f(0, 0, 1), mat0, mat0);
        float zoomLocal = this.getZoomAdvanced();
        this.zoom = zoomLocal;
        Matrix4.scale(new Vector3f(1.1f + zoomLocal, 1.1F + zoomLocal, 1.1F + zoomLocal), mat0, mat0);
        Vec2f cBodyPos = this.getTranslationAdvanced(partialTicks);
        this.position = this.getTranslationAdvanced(partialTicks);
        Matrix4.translate(new Vector3f(-cBodyPos.x, -cBodyPos.y, 0), mat0, mat0);
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        fb.rewind();
        mat0.store(fb);
        fb.flip();
        GL11.glMultMatrixf(fb);
        return mat0;
    }

    /**
     * Draw background grid
     */
    public void drawGrid(float gridSize, float gridScale) {
        RenderSystem.color4f(0.0F, 0.2F, 0.5F, 0.55F);

        GL11.glBegin(GL11.GL_LINES);

        gridSize += gridScale / 2;
        for (float v = -gridSize; v <= gridSize; v += gridScale) {
            GL11.glVertex3f(v, -gridSize, -0.0F);
            GL11.glVertex3f(v, gridSize, -0.0F);
            GL11.glVertex3f(-gridSize, v, -0.0F);
            GL11.glVertex3f(gridSize, v, -0.0F);
        }

        GL11.glEnd();
    }

    /**
     * Draw orbit circles on gui
     */
    public void drawCircles() {
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.lineWidth(3);
        int count = 0;

        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);

        for (CelestialBodyType body : bodiesToRender) {
            Vector3f systemOffset = new Vector3f(0.0F, 0.0F, 0.0F);
            if (isChildBody(body)) {
                systemOffset = this.getCelestialBodyTypePosition(body.getParent());
            } else if (isPlanet(body)) {
                systemOffset = this.getCelestialBodyTypePosition(body.getParent()); //star
            }

            float x = this.getScale(body);
            float y = 0;

            float alpha = getAlpha(body);

            if (alpha > 0.0F) {
                switch (count % 2) {
                    case 0:
                        RenderSystem.color4f(0.0F / 1.4F, 0.6F / 1.4F, 1.0F / 1.4F, alpha / 1.4F);
                        break;
                    case 1:
                        RenderSystem.color4f(0.3F / 1.4F, 0.8F / 1.4F, 1.0F / 1.4F, alpha / 1.4F);
                        break;
                }
//todo event
//                CelestialBodyTypeRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyTypeRenderEvent.CelestialRingRenderEvent.Pre(body, systemOffset);
//                MinecraftForge.EVENT_BUS.post(preEvent);

//                if (!preEvent.isCanceled())
//                {
                RenderSystem.translatef(systemOffset.getX(), systemOffset.getY(), systemOffset.getZ());

                GL11.glBegin(GL11.GL_LINE_LOOP);

                float temp;
                for (int i = 0; i < 90; i++) {
                    GL11.glVertex2f(x, y);

                    temp = x;
                    x = cos * x - sin * y;
                    y = sin * temp + cos * y;
                }

                GL11.glEnd();

                RenderSystem.translatef(-systemOffset.getX(), -systemOffset.getY(), -systemOffset.getZ());

                count++;
//                }
//
//                CelestialBodyTypeRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyTypeRenderEvent.CelestialRingRenderEvent.Post(body);
//                MinecraftForge.EVENT_BUS.post(postEvent);
            }
        }

        RenderSystem.lineWidth(1);
    }

    /**
     * Returns the transparency of the selected body.
     * <p>
     * Hidden bodies will return 0.0, opaque bodies will return 1.0, and ones fading in/out will pass between those two values
     */
    public float getAlpha(CelestialBodyType body) {
        float alpha = 1.0F;

        if (isChildBody(body)) {
            boolean selected = body == this.selectedBody || (body.getParent() == this.selectedBody && this.selectionState != EnumSelection.SELECTED);
            boolean ready = this.lastSelectedBody != null || this.ticksSinceSelectionF > 35;
            boolean isSibling = getSiblings(this.selectedBody).contains(body);
            boolean isPossible = (!isSatellite(body) || ((Satellite) body).getOwnershipData().canAccess(this.client.player))/* || (this.possibleBodies != null && this.possibleBodies.contains(body))*/;
            if ((!selected && !isSibling) || !isPossible) {
                alpha = 0.0F;
            } else if (this.isZoomed() && ((!selected || !ready) && !isSibling)) {
                alpha = Math.min(Math.max((this.ticksSinceSelectionF - 30) / 15.0F, 0.0F), 1.0F);
            }
        } else {
            boolean isSelected = this.selectedBody == body;
            boolean isChildSelected = isChildBody(this.selectedBody);
            boolean isOwnChildSelected = isChildSelected && this.selectedBody.getParent() == body;

            if (!isSelected && !isOwnChildSelected && (this.isZoomed() || isChildSelected)) {
                if (this.lastSelectedBody != null || isChildSelected) {
                    alpha = 0.0F;
                } else {
                    alpha = 1.0F - Math.min(this.ticksSinceSelectionF / 25.0F, 1.0F);
                }
            }
        }

        return alpha;
    }

    protected boolean isZoomed() {
        return this.selectionState == EnumSelection.ZOOMED;
    }

    protected boolean isSelected() {
        return this.selectionState != EnumSelection.UNSELECTED;
    }

    protected Matrix4 setupMatrix(CelestialBodyType body, Matrix4 worldMatrix, FloatBuffer fb) {
        return setupMatrix(body, worldMatrix, fb, 1.0F);
    }

    protected Matrix4 setupMatrix(CelestialBodyType body, Matrix4 worldMatrix, FloatBuffer fb, float scaleXZ) {
        Matrix4 worldMatrix0 = new Matrix4(worldMatrix);
        Matrix4.translate(this.getCelestialBodyTypePosition(body), worldMatrix0, worldMatrix0);
        Matrix4 worldMatrix1 = new Matrix4();
        Matrix4.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix1, worldMatrix1);
        Matrix4.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix1, worldMatrix1);
        if (scaleXZ != 1.0F) {
            Matrix4.scale(new Vector3f(scaleXZ, scaleXZ, 1.0F), worldMatrix1, worldMatrix1);
        }
        Matrix4.mul(worldMatrix0, worldMatrix1, worldMatrix1);
        fb.rewind();
        worldMatrix1.store(fb);
        fb.flip();
        GL11.glMultMatrixf(fb);

        return worldMatrix1;
    }

    protected enum EnumView {
        PREVIEW,
        PROFILE
    }

    protected enum EnumSelection {
        UNSELECTED,
        SELECTED,
        ZOOMED
    }
}
