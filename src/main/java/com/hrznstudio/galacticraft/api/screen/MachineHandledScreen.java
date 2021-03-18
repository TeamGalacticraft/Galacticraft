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

package com.hrznstudio.galacticraft.api.screen;

import alexiil.mc.lib.attributes.fluid.SingleFluidTank;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.AutomationType;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.machine.RedstoneState;
import com.hrznstudio.galacticraft.api.machine.SecurityInfo;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.client.gui.widget.machine.AbstractWidget;
import com.hrznstudio.galacticraft.energy.api.Capacitor;
import com.hrznstudio.galacticraft.item.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.MachineComponent;
import com.hrznstudio.galacticraft.util.ColorUtils;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public abstract class MachineHandledScreen<C extends MachineScreenHandler<? extends MachineBlockEntity>> extends HandledScreen<C> implements DrawableUtils{
    public static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.MACHINE_CONFIG_PANELS));
    public static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.OVERLAY));

    private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
    private static final ItemStack UNLIT_TORCH = new ItemStack(GalacticraftItems.UNLIT_TORCH);
    private static final ItemStack REDSTONE_TORCH = new ItemStack(Items.REDSTONE_TORCH);
    private static final ItemStack WRENCH = new ItemStack(GalacticraftItems.STANDARD_WRENCH);
    private static final ItemStack ALUMINUM_WIRE = new ItemStack(GalacticraftBlocks.ALUMINUM_WIRE);

    public static final int PANEL_WIDTH = 100;
    public static final int PANEL_HEIGHT = 93;
    public static final int TAB_WIDTH = 22;
    public static final int TAB_HEIGHT = 22;

    private static final int SPACING = 4;

    private static final int BUTTON_U = 0;
    private static final int BUTTON_V = 208;
    private static final int BUTTON_HOVERED_V = 224;
    private static final int BUTTON_PRESSED_V = 240;
    private static final int BUTTON_WIDTH = 16;
    private static final int BUTTON_HEIGHT = 16;

    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    private static final int ICON_LOCK_PRIVATE_U = 221;
    private static final int ICON_LOCK_PRIVATE_V = 47;

    private static final int ICON_LOCK_PARTY_U = 204;
    private static final int ICON_LOCK_PARTY_V = 64;

    private static final int ICON_LOCK_PUBLIC_U = 204;
    private static final int ICON_LOCK_PUBLIC_V = 47;

    private static final int TAB_REDSTONE_U = 203;
    private static final int TAB_REDSTONE_V = 0;

    private static final int TAB_CONFIG_U = 203;
    private static final int TAB_CONFIG_V = 23;

    private static final int TAB_STATS_U = 226;
    private static final int TAB_STATS_V = 0;

    private static final int TAB_SECURITY_U = 226;
    private static final int TAB_SECURITY_V = 23;

    private static final int PANEL_REDSTONE_U = 0;
    private static final int PANEL_REDSTONE_V = 0;

    private static final int PANEL_CONFIG_U = 0;
    private static final int PANEL_CONFIG_V = 93;

    private static final int PANEL_STATS_U = 101;
    private static final int PANEL_STATS_V = 0;

    private static final int PANEL_SECURITY_U = 101;
    private static final int PANEL_SECURITY_V = 93;

    private Identifier ownerSkin;

    //todo make this less cursed
    private final RenderContext renderContext = new RenderContext() {
        private final Matrix4f matrix = Util.make(new Matrix4f(), Matrix4f::loadIdentity);
        private final ObjectArrayList<QuadTransform> transformStack = new ObjectArrayList<>();
        private final QuadTransform NO_TRANSFORM = (q) -> true;
        private final QuadTransform stackTransform = (q) -> {
            int i = transformStack.size() - 1;

            while (i >= 0) {
                if (!transformStack.get(i--).transform(q)) {
                    return false;
                }
            }

            return true;
        };

        private QuadTransform activeTransform = NO_TRANSFORM;

        @Override
        public Consumer<Mesh> meshConsumer() {
            return mesh -> {
                mesh.forEach(quadView -> {
                    quadView.copyTo(RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter());
                    activeTransform.transform(RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter());
                    quadView = RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();
                    final float u0 = quadView.spriteU(0, 0);
                    final float v0 = quadView.spriteV(0, 0);
                    final float u1 = quadView.spriteU(1, 0);
                    final float v1 = quadView.spriteV(1, 0);
                    final float u2 = quadView.spriteU(2, 0);
                    final float v2 = quadView.spriteV(2, 0);
                    final float u3 = quadView.spriteU(3, 0);
                    final float v3 = quadView.spriteV(3, 0);
                    drawTexturedQuad(matrix, 0, 16, 0, 16, 0, u0, v0, u1, v1, u2, v2, u3, v3);
                });
            };
        }

        @Override
        public Consumer<BakedModel> fallbackConsumer() {
            return model -> {
                BlockState state = world.getBlockState(pos);
                MatrixStack matrices = new MatrixStack();
                VertexConsumer consumer = VertexConsumerProvider.immediate(client.getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getSolid())).getBuffer(RenderLayer.getSolid());
                matrices.push();
                matrices.translate(33, 24, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.UP, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
                matrices.push();
                matrices.translate(14, 43, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.WEST, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
                matrices.push();
                matrices.translate(33, 43, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.NORTH, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
                matrices.push();
                matrices.translate(52, 43, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.EAST, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
                matrices.push();
                matrices.translate(71, 43, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.SOUTH, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
                matrices.push();
                matrices.translate(33, 22, 0);
                for (BakedQuad quad : model.getQuads(state, Direction.DOWN, world.getRandom())) {
                    consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                }
                matrices.pop();
            };
        }

        @Override
        public QuadEmitter getEmitter() {
            return RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();
        }

        protected final boolean transform(MutableQuadView q) {
            return activeTransform.transform(q);
        }

        protected boolean hasTransform() {
            return activeTransform != NO_TRANSFORM;
        }

        @Override
        public void pushTransform(QuadTransform transform) {
            if (transform == null) {
                throw new NullPointerException("Renderer received null QuadTransform.");
            }

            transformStack.push(transform);

            if (transformStack.size() == 1) {
                activeTransform = transform;
            } else if (transformStack.size() == 2) {
                activeTransform = stackTransform;
            }
        }

        @Override
        public void popTransform() {
            transformStack.pop();

            if (transformStack.size() == 0) {
                activeTransform = NO_TRANSFORM;
            } else if (transformStack.size() == 1) {
                activeTransform = transformStack.get(0);
            }
        }
    };

    protected final BlockPos pos;
    protected final World world;
    private final List<AbstractWidget> widgets = new LinkedList<>();

    private final Map<BlockFace, AutomationType> config = new EnumMap<>(BlockFace.class);

    public MachineHandledScreen(C handler, PlayerInventory playerInventory, World world, BlockPos pos, Text textComponent) {
        super(handler, playerInventory, textComponent);
        this.pos = pos;
        this.world = world;

        for (BlockFace face : Constants.Misc.BLOCK_FACES) {
            config.put(face, handler.machine.getSideConfiguration().get(face).getAutomationType());
        }

        for (List<MachineComponent<Capacitor>> components : handler.getMachineCapacitors().values()) {
            for (MachineComponent<Capacitor> component : components) {
                this.addWidget(component.createWidget(handler.machine));
            }
        }

        for (List<MachineComponent<SingleFluidTank>> components : handler.getMachineTanks().values()) {
            for (MachineComponent<SingleFluidTank> component : components) {
                this.addWidget(component.createWidget(handler.machine));
            }
        }

        this.client.getSkinProvider().loadSkin(handler.machine.getSecurity().getOwner(), (type, identifier, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                MachineHandledScreen.this.ownerSkin = identifier;
            }
        }, true);
    }

    @NotNull
    protected Collection<? extends Text> getEnergyTooltipLines() {
        return Collections.emptyList();
    }

    public void drawConfigTabs(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DiffuseLighting.disable();
        if (this.handler.machine != null) {
            final MachineBlockEntity machine = this.handler.machine;
            boolean secondary = false;
            this.client.getTextureManager().bindTexture(TEXTURE);
            for (Tab tab : Tab.values()) {
                this.drawTexture(matrices, this.x + (tab.isLeft() ? tab.isOpen() ? -PANEL_WIDTH : -22 : this.backgroundWidth), this.y + (secondary ? (Tab.values()[tab.ordinal() - 1].isOpen() ? PANEL_HEIGHT : TAB_HEIGHT) : 0) + SPACING, tab.getU(), tab.getV(), tab.isOpen() ? PANEL_WIDTH : TAB_WIDTH, tab.isOpen() ? PANEL_HEIGHT : TAB_HEIGHT);
                secondary = !secondary;
            }
            matrices.push();
            matrices.translate(this.x, this.y, 0);

            if (Tab.REDSTONE.isOpen()) {
                matrices.push();
                matrices.translate(-PANEL_WIDTH, SPACING, 0);
                this.drawButton(matrices, 18, 33, mouseX, mouseY, delta, machine.getRedstone() == RedstoneState.IGNORE);
                this.drawButton(matrices, 43, 33, mouseX, mouseY, delta, machine.getRedstone() == RedstoneState.LOW);
                this.drawButton(matrices, 68, 33, mouseX, mouseY, delta, machine.getRedstone() == RedstoneState.HIGH);
                this.renderItemIcon(matrices, 3, 3, REDSTONE);
                this.renderItemIcon(matrices, 18, 33, REDSTONE);
                this.renderItemIcon(matrices, 43, 33, UNLIT_TORCH);
                this.renderItemIcon(matrices, 68, 33, REDSTONE_TORCH);

                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.redstone")
                        .setStyle(Constants.Text.GRAY_STYLE), 19, 6, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.redstone.state",
                        machine.getRedstone().getName()).setStyle(Constants.Text.GRAY_STYLE), 11, 54, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.redstone.status",
                        machine.disabled() ? new TranslatableText("ui.galacticraft-rewoven.machine.redstone.status.enabled")
                                : new TranslatableText("ui.galacticraft-rewoven.machine.redstone.status.disabled"))
                        .setStyle(Constants.Text.GRAY_STYLE), 11, 54 + this.textRenderer.fontHeight + 2, ColorUtils.WHITE);

                matrices.pop();
            }
            if (Tab.CONFIGURATION.isOpen()) {
                matrices.push();
                matrices.translate(-PANEL_WIDTH, 26, 0);
                this.renderItemIcon(matrices, 3, 3, WRENCH);
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration")
                        .setStyle(Constants.Text.GRAY_STYLE), 19, 6, ColorUtils.WHITE);

                this.client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
                BlockState state = this.world.getBlockState(this.pos);
                BakedModel model = this.client.getBakedModelManager().getBlockModels().getModel(state);
                if (((FabricBakedModel) model).isVanillaAdapter()) {
                    VertexConsumer consumer = VertexConsumerProvider.immediate(this.client.getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getSolid())).getBuffer(RenderLayer.getSolid());
                    matrices.push();
                    matrices.translate(33, 24, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.UP, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                    matrices.push();
                    matrices.translate(14, 43, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.WEST, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                    matrices.push();
                    matrices.translate(33, 43, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.NORTH, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                    matrices.push();
                    matrices.translate(52, 43, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.EAST, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                    matrices.push();
                    matrices.translate(71, 43, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.SOUTH, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                    matrices.push();
                    matrices.translate(33, 22, 0);
                    for (BakedQuad quad : model.getQuads(state, Direction.DOWN, this.world.getRandom())) {
                        consumer.quad(matrices.peek(), quad, 1, 1, 1, 15728880, OverlayTexture.DEFAULT_UV);
                    }
                    matrices.pop();
                } else {
                    ((FabricBakedModel) model).emitBlockQuads(world, state, pos, world::getRandom, this.renderContext);
                }
                matrices.pop();
            }
            if (Tab.STATS.isOpen()) {
                matrices.push();
                matrices.translate(this.backgroundWidth, SPACING, 0);
                this.renderItemIcon(matrices, 3, 3, ALUMINUM_WIRE);
                if (ownerSkin != null) {
                    this.client.getTextureManager().bindTexture(ownerSkin);
                    drawTexture(matrices, 11, 25, 24, 24, 8, 8, 8, 8, 64, 64);
                } else {
                    fill(matrices, 11, 25, 35, 49, ColorUtils.WHITE);
                }
                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.stats")
                        .setStyle(Constants.Text.GREEN_STYLE), 19, 6, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText((machine.getCachedState() != null ? machine.getCachedState()
                        : this.world.getBlockState(this.pos)).getBlock().getTranslationKey()), 40, 26, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new LiteralText(machine.getSecurity().getOwner().getName())
                        .setStyle(Constants.Text.GRAY_STYLE), 40, 39, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.stats.gjt", "N/A")
                        .setStyle(Constants.Text.GRAY_STYLE), 11, 54, ColorUtils.WHITE);
//                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.stats.todo", "N/A")
//                        .setStyle(Constants.Text.GRAY_STYLE), 11, 54, ColorUtils.WHITE);

                matrices.pop();
            }
            if (Tab.SECURITY.isOpen()) {
                matrices.push();
                matrices.translate(this.backgroundWidth, 26, 0);
                this.client.getTextureManager().bindTexture(TEXTURE);
                this.drawTexture(matrices, 3, 3, ICON_LOCK_PRIVATE_U, ICON_LOCK_PRIVATE_V, ICON_WIDTH, ICON_HEIGHT);

                this.drawButton(matrices, 18, 33, mouseX, mouseY, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.PUBLIC || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawButton(matrices, 43, 33, mouseX, mouseY, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.TEAM || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawButton(matrices, 68, 33, mouseX, mouseY, delta, machine.getSecurity().getAccessibility() == SecurityInfo.Accessibility.PRIVATE || !machine.getSecurity().isOwner(playerInventory.player));
                this.drawTexture(matrices, 18, 33, ICON_LOCK_PRIVATE_U, ICON_LOCK_PRIVATE_V, ICON_WIDTH, ICON_HEIGHT);
                this.drawTexture(matrices, 43, 33, ICON_LOCK_PARTY_U, ICON_LOCK_PARTY_V, ICON_WIDTH, ICON_HEIGHT);
                this.drawTexture(matrices, 68, 33, ICON_LOCK_PUBLIC_U, ICON_LOCK_PUBLIC_V, ICON_WIDTH, ICON_HEIGHT);

                this.textRenderer.drawWithShadow(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.security")
                        .setStyle(Constants.Text.GRAY_STYLE), 19, 6, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.security.state",
                        machine.getSecurity().getAccessibility().getName()).setStyle(Constants.Text.GRAY_STYLE), 11, 37, ColorUtils.WHITE);
                this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.security.owned_by", machine.getSecurity().getOwner().getName())
                        .setStyle(Constants.Text.GRAY_STYLE), 11, 37 + this.textRenderer.fontHeight + 2, ColorUtils.WHITE);

                matrices.pop();
            }
            matrices.pop();
        }
    }

    private void renderItemIcon(MatrixStack matrices, int x, int y, ItemStack stack) {
        BakedModel model = this.itemRenderer.getHeldItemModel(stack, this.world, this.playerInventory.player);
        matrices.push();
        this.client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        this.client.getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.enableAlphaTest();
//        RenderSystem.defaultAlphaFunc();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        matrices.translate(x + 8, y + 8, 100.0F + this.getZOffset());
        matrices.scale(16, -16, 16);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        this.itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
//        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
//
//        RenderSystem.disableAlphaTest();
//        RenderSystem.disableRescaleNormal();
        matrices.pop();
    }

    public void drawButton(MatrixStack matrices, int x, int y, double mouseX, double mouseY, float delta, boolean pressed) {
        this.client.getTextureManager().bindTexture(TEXTURE);
        if (pressed) this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_PRESSED_V, BUTTON_WIDTH, BUTTON_HEIGHT);
        if (check(mouseX, mouseY, x, y, 16, 16)) {
            this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_HOVERED_V, BUTTON_WIDTH, BUTTON_HEIGHT);
        } else {
            this.drawTexture(matrices, x, y, BUTTON_U, BUTTON_V, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
    }

    public boolean checkTabsClick(MatrixStack matrices, double mouseX, double mouseY, int button) {
        final double mX = mouseX, mY = mouseY;
        if (this.handler.machine != null) {
            final MachineBlockEntity machine = this.handler.machine;
            if (Tab.REDSTONE.isOpen()) {
                mouseX -= (PANEL_WIDTH + this.x);
                mouseY -= this.y;
                if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, 20)) {
                    Tab.REDSTONE.click();
                    return true;
                }
                if (check(mouseX, mouseY, 18, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    machine.setRedstone(RedstoneState.IGNORE);
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 43, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    machine.setRedstone(RedstoneState.LOW);
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 68, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    machine.setRedstone(RedstoneState.HIGH);
                    this.playButtonSound();
                    return true;
                }
                if (button == 1) {
                    if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT)) {
                        return true;
                    }
                }
            } else {
                mouseX -= (TAB_WIDTH + this.x);
                mouseY -= this.y;
                if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                    Tab.REDSTONE.click();
                    return true;
                }
            }
            mouseX = mX;
            mouseY = mY;
            if (Tab.CONFIGURATION.isOpen()) {
                mouseX -= (PANEL_WIDTH + this.x);
                mouseY -= (this.y + TAB_HEIGHT + SPACING);
                if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, 20)) {
                    Tab.CONFIGURATION.click();
                    return true;
                }
                if (check(mouseX, mouseY, 33, 24, 16, 16)) { //todo side config
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 14, 43, 16, 16)) {
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 33, 43, 16, 16)) {
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 52, 43, 16, 16)) {
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 71, 43, 16, 16)) {
                    this.playButtonSound();
                    return true;
                }
                if (check(mouseX, mouseY, 33, 22, 16, 16)) {
                    this.playButtonSound();
                    return true;
                }
                if (button == 1) {
                    if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT)) {
                        return true;
                    }
                }
            } else {
                mouseX -= (PANEL_WIDTH + this.x);
                if (Tab.REDSTONE.isOpen()) {
                    mouseY -= (this.y + PANEL_HEIGHT + SPACING);
                } else {
                    mouseY -= (this.y + TAB_HEIGHT + SPACING);
                }
                if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                    Tab.CONFIGURATION.click();
                    return true;
                }
            }
            mouseX = mX;
            mouseY = mY;
            if (Tab.STATS.isOpen()) {
                if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, 20)) {
                    Tab.STATS.click();
                    return true;
                }
                if (button == 1) {
                    if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT)) {
                        Tab.STATS.click();
                        return true;
                    }
                }
            } else {
                if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                    Tab.STATS.click();
                    return true;
                }
            }
            mouseX = mX;
            mouseY = mY;
            if (Tab.SECURITY.isOpen()) {
                mouseX -= (-this.backgroundWidth + this.x);
                mouseY -= (this.y + TAB_HEIGHT + SPACING);
                if (check(mouseX, mouseY, 0, 0, PANEL_WIDTH, 20)) {
                    Tab.SECURITY.click();
                    return true;
                }

                if (machine.getSecurity().isOwner(this.playerInventory.player)) {
                    if (check(mouseX, mouseY, 18, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                        machine.getSecurity().setAccessibility(SecurityInfo.Accessibility.PRIVATE);
                        this.playButtonSound();
                        return true;
                    }
                    if (check(mouseX, mouseY, 43, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                        machine.getSecurity().setAccessibility(SecurityInfo.Accessibility.TEAM);
                        this.playButtonSound();
                        return true;
                    }
                    if (check(mouseX, mouseY, 68, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                        machine.getSecurity().setAccessibility(SecurityInfo.Accessibility.PUBLIC);
                        this.playButtonSound();
                        return true;
                    }
                }
            } else {
                mouseX -= (-this.backgroundWidth + this.x);
                if (Tab.STATS.isOpen()) {
                    mouseY -= (this.y + PANEL_HEIGHT + SPACING);
                } else {
                    mouseY -= (this.y + TAB_HEIGHT + SPACING);
                }
                if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                    Tab.SECURITY.click();
                }
            }
        }
        return false;
    }

    protected void drawTabTooltips(MatrixStack matrices, int mouseX, int mouseY) {
        final MachineBlockEntity machine = this.handler.machine;
        final int mX = mouseX, mY = mouseY;
        if (Tab.REDSTONE.isOpen()) {
            mouseX -= (PANEL_WIDTH + this.x);
            mouseY -= (this.y + SPACING);
            if (check(mouseX, mouseY, 18, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneState.IGNORE.getName(), mX, mY);
            }
            if (check(mouseX, mouseY, 43, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneState.LOW.getName(), mX, mY);
            }
            if (check(mouseX, mouseY, 68, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.renderTooltip(matrices, RedstoneState.HIGH.getName(), mX, mY);
            }
        } else {
            mouseX -= (TAB_WIDTH + this.x);
            mouseY -= this.y;
            if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.redstone").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
        }
        mouseX = mX;
        mouseY = mY;
        if (Tab.CONFIGURATION.isOpen()) {
            mouseX -= (PANEL_WIDTH + this.x);
            mouseY -= (this.y + TAB_HEIGHT + SPACING);
            if (check(mouseX, mouseY, 33, 24, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.top").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
            if (check(mouseX, mouseY, 14, 43, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.left").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
            if (check(mouseX, mouseY, 33, 43, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.front").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
            if (check(mouseX, mouseY, 52, 43, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.right").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
            if (check(mouseX, mouseY, 71, 43, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.back").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
            if (check(mouseX, mouseY, 33, 22, 16, 16)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration.bottom").setStyle(Constants.Text.RED_STYLE), mX, mY);
            }
        } else {
            mouseX -= (PANEL_WIDTH + this.x);
            if (Tab.REDSTONE.isOpen()) {
                mouseY -= (this.y + PANEL_HEIGHT + SPACING);
            } else {
                mouseY -= (this.y + TAB_HEIGHT + SPACING);
            }
            if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.configuration").setStyle(Constants.Text.BLUE_STYLE), mX, mY);
            }
        }
        mouseX = mX;
        mouseY = mY;
        mouseX += this.backgroundWidth - this.x;
        mouseY -= this.y;
        if (Tab.STATS.isOpen()) {
            if (check(mouseX, mouseY, 11, 25, 24, 24)) {
                this.renderTooltip(matrices, new LiteralText(machine.getSecurity().getOwner().getName()), mX, mY);
            }
        } else {
            if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.stats").setStyle(Constants.Text.YELLOW_STYLE), mX, mY);
            }
        }
        mouseX = mX;
        mouseY = mY;
        if (Tab.SECURITY.isOpen()) {
            mouseX -= (-this.backgroundWidth + this.x);
            mouseY -= (this.y + TAB_HEIGHT + SPACING);

            if (machine.getSecurity().isOwner(this.playerInventory.player)) {
                if (check(mouseX, mouseY, 18, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.PRIVATE.getName(), mX, mY);
                }
                if (check(mouseX, mouseY, 43, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.TEAM.getName(), mX, mY);
                }
                if (check(mouseX, mouseY, 68, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, SecurityInfo.Accessibility.PUBLIC.getName(), mX, mY);
                }
            } else {
                if (check(mouseX, mouseY, 18, 33, BUTTON_WIDTH, BUTTON_HEIGHT)
                    || check(mouseX, mouseY, 43, 33, BUTTON_WIDTH, BUTTON_HEIGHT)
                    || check(mouseX, mouseY, 68, 33, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.security.access_denied"), mX, mY);
                }
            }
        } else {
            mouseX -= (-this.backgroundWidth + this.x);
            if (Tab.STATS.isOpen()) {
                mouseY -= (this.y + PANEL_HEIGHT + SPACING);
            } else {
                mouseY -= (this.y + TAB_HEIGHT + SPACING);
            }
            if (check(mouseX, mouseY, 0, 0, TAB_WIDTH, TAB_HEIGHT)) {
                this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.machine.security").setStyle(Constants.Text.BLUE_STYLE), mX, mY);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert this.client != null;
        if (this.handler.machine == null || !this.handler.machine.getSecurity().hasAccess(handler.player)) {
            this.onClose();
            return;
        }

        super.render(matrices, mouseX, mouseY, delta);

        this.drawConfigTabs(matrices, mouseX, mouseY, delta);
        this.handleSlotHighlight(matrices, mouseX, mouseY, delta);
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        for (AbstractWidget widget : this.widgets) {
            widget.render(matrices, mouseX - this.x, mouseY - this.y, delta);
        }
        matrices.pop();
    }

    protected void handleSlotHighlight(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAllowed()) {
            for (AbstractWidget widget : widgets) {
                widget.mouseClicked(mouseX - this.x, mouseY - this.y, button);
            }
            return this.checkTabsClick(new MatrixStack(), mouseX, mouseY, button) | super.mouseClicked(mouseX, mouseY, button);
        } else {
            return false;
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (isAllowed()) {
            super.drawMouseoverTooltip(matrices, mouseX, mouseY);
            drawTabTooltips(matrices, mouseX, mouseY);
            matrices.push();
            matrices.translate(this.x, this.y, 0);
            for (AbstractWidget widget : widgets) {
                widget.drawMouseoverTooltip(matrices, mouseX - this.x, mouseY - this.y);
            }
            matrices.pop();
        }
    }

    public boolean isAllowed() {
        if (this.handler.machine != null) {
            return handler.machine.getSecurity().hasAccess(playerInventory.player);
        }
        return false;
    }

    private void playButtonSound() {
        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseScrolled(mouseX - this.x, mouseY - this.y, amount);
        }
        return b;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.keyReleased(keyCode, scanCode, modifiers);
        }
        return b;
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.charTyped(chr, keyCode);
        }
        return b;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (AbstractWidget widget : widgets) {
            widget.mouseMoved(mouseX - this.x, mouseY - this.y);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseDragged(mouseX - this.x, mouseY - this.y, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || b;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.mouseReleased(mouseX - this.x, mouseY - this.y, button);
        }
        return super.mouseReleased(mouseX, mouseY, button) || b;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean b = false;
        for (AbstractWidget widget : widgets) {
            b |= widget.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers) || b;
    }

    @Nullable
    @Contract("null -> null")
    public <T extends AbstractWidget> T addWidget(@Nullable T widget) {
        if (widget == null) return null;
        this.widgets.add(widget);
        return widget;
    }

    private static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).texture(u0, v0).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).texture(u2, v2).next();
        bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).texture(u3, v3).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    private enum Tab {
        REDSTONE(TAB_REDSTONE_U, TAB_REDSTONE_V, PANEL_REDSTONE_U, PANEL_REDSTONE_V, true),
        CONFIGURATION(TAB_CONFIG_U, TAB_CONFIG_V, PANEL_CONFIG_U, PANEL_CONFIG_V, true),
        STATS(TAB_STATS_U, TAB_STATS_V, PANEL_STATS_U, PANEL_STATS_V, false),
        SECURITY(TAB_SECURITY_U, TAB_SECURITY_V, PANEL_SECURITY_U, PANEL_SECURITY_V, false);

        private final int tabU;
        private final int tabV;
        private final int panelU;
        private final int panelV;
        private final boolean left;
        private boolean open = false;

        Tab(int tabU, int tabV, int panelU, int panelV, boolean left) {
            this.tabU = tabU;
            this.tabV = tabV;
            this.panelU = panelU;
            this.panelV = panelV;
            this.left = left;
        }

        public int getU() {
            return open ? this.panelU : this.tabU;
        }

        public boolean isLeft() {
            return left;
        }

        public int getV() {
            return open ? this.panelV : this.tabV;
        }

        public boolean isOpen() {
            return open;
        }

        public void click() {
            this.open = !this.open;
            if (this.open) {
                Tab.values()[this.ordinal() + 1 - ((this.ordinal() % 2) * 2)].open = false;
            }
        }
    }
}
