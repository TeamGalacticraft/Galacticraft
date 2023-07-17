/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.render.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FootprintRenderer {
    public static Map<Long, List<Footprint>> footprints = new ConcurrentHashMap<Long, List<Footprint>>();
    private static final ResourceLocation footprintTexture = Constant.id("textures/misc/footprint.png");

    public static void renderFootprints(Player player, PoseStack stack, float partialTicks, Camera camera) {
        ResourceKey<Level> dimActive = player.level().dimension();
        List<Footprint> footprintsToDraw = new LinkedList<>();

        for (List<Footprint> footprintList : footprints.values()) {
            for (Footprint footprint : footprintList) {
                if (footprint.dimension.location().equals(dimActive.location())) {
                    footprintsToDraw.add(footprint);
                }
            }
        }

        if (footprintsToDraw.isEmpty())
        {
            return;
        }

        stack.pushPose();
        double interpPosX = Mth.lerp(partialTicks, player.xOld, player.getX());
        double interpPosY = Mth.lerp(partialTicks, player.yOld, player.getY());
        double interpPosZ = Mth.lerp(partialTicks, player.zOld, player.getZ());
        RenderSystem.setShaderTexture(0, FootprintRenderer.footprintTexture);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
//        RenderSystem.enableTexture2D();
        RenderSystem.disableCull();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Tesselator tessellator = Tesselator.getInstance();
        float f7 = 1.0F;
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = 1.0F;

//        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Footprint footprint : footprintsToDraw)
        {
            stack.pushPose();

            float ageScale = footprint.age / (float) Footprint.MAX_AGE;
            BufferBuilder worldRenderer = tessellator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            float footprintX = (float) (footprint.position.x - interpPosX);
            float footprintY = (float) (footprint.position.y - interpPosY) + 0.001F;
            float footprintZ = (float) (footprint.position.z - interpPosZ);

            Vec3 vec3 = camera.getPosition();
//            stack.translate(footprintX, footprintY, footprintZ);
            float relativeX = (float) (footprintX - vec3.x());
            float relativeY = (float) (footprintY - vec3.y());
            float relativeZ = (float) (footprintZ - vec3.z());

            RenderSystem.setShaderColor(1F - ageScale, 1F - ageScale, 1F - ageScale, 1F - ageScale);
            float footprintScale = 0.5F;
            worldRenderer
                    .vertex(stack.last().pose(), (Mth.sin((45 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeX, relativeY, (Mth.cos((45 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeZ)
                    .uv(f7, f9).endVertex();
            worldRenderer
                    .vertex(stack.last().pose(), (Mth.sin((135 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeX, relativeY, (Mth.cos((135 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeZ)
                    .uv(f7, f8).endVertex();
            worldRenderer
                    .vertex(stack.last().pose(), (Mth.sin((225 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeX, relativeY, (Mth.cos((225 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeZ)
                    .uv(f6, f8).endVertex();
            worldRenderer
                    .vertex(stack.last().pose(), (Mth.sin((315 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeX, relativeY, (Mth.cos((315 - footprint.rotation) / Mth.RAD_TO_DEG) * footprintScale) + relativeZ)
                    .uv(f6, f9).endVertex();

            tessellator.end();
            stack.popPose();
        }

        stack.popPose();
    }

    public static void addFootprint(long chunkKey, Footprint footprint)
    {
        List<Footprint> footprintList = footprints.get(chunkKey);

        if (footprintList == null)
        {
            footprintList = new ArrayList<Footprint>();
        }

        footprintList.add(new Footprint(footprint.dimension, footprint.position, footprint.rotation, footprint.owner));
        footprints.put(chunkKey, footprintList);
    }

    public static void addFootprint(long chunkKey, ResourceKey<Level> dimension, Vector3d position, float rotation, String owner)
    {
        addFootprint(chunkKey, new Footprint(dimension, position, rotation, owner));
    }

    public static void setFootprints(long chunkKey, List<Footprint> prints)
    {
        List<Footprint> footprintList = footprints.get(chunkKey);

        if (footprintList == null)
        {
            footprintList = new ArrayList<Footprint>();
        }

        Iterator<Footprint> i = footprintList.iterator();
        while (i.hasNext()) {
            Footprint print = i.next();
            if (!print.owner.equals(Minecraft.getInstance().player.getGameProfile().getName())) {
                i.remove();
            }
        }

        footprintList.addAll(prints);
        footprints.put(chunkKey, footprintList);
    }

    public static Vector3d getFootprintPosition(Level world, float rotation, Vector3d startPosition, BlockPos playerCenter)
    {
        Vector3d position = new Vector3d(startPosition);
        float footprintScale = 0.375F;

        int mainPosX = Mth.floor(position.x());
        int mainPosY = Mth.floor(position.y());
        int mainPosZ = Mth.floor(position.z());
        BlockPos posMain = new BlockPos(mainPosX, mainPosY, mainPosZ);

        // If the footprint is hovering over air...
        if (world.getBlockState(posMain).isAir())
        {
            position.x += (playerCenter.getX() - mainPosX);
            position.z += (playerCenter.getZ() - mainPosZ);

            BlockPos pos1 = new BlockPos(Mth.floor(position.x()), Mth.floor(position.y()), Mth.floor(position.z()));
            // If the footprint is still over air....
            if (world.getBlockState(pos1).isAir())
            {
                for (Direction direction : Direction.values())
                {
                    BlockPos offsetPos = posMain.relative(direction);
                    if (direction != Direction.DOWN && direction != Direction.UP)
                    {
                        if (!world.getBlockState(offsetPos).isAir())
                        {
                            position.x += direction.getStepX();
                            position.z += direction.getStepZ();
                            break;
                        }
                    }
                }
            }
        }

        mainPosX = Mth.floor(position.x());
        mainPosZ = Mth.floor(position.z());

        double x0 = (Math.sin((45 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.x;
        double x1 = (Math.sin((135 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.x;
        double x2 = (Math.sin((225 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.x;
        double x3 = (Math.sin((315 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.x;
        double z0 = (Math.cos((45 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.z;
        double z1 = (Math.cos((135 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.z;
        double z2 = (Math.cos((225 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.z;
        double z3 = (Math.cos((315 - rotation) / Mth.RAD_TO_DEG) * footprintScale) + position.z;

        double xMin = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        double xMax = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        double zMin = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        double zMax = Math.max(Math.max(z0, z1), Math.max(z2, z3));

        if (xMin < mainPosX)
        {
            position.x += mainPosX - xMin;
        }

        if (xMax > mainPosX + 1)
        {
            position.x -= xMax - (mainPosX + 1);
        }

        if (zMin < mainPosZ)
        {
            position.z += mainPosZ - zMin;
        }

        if (zMax > mainPosZ + 1)
        {
            position.z -= zMax - (mainPosZ + 1);
        }

        return position;
    }
}
