/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.machines.rocketassembler;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.recipes.RocketAssemblerRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketAssemblerScreen extends ContainerScreen<RocketAssemblerContainer> {

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

    public static final ContainerFactory<ContainerScreen> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof RocketAssemblerBlockEntity) {
            return new RocketAssemblerScreen(syncId, player, (RocketAssemblerBlockEntity) be);
        } else {
            return null;
        }
    };

    protected final Identifier TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ROCKET_ASSEMBLER_SCREEN));
    private World world;
    private RocketAssemblerBlockEntity blockEntity;
    private Tab tab = Tab.ROCKET;

    private RocketAssemblerScreen(int syncId, PlayerEntity playerEntity, RocketAssemblerBlockEntity blockEntity) {
        super(new RocketAssemblerContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, new TranslatableText("ui.galacticraft-rewoven.rocket_designer.name"));
        this.containerWidth = 323;
        this.containerHeight = 175;
        this.world = playerEntity.world;
        this.blockEntity = blockEntity;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        this.renderBackground();
        RenderSystem.disableLighting();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        blit(this.x, this.y, 0, 0, containerWidth, containerHeight);

        blit(this.x + ENERGY_OVERLAY_RENDER_X, this.y + ENERGY_OVERLAY_RENDER_Y, ENERGY_OVERLAY_X, ENERGY_OVERLAY_Y, ENERGY_OVERLAY_WIDTH, (int) (((float) ENERGY_OVERLAY_HEIGHT) * (((float) this.blockEntity.getEnergyAttribute().getCurrentEnergy() / (float) this.blockEntity.getEnergyAttribute().getMaxEnergy()))));

        if (blockEntity.ready() && !blockEntity.building()) {
            blit(this.x + 257, this.y + 18, BUILD_X, BUILD_Y, BUILD_WIDTH, BUILD_HEIGHT);
        }

        if (tab == Tab.ROCKET) {
            blit(this.x - 29, this.y + 3, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            blit(this.x - 27, this.y + 30, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);

            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlocks.ALUMINUM_BLOCK), this.x - 20, this.y + 8);
            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlocks.ALUMINUM_BLOCK), this.x - 20, this.y + 35);

            if (!this.blockEntity.data.isEmpty()) {
                drawEntity(this.x + 186 + 17, this.y + 73);
            }
        } else if (tab == Tab.LANDER) {
            blit(this.x - 27, this.y + 3, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);
            blit(this.x - 29, this.y + 30, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);

            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlocks.ALUMINUM_BLOCK), this.x - 20, this.y + 8);
            itemRenderer.renderGuiItem(new ItemStack(GalacticraftBlocks.ALUMINUM_BLOCK), this.x - 20, this.y + 35);
        }

        if (blockEntity.building()) {
            float pro = blockEntity.getProgress();
            this.minecraft.getTextureManager().bindTexture(TEXTURE);//OUT OF 600
            if (pro <= 570.0F) {
                blit(this.x + 176, this.y + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, (int) (((float) PROGRESS_ARROW_WIDTH) * (pro / 570.0F)), PROGRESS_ARROW_HEIGHT);
            } else {
                blit(this.x + 176, this.y + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, PROGRESS_ARROW_WIDTH_MAX, (int) (PROGRESS_ARROW_HEIGHT + (4F * ((pro - 570F) / 30F))));
            }
        }

        if (tab == Tab.ROCKET) {
            int offsetY = 0;
            int offsetX = 0;
            int slot = 0;
            if (this.blockEntity.data != RocketData.EMPTY) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (blockEntity.data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                            offsetX = 0;
                        }
                        this.minecraft.getTextureManager().bindTexture(TEXTURE);
                        RenderSystem.enableLighting();

                        final int baOY = offsetY;
                        boolean aG = true;
                        offsetX++;

                        RocketAssemblerRecipe recipe = blockEntity.recipes.get(Galacticraft.ROCKET_PARTS.getId(blockEntity.data.getPartForType(RocketPartType.values()[i])));
                        for (ItemStack stack : recipe.getInput()) {
                            this.minecraft.getTextureManager().bindTexture(TEXTURE);
                            RenderSystem.enableLighting();

                            if (this.blockEntity.getExtendedInventory().getInvStack(slot).getCount() == stack.getCount()) {
                                blit(this.x + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
                            } else {
                                blit(this.x + 9 + ((RED_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((RED_BOX_HEIGHT + 2) * offsetY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
                                aG = false;
                            }
                            RenderSystem.enableLighting();

                            itemRenderer.renderGuiItem(stack, this.x + 13 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 13 + ((GREEN_BOX_HEIGHT + 2) * offsetY));
                            itemRenderer.renderGuiItemOverlay(minecraft.textRenderer, stack, this.x + 13 + (GREEN_BOX_WIDTH + 2) * offsetX, this.y + 13 + (GREEN_BOX_HEIGHT + 2) * offsetY, this.blockEntity.getExtendedInventory().getInvStack(slot).getCount() + "/" + stack.getCount());

                            if (check(mouseX, mouseY, (this.x + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2, (this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2, GREEN_BOX_WIDTH - 4, GREEN_BOX_HEIGHT - 4)) {
                                RenderSystem.disableLighting();
                                RenderSystem.disableDepthTest();
                                int n = (this.x + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2;
                                int r = (this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2;
                                RenderSystem.colorMask(true, true, true, false);
                                this.fillGradient(n, r, n + GREEN_BOX_WIDTH - 4, r + GREEN_BOX_HEIGHT - 4, -2130706433, -2130706433);
                                RenderSystem.colorMask(true, true, true, true);
                                RenderSystem.enableLighting();
                                RenderSystem.enableDepthTest();
                            }
                            if (++offsetX == 5) {
                                offsetX = 0;
                                offsetY++;
                            }
                            slot++;
                        }

                        this.minecraft.getTextureManager().bindTexture(TEXTURE);
                        if (aG) {
                            blit(this.x + 9, this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * baOY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
                        } else {
                            blit(this.x + 9, this.y + 9 + ((RED_BOX_HEIGHT + 2) * baOY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
                        }
                        itemRenderer.renderGuiItem(new ItemStack(blockEntity.data.getPartForType(RocketPartType.values()[i]).getDesignerItem()), this.x + 13, this.y + 13 + ((GREEN_BOX_HEIGHT + 2) * baOY));

                    }
                }
            }
        } else if (tab == Tab.LANDER) {
            drawCenteredString(minecraft.textRenderer, "WIP - TO BE DESIGNED", this.x / 2, this.y + (containerHeight) / 2, Integer.MAX_VALUE);
        }
    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);

        if (blockEntity.data != null && blockEntity.data != RocketData.EMPTY) {
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.rocket_info").asString(), this.x + 234, this.y + 41, 11184810);
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.tier", blockEntity.data.getTier()).asString(), this.x + 234, this.y + 41 + 11, 11184810);
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.assembler_status").asString(), this.x + 234, this.y + 41 + 22, 11184810);
            drawString(minecraft.textRenderer, getStatus(), this.x + 234, this.y + 41 + 33, 11184810);
        } else {
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.put_schematic").asString(), this.x + 234, this.y + 41, 11184810);
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.put_schematic_2").asString(), this.x + 234, this.y + 41 + 11, 11184810);
            drawString(minecraft.textRenderer, new TranslatableText("tooltip.galacticraft-rewoven.assembler_status").asString(), this.x + 234, this.y + 41 + 22, 11184810);
            drawString(minecraft.textRenderer, getStatus(), this.x + 234, this.y + 41 + 33, 11184810);
        }
    }

    private String getStatus() {
        if (blockEntity.building()) {
            return new TranslatableText("tooltip.galacticraft-rewoven.building").asString();
        } else if (blockEntity.ready()) {
            if (blockEntity.getEnergyAttribute().getCurrentEnergy() > 20) {
                return new TranslatableText("tooltip.galacticraft-rewoven.ready").asString();
            } else {
                return new TranslatableText("tooltip.galacticraft-rewoven.no_energy").asString();
            }
        } else if (this.blockEntity.data == null || this.blockEntity.data.isEmpty()) {
            return new TranslatableText("tooltip.galacticraft-rewoven.no_schematic").asString();
        } else {
            return new TranslatableText("tooltip.galacticraft-rewoven.missing_resources").asString();
        }
    }

    public void drawEntity(int x, int y) {
        MatrixStack stack = new MatrixStack();
        VertexConsumerProvider consumerProvider = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        RenderSystem.pushMatrix();
        RenderSystem.enableColorMaterial();
        stack.push();
        stack.translate((float) x, (float) y, 3.0F);
        stack.scale(-10.0F, -10.0F, -10.0F);
        RenderSystem.enableLighting();
        RenderSystem.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.enableLighting();
        RenderSystem.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        RenderSystem.rotatef(-((float) Math.atan(this.y + 25 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        blockEntity.fakeEntity.yaw = 225;
        blockEntity.fakeEntity.pitch = 0;
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
//        entityRenderDispatcher.method_3945(180.0F);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(blockEntity.fakeEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, consumerProvider, 15);
        entityRenderDispatcher.setRenderShadows(true);
        Tessellator.getInstance().getBuffer().end();
        stack.pop();
        RenderSystem.popMatrix();
        RenderSystem.disableLighting();
        RenderSystem.disableRescaleNormal();
//        RenderSystem.activeTexture(GLX.GL_TEXTURE1);
        RenderSystem.disableTexture();
//        RenderSystem.activeTexture(GLX.GL_TEXTURE0);
    }

    @Override
    protected void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
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
            if (this.blockEntity.data != RocketData.EMPTY) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (blockEntity.data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                        }
                        offsetX = 1;
                        RocketAssemblerRecipe recipe = blockEntity.recipes.get(Galacticraft.ROCKET_PARTS.getId(blockEntity.data.getPartForType(RocketPartType.values()[i])));
                        DefaultedList<ItemStack> input = recipe.getInput();
                        for (int i1 = 0; i1 < input.size(); i1++) {
                            if (check(mouseX, mouseY, this.x + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT)) {
                                boolean success = false;
                                if (slot < blockEntity.getExtendedInventory().getSlotCount()) {
                                    if (playerInventory.getCursorStack().isEmpty()) {
                                        success = true;
                                        playerInventory.setCursorStack(blockEntity.getExtendedInventory().getInvStack(slot));
                                        blockEntity.getExtendedInventory().setInvStack(slot, ItemStack.EMPTY, Simulation.ACTION);
                                    } else {
                                        if (blockEntity.getExtendedInventory().isItemValidForSlot(slot, playerInventory.getCursorStack().copy())) {
                                            if (blockEntity.getExtendedInventory().getInvStack(slot).isEmpty()) {
                                                if (blockEntity.getExtendedInventory().getMaxAmount(slot, playerInventory.getCursorStack()) >= playerInventory.getCursorStack().getCount()) {
                                                    blockEntity.getExtendedInventory().setInvStack(slot, playerInventory.getCursorStack().copy(), Simulation.ACTION);
                                                    playerInventory.setCursorStack(ItemStack.EMPTY);
                                                } else {
                                                    ItemStack stack = playerInventory.getCursorStack().copy();
                                                    ItemStack stack1 = playerInventory.getCursorStack().copy();
                                                    stack.setCount(blockEntity.getExtendedInventory().getMaxAmount(slot, playerInventory.getCursorStack()));
                                                    stack1.setCount(stack1.getCount() - blockEntity.getExtendedInventory().getMaxAmount(slot, playerInventory.getCursorStack()));
                                                    blockEntity.getExtendedInventory().setInvStack(slot, stack, Simulation.ACTION);
                                                    playerInventory.setCursorStack(stack1);
                                                }
                                            } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                                // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                                ItemStack stack = playerInventory.getCursorStack().copy();
                                                int max = blockEntity.getExtendedInventory().getMaxAmount(slot, playerInventory.getCursorStack());
                                                stack.setCount(stack.getCount() + blockEntity.getExtendedInventory().getInvStack(slot).getCount());
                                                if (stack.getCount() <= max) {
                                                    playerInventory.setCursorStack(ItemStack.EMPTY);
                                                } else {
                                                    ItemStack stack1 = stack.copy();
                                                    stack.setCount(max);
                                                    stack1.setCount(stack1.getCount() - max);
                                                    playerInventory.setCursorStack(stack1);
                                                }
                                                blockEntity.getExtendedInventory().setInvStack(slot, stack, Simulation.ACTION);
                                            }
                                            success = true;
                                        }
                                    }
                                }

                                if (success) {
                                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer().writeInt(slot)).writeBlockPos(this.blockEntity.getPos());
                                    this.minecraft.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "assembler_wc"), buf));
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
            if (check(mouseX, mouseY, this.x + 257, this.y + 18, BUILD_WIDTH, BUILD_HEIGHT)) {
                blockEntity.startBuilding();
                minecraft.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "assembler_build"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(blockEntity.getPos())));
            }
        }
        return false;
    }

    public SlotActionType getType(int button, int slot) {
        if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                return SlotActionType.CLONE;
            } else {
                boolean ok = slot != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344));
                if (ok) {
                    return SlotActionType.QUICK_MOVE;
                } else if (slot == -999) {
                    return SlotActionType.THROW;
                }
                return SlotActionType.PICKUP;
            }
        } else {
            return SlotActionType.SWAP;
        }
    }


    private boolean tabClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (tab == Tab.ROCKET) {
                if (check(mouseX, mouseY, this.x - 27, this.y + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                }
            } else if (tab == Tab.LANDER) {
                if (check(mouseX, mouseY, this.x - 27, this.y + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            } else {
                if (check(mouseX, mouseY, this.x - 27, this.y + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                } else if (check(mouseX, mouseY, this.x - 27, this.y + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            }
        }
        return false;
    }

    @Override
    public void blit(int x, int y, int u, int v, int width, int height) {
        blit(x, y, u, v, width, height, 512, 256);
    }

    private boolean check(double mouseX, double mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseY >= buttonY && mouseX <= buttonX + buttonWidth && mouseY <= buttonY + buttonHeight;
    }

    private enum Tab {
        ROCKET,
        LANDER
    }

}
