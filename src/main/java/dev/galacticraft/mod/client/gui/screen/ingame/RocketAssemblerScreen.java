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

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.RocketAssemblerBlockEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.RocketAssemblerRecipe;
import dev.galacticraft.mod.screen.RocketAssemblerScreenHandler;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketAssemblerScreen extends AbstractContainerScreen<RocketAssemblerScreenHandler> {

    public static final int SELECTED_TAB_X = 324;
    public static final int SELECTED_TAB_Y = 4;
    public static final int SELECTED_TAB_WIDTH = 32;
    public static final int SELECTED_TAB_HEIGHT = 25;

    public static final int TAB_X = 325;
    public static final int TAB_Y = 30;
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 25;

    public static final int RED_BOX_X = 324;
    public static final int RED_BOX_Y = 56;
    public static final int RED_BOX_WIDTH = 24;
    public static final int RED_BOX_HEIGHT = 24;

    public static final int GREEN_BOX_X = 324;
    public static final int GREEN_BOX_Y = 81;
    public static final int GREEN_BOX_WIDTH = 24;
    public static final int GREEN_BOX_HEIGHT = 24;

    public static final int ARROW_X = 324;
    public static final int ARROW_Y = 106;
    public static final int ARROW_WIDTH = 6;
    public static final int ARROW_HEIGHT = 10;

    public static final int SELECTED_ARROW_X = 324;
    public static final int SELECTED_ARROW_Y = 117;
    public static final int SELECTED_ARROW_WIDTH = 6;
    public static final int SELECTED_ARROW_HEIGHT = 10;

    public static final int BACK_ARROW_X = ARROW_X + ARROW_WIDTH;
    public static final int BACK_ARROW_Y = ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_ARROW_WIDTH = -ARROW_WIDTH;
    public static final int BACK_ARROW_HEIGHT = -ARROW_HEIGHT;

    public static final int BACK_SELECTED_ARROW_X = SELECTED_ARROW_X + ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_Y = SELECTED_ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_SELECTED_ARROW_WIDTH = -SELECTED_ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_HEIGHT = -SELECTED_ARROW_HEIGHT;

    public static final int BACK_ARROW_OFFSET_X = ARROW_WIDTH;
    public static final int BACK_ARROW_OFFSET_Y = ARROW_HEIGHT;

    public static final int ENERGY_OVERLAY_WIDTH = -12;
    public static final int ENERGY_OVERLAY_HEIGHT = -60;
    public static final int ENERGY_OVERLAY_X = 336;
    public static final int ENERGY_OVERLAY_Y = 188;
    public static final int ENERGY_OVERLAY_RENDER_X = 169;
    public static final int ENERGY_OVERLAY_RENDER_Y = 67;

    public static final int BUILD_X = 338;
    public static final int BUILD_Y = 129;
    public static final int BUILD_WIDTH = 35;
    public static final int BUILD_HEIGHT = 16;

    public static final int PROGRESS_ARROW_WIDTH = 133;
    public static final int PROGRESS_ARROW_WIDTH_MAX = 136;
    public static final int PROGRESS_ARROW_HEIGHT = 4;
    public static final int PROGRESS_ARROW_X = 364;
    public static final int PROGRESS_ARROW_Y = 8;

    private final RocketAssemblerBlockEntity assembler;
    private Registry<RocketPart> registry;
    private Tab tab = Tab.ROCKET;

    public RocketAssemblerScreen(RocketAssemblerScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title);
        this.imageWidth = 323;
        this.imageHeight = 175;
        this.assembler = handler.assembler;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        assert this.minecraft.level != null;
        this.registry = RocketPart.getRegistry(this.minecraft.level.registryAccess());
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        Lighting.setupFor3DItems();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_ASSEMBLER_SCREEN);
        blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        blit(matrices, this.leftPos + ENERGY_OVERLAY_RENDER_X, this.topPos + ENERGY_OVERLAY_RENDER_Y, ENERGY_OVERLAY_X, ENERGY_OVERLAY_Y, ENERGY_OVERLAY_WIDTH, (int) (((float) ENERGY_OVERLAY_HEIGHT) * (((float) this.assembler.getEnergyAttribute().getEnergy() / (float) this.assembler.getEnergyAttribute().getMaxCapacity()))));

        if (assembler.ready() && !assembler.building()) {
            blit(matrices, this.leftPos + 257, this.topPos + 18, BUILD_X, BUILD_Y, BUILD_WIDTH, BUILD_HEIGHT);
        }

        if (tab == Tab.ROCKET) {
            blit(matrices, this.leftPos - 29, this.topPos + 3, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            blit(matrices, this.leftPos - 27, this.topPos + 30, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);

            itemRenderer.renderGuiItem(new ItemStack(GalacticraftItem.ROCKET_SCHEMATIC), this.leftPos - 20, this.topPos + 8);
            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlock.MOON_TURF), this.leftPos - 20, this.topPos + 35);

            if (!this.assembler.data.isEmpty()) {
                RocketDesignerScreen.drawRocket(this.leftPos + 186 + 17, this.topPos + 73, 1, mouseX, mouseY, this.assembler.fakeEntity);
            }
        } else if (tab == Tab.LANDER) {
            blit(matrices, this.leftPos - 27, this.topPos + 3, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);
            blit(matrices, this.leftPos - 29, this.topPos + 30, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);

            itemRenderer.renderGuiItem(new ItemStack(GalacticraftItem.ROCKET_SCHEMATIC), this.leftPos - 20, this.topPos + 8);
            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlock.MOON_TURF), this.leftPos - 20, this.topPos + 35);
        }

        if (assembler.building()) {
            float progress = assembler.getProgress();
            RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_ASSEMBLER_SCREEN);
            final float maxProgress = Galacticraft.CONFIG_MANAGER.get().rocketAssemblerProcessTime();
            if (progress < ((maxProgress / 140F) * 133F)) {
                blit(matrices, this.leftPos + 176, this.topPos + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, (int) (((float) PROGRESS_ARROW_WIDTH) * (progress / ((maxProgress / 140F) * 133F))), PROGRESS_ARROW_HEIGHT);
            } else {
                blit(matrices, this.leftPos + 176, this.topPos + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, PROGRESS_ARROW_WIDTH_MAX, (int) ((PROGRESS_ARROW_HEIGHT) + (7 * ((progress - ((maxProgress / 140F) * 133F)) / (maxProgress - ((maxProgress / 140F) * 133F))))));
            }
        }

        if (tab == Tab.ROCKET) {
            int offsetY = 0;
            int offsetX = 0;
            int slot = 0;
            if (this.assembler.data != RocketData.empty()) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (RocketPart.getById(this.registry, assembler.data.getPartForType(RocketPartType.values()[i])).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                            offsetX = 0;
                        }
                        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_ASSEMBLER_SCREEN);
                        final int baOY = offsetY;
                        boolean aG = true;
                        offsetX++;

                        RocketAssemblerRecipe recipe = assembler.recipes.get(assembler.data.getPartForType(RocketPartType.values()[i]));
                        for (Object2IntMap.Entry<Ingredient> stack : recipe.getInput().object2IntEntrySet()) {
                            RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_ASSEMBLER_SCREEN);

//                            if (this.assembler.getExtendedInv().getInvStack(slot).getCount() == stack.getIntValue()) {
//                                blit(matrices, this.leftPos + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.topPos + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
//                            } else {
//                                blit(matrices, this.leftPos + 9 + ((RED_BOX_WIDTH + 2) * offsetX), this.topPos + 9 + ((RED_BOX_HEIGHT + 2) * offsetY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
//                                aG = false;
//                            }
                            int time = (int) (System.currentTimeMillis() % 50000) / 1000;
                            ItemStack[] msc = stack.getKey().getItems();
                            itemRenderer.renderGuiItem(msc[time % msc.length], this.leftPos + 13 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.topPos + 13 + ((GREEN_BOX_HEIGHT + 2) * offsetY));
//                            itemRenderer.renderGuiItemDecorations(minecraft.font, msc[time % msc.length], this.leftPos + 13 + (GREEN_BOX_WIDTH + 2) * offsetX, this.topPos + 13 + (GREEN_BOX_HEIGHT + 2) * offsetY, this.assembler.getExtendedInv().getInvStack(slot).getCount() + "/" + stack.getIntValue());

                            if (check(mouseX, mouseY, (this.leftPos + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2, (this.topPos + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2, GREEN_BOX_WIDTH - 4, GREEN_BOX_HEIGHT - 4)) {
                                RenderSystem.disableDepthTest();
                                int n = (this.leftPos + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2;
                                int r = (this.topPos + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2;
                                RenderSystem.colorMask(true, true, true, false);
                                this.fillGradient(matrices, n, r, n + GREEN_BOX_WIDTH - 4, r + GREEN_BOX_HEIGHT - 4, -2130706433, -2130706433);
                                RenderSystem.colorMask(true, true, true, true);
                                RenderSystem.enableDepthTest();
                            }
                            if (++offsetX == 5) {
                                offsetX = 0;
                                offsetY++;
                            }
                            slot++;
                        }

                        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ROCKET_ASSEMBLER_SCREEN);
                        if (aG) {
                            blit(matrices, this.leftPos + 9, this.topPos + 9 + ((GREEN_BOX_HEIGHT + 2) * baOY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
                        } else {
                            blit(matrices, this.leftPos + 9, this.topPos + 9 + ((RED_BOX_HEIGHT + 2) * baOY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
                        }
                        matrices.pushPose();
                        matrices.translate(this.leftPos + 13, this.topPos + 13 + ((GREEN_BOX_HEIGHT + 2) * baOY), 0);
                        RocketPartRendererRegistry.INSTANCE.getRenderer(assembler.data.getPartForType(RocketPartType.values()[i])).renderGUI(minecraft.level, matrices, mouseX, mouseY, delta);
                        matrices.popPose();
                    }
                }
            }
        } else if (tab == Tab.LANDER) {
            drawCenteredString(matrices, minecraft.font, "WIP - TO BE DESIGNED", this.leftPos / 2, this.topPos + (height) / 2, Integer.MAX_VALUE);
        }
    }


    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        Lighting.setupFor3DItems();

        if (assembler.data != null && assembler.data != RocketData.empty()) {
            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.rocket_info").asString(), this.leftPos + 234, this.topPos + 41, 11184810);
//            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.tier", blockEntity.data.getTier()).asString(), this.x + 234, this.y + 41 + 11, 11184810);
            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.assembler_status").asString(), this.leftPos + 234, this.topPos + 41 + 22, 11184810);
            minecraft.font.draw(stack, getStatus(), this.leftPos + 234, this.topPos + 41 + 33, 11184810);
        } else {
            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.put_schematic").asString(), this.x + 234, this.topPos + 41, 11184810);
            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.put_schematic_2").asString(), this.x + 234, this.topPos + 41 + 11, 11184810);
            minecraft.font.draw(stack, Component.translatable("tooltip.galacticraft.assembler_status").asString(), this.x + 234, this.topPos + 41 + 22, 11184810);
            minecraft.font.draw(stack, getStatus(), this.leftPos + 234, this.topPos + 41 + 33, 11184810);
        }

        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private String getStatus() {
        if (assembler.building()) {
            return Component.translatable("tooltip.galacticraft.building").asString();
        } else if (assembler.ready()) {
            if (assembler.getEnergyAttribute().getEnergy() > 20) {
                return Component.translatable("tooltip.galacticraft.ready").asString();
            } else {
                return Component.translatable("tooltip.galacticraft.no_energy").asString();
            }
        } else if (this.assembler.data == null || this.assembler.data.isEmpty()) {
            return Component.translatable("tooltip.galacticraft.no_schematic").asString();
        } else {
            return Component.translatable("tooltip.galacticraft.missing_resources").asString();
        }
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int i, int j) {
        super.renderTooltip(matrixStack, i, j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | tabClicked(mouseX, mouseY, button) | contentClicked(mouseX, mouseY, button);
    }

    private boolean contentClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && tab == Tab.ROCKET) {
            int offsetX = 0;
            int offsetY = 0;
            int slot = 0;
            if (this.assembler.data != RocketData.empty()) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (RocketPart.getById(this.registry, assembler.data.getPartForType(RocketPartType.values()[i])).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                        }
                        offsetX = 1;
                        RocketAssemblerRecipe recipe = assembler.recipes.get(assembler.data.getPartForType(RocketPartType.values()[i]));
                        Object2IntMap<Ingredient> input = recipe.getInput();
                        for (int i1 = 0; i1 < input.size(); i1++) {
                            if (check(mouseX, mouseY, this.leftPos + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.topPos + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT)) {
                                boolean success = false;
//                                if (slot < assembler.getExtendedInv().getSlotCount()) {
//                                    if (this.menu.getCursorStack().isEmpty()) {
//                                        success = true;
//                                        this.menu.setCursorStack(assembler.getExtendedInv().getInvStack(slot));
//                                        assembler.getExtendedInv().setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
//                                    } else {
//                                        if (assembler.getExtendedInv().getFilterForSlot(slot).matches(this.handler.getCursorStack())) {
//                                            if (assembler.getExtendedInv().getInvStack(slot).isEmpty()) {
//                                                if (assembler.getExtendedInv().getMaxAmount(slot, this.handler.getCursorStack()) >= this.handler.getCursorStack().getCount()) {
//                                                    assembler.getExtendedInv().setInvStack(slot, this.handler.getCursorStack().copy(), Simulation.ACTION);
//                                                    this.handler.setCursorStack(ItemStack.EMPTY);
//                                                } else {
//                                                    ItemStack stack = this.handler.getCursorStack().copy();
//                                                    ItemStack stack1 = this.handler.getCursorStack().copy();
//                                                    stack.setCount(assembler.getExtendedInv().getMaxAmount(slot, assembler.getExtendedInv().getInvStack(slot)));
//                                                    stack1.setCount(stack1.getCount() - assembler.getExtendedInv().getMaxAmount(slot, assembler.getExtendedInv().getInvStack(slot)));
//                                                    assembler.getExtendedInv().setInvStack(slot, stack, Simulation.ACTION);
//                                                    this.handler.setCursorStack(stack1);
//                                                }
//                                            } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
//                                                // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
//                                                ItemStack stack = this.handler.getCursorStack().copy();
//                                                int max = assembler.getExtendedInv().getMaxAmount(slot, assembler.getExtendedInv().getInvStack(slot));
//                                                stack.setCount(stack.getCount() + assembler.getExtendedInv().getInvStack(slot).getCount());
//                                                if (stack.getCount() <= max) {
//                                                    this.handler.setCursorStack(ItemStack.EMPTY);
//                                                } else {
//                                                    ItemStack stack1 = stack.copy();
//                                                    stack.setCount(max);
//                                                    stack1.setCount(stack1.getCount() - max);
//                                                    this.handler.setCursorStack(stack1);
//                                                }
//                                                assembler.getExtendedInv().setInvStack(slot, stack, Simulation.ACTION);
//                                            }
//                                            success = true;
//                                        }
//                                    }
//                                }

                                if (success) {
                                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer().writeInt(slot)).writeBlockPos(this.assembler.getBlockPos());
                                    ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "assembler_wc"), buf);
                                    return true;
                                }
                            }
                            slot++;
                            if (++offsetX == 5) {
                                offsetX = 0;
                                offsetY++;
                            }
                        }

                    }
                }
            }
        }

        if (button == 0) {
            if (check(mouseX, mouseY, this.leftPos + 257, this.topPos + 18, BUILD_WIDTH, BUILD_HEIGHT)) {
                assembler.startBuilding();
                ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "assembler_build"), PacketByteBufs.create().writeBlockPos(assembler.getBlockPos()));
            }
        }
        return false;
    }

    public ClickType getType(int button, int slot) {
        if (this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                return ClickType.CLONE;
            } else {
                boolean ok = slot != -999 && (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 340) || InputConstants.isKeyDown(minecraft.getWindow().getWindow(), 344));
                if (ok) {
                    return ClickType.QUICK_MOVE;
                } else if (slot == -999) {
                    return ClickType.THROW;
                }
                return ClickType.PICKUP;
            }
        } else {
            return ClickType.SWAP;
        }
    }


    private boolean tabClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (tab == Tab.ROCKET) {
                if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                }
            } else if (tab == Tab.LANDER) {
                if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            } else {
                if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                } else if (check(mouseX, mouseY, this.leftPos - 27, this.topPos + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            }
        }
        return false;
    }

    @Override
    public void blit(PoseStack stack, int x, int y, int u, int v, int width, int height) {
        blit(stack, x, y, u, v, width, height, 512, 256);
    }

    private boolean check(double mouseX, double mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseY >= buttonY && mouseX <= buttonX + buttonWidth && mouseY <= buttonY + buttonHeight;
    }

    private enum Tab {
        ROCKET,
        LANDER
    }

}
