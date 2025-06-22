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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.api.client.accessor.ClientSatelliteAccessor;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.satellite.Satellite;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.star.Star;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.mod.client.util.Graphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static dev.galacticraft.mod.Constant.CelestialScreen.*;
import static dev.galacticraft.mod.Constant.REENTRY_HEIGHT;

@SuppressWarnings({"DataFlowIssue"})
@Environment(EnvType.CLIENT)
public class CelestialScreen extends Screen implements ClientSatelliteAccessor.SatelliteListener {
    protected int borderSize = 0;
    protected int borderEdgeSize = 0;

    protected final RegistryAccess manager = Minecraft.getInstance().level.registryAccess();
    protected final Registry<CelestialBody<?, ?>> celestialBodies = this.manager.registryOrThrow(AddonRegistries.CELESTIAL_BODY);
    protected final Registry<Galaxy> galaxies = this.manager.registryOrThrow(AddonRegistries.GALAXY);
    protected final List<CelestialBody<?, ?>> bodiesToRender = new ArrayList<>();

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
    protected final Map<CelestialBody<?, ?>, Vec3> planetPositions = new IdentityHashMap<>();

    protected @Nullable CelestialBody<?, ?> selectedBody;
    protected @Nullable CelestialBody<?, ?> selectedParent = celestialBodies.get(SOL);
    protected @Nullable CelestialBody<?, ?> lastSelectedBody;

    protected EnumSelection selectionState = EnumSelection.UNSELECTED;
    protected float translationX = 0.0f;
    protected float translationY = 0.0f;
    protected boolean mouseDragging = false;

    public CelestialScreen(Component title) {
        super(title);

        this.bodiesToRender.addAll(this.celestialBodies.stream().toList());
        this.bodiesToRender.sort((o1, o2) -> Float.compare(o1.position().lineScale(), o2.position().lineScale()));

        ClientSatelliteAccessor accessor = (ClientSatelliteAccessor) Objects.requireNonNull(Minecraft.getInstance().getConnection());
        accessor.addListener(this);
    }

    @Override
    public void init() {
        assert this.minecraft != null;

        this.borderSize = this.width / 65;
        this.borderEdgeSize = this.borderSize / 4;
    }

    @Override
    public void onClose() {
        super.onClose();
        assert this.minecraft != null;
        ((ClientSatelliteAccessor) Objects.requireNonNull(this.minecraft.getConnection())).removeListener(this);
    }

    protected boolean isGrandchildBody(@Nullable CelestialBody<?, ?> type) {
        return type != null && (type.parent().isPresent() && type.parentValue(celestialBodies).parent().isPresent());
    }

    protected boolean isPlanet(@Nullable CelestialBody<?, ?> type) {
        return type != null && type.parent().isPresent() && type.parentValue(celestialBodies).type() instanceof Star;
    }

    protected boolean isStar(CelestialBody<?, ?> type) {
        return type != null && type.type() instanceof Star;
    }

    protected float lineScale(CelestialBody<?, ?> celestialBody) {
        if (Float.isNaN(celestialBody.position().lineScale())) return Float.NaN;
        return 3.0F * celestialBody.position().lineScale() * (isPlanet(celestialBody) ? 25.0F : 1.0F / 5.0F);
    }

    protected List<CelestialBody<?, ?>> getSiblings(CelestialBody<?, ?> celestialBody) {
        if (celestialBody == null) return Collections.emptyList();
        List<CelestialBody<?, ?>> bodyList = Lists.newArrayList();

        Optional<ResourceKey<CelestialBody<?, ?>>> parent = celestialBody.parent();
        if (parent.isEmpty()) return Collections.emptyList();

        for (CelestialBody<?, ?> planet : celestialBodies) {
            if (planet.parent().isPresent() && planet.parent().equals(parent)) {
                bodyList.add(planet);
            }
        }

        bodyList.sort((o1, o2) -> Float.compare(o1.position().lineScale(), o2.position().lineScale()));
        return bodyList;
    }

    protected float getZoomAdvanced() {
        if (this.ticksTotalF < 30) {
            float scale = Mth.clamp(this.ticksTotalF / 30.0F, 0.0F, 1.0F);
            return Mth.lerp(Mth.sqrt(scale), -0.75F, 0.0F);
        }

        if (this.selectedBody == null || this.selectionState != EnumSelection.ZOOMED) {
            if (!this.doneZooming) {
                float unselectScale = Mth.lerp(Mth.clamp(this.ticksSinceUnselectionF / 100.0F, 0.0F, 1.0F), this.zoom, this.preSelectZoom);

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
            float f = Mth.lerp(Mth.clamp((this.ticksSinceSelectionF - 20) / 40.0F, 0.0F, 1.0F), this.zoom, 12);

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
                float f0 = Mth.clamp(this.ticksSinceUnselectionF / 100.0F, 0.0F, 1.0F);
                if (f0 >= 0.999999F) {
                    this.ticksSinceUnselectionF = 0;
                }
                return lerpVec2(f0, this.position, this.preSelectPosition);
            }

            return new Vec2(this.position.x + translationX, this.position.y + translationY);
        }

        if (!this.isZoomed()) {
            if (isGrandchildBody(this.selectedBody)) {
                Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody.parentValue(celestialBodies), delta);
                return new Vec2(posVec.x(), posVec.y());
            }

            return new Vec2(this.position.x + translationX, this.position.y + translationY);
        }

        if (this.lastSelectedBody != null) {
            Vector3f pos3 = this.getCelestialBodyPosition(this.lastSelectedBody, delta);
            this.position = new Vec2(pos3.x(), pos3.y());
        }

        Vector3f celestialBodyPosition = this.getCelestialBodyPosition(this.selectedBody, delta);

        return lerpVec2(Mth.clamp((this.ticksSinceSelectionF - 18.0f) / 7.5F, 0.0F, 1.0F), this.position, new Vec2(celestialBodyPosition.x(), celestialBodyPosition.y()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void unselectCelestialBody() {
        this.selectionState = EnumSelection.UNSELECTED;
        this.ticksSinceUnselectionF = 0;
        this.lastSelectedBody = this.selectedBody;
        this.selectedBody = null;
        this.doneZooming = false;
        this.animateGrandchildren = 0;
    }

    @Override
    public void tick() {
        this.translationX = 0.0F;
        this.translationY = 0.0F;
        if (this.minecraft.player != null && this.minecraft.player.getY() >= REENTRY_HEIGHT) {
            this.minecraft.player.setDeltaMovement(new Vec3(0.0D, 0.0D, 0.0D));
        }
        this.keyboardTranslation();
    }

    protected void keyboardTranslation() {
        if (this.selectedBody == null || !this.isZoomed()) {
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

    @Override
    public boolean mouseDragged(double x, double y, int activeButton, double dragX, double dragY) {
        if (activeButton == GLFW.GLFW_MOUSE_BUTTON_MIDDLE || (this.mouseDragging && activeButton == GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            float scrollMultiplier = -Math.abs(this.zoom);
            if (this.zoom == -1.0F) {
                scrollMultiplier = -1.5F;
            } else if (-0.25F <= this.zoom && this.zoom <= 0.15F) {
                scrollMultiplier = -0.2F;
            } else if (this.zoom >= 0.15F) {
                scrollMultiplier = -0.15F;
            }
            this.translationX += (float) ((dragX - dragY) * scrollMultiplier * 0.4F);
            this.translationY += (float) ((dragY + dragX) * scrollMultiplier * 0.4F);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        super.mouseReleased(x, y, button);

        this.mouseDragging = false;

        this.translationX = 0;
        this.translationY = 0;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        boolean clickHandled = tryClickCelestialBody(mouseX, mouseY);

        if (!clickHandled) {
            if (this.selectedBody != null) {
                this.unselectCelestialBody();
                this.planetZoom = 0.0F;
            }

            mouseDragging = true;
        }

        CelestialBody<?, ?> selectedParent;

        if (this.selectedBody == null || this.selectedBody.parent().isEmpty()) {
            selectedParent = celestialBodies.get(SOL);
        } else {
            selectedParent = this.selectedBody.parentValue(celestialBodies);
        }

        if (this.selectedParent != selectedParent) {
            this.selectedParent = selectedParent;
        }

        return clickHandled;
    }

    protected boolean tryClickCelestialBody(double mouseX, double mouseY) {
        for (Map.Entry<CelestialBody<?, ?>, Vec3> entry : this.planetPositions.entrySet()) {
            CelestialBody<?, ?> bodyClicked = entry.getKey();
            if (this.selectedBody == null && isGrandchildBody(bodyClicked)) {
                continue;
            }

            double iconSize = entry.getValue().z; // Z value holds size on-screen

            if (mouseX >= entry.getValue().x - iconSize / 2 && mouseX <= entry.getValue().x + iconSize / 2 && mouseY >= entry.getValue().y - iconSize / 2 && mouseY <= entry.getValue().y + iconSize / 2) {
                if (this.selectedBody != bodyClicked || !this.isZoomed()) {
                    if (this.isSelected() && this.selectedBody != bodyClicked) {
                        if (this.isZoomed()) {
                            this.selectionState = EnumSelection.SELECTED;
                        }
                    }

                    if (bodyClicked != this.selectedBody) {
                        this.lastSelectedBody = this.selectedBody;
                        this.animateGrandchildren = 0;
                        if (!(isGrandchildBody(this.selectedBody)) || this.selectedBody.parentValue(celestialBodies) != bodyClicked) {
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

                    if (this.selectionState == EnumSelection.UNSELECTED) {
                        this.preSelectZoom = zoom;
                        this.preSelectPosition = this.position;
                    }

                    if (isGrandchildBody(bodyClicked)) {
                        this.selectionState = EnumSelection.ZOOMED;
                    }

                    if (this.isZoomed()) {
                        this.ticksSinceMenuOpenF = 0;
                    }

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double wheel = scrollY / (this.selectedBody == null ? 5.0 : 2.5);

        if (wheel != 0) {
            if (this.selectedBody == null || !this.isZoomed()) {
                //Minimum zoom increased from 0.55F to 1F to allow zoom out to see other solar systems
                this.zoom = (float) Mth.clamp(this.zoom + wheel * ((this.zoom + 2.0)) / 10.0, 0.01f, 10.0f);
            } else {
                this.planetZoom = (float) Mth.clamp(this.planetZoom + wheel, -8, 8); //+12 (4x-20x)
            }
            return true;
        }
        return false;
    }


    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.ticksSinceMenuOpenF += delta;
        this.ticksTotalF += delta;

        if (this.selectedBody != null) {
            this.ticksSinceSelectionF += delta;
        }

        if (this.selectedBody == null && this.ticksSinceUnselectionF >= 0) {
            this.ticksSinceUnselectionF += delta;
        }

        PoseStack matrices = graphics.pose();

        matrices.pushPose();
        RenderSystem.disableBlend();

        this.setBlackBackground(graphics);

        this.setIsometric(delta, matrices);
        float gridSize = 7000F; //194.4F;
        //TODO: Add dynamic map sizing, to allow the map to be small by default and expand when more distant solar systems are added.
        this.drawGrid(graphics, gridSize, this.height / 10.5F);

        RenderSystem.enableBlend();
        this.drawOrbitRings(graphics, mouseX, mouseY, delta);
        RenderSystem.disableBlend();

        this.drawCelestialBodies(graphics, mouseX, mouseY, delta);
        this.drawSelectionCursor(matrices, delta);

        matrices.popPose();
        this.drawBorder(graphics);
    }

    protected void drawSelectionCursor(PoseStack matrices, float delta) {
        if (this.selectedBody != null) {
            int size = 0;
            int color = 0;
            float scale = 0.0f;
            switch (this.selectionState) {
                case SELECTED -> {
                    float colMod = this.getZoomAdvanced() < 4.9F ? (Mth.sin(this.ticksSinceSelectionF / 2.0F) * 0.5F + 0.5F) : 1.0F;

                    scale = 1.0f / 15.0f;
                    size = Mth.floor((getWidthForCelestialBody(this.selectedBody) / 2.0) * (isGrandchildBody(this.selectedBody) ? 18.0 : 60.0));
                    color = FastColor.ARGB32.color((int) (255 * colMod), 255, 255, 0);
                }
                case ZOOMED -> {
                    float div = (this.zoom + 1.0F - this.planetZoom);
                    float colMod = this.getZoomAdvanced() < 4.9F ? (Mth.sin(this.ticksSinceSelectionF / 2.0f) * 0.5F + 0.5F) : 1.0F;

                    scale = Math.max(0.3F, 1.5F / (this.ticksSinceSelectionF / 5.0F)) * 2.0F / div;
                    size = getWidthForCelestialBody(this.selectedBody) * 26;
                    color = FastColor.ARGB32.color((int) (255 * colMod), 102, 204, 255);
                }
            }
            matrices.pushPose();
            this.setupMatrix(this.selectedBody, matrices, scale, delta);
            Graphics.blitCentered(matrices.last().pose(), 0.0f, 0.0f, size, size, 0, SELECTION_CURSOR_U, SELECTION_CURSOR_V, SELECTION_CURSOR_SIZE, SELECTION_CURSOR_SIZE, SELECTION_CURSOR_SIZE, SELECTION_CURSOR_SIZE, SELECTION_CURSOR, color);
            matrices.popPose();
        }
    }

    protected Vector3f getCelestialBodyPosition(CelestialBody<?, ?> cBody, float delta) {
        if (cBody == null) {
            return new Vector3f();
        }
        assert this.minecraft != null;
        assert this.minecraft.level != null;
        long time = this.minecraft.level.getGameTime();
        Vector3f cBodyPos = new Vector3f((float) cBody.position().x(time, delta), (float) cBody.position().y(time, delta), 0);

        if (cBody.parent().isPresent()) {
            cBodyPos.add(this.getCelestialBodyPosition(cBody.parentValue(celestialBodies), delta));
        } else {
            cBodyPos.add((float) cBody.galaxyValue(galaxies, celestialBodies).position().x(time, delta), (float) cBody.galaxyValue(galaxies, celestialBodies).position().y(time, delta), 0);
        }
        return cBodyPos;
    }

    public int getWidthForCelestialBody(CelestialBody<?, ?> celestialBody) {
        boolean zoomed = celestialBody == this.selectedBody && this.selectionState == EnumSelection.SELECTED;
        return isStar(celestialBody) ? (zoomed ? 12 : 8) :
                isPlanet(celestialBody) ? (zoomed ? 6 : 4) :
                        isGrandchildBody(celestialBody) ? (zoomed ? 6 : 4) : 2;
    }

    public void drawCelestialBodies(GuiGraphics graphics, double mouseX, double mouseY, float delta) {
        PoseStack matrices = graphics.pose();
        this.planetPositions.clear();

        for (CelestialBody<?, ?> body : this.bodiesToRender) {
            boolean moon = isGrandchildBody(body);

            float alpha = getAlpha(body);

            if (alpha > 0.0F) {
                matrices.pushPose();
                this.setupMatrix(body, matrices, moon ? 0.25F : 1.0F, delta);
                CelestialDisplay<?, ?> display = body.display();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                Vector4f vector4f = display.render(graphics, getWidthForCelestialBody(body), mouseX, mouseY, delta);
                Matrix4f planetMatrix = matrices.last().pose();

                Matrix4f matrix0 = RenderSystem.getProjectionMatrix().mul(planetMatrix, planetMatrix);
                int x = Mth.floor((matrix0.m30() * 0.5 + 0.5) * minecraft.getWindow().getScreenWidth());
                int y = Mth.floor(minecraft.getWindow().getScreenHeight() - (matrix0.m31() * 0.5 + 0.5) * minecraft.getWindow().getScreenHeight());
                double planetX = (x * (this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getScreenWidth()));
                double planetY = (y * (this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getScreenHeight()));

                Matrix4f scaleVec = new Matrix4f();
                scaleVec.m00(matrix0.m00());
                scaleVec.m11(matrix0.m11());
                scaleVec.m22(matrix0.m22());
                Vector4f newVec = scaleVec.transform(new Vector4f(2, -2, 0, 0), new Vector4f());
                float iconSize = (newVec.y * (minecraft.getWindow().getScreenHeight() / 2.0F)) * (body.type() instanceof Star ? 2 : 1) * (body == this.selectedBody ? 1.5F : 1.0F);

                this.planetPositions.put(body, new Vec3(planetX, planetY, iconSize)); // Store size on-screen in Z-value for ease
                matrices.popPose();
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Draws border around gui
     */
    public void drawBorder(GuiGraphics graphics) {
        graphics.fill(0, 0, this.borderSize, this.height, BORDER_Z, BORDER_GREY);
        graphics.fill(this.width - this.borderSize, 0, this.width, this.height, BORDER_Z, BORDER_GREY);
        graphics.fill(0, 0, this.width, this.borderSize, BORDER_Z, BORDER_GREY);
        graphics.fill(0, this.height - this.borderSize, this.width, this.height, BORDER_Z, BORDER_GREY);
        graphics.fill(this.borderSize, this.borderSize, this.borderSize + this.borderEdgeSize, this.height - this.borderSize, BORDER_Z, BORDER_EDGE_TOP_LEFT);
        graphics.fill(this.borderSize, this.borderSize, this.width - this.borderSize, this.borderSize + this.borderEdgeSize, BORDER_Z, BORDER_EDGE_TOP_LEFT);
        graphics.fill(this.width - this.borderSize - this.borderEdgeSize, this.borderSize, this.width - this.borderSize, this.height - this.borderSize, BORDER_Z, BORDER_EDGE_BOTTOM_RIGHT);
        graphics.fill(this.borderSize + this.borderEdgeSize, this.height - this.borderSize - this.borderEdgeSize, this.width - this.borderSize, this.height - this.borderSize, BORDER_Z, BORDER_EDGE_BOTTOM_RIGHT);
    }

    protected boolean isSatellite(CelestialBody<?, ?> selectedBody) {
        return selectedBody != null && selectedBody.isSatellite();
    }

    public void setBlackBackground(GuiGraphics graphics) {
        RenderSystem.depthMask(false);
        graphics.fill(0, 0, this.width, this.height, 0, BLACK);
        RenderSystem.depthMask(true);
    }

    /**
     * Rotates/translates/scales to appropriate values before drawing celestial bodies
     */
    public void setIsometric(float delta, PoseStack matrices) {
        matrices.setIdentity();
        matrices.translate(this.width / 2.0f, this.height / 2.0f, 0);
        matrices.mulPose(Axis.XP.rotationDegrees(55));
        matrices.mulPose(Axis.ZN.rotationDegrees(45));

        this.position = this.getTranslationAdvanced(delta);
        this.zoom = this.getZoomAdvanced();
        matrices.scale(1.1f + this.zoom, 1.1F + this.zoom, 1.1F + this.zoom);
        matrices.translate(-this.position.x, -this.position.y, 0);
    }

    /**
     * Draw background grid
     */
    public void drawGrid(GuiGraphics graphics, float gridSize, float gridScale) {
        RenderSystem.depthMask(false);
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.LINES);
        Matrix4f model = graphics.pose().last().pose();

        RenderSystem.lineWidth(2.0f);
        gridSize += gridScale / 2.0f;

        for (float v = -gridSize; v <= gridSize; v += gridScale) {
            buffer.addVertex(model, v, -gridSize, 0).setColor(0, 51, 127, 140).setNormal(1.0f, 1.0f, 1.0f)
                    .addVertex(model, v, gridSize, 0).setColor(0, 51, 127, 140).setNormal(1.0f, 1.0f, 1.0f)
                    .addVertex(model, -gridSize, v, 0).setColor(0, 51, 127, 140).setNormal(1.0f, 0.0f, 1.0f)
                    .addVertex(model, gridSize, v, 0).setColor(0, 51, 127, 140).setNormal(1.0f, 0.0f, 1.0f);
        }

        graphics.bufferSource().endBatch();
        RenderSystem.depthMask(true);
    }

    /**
     * Draw orbit circles on gui
     */
    public void drawOrbitRings(GuiGraphics graphics, double mouseX, double mouseY, float delta) {
        RenderSystem.lineWidth(4.0f);
        int count = 0;

        for (CelestialBody<?, ?> body : this.bodiesToRender) {
            Vector3f systemOffset = new Vector3f();
            if (body.parent().isPresent()) {
                systemOffset = getCelestialBodyPosition(body.parentValue(celestialBodies), delta);
            }
            if (body.ring().render(body, graphics, count, systemOffset, getAlpha(body), lineScale(body), mouseX, mouseY, delta))
                count++;
        }
        RenderSystem.lineWidth(1.0f);
    }

    /**
     * Returns the transparency of the selected body.
     * <p>
     * Hidden bodies will return 0.0, opaque bodies will return 1.0, and ones fading in/out will pass between those two values
     */
    public float getAlpha(CelestialBody<?, ?> body) {
        float alpha = 1.0F;

        if (isGrandchildBody(body)) {
            boolean selected = body == this.selectedBody || (body.parentValue(celestialBodies) == this.selectedBody && this.selectionState != EnumSelection.SELECTED);
            boolean ready = this.lastSelectedBody != null || this.ticksSinceSelectionF > 35;
            boolean isSibling = getSiblings(this.selectedBody).contains(body);
            boolean isPossible = (!isSatellite(body) || ((Satellite) body.type()).ownershipData(body.config()).canAccess(Objects.requireNonNull(this.minecraft).player))/* || (this.possibleBodies != null && this.possibleBodies.contains(body))*/;
            if ((!selected && !isSibling) || !isPossible) {
                alpha = 0.0F;
            } else if (this.isZoomed() && ((!selected || !ready) && !isSibling)) {
                alpha = Mth.clamp((this.ticksSinceSelectionF - 30) / 15.0F, 0.0F, 1.0F);
            }
        } else {
            boolean isSelected = this.selectedBody == body;
            boolean isChildSelected = isGrandchildBody(this.selectedBody);
            boolean isOwnChildSelected = isChildSelected && this.selectedBody.parentValue(celestialBodies) == body;

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

    protected void setupMatrix(CelestialBody<?, ?> body, PoseStack matrices, float scaleXZ, float delta) {
        Vector3f celestialBodyPosition = this.getCelestialBodyPosition(body, delta);
        matrices.translate(celestialBodyPosition.x(), celestialBodyPosition.y(), celestialBodyPosition.z());
        matrices.mulPose(Axis.ZP.rotationDegrees(45));
        matrices.mulPose(Axis.XN.rotationDegrees(55));
        if (scaleXZ != 1.0F) {
            matrices.scale(scaleXZ, scaleXZ, 1.0F);
        }
    }

    @Contract("_, _, _ -> new")
    protected static @NotNull Vec2 lerpVec2(float delta, @NotNull Vec2 start, @NotNull Vec2 end) {
        return new Vec2(Mth.lerp(delta, start.x, end.x), Mth.lerp(delta, start.y, end.y));
    }

    @Override
    public void onSatelliteUpdated(CelestialBody<SatelliteConfig, SatelliteType> satellite, boolean added) {
        if (!added) {
            this.bodiesToRender.remove(satellite);
        } else {
            this.bodiesToRender.add(satellite);
        }

        this.bodiesToRender.sort((o1, o2) -> Float.compare(o1.position().lineScale(), o2.position().lineScale()));
    }

    protected enum EnumSelection {
        UNSELECTED,
        SELECTED,
        ZOOMED
    }
}
