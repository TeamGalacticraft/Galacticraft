/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.client.accessor.ClientSatelliteAccessor;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.satellite.Satellite;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.api.universe.celestialbody.star.Star;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.ColorUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
@Environment(EnvType.CLIENT)
public class CelestialSelectionScreen extends Screen {
    protected static final int MAX_SPACE_STATION_NAME_LENGTH = 32;
    // String colours
    protected static final int WHITE = ColorUtil.to32BitColor(255, 255, 255, 255);
    protected static final int GREY5 = ColorUtil.to32BitColor(255, 150, 150, 150);
    protected static final int GREY4 = ColorUtil.to32BitColor(255, 140, 140, 140);
    protected static final int GREY3 = ColorUtil.to32BitColor(255, 120, 120, 120);
    protected static final int GREY2 = ColorUtil.to32BitColor(255, 100, 100, 100);
    protected static final int GREY1 = ColorUtil.to32BitColor(255, 80, 80, 80);
    protected static final int GREY0 = ColorUtil.to32BitColor(255, 40, 40, 40);
    protected static final int GREEN = ColorUtil.to32BitColor(255, 0, 255, 0);
    protected static final int RED = ColorUtil.to32BitColor(255, 255, 0, 0);
    protected static final int RED3 = ColorUtil.to32BitColor(255, 255, 100, 100);
    protected static final int CYAN = ColorUtil.to32BitColor(255, 150, 200, 255);
    protected static final ResourceLocation TEXTURE_0 = new ResourceLocation(Constant.MOD_ID, "textures/gui/celestial_selection_0.png");
    protected static final ResourceLocation TEXTURE_1 = new ResourceLocation(Constant.MOD_ID, "textures/gui/celestial_selection_1.png");
    protected static int BORDER_SIZE = 0;
    protected static int BORDER_EDGE_SIZE = 0;
    protected final boolean mapMode;
    private final RocketData data;
    public final boolean canCreateStations;
    protected float zoom = 0.0F;
    protected float planetZoom = 0.0F;
    protected boolean doneZooming = false;
    protected float preSelectZoom = 0.0F;
    protected Vec2 preSelectPosition = Vec2.ZERO;
    protected float ticksSinceSelectionF = 0;
    protected float ticksSinceUnselectionF = -1;
    protected float ticksSinceMenuOpenF = 0;
    protected float ticksTotalF = 0;
    protected int animateGrandchildren = 0;
    protected Vec2 position = Vec2.ZERO;
    protected final Map<CelestialBody<?, ?>, Vec3> planetPosMap = new IdentityHashMap<>();
    protected @Nullable CelestialBody<?, ?> selectedBody;
    protected @Nullable CelestialBody<?, ?> lastSelectedBody;
    protected int canCreateOffset = 24;
    protected final EnumView viewState = EnumView.PREVIEW;
    protected EnumSelection selectionState = EnumSelection.UNSELECTED;
    protected int zoomTooltipPos = 0;
    protected String selectedStationOwner = "";
    protected int spaceStationListOffset = 0;
    protected boolean renamingSpaceStation;
    protected String renamingString = "";
    protected float translationX = 0.0f;
    protected float translationY = 0.0f;
    protected boolean mouseDragging = false;
    protected double lastMovePosX = -1;
    protected double lastMovePosY = -1;
    protected final RegistryAccess manager = Minecraft.getInstance().level.registryAccess();
    protected final Registry<Galaxy> galaxyRegistry = manager.registryOrThrow(AddonRegistry.GALAXY_KEY);
    protected final Registry<CelestialBody<?, ?>> celestialBodyRegistry = manager.registryOrThrow(AddonRegistry.CELESTIAL_BODY_KEY);
    protected @Nullable CelestialBody<?, ?> selectedParent = celestialBodyRegistry.get(new ResourceLocation("galacticraft-api", "sol"));
    protected final List<CelestialBody<?, ?>> bodiesToRender = new ArrayList<>();
    private final ClientSatelliteAccessor.SatelliteListener listener = (satellite, added) -> {
        if (!added) {
            this.bodiesToRender.remove(satellite);
        } else {
            this.bodiesToRender.add(satellite);
        }
    };

    public CelestialSelectionScreen(boolean mapMode, RocketData data, boolean canCreateStations) {
        super(Component.empty());
        this.mapMode = mapMode;
        this.data = data;
        this.canCreateStations = canCreateStations;
    }

    protected static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }

    protected static Vec2 lerpVec2(Vec2 v0, Vec2 v1, float t) {
        return new Vec2(v0.x + t * (v1.x - v0.x), v0.y + t * (v1.y - v0.y));
    }

    @Override
    public void init() {
        CelestialSelectionScreen.BORDER_SIZE = this.width / 65;
        CelestialSelectionScreen.BORDER_EDGE_SIZE = CelestialSelectionScreen.BORDER_SIZE / 4;
        this.bodiesToRender.clear();
        this.bodiesToRender.addAll(celestialBodyRegistry.stream().collect(Collectors.toList()));
        assert this.minecraft != null;
        this.bodiesToRender.addAll(((ClientSatelliteAccessor) Objects.requireNonNull(this.minecraft.getConnection())).getSatellites().values());
        ((ClientSatelliteAccessor) Objects.requireNonNull(this.minecraft.getConnection())).addListener(this.listener);
    }

    @Override
    public void onClose() {
        super.onClose();
        assert this.minecraft != null;
        ((ClientSatelliteAccessor) Objects.requireNonNull(this.minecraft.getConnection())).removeListener(this.listener);
    }

    protected String getGrandparentName() {
        CelestialBody<?, ?> body = this.selectedBody;
        if (body == null) return I18n.get(((TranslatableContents)BuiltinObjects.MILKY_WAY.name().getContents()).getKey());
        if (body.parent(manager) != null) {
            if (body.parent(manager).parent(manager) != null) {
                return I18n.get(((TranslatableContents)body.parent(manager).parent(manager).name().getContents()).getKey());
            } else {
                return I18n.get(((TranslatableContents)galaxyRegistry.get(body.parent(manager).galaxy()).name().getContents()).getKey());
            }
        } else {
            return I18n.get(((TranslatableContents)galaxyRegistry.get(body.galaxy()).name().getContents()).getKey());
        }
    }

    private boolean isChildBody(CelestialBody<?, ?> type) {
        return type != null && (type.parent(manager) != null && type.parent(manager).parent(manager) != null);
    }

    private boolean isPlanet(CelestialBody<?, ?> type) {
        return type != null && type.parent(manager) != null && type.parent(manager).type() instanceof Star;
    }

    private boolean isStar(CelestialBody<?, ?> type) {
        return type != null && type.type() instanceof Star;
    }

    protected ResourceKey<Level> getSatelliteParentID(CelestialBody<SatelliteConfig, SatelliteType> satellite) {
        return satellite.parent(manager) != null && satellite.parent(manager).type() instanceof Landable o ? o.world(satellite.parent(manager).config()) : null;
    }

    protected String parentName() {
        if (this.selectedBody == null) return I18n.get(((TranslatableContents)BuiltinObjects.SOL.name().getContents()).getKey());
        if (this.selectedBody.parent(manager) != null) return I18n.get(((TranslatableContents)this.selectedBody.parent(manager).name().getContents()).getKey());
        return I18n.get(((TranslatableContents)galaxyRegistry.get(this.selectedBody.galaxy()).name().getContents()).getKey());
    }

    protected float lineScale(CelestialBody<?, ?> celestialBody) {
        if (Float.isNaN(celestialBody.position().lineScale())) return Float.NaN;
        return 3.0F * celestialBody.position().lineScale() * (isPlanet(celestialBody) ? 25.0F : 1.0F / 5.0F);
    }

    protected List<CelestialBody<?, ?>> getSiblings(CelestialBody<?, ?> celestialBody) {
        if (celestialBody == null) return Collections.emptyList();
        List<CelestialBody<?, ?>> bodyList = Lists.newArrayList();

        CelestialBody<?, ?> parent = celestialBody.parent(manager);
        if (parent == null) return Collections.emptyList();

        for (CelestialBody<?, ?> planet : celestialBodyRegistry) {
            if (planet.parent(manager) != null && planet.parent(manager).equals(parent)) {
                bodyList.add(planet);
            }
        }
        return bodyList;
    }

    protected List<CelestialBody<?, ?>> getChildren(CelestialBody<?, ?> celestialBody) {
        if (celestialBody != null) {
            List<CelestialBody<?, ?>> list = celestialBodyRegistry.stream().filter(celestialBodyType -> celestialBodyType.parent(manager) == celestialBody).collect(Collectors.toList());
            list.addAll(getVisibleSatellitesForCelestialBody(celestialBody));
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

    protected Vec2 getTranslationAdvanced(float delta) {
        if (this.selectedBody == null) {
            if (this.ticksSinceUnselectionF > 0) {
                float f0 = Math.max(0.0F, Math.min(this.ticksSinceUnselectionF / 100.0F, 1.0F));
                if (f0 >= 0.999999F) {
                    this.ticksSinceUnselectionF = 0;
                }
                return lerpVec2(this.position, this.preSelectPosition, f0);
            }

            return new Vec2(this.position.x + translationX, this.position.y + translationY);
        }

        if (!this.isZoomed()) {
            if (isChildBody(this.selectedBody)) {
                Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody.parent(manager), delta);
                return new Vec2(posVec.x(), posVec.y());
            }

            return new Vec2(this.position.x + translationX, this.position.y + translationY);
        }

//        if (this.selectedBody instanceof Planet && this.lastSelectedBody instanceof IChildBody && ((IChildBody) this.lastSelectedBody).parent(manager) == this.selectedBody)
//        {
//            Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
//            return new Vec2(posVec.x, posVec.y);
//        }


        if (this.lastSelectedBody != null) {
            Vector3f pos3 = this.getCelestialBodyPosition(this.lastSelectedBody, delta);
            this.position = new Vec2(pos3.x(), pos3.y());
        }

        Vector3f celestialBodyPosition = this.getCelestialBodyPosition(this.selectedBody, delta);

        return lerpVec2(this.position, new Vec2(celestialBodyPosition.x(), celestialBodyPosition.y()), Math.max(0.0F, Math.min((this.ticksSinceSelectionF - 18) / 7.5F, 1.0F)));
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody();
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
                assert this.minecraft != null;
                String pastestring = this.minecraft.keyboardHandler.getClipboard();

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
        if (renamingSpaceStation && SharedConstants.isAllowedChatCharacter(character)) {
            this.renamingString = this.renamingString + character;
            this.renamingString = this.renamingString.substring(0, Math.min(this.renamingString.length(), MAX_SPACE_STATION_NAME_LENGTH));

            return true;
        } else {
            return false;
        }
    }

    public boolean isValid(String string) {
        return string.length() > 0 && SharedConstants.isAllowedChatCharacter(string.charAt(string.length() - 1));
    }

    protected boolean canCreateSpaceStation(CelestialBody<?, ?> atBody) {
        if (!(atBody.type() instanceof Orbitable orbitable) || orbitable.satelliteRecipe(atBody.config()) == null) return false;
        if (this.mapMode/* || ConfigManagerCore.disableSpaceStationCreation.get()*/ || !this.canCreateStations) //todo SSconfig
        {
            return false;
        }

        if (!this.data.canTravelTo(manager, atBody) && this.data != RocketData.empty()) {
            // If parent body is unreachable, the satellite is also unreachable
            return false;
        }

        boolean foundSatellite = false;
        assert this.minecraft != null;
        assert this.minecraft.level != null;
        for (CelestialBody<SatelliteConfig, SatelliteType> type : ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values()) {
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

    protected void unselectCelestialBody() {
        this.selectionState = EnumSelection.UNSELECTED;
        this.ticksSinceUnselectionF = 0;
        this.lastSelectedBody = this.selectedBody;
        this.selectedBody = null;
        this.doneZooming = false;
        this.selectedStationOwner = "";
        this.animateGrandchildren = 0;
    }

    @Override
    public void tick() {
        if (!this.renamingSpaceStation && (this.selectedBody == null || !this.isZoomed())) {
            assert this.minecraft != null;
            if (InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT)) {
                this.translationX = this.translationX - 2;
                this.translationY = this.translationY - 2;
            }

            if (InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT)) {
                this.translationX = this.translationX + 2;
                this.translationY = this.translationY + 2;
            }

            if (InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_UP)) {
                this.translationX = this.translationX + 2;
                this.translationY = this.translationY - 2;
            }

            if (InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_DOWN)) {
                this.translationX = this.translationX - 2;
                this.translationY = this.translationY + 2;
            }
        }
    }

    protected void teleportToSelectedBody() {
        if (this.selectedBody != null && this.selectedBody.type() instanceof Landable landable) {
            landable.world(this.selectedBody.config());
            if (this.data.canTravelTo(manager, this.selectedBody) || this.data == RocketData.empty()) {
                try {
                    assert this.minecraft != null;
                    ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "planet_tp"), PacketByteBufs.create().writeResourceLocation(celestialBodyRegistry.getKey(this.selectedBody)));
                    this.minecraft.setScreen(new SpaceTravelScreen(isSatellite(selectedBody) ? ((Satellite) this.selectedBody.type()).getCustomName(this.selectedBody.config()).getString() : ((TranslatableContents)this.selectedBody.name().getContents()).getKey(), ((Landable) this.selectedBody.type()).world(this.selectedBody.config())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int activeButton, double relOffsetX, double relOffsetY) {
        if (mouseDragging && lastMovePosX != -1 && activeButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            float scrollMultiplier = /*-Math.abs(this.zoom)*/ -1;
//            if (this.zoom == -1.0F) {
//                scrollMultiplier = -1.5F;
//            }
//
//            if (this.zoom >= -0.25F && this.zoom <= 0.15F) {
//                scrollMultiplier = -0.2F;
//            }
//
//            if (this.zoom >= 0.15F) {
//                scrollMultiplier = -0.15F;
//            }
            this.translationX = (float) ((relOffsetX - relOffsetY) * scrollMultiplier * 0.2F);
            this.translationY = (float) ((relOffsetY + relOffsetX) * scrollMultiplier * 0.2F);
        }

        lastMovePosX = relOffsetX;
        lastMovePosY = relOffsetY;
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);

        mouseDragging = false;
        lastMovePosX = -1;
        lastMovePosY = -1;

        this.translationX = 0;
        this.translationY = 0;
        return true;
    }

//    @Override
//    public void handleInput() throws IOException
//    {
//        this.translationX = 0.0F;
//        this.translationY = 0.0F;
//        super.handleInput();
//    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        super.mouseClicked(x, y, button);
        boolean clickHandled = false;

        final int LHS = CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE;
        final int RHS = width - LHS;

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
                            ClientPlayNetworking.send(Constant.Packet.CREATE_SATELLITE, PacketByteBufs.create().writeResourceLocation(celestialBodyRegistry.getKey(this.selectedBody)));
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
                if (!(isSatellite(this.selectedBody)) || !this.selectedStationOwner.equals("")) {
                    this.teleportToSelectedBody();
                }
                clickHandled = true;
            }
        }

        // Need unscaled mouse coords
//        int mouseX = Mouse.getX();
//        int mouseY = Mouse.getY() * -1 + this.minecraft.displayHeight - 1;
//        double mouseX = (x / (double) this.minecraft.getWindow().getScaledWidth() / (double) this.minecraft.getWindow().getWidth());
//        double mouseY = (y / (double) this.minecraft.getWindow().getScaledHeight() / (double) this.minecraft.getWindow().getHeight());

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
                this.blit(width / 2 - 47, LHS, 94, 11, 0, 414, 188, 22, false, false);

                if (x >= width / 2f - 47 && x <= width / 2f - 47 + 94 && y >= LHS && y <= LHS + 11) {
                    if (this.selectedStationOwner.length() != 0) {
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
                int stationListSize = ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().size();
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

                Iterator<CelestialBody<SatelliteConfig, SatelliteType>> it = ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values().iterator();
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

        double mouseX = (x / (this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getWidth()));
        double mouseY = (y / (this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getHeight()));

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

        if (!clickHandled) {
            for (Map.Entry<CelestialBody<?, ?>, Vec3> entry : this.planetPosMap.entrySet()) {
                CelestialBody<?, ?> bodyClicked = entry.getKey();
                if (this.selectedBody == null && isChildBody(bodyClicked)) {
                    continue;
                }

                double iconSize = entry.getValue().z; // Z value holds size on-screen

                if (x >= entry.getValue().x && x <= entry.getValue().x + iconSize && y >= entry.getValue().y && y <= entry.getValue().y + iconSize) {
                    if (this.selectedBody != bodyClicked || !this.isZoomed()) {
                        if (this.isSelected() && this.selectedBody != bodyClicked) {
                            /*if (!(this.selectedBody instanceof IChildBody) || ((IChildBody) this.selectedBody).parent(manager) != bodyClicked)
                            {
//                                this.unselectCelestialBody();
                            }
                            else */
                            if (this.isZoomed()) {
                                this.selectionState = EnumSelection.SELECTED;
                            }
                        }

                        if (bodyClicked != this.selectedBody) {
                            this.lastSelectedBody = this.selectedBody;
                            this.animateGrandchildren = 0;
                            if (!(isChildBody(this.selectedBody)) || this.selectedBody.parent(manager) != bodyClicked) {
                                // Only unzoom if the new selected body is not the child of the previously selected body
                                this.selectionState = EnumSelection.UNSELECTED;
                            }
                        } else {
                            this.doneZooming = false;
                            this.planetZoom = 0.0F;
                        }

                        this.selectedBody = bodyClicked;
                        this.ticksSinceSelectionF = 0;
                        if (this.selectionState == EnumSelection.UNSELECTED) {
                            this.preSelectZoom = zoom;
                            this.preSelectPosition = this.position;
                        }
                        this.selectionState = EnumSelection.values()[this.selectionState.ordinal() + 1];

                        if (isChildBody(bodyClicked)) {
                            this.selectionState = EnumSelection.ZOOMED;
                        }

                        if (this.isZoomed()) {
                            this.ticksSinceMenuOpenF = 0;
                        }

                        //Auto select if it's a spacestation and there is only a single entry
                        if (isSatellite(this.selectedBody) && ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values().stream().filter(s -> s.parent(manager) == this.selectedBody.parent(manager)).count() == 1) {
                            this.selectedStationOwner = ((Satellite) this.selectedBody.type()).ownershipData(this.selectedBody.config()).username();
                        }

                        clickHandled = true;
                        break;
                    }
                }
            }
        }

        if (!clickHandled) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody();
                this.planetZoom = 0.0F;
            }

            mouseDragging = true;
        }

        CelestialBody<?, ?> selectedParent = this.selectedParent;

        if (this.selectedBody != null) {
            selectedParent = this.selectedBody.parent(manager);
        }
        if (this.selectedBody == null) {
            selectedParent = celestialBodyRegistry.get(new ResourceLocation("galacticraft-api", "sol"));
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
//            translationX += (deltaX - deltaY) * scollMultiplier * (ConfigManagerCore.invertMapMouseScroll.get() ? -1.0F : 1.0F) * ConfigManagerCore.mapMouseScrollSensitivity.get() * 0.2F;
//            translationY += (deltaY + deltaX) * scollMultiplier * (ConfigManagerCore.invertMapMouseScroll.get() ? -1.0F : 1.0F) * ConfigManagerCore.mapMouseScrollSensitivity.get() * 0.2F;
//        }
//
//        lastMovePosX = x;
//        lastMovePosY = y;
//    }

    protected boolean testClicked(CelestialBody<?, ?> body, int xOffset, int yPos, double x, double y, boolean grandchild) {
        int xPos = CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE + 2 + xOffset;
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
        double wheel = amount / (this.selectedBody == null ? 5.0 : 2.5);

        if (wheel != 0) {
            if (this.selectedBody == null || (this.viewState == EnumView.PREVIEW && !this.isZoomed())) {
                //Minimum zoom increased from 0.55F to 1F to allow zoom out to see other solar systems
                this.zoom = (float) Math.min(Math.max(this.zoom + wheel * ((this.zoom + 2.0)) / 10.0, 0.5f), 10.0f);
            } else {
                this.planetZoom = (float) Math.min(Math.max(this.planetZoom + wheel, -8), 8); //+12 (4x-20x)
            }
            return true;
        }
        return false;
    }


    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();

        this.ticksSinceMenuOpenF += delta;
        this.ticksTotalF += delta;

        if (this.selectedBody != null) {
            this.ticksSinceSelectionF += delta;
        }

        if (this.selectedBody == null && this.ticksSinceUnselectionF >= 0) {
            this.ticksSinceUnselectionF += delta;
        }

        matrices.pushPose();
        modelViewStack.pushPose();
        {
            RenderSystem.enableBlend();

            modelViewStack.setIdentity();
            modelViewStack.translate(0.0F, 0.0F, -9000.0F);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.backupProjectionMatrix();
            Matrix4f projectionMatrix = new Matrix4f();
            projectionMatrix.setIdentity();
            projectionMatrix.m00 = 2.0F / width;
            projectionMatrix.m11 = 2.0F / -height;
            projectionMatrix.m22 = -2.0F / 9000.0F;
            projectionMatrix.m03 = -1.0F;
            projectionMatrix.m13 = 1.0F;
            projectionMatrix.m23 = -2.0F;

            RenderSystem.setProjectionMatrix(projectionMatrix);
            RenderSystem.applyModelViewMatrix();
            resetShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            this.setBlackBackground();

            matrices.pushPose();
            {
                this.setIsometric(delta, matrices);
                float gridSize = 7000F; //194.4F;
                //TODO: Add dynamic map sizing, to allow the map to be small by default and expand when more distant solar systems are added.
                this.drawGrid(matrices.last().pose(), gridSize, height / 3f / 3.5F);
                this.drawCircles(matrices, delta);

                this.drawCelestialBodies(matrices, mouseX, mouseY, delta);

//                this.planetPosMap.clear();
//
//                for (Map.Entry<CelestialBody<?, ?>, Matrix4f> e : this.matrixMap.entrySet()) {
//                    Matrix4f planetMatrix = e.getValue();
//                    planetMatrix.multiply(projectionMatrix);
//                    assert this.minecraft != null;
//                    int x = (int) Math.floor((planetMatrix.m03 * 0.5 + 0.5) * this.minecraft.getWindow().getWidth());
//                    int y = (int) Math.floor(this.minecraft.getWindow().getHeight() - (planetMatrix.m13 * 0.5 + 0.5) * this.minecraft.getWindow().getHeight());
//                    double mx = (x * (this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getWidth()));
//                    double my = (y * (this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getHeight()));
//                    Vec2 vec = new Vec2((float) mx, (float) my);
//
//                    Vector4f newVec = new Vector4f(2, -2, 0, 0);
//                    newVec.transform(Matrix4f.createScaleMatrix(planetMatrix.m00, planetMatrix.m11, planetMatrix.m22));
//                    float iconSize = (newVec.y() * (this.minecraft.getWindow().getHeight() / 2.0F)) * (isStar(e.getKey()) ? 2 : 1) * (e.getKey() == this.selectedBody ? 1.5F : 1.0F);
//
//                    this.planetPosMap.put(e.getKey(), new Vec3(vec.x, vec.y, iconSize)); // Store size on-screen in Z-value for ease
//                }

                this.drawSelectionCursor(matrices, delta);
            }
            matrices.popPose();

            try {
                this.drawButtons(matrices, mouseX, mouseY);
            } catch (Exception e) {
                throw new RuntimeException("Problem identifying planet or dimension in an add on for Galacticraft!\n(The problem is likely caused by a dimension ID conflict.  Check configs for dimension clashes.  You can also try disabling Mars space station in configs.)", e);
            }

            this.drawBorder(matrices);
        }
        matrices.popPose();
        RenderSystem.restoreProjectionMatrix();
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    protected static void resetShader(Supplier<ShaderInstance> supplier) {
        RenderSystem.setShader(supplier);
        RenderSystem.getShader().MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
        RenderSystem.getShader().PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
    }

    protected void drawSelectionCursor(PoseStack matrices, float delta) {
        matrices.pushPose();
        if (this.selectedBody != null) {
            switch (this.selectionState) {
                case SELECTED -> {
                    this.setupMatrix(this.selectedBody, matrices, 1.0F / 15.0F, delta);
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                    float colMod = this.getZoomAdvanced() < 4.9F ? (float) (Math.sin(this.ticksSinceSelectionF / 2.0F) * 0.5F + 0.5F) : 1.0F;
                    RenderSystem.setShaderColor(1.0F, 1.0F, 0.0F, 1 * colMod);
                    int width = (int) Math.floor((getWidthForCelestialBody(this.selectedBody) / 2.0) * (isChildBody(this.selectedBody) ? 9.0 : 30.0));
                    this.blit(matrices.last().pose(), -width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
                }
                case ZOOMED -> {
                    float div = (this.zoom + 1.0F - this.planetZoom);
                    float scale = Math.max(0.3F, 1.5F / (this.ticksSinceSelectionF / 5.0F)) * 2.0F / div;
                    this.setupMatrix(this.selectedBody, matrices, scale, delta);
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                    float colMod = this.getZoomAdvanced() < 4.9F ? (float) (Math.sin(this.ticksSinceSelectionF) * 0.5F + 0.5F) : 1.0F;
                    RenderSystem.setShaderColor(0.4F, 0.8F, 1.0F, 1 * colMod);
                    int width = getWidthForCelestialBody(this.selectedBody) * 13;
                    this.blit(matrices.last().pose(), -width, -width, width * 2, width * 2, 266, 29, 100, 100, false, false);
                }
            }
        }
        matrices.popPose();
    }

    private void blit(Matrix4f model, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, boolean invertX, boolean invertY) {
        this.blit(model, x, y, width, height, u, v, uWidth, vHeight, invertX, invertY, 512, 512);
    }

    public void blit(Matrix4f model, float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, boolean invertX, boolean invertY, float texSizeX, float texSizeY) {
        resetShader(GameRenderer::getPositionTexColorShader);
        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(model, x, y + height, this.getBlitOffset()).uv((u + width0) * texModX, (v + height0) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(model, x + width, y + height, this.getBlitOffset()).uv((u + width1) * texModX, (v + height0) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(model, x + width, y, this.getBlitOffset()).uv((u + width1) * texModX, (v + height1) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(model, x, y, this.getBlitOffset()).uv((u + width0) * texModX, (v + height1) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    protected Vector3f getCelestialBodyPosition(CelestialBody<?, ?> cBody, float delta) {
        if (cBody == null) {
            return Vector3f.ZERO;
        }
        assert this.minecraft != null;
        assert this.minecraft.level != null;
        long time = this.minecraft.level.getGameTime();
        Vector3f cBodyPos = new Vector3f((float)cBody.position().x(time, delta), (float)cBody.position().y(time, delta), 0);

        if (cBody.parent(manager) != null) {
            cBodyPos.add(this.getCelestialBodyPosition(cBody.parent(manager), delta));
        } else {
            cBodyPos.add((float)this.galaxyRegistry.get(cBody.galaxy()).position().x(time, delta), (float)this.galaxyRegistry.get(cBody.galaxy()).position().y(time, delta), 0);
        }
        return cBodyPos;
    }

    public int getWidthForCelestialBody(CelestialBody<?, ?> celestialBody) {
        boolean zoomed = celestialBody == this.selectedBody && this.selectionState == EnumSelection.SELECTED;
        return isStar(celestialBody) ? (zoomed ? 12 : 8) :
                isPlanet(celestialBody) ? (zoomed ? 6 : 4) :
                        isChildBody(celestialBody) ? (zoomed ? 6 : 4) : 2;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.mapMode;
    }

    public void drawCelestialBodies(PoseStack matrices, double mouseX, double mouseY, float delta) {
        this.planetPosMap.clear();
        RenderSystem.enableTexture();

        for (CelestialBody<?, ?> body : this.bodiesToRender) {
            boolean moon = isChildBody(body);

            float alpha = getAlpha(body);

            if (alpha > 0.0F) {
                matrices.pushPose();
                this.setupMatrix(body, matrices, moon ? 0.25F : 1.0F, delta);
                CelestialDisplay<?, ?> display = body.display();
                Vector4f vector4f = display.render(matrices, Tesselator.getInstance().getBuilder(), this.getWidthForCelestialBody(body), mouseX, mouseY, delta, s -> resetAlphaShader(alpha, s));
                matrices.translate(vector4f.x(), vector4f.y(), 0);
                Matrix4f model = matrices.last().pose();
                planetPosMap.put(body, new Vec3(model.m03, model.m13, vector4f.z() * model.m00));
                matrices.popPose();
            }
        }
    }

    protected static void resetAlphaShader(float alpha, Supplier<ShaderInstance> supplier) {
        resetShader(supplier);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
    }

    /**
     * Draws gray border around outside of gui
     */
    public void drawBorder(PoseStack matrices) {
        resetShader(GameRenderer::getPositionColorShader);
        RenderSystem.colorMask(true, true, true, false);
        RenderSystem.disableBlend();
        fill(matrices, 0, 0, CelestialSelectionScreen.BORDER_SIZE, height, GREY2);
        fill(matrices, width - CelestialSelectionScreen.BORDER_SIZE, 0, width, height, GREY2);
        fill(matrices, 0, 0, width, CelestialSelectionScreen.BORDER_SIZE, GREY2);
        fill(matrices, 0, height - CelestialSelectionScreen.BORDER_SIZE, width, height, GREY2);
        fill(matrices, CelestialSelectionScreen.BORDER_SIZE, CelestialSelectionScreen.BORDER_SIZE, CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE, height - CelestialSelectionScreen.BORDER_SIZE, GREY0);
        fill(matrices, CelestialSelectionScreen.BORDER_SIZE, CelestialSelectionScreen.BORDER_SIZE, width - CelestialSelectionScreen.BORDER_SIZE, CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE, GREY0);
        fill(matrices, width - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE, CelestialSelectionScreen.BORDER_SIZE, width - CelestialSelectionScreen.BORDER_SIZE, height - CelestialSelectionScreen.BORDER_SIZE, GREY1);
        fill(matrices, CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE, width - CelestialSelectionScreen.BORDER_SIZE, height - CelestialSelectionScreen.BORDER_SIZE, GREY1);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableBlend();
    }

    public void drawButtons(PoseStack matrices, int mousePosX, int mousePosY) {
        this.setBlitOffset(0);
        boolean handledSliderPos = false;

        final int LHS = CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE;
        final int RHS = width - LHS;
        final int BOT = height - LHS;

        if (this.viewState == EnumView.PROFILE) {
            resetShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
            this.blit(width / 2 - 43, LHS, 86, 15, 266, 0, 172, 29, false, false);
            String str = I18n.get("ui.galacticraft.celestialselection.catalog").toUpperCase();
            this.font.draw(matrices, str, width / 2f - this.font.width(str) / 2f, LHS + this.font.lineHeight / 2f, WHITE);

            if (this.selectedBody != null) {
                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);


                if (mousePosX > LHS && mousePosX < LHS + 88 && mousePosY > LHS && mousePosY < LHS + 13) {
                    RenderSystem.setShaderColor(3.0F, 0.0F, 0.0F, 1.0F);
                } else {
                    RenderSystem.setShaderColor(0.9F, 0.2F, 0.2F, 1.0F);
                }

                this.blit(LHS, LHS, 88, 13, 0, 392, 148, 22, false, false);
                str = I18n.get("ui.galacticraft.celestialselection.back").toUpperCase();
                this.font.draw(matrices, str, LHS + 45 - this.font.width(str) / 2f, LHS + this.font.lineHeight / 2f - 2, WHITE);

                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                if (mousePosX > RHS - 88 && mousePosX < RHS && mousePosY > LHS && mousePosY < LHS + 13) {
                    RenderSystem.setShaderColor(0.0F, 3.0F, 0.0F, 1.0F);
                } else {
                    RenderSystem.setShaderColor(0.2F, 0.9F, 0.2F, 1.0F);
                }

                this.blit(RHS - 88, LHS, 88, 13, 0, 392, 148, 22, true, false);

                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                this.blit(LHS, BOT - 13, 88, 13, 0, 392, 148, 22, false, true);
                this.blit(RHS - 88, BOT - 13, 88, 13, 0, 392, 148, 22, true, true);
                int menuTopLeft = LHS - 115 + height / 2 - 4;
                int posX = LHS + Math.min((int) this.ticksSinceSelectionF * 10, 133) - 134;
                int posX2 = (int) (LHS + Math.min(this.ticksSinceSelectionF * 1.25F, 15) - 15);
                int textRendererPosY = menuTopLeft + CelestialSelectionScreen.BORDER_EDGE_SIZE + this.font.lineHeight / 2 - 2;
                this.blit(posX, menuTopLeft + 12, 133, 196, 0, 0, 266, 392, false, false);

//			str = this.selectedBody.getLocalizedName();
//			this.font.draw(matrices, str, posX + 20, textRendererPosY, GCCoreUtil.to32BitColor(255, 255, 255, 255));

                str = I18n.get("ui.galacticraft.celestialselection.daynightcycle") + ":";
                this.font.draw(matrices, str, posX + 5, textRendererPosY + 14, CYAN);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".daynightcycle.0");
                this.font.draw(matrices, str, posX + 10, textRendererPosY + 25, WHITE);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".daynightcycle.1");
                if (!str.isEmpty()) {
                    this.font.draw(matrices, str, posX + 10, textRendererPosY + 36, WHITE);
                }

                str = I18n.get("ui.galacticraft.celestialselection.surfacegravity") + ":";
                this.font.draw(matrices, str, posX + 5, textRendererPosY + 50, CYAN);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".surfacegravity.0");
                this.font.draw(matrices, str, posX + 10, textRendererPosY + 61, WHITE);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".surfacegravity.1");
                if (!str.isEmpty()) {
                    this.font.draw(matrices, str, posX + 10, textRendererPosY + 72, WHITE);
                }

                str = I18n.get("ui.galacticraft.celestialselection.surfacecomposition") + ":";
                this.font.draw(matrices, str, posX + 5, textRendererPosY + 88, CYAN);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".surfacecomposition.0");
                this.font.draw(matrices, str, posX + 10, textRendererPosY + 99, WHITE);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".surfacecomposition.1");
                if (!str.isEmpty()) {
                    this.font.draw(matrices, str, posX + 10, textRendererPosY + 110, WHITE);
                }

                str = I18n.get("ui.galacticraft.celestialselection.atmosphere") + ":";
                this.font.draw(matrices, str, posX + 5, textRendererPosY + 126, CYAN);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".atmosphere.0");
                this.font.draw(matrices, str, posX + 10, textRendererPosY + 137, WHITE);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".atmosphere.1");
                if (!str.isEmpty()) {
                    this.font.draw(matrices, str, posX + 10, textRendererPosY + 148, WHITE);
                }

                str = I18n.get("ui.galacticraft.celestialselection.meansurfacetemp") + ":";
                this.font.draw(matrices, str, posX + 5, textRendererPosY + 165, CYAN);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".meansurfacetemp.0");
                this.font.draw(matrices, str, posX + 10, textRendererPosY + 176, WHITE);
                str = I18n.get("ui.galacticraft.celestialselection." + ((TranslatableContents)this.selectedBody.name().getContents()).getKey() + ".meansurfacetemp.1");
                if (!str.isEmpty()) {
                    this.font.draw(matrices, str, posX + 10, textRendererPosY + 187, WHITE);
                }

                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                this.blit(posX2, menuTopLeft + 12, 17, 199, 439, 0, 32, 399, false, false);
//			this.drawRectD(posX2 + 16.5, menuTopLeft + 13, posX + 131, menuTopLeft + 14, GCCoreUtil.to32BitColor(120, 0, (int) (0.6F * 255), 255));
            }
        } else {
            String str;
            // Catalog:
            resetShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
            this.blit(LHS, LHS, 74, 11, 0, 392, 148, 22, false, false);
            str = I18n.get("ui.galacticraft.celestialselection.catalog").toUpperCase();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.font.draw(matrices, str, LHS + 40 - font.width(str) / 2f, LHS + 1, WHITE);

            int scale = (int) Math.min(95, this.ticksSinceMenuOpenF * 12.0F);
            boolean planetZoomedNotMoon = this.isZoomed() && !(isChildBody(this.selectedParent));

            // Parent frame:
            resetShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);

            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            this.blit(LHS - 95 + scale, LHS + 12, 95, 41, 0, 436, 95, 41, false, false);
            str = planetZoomedNotMoon ? I18n.get(((TranslatableContents)this.selectedBody.name().getContents()).getKey()) : this.parentName();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.font.draw(matrices, str, LHS + 9 - 95 + scale, LHS + 34, WHITE);
            resetShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 0.0f, 1.0f);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);

            // Grandparent frame:
            this.blit(LHS + 2 - 95 + scale, LHS + 14, 93, 17, 95, 436, 93, 17, false, false);
            str = planetZoomedNotMoon ? this.parentName() : this.getGrandparentName();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.font.draw(matrices, str, LHS + 7 - 95 + scale, LHS + 16, GREY3);
            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);

            List<CelestialBody<?, ?>> children = this.getChildren(/*planetZoomedNotMoon*/this.isZoomed() ? this.selectedBody : celestialBodyRegistry.get(new ResourceLocation("galacticraft-api", "sol")));
            drawChildren(matrices, children, 0, 0, true);

            if (this.mapMode) {
                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1);
                this.blit(RHS - 74, LHS, 74, 11, 0, 392, 148, 22, true, false);
                str = I18n.get("ui.galacticraft.celestialselection.exit").toUpperCase();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                this.font.draw(matrices, str, RHS - 40 - font.width(str) / 2f, LHS + 1, WHITE);
            }

            if (this.selectedBody != null) {
                // Right-hand bar (basic selectionState info)

                if (isSatellite(this.selectedBody)) {
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_1);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    CelestialBody<SatelliteConfig, SatelliteType> selectedSatellite = (CelestialBody<SatelliteConfig, SatelliteType>) this.selectedBody;
                    int stationListSize = (int) ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values().stream().filter(s -> s.parent(manager) == this.selectedBody.parent(manager)).count();

                    int max = Math.min((this.height / 2) / 14, stationListSize);
                    this.blit(RHS - 95, LHS, 95, 53, this.selectedStationOwner.length() == 0 ? 95 : 0, 186, 95, 53, false, false);
                    if (this.spaceStationListOffset <= 0) {
                        RenderSystem.setShaderColor(0.65F, 0.65F, 0.65F, 1);
                    } else {
                        RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    }
                    this.blit(RHS - 85, LHS + 45, 61, 4, 0, 239, 61, 4, false, false);
                    if (max + spaceStationListOffset >= stationListSize) {
                        RenderSystem.setShaderColor(0.65F, 0.65F, 0.65F, 1);
                    } else {
                        RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    }
                    this.blit(RHS - 85, LHS + 49 + max * 14, 61, 4, 0, 239, 61, 4, false, true);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);

                    if (((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values().stream().noneMatch(s -> s.parent(manager) == this.selectedBody.parent(manager) && s.type().ownershipData(s.config()).canAccess(this.minecraft.player))) {
                        str = I18n.get("ui.galacticraft.celestialselection.select_ss");
                        this.drawSplitString(matrices, str, RHS - 47, LHS + 20, 91, WHITE, false, false);
                    } else {
                        str = I18n.get("ui.galacticraft.celestialselection.ss_owner");
                        this.font.draw(matrices, str, RHS - 85, LHS + 18, WHITE);
                        str = this.selectedStationOwner;
                        this.font.draw(matrices, str, RHS - 47 - this.font.width(str) / 2f, LHS + 30, WHITE);
                    }

                    Iterator<CelestialBody<SatelliteConfig, SatelliteType>> it = ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values().stream().filter(s -> s.parent(manager) == this.selectedBody.parent(manager) && s.type().ownershipData(s.config()).canAccess(this.minecraft.player)).iterator();
                    int i = 0;
                    int j = 0;
                    while (it.hasNext() && i < max) {
                        CelestialBody<SatelliteConfig, SatelliteType> e = it.next();

                        if (j >= this.spaceStationListOffset) {
                            resetShader(GameRenderer::getPositionTexColorShader);
                            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                            RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                            int xOffset = 0;

                            if (e.type().ownershipData(e.config()).username().equalsIgnoreCase(this.selectedStationOwner)) {
                                xOffset -= 5;
                            }

                            this.blit(RHS - 95 + xOffset, LHS + 50 + i * 14, 93, 12, 95, 464, 93, 12, true, false);
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
                            this.font.draw(matrices, str, RHS - 88 + xOffset, LHS + 52 + i * 14, WHITE);
                            i++;
                        }
                        j++;
                    }
                } else {
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_1);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    this.blit(RHS - 96, LHS, 96, 139, 63, 0, 96, 139, false, false);
                }

                if (this.canCreateSpaceStation(this.selectedBody) && (!(isSatellite(this.selectedBody))))
                {
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    int canCreateLength = Math.max(0, this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.can_create_space_station"), 0, 0, 91, 0, true, true) - 2);
                    canCreateOffset = canCreateLength * this.font.lineHeight;
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, TEXTURE_1);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);

                    this.blit(RHS - 95, LHS + 134, 93, 4, 159, 102, 93, 4, false, false);
                    for (int barY = 0; barY < canCreateLength; ++barY)
                    {
                        this.blit(RHS - 95, LHS + 138 + barY * this.font.lineHeight, 93, this.font.lineHeight, 159, 106, 93, this.font.lineHeight, false, false);
                    }
                    this.blit(RHS - 95, LHS + 138 + canCreateOffset, 93, 43, 159, 106, 93, 43, false, false);
                    this.blit(RHS - 79, LHS + 129, 61, 4, 0, 170, 61, 4, false, false);


                    SatelliteRecipe recipe = ((Orbitable) this.selectedBody.type()).satelliteRecipe(this.selectedBody.config());
                    if (recipe != null)
                    {
                        RenderSystem.setShaderColor(0.0F, 1.0F, 0.1F, 1);
                        boolean validInputMaterials = true;

                        int i = 0;
                        for (Int2ObjectMap.Entry<Ingredient> entry : recipe.ingredients().int2ObjectEntrySet())
                        {
                            Ingredient ingredient = entry.getValue();
                            int xPos = (int) (RHS - 95 + i * 93 / (double) recipe.ingredients().size() + 5);
                            int yPos = LHS + 154 + canCreateOffset;

                            boolean b = mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16;
                            int amount = getAmountInInventory(ingredient);
                            Lighting.setupFor3DItems();
                            ItemStack stack = ingredient.getItems()[(int) (minecraft.level.getGameTime() % (20 * ingredient.getItems().length) / 20)];
                            this.itemRenderer.renderGuiItem(stack, xPos, yPos);
                            this.itemRenderer.renderGuiItemDecorations(font, stack, xPos, yPos, null);
                            Lighting.setupForFlatItems();
                            RenderSystem.enableBlend();

                            if (b) {
                                RenderSystem.depthMask(true);
                                RenderSystem.enableDepthTest();
                                matrices.pushPose();
                                matrices.translate(0, 0, 300);
                                int k = this.font.width(stack.getHoverName());
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

                                int j1 = ColorUtil.to32BitColor(190, 0, 153, 255);
                                this.fillGradient(matrices, j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
                                this.fillGradient(matrices, j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
                                this.fillGradient(matrices, j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
                                this.fillGradient(matrices, j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
                                this.fillGradient(matrices, j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
                                int k1 = ColorUtil.to32BitColor(170, 0, 153, 255);
                                int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
                                this.fillGradient(matrices, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
                                this.fillGradient(matrices, j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
                                this.fillGradient(matrices, j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
                                this.fillGradient(matrices, j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

                                this.font.draw(matrices, stack.getHoverName(), j2, k2, WHITE);

                                matrices.popPose();
                            }

                            str = "" + entry.getIntKey();
                            boolean valid = amount >= entry.getIntKey();
                            if (!valid && validInputMaterials) {
                                validInputMaterials = false;
                            }
                            int color = valid | this.minecraft.player.getAbilities().instabuild ? GREEN : RED;
                            this.font.draw(matrices, str, xPos + 8 - this.font.width(str) / 2f, LHS + 170 + canCreateOffset, color);

                            i++;
                        }

                        resetShader(GameRenderer::getPositionTexColorShader);
                        if (validInputMaterials || this.minecraft.player.getAbilities().instabuild)
                        {
                            RenderSystem.setShaderColor(0.0F, 1.0F, 0.1F, 1);
                        }
                        else
                        {
                            RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1);
                        }

                        RenderSystem.setShaderTexture(0, TEXTURE_1);

                        if (!this.mapMode)
                        {
                            if (mousePosX >= RHS - 95 && mousePosX <= RHS && mousePosY >= LHS + 182 + canCreateOffset && mousePosY <= LHS + 182 + 12 + canCreateOffset)
                            {
                                this.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);
                            }
                        }

                        this.blit(RHS - 95, LHS + 182 + canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);

                        int color = (int) ((Math.sin(this.ticksSinceMenuOpenF / 5.0) * 0.5 + 0.5) * 255);
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.can_create_space_station"), RHS - 48, LHS + 137, 91, ColorUtil.to32BitColor(255, color, 255, color), true, false);

                        if (!mapMode)
                        {
                            this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.create_ss").toUpperCase(), RHS - 48, LHS + 185 + canCreateOffset, 91, WHITE, false, false);
                        }
                    }
                    else
                    {
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.cannot_create_space_station"), RHS - 48, LHS + 138, 91, WHITE, true, false);
                    }
                }

                // Catalog overlay
                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.3F - Math.min(0.3F, this.ticksSinceSelectionF / 50.0F));
                this.blit(LHS, LHS, 74, 11, 0, 392, 148, 22, false, false);
                str = I18n.get("ui.galacticraft.celestialselection.catalog").toUpperCase();
                this.font.draw(matrices, str, LHS + 40 - font.width(str) / 2f, LHS + 1, WHITE);

                // Top bar title:
                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                if (isSatellite(this.selectedBody)) {
                    if (this.selectedStationOwner.length() == 0 || !this.selectedStationOwner.equalsIgnoreCase(this.minecraft.player.getName().getString())) {
                        RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1.0F);
                    } else {
                        RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 1.0F);
                    }
                }
                this.blit(width / 2 - 47, LHS, 94, 11, 0, 414, 188, 22, false, false);
                if (this.selectedBody.type() instanceof Landable landable && landable.accessWeight(this.selectedBody.config()) >= 0 && (!(isSatellite(this.selectedBody)))) {
                    boolean canReach;
                    if ((!this.data.canTravelTo(manager, this.selectedBody) && this.data != RocketData.empty())) {
                        canReach = false;
                        RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1.0F);
                    } else {
                        canReach = true;
                        RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 1.0F);
                    }
                    this.blit(width / 2 - 30, LHS + 11, 30, 11, 0, 414, 60, 22, false, false);
                    this.blit(width / 2, LHS + 11, 30, 11, 128, 414, 60, 22, false, false);
                    str = I18n.get("ui.galacticraft.celestialselection.tier", landable.accessWeight(this.selectedBody.config()) == -1 ? "?" : landable.accessWeight(this.selectedBody.config()));
                    this.font.draw(matrices, str, width / 2f - this.font.width(str) / 2f, LHS + 13, canReach ? GREY4 : RED3);
                }

                str = I18n.get(((TranslatableContents)this.selectedBody.name().getContents()).getKey());

                if (isSatellite(this.selectedBody)) {
                    str = I18n.get("ui.galacticraft.celestialselection.rename").toUpperCase();
                }

                this.font.draw(matrices, str, width / 2f - this.font.width(str) / 2f, LHS + 2, WHITE);

                // Catalog wedge:
                resetShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                this.blit(LHS + 4, LHS, 83, 12, 0, 477, 83, 12, false, false);

                if (!this.mapMode) {
                    resetShader(GameRenderer::getPositionTexColorShader);
                    if (!this.data.canTravelTo(manager, this.selectedBody) && this.data != RocketData.empty() || !(this.selectedBody.type() instanceof Landable) || isSatellite(this.selectedBody) && !((Satellite) this.selectedBody.type()).ownershipData(this.selectedBody.config()).canAccess(this.minecraft.player))
                    {
                        RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1);
                    } else {
                        RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 1);
                    }

                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                    this.blit(RHS - 74, LHS, 74, 11, 0, 392, 148, 22, true, false);
                    str = I18n.get("ui.galacticraft.celestialselection.launch").toUpperCase();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    this.font.draw(matrices, str, RHS - 40 - font.width(str) / 2f, LHS + 2, WHITE);
                }

                if (this.selectionState == EnumSelection.SELECTED && !(isSatellite(this.selectedBody))) {
                    handledSliderPos = true;

                    int sliderPos = this.zoomTooltipPos;
                    if (zoomTooltipPos != 38) {
                        sliderPos = Math.min((int) this.ticksSinceSelectionF * 2, 38);
                        this.zoomTooltipPos = sliderPos;
                    }

                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
                    this.blit(RHS - 182, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE - sliderPos, 83, 38, 512 - 166, 512 - 76, 166, 76, true, false);

                    boolean flag0 = getVisibleSatellitesForCelestialBody(this.selectedBody).size() > 0;
                    boolean flag1 = isPlanet(this.selectedBody) && getChildren(this.selectedBody).size() > 0;
                    if (flag0 && flag1) {
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.click_again.0"), RHS - 182 + 41, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE + 2 - sliderPos, 79, GREY5, false, false);
                    } else if (!flag0 && flag1) {
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.click_again.1"), RHS - 182 + 41, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE + 6 - sliderPos, 79, GREY5, false, false);
                    } else if (flag0) {
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.click_again.2"), RHS - 182 + 41, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE + 6 - sliderPos, 79, GREY5, false, false);
                    } else {
                        this.drawSplitString(matrices, I18n.get("ui.galacticraft.celestialselection.click_again.3"), RHS - 182 + 41, height - CelestialSelectionScreen.BORDER_SIZE - CelestialSelectionScreen.BORDER_EDGE_SIZE + 11 - sliderPos, 79, GREY5, false, false);
                    }
                }

                if (isSatellite(this.selectedBody) && renamingSpaceStation) {
//                    this.renderBackground(matrices);
                    resetShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
                    RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_1);
                    this.blit(width / 2 - 90, this.height / 2 - 38, 179, 67, 159, 0, 179, 67, false, false);
                    this.blit(width / 2 - 90 + 4, this.height / 2 - 38 + 2, 171, 10, 159, 92, 171, 10, false, false);
                    this.blit(width / 2 - 90 + 8, this.height / 2 - 38 + 18, 161, 13, 159, 67, 161, 13, false, false);
                    this.blit(width / 2 - 90 + 17, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, true, false);
                    this.blit(width / 2, this.height / 2 - 38 + 59, 72, 12, 159, 80, 72, 12, false, false);
                    str = I18n.get("ui.galacticraft.celestialselection.assign_name");
                    this.font.draw(matrices, str, width / 2f - this.font.width(str) / 2f, this.height / 2f - 35, WHITE);
                    str = I18n.get("ui.galacticraft.celestialselection.apply");
                    this.font.draw(matrices, str, width / 2f - this.font.width(str) / 2f - 36, this.height / 2f + 23, WHITE);
                    str = I18n.get("ui.galacticraft.celestialselection.cancel");
                    this.font.draw(matrices, str, width / 2f + 36 - this.font.width(str) / 2f, this.height / 2f + 23, WHITE);

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

                    this.font.draw(matrices, str0, width / 2f - this.font.width(str) / 2f, this.height / 2f - 17, WHITE);
                }

//                resetShader(GameRenderer::getPositionTexColorShader);
//                RenderSystem.setShaderTexture(0, guiMain0);
//                RenderSystem.setShaderColor(0.0F, 0.6F, 1.0F, 1);
            }
        }

        if (!handledSliderPos) {
            this.zoomTooltipPos = 0;
        }
    }

    private List<CelestialBody<SatelliteConfig, SatelliteType>> getVisibleSatellitesForCelestialBody(CelestialBody<?, ?> selectedBody) {
        if (selectedBody == null || selectedBody.type() instanceof Satellite) return Collections.emptyList();
        List<CelestialBody<SatelliteConfig, SatelliteType>> list = new LinkedList<>();
        for (CelestialBody<SatelliteConfig, SatelliteType> satellite : ((SatelliteAccessor) this.minecraft.getConnection()).getSatellites().values()) {
            if (satellite.parent(manager) == selectedBody && satellite.type().ownershipData(satellite.config()).canAccess(this.minecraft.player)) {
                list.add(satellite);
            }
        }
        return list;
    }

    private boolean isSatellite(CelestialBody<?, ?> selectedBody) {
        return selectedBody != null && selectedBody.type() instanceof Satellite;
    }

    /**
     * Draws child bodies (when appropriate) on the left-hand interface
     */
    protected int drawChildren(PoseStack matrices, List<CelestialBody<?, ?>> children, int xOffsetBase, int yOffsetPrior, boolean recursive) {
        xOffsetBase += CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE;
        final int yOffsetBase = CelestialSelectionScreen.BORDER_SIZE + CelestialSelectionScreen.BORDER_EDGE_SIZE + 50 + yOffsetPrior;
        int yOffset = 0;
        for (int i = 0; i < children.size(); i++) {
            CelestialBody<?, ?> child = children.get(i);
            int xOffset = xOffsetBase + (child.equals(this.selectedBody) ? 5 : 0);
            final int scale = (int) Math.min(95.0F, Math.max(0.0F, (this.ticksSinceMenuOpenF * 25.0F) - 95 * i));

            resetShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, CelestialSelectionScreen.TEXTURE_0);
            float brightness = child.equals(this.selectedBody) ? 0.2F : 0.0F;
            if (child.type() instanceof Landable landable && (this.data.canTravelTo(manager, child) || this.data == RocketData.empty())) {
                RenderSystem.setShaderColor(0.0F, 0.6F + brightness, 0.0F, scale / 95.0F);
            } else {
                RenderSystem.setShaderColor(0.6F + brightness, 0.0F, 0.0F, scale / 95.0F);
            }
            this.blit(3 + xOffset, yOffsetBase + yOffset + 1, 86, 10, 0, 489, 86, 10, false, false);
//            RenderSystem.setShaderColor(5 * brightness, 0.6F + 2 * brightness, 1.0F - 4 * brightness, scale / 95.0F);
            RenderSystem.setShaderColor(3 * brightness, 0.6F + 2 * brightness, 1.0F, scale / 95.0F);
            this.blit(2 + xOffset, yOffsetBase + yOffset, 93, 12, 95, 464, 93, 12, false, false);

            if (scale > 0) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                int color = 14737632;
                this.font.draw(matrices, I18n.get(((TranslatableContents)child.name().getContents()).getKey()), 7 + xOffset, yOffsetBase + yOffset + 2, color);
            }

            yOffset += 14;
            if (recursive && child.equals(this.selectedBody)) {
                List<CelestialBody<?, ?>> grandchildren = this.getChildren(child);
                if (grandchildren.size() > 0) {
                    if (this.animateGrandchildren == 14 * grandchildren.size()) {
                        yOffset += drawChildren(matrices, grandchildren, 10, yOffset, false);
                    } else {
                        if (this.animateGrandchildren >= 14) {
                            List<CelestialBody<?, ?>> partial = new LinkedList<>();
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

    public int drawSplitString(PoseStack matrices, String par1Str, int par2, int par3, int par4, int par5, boolean small, boolean simulate) {
        return this.renderSplitString(matrices, par1Str, par2, par3, par4, par5, small, simulate);
    }

    protected int renderSplitString(PoseStack matrices, String par1Str, int par2, int par3, int par4, int par6, boolean small, boolean simulate) {
        List<FormattedCharSequence> list = this.font.split(Component.translatable(par1Str), par4);

        for (Iterator<FormattedCharSequence> iterator = list.iterator(); iterator.hasNext(); par3 += this.font.lineHeight) {
            FormattedCharSequence s1 = iterator.next();
            if (!simulate) {
                this.renderStringAligned(matrices, s1, par2, par3, par4, par6);
            }
        }

        return list.size();

    }

    protected void renderStringAligned(PoseStack matrices, FormattedCharSequence par1Str, int par2, int par3, int par4, int par5) {
//        if (this.font.getBidiFlag())//fixme
//        {
//            int i1 = this.font.width(this.bidiReorder(par1Str));
//            par2 = par2 + par4 - i1;
//        }

        this.font.draw(matrices, par1Str, par2 - this.font.width(par1Str) / 2f, par3, par5);
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
        resetShader(GameRenderer::getPositionTexColorShader);
        float texModX = 1F / texSizeX;
        float texModY = 1F / texSizeY;
        float height0 = invertY ? 0 : vHeight;
        float height1 = invertY ? vHeight : 0;
        float width0 = invertX ? uWidth : 0;
        float width1 = invertX ? 0 : uWidth;
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(x, y + height, this.getBlitOffset()).uv((u + width0) * texModX, (v + height0) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(x + width, y + height, this.getBlitOffset()).uv((u + width1) * texModX, (v + height0) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(x + width, y, this.getBlitOffset()).uv((u + width1) * texModX, (v + height1) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        buffer.vertex(x, y, this.getBlitOffset()).uv((u + width0) * texModX, (v + height1) * texModY).color(RenderSystem.getShaderColor()[0], RenderSystem.getShaderColor()[1], RenderSystem.getShaderColor()[2], RenderSystem.getShaderColor()[3]).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public void setBlackBackground() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        resetShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(0.0D, height, -90.0D).color(0, 0, 0, 1).endVertex();
        buffer.vertex(width, height, -90.0D).color(0, 0, 0, 1).endVertex();
        buffer.vertex(width, 0.0D, -90.0D).color(0, 0, 0, 1).endVertex();
        buffer.vertex(0.0D, 0.0D, -90.0D).color(0, 0, 0, 1).endVertex();
        tessellator.end();
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(() -> null);
    }

    /**
     * Rotates/translates/scales to appropriate values before drawing celestial bodies
     */
    public void setIsometric(float delta, PoseStack matrices) {
        matrices.setIdentity();
        matrices.translate(width / 2.0F, height / 2f, 0);
        Vec2 cBodyPos = this.getTranslationAdvanced(delta);
        this.position = cBodyPos;
        float zoomLocal = this.getZoomAdvanced();
        this.zoom = zoomLocal;
        matrices.scale(1.1f + zoomLocal, 1.1F + zoomLocal, 1.1F + zoomLocal);
        matrices.mulPose(Vector3f.XP.rotationDegrees(55));
        matrices.translate(-cBodyPos.x, -cBodyPos.y, 0);
        matrices.mulPose(Vector3f.ZN.rotationDegrees(45));
    }

    /**
     * Draw background grid
     */
    public void drawGrid(Matrix4f model, float gridSize, float gridScale) {
        resetShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.lineWidth(2);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        gridSize += gridScale / 2;
        for (float v = -gridSize; v <= gridSize; v += gridScale) {
            buffer.vertex(model, v, -gridSize, 0).color(0.0F, 0.2F, 0.5F, 0.55F).normal(1, 1, 1).endVertex();
            buffer.vertex(model, v, gridSize, 0).color(0.0F, 0.2F, 0.5F, 0.55F).normal(1, 1, 1).endVertex();

            buffer.vertex(model, -gridSize, v, 0).color(0.0F, 0.2F, 0.5F, 0.55F).normal(1, 0, 1).endVertex();
            buffer.vertex(model, gridSize, v, 0).color(0.0F, 0.2F, 0.5F, 0.55F).normal(1, 0, 1).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());
    }

    /**
     * Draw orbit circles on gui
     */
    public void drawCircles(PoseStack matrices, float delta) {
        resetShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.lineWidth(4);
        int count = 0;

        final float theta = (float) (2f * Math.PI / 90f);
        final float cos = Mth.cos(theta);
        final float sin = Mth.sin(theta);
        final float theta2 = (float) (2f * Math.PI / -90f);
        final float cos2 = Mth.cos(theta2);
        final float sin2 = Mth.sin(theta2);

        for (CelestialBody<?, ?> body : this.bodiesToRender) {
            Vector3f systemOffset = Vector3f.ZERO;
            if (body.parent(manager) != null) {
                systemOffset = this.getCelestialBodyPosition(body.parent(manager), delta);
            }

            float x = this.lineScale(body);
            if (Float.isNaN(x)) continue;
            float y = 0;

            float alpha = getAlpha(body);

            if (alpha > 0.0F) {
                matrices.pushPose();
                matrices.mulPose(Vector3f.ZP.rotationDegrees(45));
                matrices.translate(systemOffset.x(), systemOffset.y(), systemOffset.z());
//                matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(55));
                float[] color = switch (count % 2) {
                    case 0 -> new float[]{0.0F / 1.4F, 0.6F / 1.4F, 1.0F / 1.4F, alpha / 1.4F};
                    case 1 -> new float[]{0.3F / 1.4F, 0.8F / 1.4F, 1.0F / 1.4F, alpha / 1.4F};
                    default -> throw new IllegalStateException("Unexpected value: " + count % 2);
                };

                BufferBuilder buffer = Tesselator.getInstance().getBuilder();
                buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR_NORMAL);

                float temp;
                float x1 = x;
                float y1 = y;
                Matrix4f model = matrices.last().pose();
                for (int i = 0; i < 180; i++) {
                    buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
                    if (i < 90) {
                        buffer.normal(1, 1, 1);
                    } else {
                        buffer.normal(1, -1, -1);
                    }

                    buffer.endVertex();

                    temp = x;
                    x = cos * x - sin * y;
                    y = sin * temp + cos * y;
                }
                buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone
                x = x1;
                y = y1;
                for (int i = 0; i < 180; i++) {
                    buffer.vertex(model, x, y, 0).color(color[0], color[1], color[2], color[3]);
                    if (i < 90) {
                        buffer.normal(1, 1, 1);
                    } else {
                        buffer.normal(1, -1, -1);
                    }

                    buffer.endVertex();

                    temp = x;
                    x = cos2 * x - sin2 * y;
                    y = sin2 * temp + cos2 * y;
                }
                buffer.vertex(model, x1, y1, 0).color(color[0], color[1], color[2], color[3]).normal(1, 1, 1).endVertex(); //LINE_LOOP is gone

                BufferUploader.drawWithShader(buffer.end());
                count++;
                matrices.popPose();
            }

        }
        RenderSystem.lineWidth(1);
    }

    /**
     * Returns the transparency of the selected body.
     * <p>
     * Hidden bodies will return 0.0, opaque bodies will return 1.0, and ones fading in/out will pass between those two values
     */
    public float getAlpha(CelestialBody<?, ?> body) {
        float alpha = 1.0F;

        if (isChildBody(body)) {
            boolean selected = body == this.selectedBody || (body.parent(manager) == this.selectedBody && this.selectionState != EnumSelection.SELECTED);
            boolean ready = this.lastSelectedBody != null || this.ticksSinceSelectionF > 35;
            boolean isSibling = getSiblings(this.selectedBody).contains(body);
            boolean isPossible = (!isSatellite(body) || ((Satellite) body.type()).ownershipData(body.config()).canAccess(Objects.requireNonNull(this.minecraft).player))/* || (this.possibleBodies != null && this.possibleBodies.contains(body))*/;
            if ((!selected && !isSibling) || !isPossible) {
                alpha = 0.0F;
            } else if (this.isZoomed() && ((!selected || !ready) && !isSibling)) {
                alpha = Math.min(Math.max((this.ticksSinceSelectionF - 30) / 15.0F, 0.0F), 1.0F);
            }
        } else {
            boolean isSelected = this.selectedBody == body;
            boolean isChildSelected = isChildBody(this.selectedBody);
            boolean isOwnChildSelected = isChildSelected && this.selectedBody.parent(manager) == body;

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

    protected void setupMatrix(CelestialBody<?, ?> body, PoseStack matrices, float delta) {
        this.setupMatrix(body, matrices, 1.0F, delta);
    }

    protected void setupMatrix(CelestialBody<?, ?> body, PoseStack matrices, float scaleXZ, float delta) {
        Vector3f celestialBodyPosition = this.getCelestialBodyPosition(body, delta);
        matrices.mulPose(Vector3f.ZP.rotationDegrees(45));
        matrices.translate(celestialBodyPosition.x(), celestialBodyPosition.y(), celestialBodyPosition.z());
        matrices.mulPose(Vector3f.XN.rotationDegrees(55));
        if (scaleXZ != 1.0F) {
            matrices.scale(scaleXZ, scaleXZ, 1.0F);
        }
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
