/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class KeycardItem extends Item {
    public KeycardItem(Properties properties) {
        super(properties);
    }

    public static boolean isBound(
            ItemStack stack
    ) {
        String accessId =
                getAccessId(stack);

        return accessId != null
                && !accessId.isBlank();
    }

    public static String getAccessId(
            ItemStack stack
    ) {
        return stack.get(
                GCDataComponents.KEYCARD_ACCESS_ID
        );
    }

    public static void bind(
            ItemStack stack,
            String accessId
    ) {
        if (accessId == null
                || accessId.isBlank()) {

            throw new IllegalArgumentException(
                    "Keycard access ID cannot be blank"
            );
        }

        stack.set(
                GCDataComponents.KEYCARD_ACCESS_ID,
                accessId
        );
    }

    public static ItemStack createBoundStack(
            Item item,
            String accessId
    ) {
        ItemStack stack =
                new ItemStack(item);

        bind(
                stack,
                accessId
        );

        return stack;
    }

    public void interactWithAirlock(
            ItemStack stack,
            AirlockControllerBlockEntity airlock,
            Player player
    ) {
        if (player.isShiftKeyDown()) {
            bindToAirlock(
                    stack,
                    airlock,
                    player
            );

            return;
        }

        useBoundKeycard(
                stack,
                airlock,
                player
        );
    }

    @Override
    public InteractionResult useOn(
            UseOnContext context
    ) {
        Player player =
                context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!(context.getLevel()
                .getBlockEntity(
                        context.getClickedPos()
                )
                instanceof AirlockControllerBlockEntity airlock)) {

            return InteractionResult.PASS;
        }

        if (!context.getLevel().isClientSide) {
            interactWithAirlock(
                    context.getItemInHand(),
                    airlock,
                    player
            );
        }

        return InteractionResult.sidedSuccess(
                context.getLevel().isClientSide
        );
    }

    protected boolean canPlayerBind() {
        return true;
    }

    protected boolean consumeOnSuccessfulUse() {
        return false;
    }

    private void bindToAirlock(
            ItemStack stack,
            AirlockControllerBlockEntity airlock,
            Player player
    ) {
        if (!canPlayerBind()) {
            player.displayClientMessage(
                    Component.translatable(Translations.Chat.CANNOT_REPROGRAM_KEYCARD).withStyle(ChatFormatting.RED),
                    true
            );

            return;
        }

        if (!airlock.canBindKeycard(player)) {
            player.displayClientMessage(
                    Component.translatable(Translations.Chat.KEYCARD_NO_PERMISSION).withStyle(ChatFormatting.RED),
                    true
            );

            return;
        }

        bind(
                stack,
                airlock.getAccessId()
        );

        player.displayClientMessage(
                Component.translatable(Translations.Chat.KEYCARD_SUCCESSFUL_BIND).withStyle(ChatFormatting.GREEN),
                true
        );
    }

    private void useBoundKeycard(
            ItemStack stack,
            AirlockControllerBlockEntity airlock,
            Player player
    ) {
        String cardAccessId =
                getAccessId(stack);

        if (cardAccessId == null
                || cardAccessId.isBlank()) {

            player.displayClientMessage(
                    Component.translatable(Translations.Chat.KEYCARD_NOT_BOUND).withStyle(ChatFormatting.YELLOW),
                    true
            );

            return;
        }

        if (!airlock.acceptsKeycard(
                cardAccessId
        )) {
            player.displayClientMessage(
                    Component.translatable(Translations.Chat.KEYCARD_UNSUCCESSFUL_USE).withStyle(ChatFormatting.RED),
                    true
            );

            return;
        }

        boolean activated =
                airlock.activateKeycard(
                        player
                );

        if (!activated) {
            player.displayClientMessage(
                    Component.translatable(Translations.Chat.AIRLOCK_ALREADY_UNLOCKED).withStyle(ChatFormatting.GRAY),
                    true
            );

            return;
        }

        player.displayClientMessage(
                Component.translatable(Translations.Chat.KEYCARD_SUCCESSFUL_USE).withStyle(ChatFormatting.GREEN),
                true
        );

        if (consumeOnSuccessfulUse()
                && !player.getAbilities().instabuild) {

            stack.shrink(1);
        }
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        String accessId =
                getAccessId(stack);

        if (accessId == null
                || accessId.isBlank()) {

            tooltip.add(
                    Component.translatable(Translations.Tooltip.KEYCARD_UNBOUND).withStyle(ChatFormatting.GRAY)
            );

            tooltip.add(
                    Component.translatable(Translations.Tooltip.KEYCARD_SNEAK_TO_BIND).withStyle(ChatFormatting.DARK_GRAY)
            );

            return;
        }

        String shortId =
                accessId.length() > 8
                        ? accessId.substring(
                        0,
                        8
                )
                        : accessId;

        tooltip.add(
                Component.literal(
                        "Bound Access ID: "
                                + shortId
                ).withStyle(
                        ChatFormatting.GRAY
                )
        );
    }
}