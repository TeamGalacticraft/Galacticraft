/*
 * Copyright (c) 2020 HRZN LTD
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

import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

public class PlanetSelectScreen extends Screen {
    private static final double MIN_ZOOM = 0.25D;
    private static final double MAX_ZOOM = 10.0D;

    private final List<CelestialBodyData> stars = new LinkedList<>();
    private final HashMap<CelestialBodyType, Square> posMap;
    private VertexBuffer starBuffer;

    public CelestialBodyType selectedPlanet = null;
    private final int tier;
    private float zoom = 1.0f;
    private float scrollX = 0;
    private float scrollY = 0;

    public PlanetSelectScreen(int tier) {
        super(new LiteralText(""));
        this.tier = tier;

        Map<CelestialBodyType, List<CelestialBodyType>> childmap = new LinkedHashMap<>();
        for (CelestialBodyType celestialBodyType : AddonRegistry.CELESTIAL_BODIES) {
            childmap.putIfAbsent(celestialBodyType, new ArrayList<>());

            if (celestialBodyType.getParent() != null) {
                childmap.putIfAbsent(celestialBodyType.getParent(), new ArrayList<>());

                childmap.get(celestialBodyType.getParent()).add(celestialBodyType);
            }
        }

        for (CelestialBodyType celestialBodyType : AddonRegistry.CELESTIAL_BODIES) {
            if (celestialBodyType.getParent() == null) {
                stars.add(new CelestialBodyData(celestialBodyType, genData(childmap, celestialBodyType)));
            }
        }
        posMap = new HashMap<>(childmap.size());
        childmap.clear();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return tier == Integer.MAX_VALUE;
    }

    @Override
    protected void init() {
        super.init();
        genStars();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        renderBackground(matrices);
        posMap.clear();

        matrices.push();
        matrices.translate(scrollX, scrollY, 0);
        matrices.translate(this.width / 2F, this.height / 2F, 0.0D);
        matrices.scale(this.zoom, this.zoom, this.zoom);
        renderMain(matrices, mouseX, mouseY, delta);
        matrices.pop();
        renderOverlay(matrices, mouseX, mouseY, delta);

        if (selectedPlanet != null) {
            textRenderer.draw(matrices, new TranslatableText(selectedPlanet.getTranslationKey()), 50, 10, 111111);
        }
    }

    public void renderOverlay(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    public void renderMain(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.selectedPlanet == null) {
            for (CelestialBodyData data : stars) {
                matrices.push();
                matrices.translate(data.getType().getDisplayInfo().getOrbitOffsetX(), data.getType().getDisplayInfo().getOrbitOffsetY(), 0.0F);
                renderIconCentered(matrices, data);
                for (CelestialBodyData child : data.getChildren()) {
                    matrices.push();
                    renderPlanet(matrices, child, mouseX, mouseY, delta);
                    matrices.pop();
                }
                matrices.pop();
            }
        }
    }

    private void renderPlanet(MatrixStack matrices, CelestialBodyData data, int mouseX, int mouseY, float delta) {
        matrices.push();
        drawPlanetEllipseAndTex(matrices, data.getType().getDisplayInfo().getOrbitRadX(), data.getType().getDisplayInfo().getOrbitRadY(), data);
        matrices.pop();
        for (CelestialBodyData child : data.getChildren()) {
            matrices.push();
            matrices.translate(0, 0, 1);
            renderPlanet(matrices, child, mouseX, mouseY, delta);
            matrices.pop();
        }
    }

    private void renderIconCentered(MatrixStack matrices, CelestialBodyData data) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        Vector4f vector4f = new Vector4f(0, 0, 0, 1.0F);

        client.getTextureManager().bindTexture(new Identifier(data.getType().getDisplayInfo().getIconTexture().getNamespace(), "textures/" + data.getType().getDisplayInfo().getIconTexture().getPath() + ".png"));
        float width = GlStateManager.getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        float height = GlStateManager.getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        float sizeX = (data.getType().getDisplayInfo().getScale() * data.getType().getDisplayInfo().getIconW()) / 2.0F;
        float sizeY = (data.getType().getDisplayInfo().getScale() * data.getType().getDisplayInfo().getIconH()) / 2.0F;
        Matrix4f matrix = matrices.peek().getModel();
        vector4f.transform(matrix);
        posMap.put(data.getType(), new Square(vector4f.getX(), vector4f.getY(), sizeX * 2, sizeY * 2));
        buffer.begin(7, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, -sizeX, sizeY, 1.0F).texture(data.getType().getDisplayInfo().getIconX() / width, (data.getType().getDisplayInfo().getIconY() + data.getType().getDisplayInfo().getIconH()) / height).next();
        buffer.vertex(matrix, sizeX, sizeY, 1.0F).texture((data.getType().getDisplayInfo().getIconX() + data.getType().getDisplayInfo().getIconW()) / width, (data.getType().getDisplayInfo().getIconY() + data.getType().getDisplayInfo().getIconH()) / height).next();
        buffer.vertex(matrix, sizeX, -sizeY, 1.0F).texture((data.getType().getDisplayInfo().getIconX() + data.getType().getDisplayInfo().getIconW()) / width, data.getType().getDisplayInfo().getIconY() / height).next();
        buffer.vertex(matrix, -sizeX, -sizeY, 1.0F).texture(data.getType().getDisplayInfo().getIconX() / width, data.getType().getDisplayInfo().getIconY() / height).next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        RenderSystem.disableTexture();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.push();
        starBuffer.bind();
        VertexFormats.POSITION.startDrawing(0L);
        starBuffer.draw(matrices.peek().getModel(), GL11.GL_POINTS);
        VertexFormats.POSITION.endDrawing();
        VertexBuffer.unbind();
        matrices.pop();
        RenderSystem.enableTexture();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {

        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.zoom = (float) Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, zoom + (amount * 0.05D)));
        return true;
    }

    // https://stackoverflow.com/questions/5886628/effecient-way-to-draw-ellipse-with-opengl-or-d3d#34735255
    private void drawPlanetEllipseAndTex(MatrixStack matrices, float rx, float ry, CelestialBodyData data) {
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableTexture();
        matrices.translate(data.getType().getDisplayInfo().getOrbitOffsetX(), data.getType().getDisplayInfo().getOrbitOffsetY(), 0.0F);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(data.getType().getDisplayInfo().getOrbitRot()));
        matrices.scale(1F / this.zoom, 1F / this.zoom, 1F / this.zoom);

        rx *= zoom;
        ry *= zoom;
        int segments = 360;
        float theta = (float) ((Math.PI * 2.0D) / ((double) segments));
        float c = MathHelper.cos(theta); //precalculate the sine and cosine
        float s = MathHelper.sin(theta);
        float t;

        float x = 1;//we start at angle = 0
        float y = 0;

        //47999 % 24000 = 23999 / 24000 = 0.99995833333
        double pos = (client.world.getTime() % data.getType().getDisplayInfo().getOrbitTime());
        if (pos == 0.0D) pos = 0.01D;
        pos /= data.getType().getDisplayInfo().getOrbitTime();
        GL11.glPointSize(3.0F * zoom);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_POINTS, VertexFormats.POSITION_COLOR);
        boolean drawn = false;
        float xx = 0;
        float yy = 0;
        for (int i = 0; i < segments; i++) {
            //apply radius

            buffer.vertex(matrices.peek().getModel(), x * rx, y * ry, -2).color(107, 13, 21, 255).next();

            if (!drawn && (((double) i + 1) / ((double) segments) >= pos && ((double) i - 1) / ((double) segments) <= pos)) {
                drawn = true;
                xx = x * rx;
                yy = y * ry;
            }

            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        buffer.end();

        BufferRenderer.draw(buffer);
        GL11.glPointSize(1.0F);
        matrices.pop();
        matrices.push();
        matrices.translate(data.getType().getDisplayInfo().getOrbitOffsetX(), data.getType().getDisplayInfo().getOrbitOffsetY(), 0.0F);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(data.getType().getDisplayInfo().getOrbitRot()));
        RenderSystem.enableTexture();
        if (drawn) {
            matrices.push();
            matrices.translate(xx / zoom, yy / zoom, 1.0F);
            renderIconCentered(matrices, data);
            matrices.pop();
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.scrollX += (deltaX);
        this.scrollY += (deltaY);
        return true;
    }

    private static List<CelestialBodyData> genData(Map<CelestialBodyType, List<CelestialBodyType>> map, CelestialBodyType type) {
        return map.get(type).stream().map((child) -> new CelestialBodyData(child, genData(map, child))).collect(Collectors.toList());
    }

    private void genStars() {
        Random random;
        if (MinecraftClient.getInstance().world.getLevelProperties() != null) random = new Random(MinecraftClient.getInstance().world.getLevelProperties().getSpawnX() * MinecraftClient.getInstance().world.getLevelProperties().getSpawnZ() + MinecraftClient.getInstance().world.getLevelProperties().getSpawnY());
        else random = new Random(1234567890L);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_POINTS, VertexFormats.POSITION);
        for (int i = 0; i < 12000; ++i) {
            buffer.vertex(random.nextInt((int) (MinecraftClient.getInstance().getWindow().getWidth() * zoom)) - random.nextFloat(), random.nextInt((int) (MinecraftClient.getInstance().getWindow().getHeight() * zoom)) - random.nextFloat(), 0).next();
        }
        buffer.end();
        if (starBuffer != null) {
            starBuffer.close();
        }
        starBuffer = new VertexBuffer(VertexFormats.POSITION);
        starBuffer.upload(buffer);
    }

    private static class CelestialBodyData {
        private final CelestialBodyType type;
        private final List<CelestialBodyData> children;

        private CelestialBodyData(CelestialBodyType type, List<CelestialBodyData> children) {
            this.type = type;
            this.children = children;
        }

        public CelestialBodyType getType() {
            return type;
        }

        public List<CelestialBodyData> getChildren() {
            return children;
        }
    }

    public static class Square {
        private final float x;
        private final float y;
        private final float w;
        private final float h;

        public Square(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getW() {
            return w;
        }

        public float getH() {
            return h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Square square = (Square) o;
            return x == square.x &&
                    y == square.y &&
                    w == square.w &&
                    h == square.h;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, w, h);
        }

        @Override
        public String toString() {
            return "Square{" +
                    "x=" + x +
                    ", y=" + y +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }
}
