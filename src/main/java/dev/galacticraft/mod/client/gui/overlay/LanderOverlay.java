package dev.galacticraft.mod.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.content.entity.LanderEntity;
import dev.galacticraft.mod.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LanderOverlay {
    private static long tickCount;
    public static void onRenderHud(PoseStack matrixStack, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        final Window scaledresolution = mc.getWindow();
        final int width = scaledresolution.getGuiScaledWidth();
        final int height = scaledresolution.getGuiScaledHeight();
        if (mc.player.getVehicle() instanceof LanderEntity lander) {
            double motionY = lander.getDeltaMovement().y();
            matrixStack.pushPose();
            matrixStack.scale(2.0F, 2.0F, 0.0F);

            if (motionY < -2.0) {
                mc.font.draw(matrixStack, Component.translatable("ui.warning"), width / 4 - mc.font.width(Component.translatable("ui.warning")) / 2, height / 8 - 20,
                        ColorUtil.to32BitColor(255, 255, 0, 0));
                final int alpha = (int) (200 * (Math.sin(tickCount) * 0.5F + 0.5F)) + 5;
                final MutableComponent press1 = Component.translatable("ui.lander.warning2");
                final MutableComponent press2 = Component.translatable("ui.lander.warning3");
                mc.font.draw(matrixStack, press1.append(mc.options.keyJump.getTranslatedKeyMessage()).append(press2),
                        width / 4 - mc.font.width(press1.append(mc.options.keyJump.getTranslatedKeyMessage()).append(press2)) / 2, height / 8,
                        ColorUtil.to32BitColor(alpha, alpha, alpha, alpha));
            }

            matrixStack.popPose();

            if (mc.player.getVehicle().getDeltaMovement().y() != 0.0D) {
                Component string = Component.translatable("ui.lander.velocity").append(": " + Math.round(mc.player.getVehicle().getDeltaMovement().y() * 1000) / 100.0D + " ")
                        .append(Component.translatable("ui.lander.velocityu"));
                int color =
                        ColorUtil.to32BitColor(255, (int) Math.floor(Math.abs(motionY) * 51.0D), 255 - (int) Math.floor(Math.abs(motionY) * 51.0D), 0);
                mc.font.draw(matrixStack, string, width / 2 - mc.font.width(string) / 2, height / 3, color);
            }
        }
    }

    public static void clientTick(Minecraft minecraft) {
        tickCount++;
    }
}
