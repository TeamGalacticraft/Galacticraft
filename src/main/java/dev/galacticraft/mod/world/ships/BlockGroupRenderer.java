package dev.galacticraft.mod.world.ships;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class BlockGroupRenderer {

    public static final RenderType.CompositeRenderType HITBOX_LINES;

    static {
        HITBOX_LINES = RenderType.create("hitbox_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(OUTLINE_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setDepthTestState(NO_DEPTH_TEST).setCullState(NO_CULL).createCompositeState(false));

    }
    private static Vec3d previousPlayerPosition;
    private static Vec3d currentPlayerPosition;

    public static ConcurrentHashMap<String, BlockGroup> groups = new ConcurrentHashMap<>();

    public static void startShipRenderer()
    {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            currentPlayerPosition = new Vec3d(context.gameRenderer().getMainCamera().getPosition());
            if (previousPlayerPosition == null)
            {
                previousPlayerPosition = currentPlayerPosition;
            }
            groups.keySet().forEach(name ->
            {
                render(context, groups.get(name));
            });
            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes())
            {
                groups.keySet().forEach(name ->
                {
                    renderHitbox(context, groups.get(name));
                });
            }
            previousPlayerPosition = currentPlayerPosition;
        });

        ClientTickEvents.END_CLIENT_TICK.register(BlockGroupRenderer::onEndClientTick);
    }

    private static void onEndClientTick(Minecraft context)
    {
        if (context.level != null)
        {
            if (!context.level.tickRateManager().isFrozen())
            {
                groups.forEach((name, group) ->
                {
                    group.updatePhysics(0.05f, context);
                });
            }
        }
    }

    private static void renderHitbox(WorldRenderContext context, BlockGroup group) {
        PoseStack matrixStack = context.matrixStack();
        MultiBufferSource multiBufferSource = context.consumers();
        Vec3d interpolatedPlayer = getInterpolatedPosition(context.tickDelta());
        VertexConsumer hitbox_lines = multiBufferSource.getBuffer(HITBOX_LINES);
        matrixStack.pushPose();
        float centerOfMassRadius = 0.2f;
        Vec3d centerMassPosition = group.getCenterOfMass().add(group.getPosition());
        LevelRenderer.renderLineBox(
                matrixStack,
                hitbox_lines,
                centerMassPosition.x + centerOfMassRadius - interpolatedPlayer.x,
                centerMassPosition.y + centerOfMassRadius - interpolatedPlayer.y,
                centerMassPosition.z + centerOfMassRadius - interpolatedPlayer.z,
                centerMassPosition.x - centerOfMassRadius - interpolatedPlayer.x,
                centerMassPosition.y - centerOfMassRadius - interpolatedPlayer.y,
                centerMassPosition.z - centerOfMassRadius - interpolatedPlayer.z,
                1,
                1,
                0,
                1
        );
        matrixStack.popPose();
    }

    private static void render(WorldRenderContext context, BlockGroup group)
    {
        PoseStack matrixStack = context.matrixStack();
        MultiBufferSource multiBufferSource = context.consumers();
        float tickDelta = context.tickDelta();

        Vec3d interpolatedPlayer = getInterpolatedPosition(tickDelta);
        Vec3d interpolatedGroupPos = group.interpolatePosition(tickDelta);
        Vec3d centerOfMass = group.getCenterOfMass();
        Vec3d centerOfMassInterpolated = new Vec3d(interpolatedGroupPos.x + centerOfMass.x - interpolatedPlayer.x, interpolatedGroupPos.y + centerOfMass.y - interpolatedPlayer.y, interpolatedGroupPos.z + centerOfMass.z - interpolatedPlayer.z);

        //System.out.println("position differance " + (group.getPosition().y - interpolatedGroupPos.y));
        //System.out.println("actual position " + group.getPosition().y);
        //System.out.println("interpolated position " + interpolatedGroupPos.y);

        for (VirtualBlock block : group.getBlocks())
        {
            matrixStack.pushPose();
            renderBlockAt(context, block, group, matrixStack, multiBufferSource, centerOfMassInterpolated, centerOfMass);
            matrixStack.popPose();
        }
    }

    public static void renderBlockAt(WorldRenderContext context, VirtualBlock block, BlockGroup group, PoseStack matrixStack, MultiBufferSource multiBufferSource, Vec3d centerOfMassInterpolated, Vec3d centerOfMass) {

        matrixStack.translate(centerOfMassInterpolated.x, centerOfMassInterpolated.y, centerOfMassInterpolated.z);
        Quaternion quaternion = group.getRotation();
        matrixStack.mulPose(quaternion.f());

        Vec3d relativePos = block.getRelativePos();
        matrixStack.translate(relativePos.x - centerOfMass.x, relativePos.y - centerOfMass.y, relativePos.z - centerOfMass.z);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(block.getState(), matrixStack, multiBufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }

    public static Vec3d getInterpolatedPosition(float tickDelta) {
        return previousPlayerPosition.lerp(currentPlayerPosition, tickDelta);
    }


    public static int createNewGroup(BlockInput block) {
        if (Minecraft.getInstance().player != null) {
            System.out.println("new ship created! name: ship" + groups.size());
            groups.put("ship" + groups.size(), new BlockGroup(block.getState(), new Vec3d(Minecraft.getInstance().player.position().x, Minecraft.getInstance().player.position().y, Minecraft.getInstance().player.position().z)));
            return 1;
        }
        return 0;
    }

    public static int listGroups() {
        Component text = Component.literal(String.valueOf(groups.size()));
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(text);
            return 1;
        }
        return 0;
    }

    public static ConcurrentHashMap<String, BlockGroup> getGroups()
    {
        return groups;
    }

    public static int translateShip(String shipName, Vec3d translation) {
        System.out.println("command called with translation of: " + translation);
        if (groups.containsKey(shipName))
        {
            groups.get(shipName).translate(translation);
            return 1;
        }
        return 0;
    }

    public static int rotateShip(String shipName, Vec3d rotation) {
        System.out.println("command called with rotation of: " + rotation);
        if (groups.containsKey(shipName))
        {
            groups.get(shipName).rotate(rotation);
            return 1;
        }
        return 0;
    }
}
