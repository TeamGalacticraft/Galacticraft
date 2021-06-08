/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

//
//package dev.galacticraft.mod.recipe.rei;
//
//import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
//import me.shedaniel.rei.api.EntryStack;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.item.ItemStack;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
// */
//@Environment(EnvType.CLIENT)
//public class DefaultShapedCompressingDisplay implements DefaultCompressingDisplay {
//    private final List<List<EntryStack>> input;
//    private final List<EntryStack> output;
//
//    public DefaultShapedCompressingDisplay(ShapedCompressingRecipe recipe) {
//        this.input = new ArrayList<>();
//        recipe.getIngredients().forEach((ingredient) -> {
//            List<EntryStack> stacks = new ArrayList<>();
//            for (ItemStack stack : ingredient.getMatchingStacksClient()) {
//                stacks.add(EntryStack.create(stack));
//            }
//            input.add(stacks);
//        });
//        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
//    }
//
//    //@Override
//    //public Optional getRecipe() {
//    //    return Optional.ofNullable(this.display);
//    //}
//
//    @Override
//    public @NotNull List<List<EntryStack>> getRequiredEntries() {
//        return input;
//    }
//
//    @Override
//    public @NotNull List<List<EntryStack>> getInputEntries() {
//        return input;
//    }
//
//    @Override
//    public @NotNull List<EntryStack> getOutputEntries() {
//        return output;
//    }
//}