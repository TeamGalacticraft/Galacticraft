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

package dev.galacticraft.mod.api.block.entity;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public enum PipeColor {
    WHITE("white", Items.WHITE_STAINED_GLASS_PANE, ConventionalItemTags.WHITE_DYES),
    ORANGE("orange", Items.ORANGE_STAINED_GLASS_PANE, ConventionalItemTags.ORANGE_DYES),
    MAGENTA("magenta", Items.MAGENTA_STAINED_GLASS_PANE, ConventionalItemTags.MAGENTA_DYES),
    LIGHT_BLUE("light_blue", Items.LIGHT_BLUE_STAINED_GLASS_PANE, ConventionalItemTags.LIGHT_BLUE_DYES),
    YELLOW("yellow", Items.YELLOW_STAINED_GLASS_PANE, ConventionalItemTags.YELLOW_DYES),
    LIME("lime", Items.LIME_STAINED_GLASS_PANE, ConventionalItemTags.LIME_DYES),
    PINK("pink", Items.PINK_STAINED_GLASS_PANE, ConventionalItemTags.PINK_DYES),
    GRAY("gray", Items.GRAY_STAINED_GLASS_PANE, ConventionalItemTags.GRAY_DYES),
    LIGHT_GRAY("light_gray", Items.LIGHT_GRAY_STAINED_GLASS_PANE, ConventionalItemTags.LIGHT_GRAY_DYES),
    CYAN("cyan", Items.CYAN_STAINED_GLASS_PANE, ConventionalItemTags.CYAN_DYES),
    PURPLE("purple", Items.PURPLE_STAINED_GLASS_PANE, ConventionalItemTags.PURPLE_DYES),
    BLUE("blue", Items.BLUE_STAINED_GLASS_PANE, ConventionalItemTags.BLUE_DYES),
    BROWN("brown", Items.BROWN_STAINED_GLASS_PANE, ConventionalItemTags.BROWN_DYES),
    GREEN("green", Items.GREEN_STAINED_GLASS_PANE, ConventionalItemTags.GREEN_DYES),
    RED("red", Items.RED_STAINED_GLASS_PANE, ConventionalItemTags.RED_DYES),
    BLACK("black", Items.BLACK_STAINED_GLASS_PANE, ConventionalItemTags.BLACK_DYES),
    CLEAR("clear", Ingredient.of(ConventionalItemTags.GLASS_PANES_COLORLESS), null);

    public static final Codec<PipeColor> CODEC = Codec.STRING.xmap(s -> {
        for (PipeColor color : PipeColor.values()) {
            if (color.name.equals(s)) {
                return color;
            }
        }
        return null;
    }, color -> color.name);

    private final String name;
    private final Ingredient glassPane;
    @Nullable
    private final Ingredient dye;

    PipeColor(final String name, final Ingredient glassPane, final @Nullable Ingredient dye) {
        this.name = name;
        this.glassPane = glassPane;
        this.dye = dye;
    }

    PipeColor(final String name, final Item glassPane, final TagKey<Item> dye) {
        this(name, Ingredient.of(glassPane), Ingredient.of(dye));
    }

    public static PipeColor fromDye(DyeColor dye) {
        return PipeColor.values()[dye.ordinal()];
    }

    public boolean canConnectTo(PipeColor other) {
        return this == CLEAR || other == CLEAR || this == other;
    }

    public String getName() {
        return this.name;
    }

    public Ingredient glassPane() {
        return glassPane;
    }

    public @Nullable Ingredient dye() {
        return dye;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static PipeColor[] byRainbowOrder() {
        return new PipeColor[] {
                CLEAR, WHITE, LIGHT_GRAY, GRAY, BLACK, BROWN, RED, ORANGE, YELLOW,
                LIME, GREEN, CYAN, LIGHT_BLUE, BLUE, PURPLE, MAGENTA, PINK
        };
    }
}
