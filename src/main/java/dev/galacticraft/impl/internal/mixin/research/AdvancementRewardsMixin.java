/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.galacticraft.api.accessor.ServerResearchAccessor;
import dev.galacticraft.impl.internal.accessor.AdvancementRewardsAccessor;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(AdvancementRewards.class)
public abstract class AdvancementRewardsMixin implements AdvancementRewardsAccessor {
    @Unique
    @NotNull
    private ResourceLocation @Nullable [] rocketPartRecipes = null;

//    @Inject(method = "deserialize", at = @At("RETURN")) TODO: Port (are we even using this?)
//    private static void parseRocketPartReward(JsonObject json, CallbackInfoReturnable<AdvancementRewards> cir) {
//        if (json.has("rocket_parts")) {
//            AdvancementRewards rewards = cir.getReturnValue();
//            JsonArray array = json.get("rocket_parts").getAsJsonArray();
//            ResourceLocation[] ids = new ResourceLocation[array.size()];
//            for (int i = 0; i < array.size(); i++) {
//                ids[i] = new ResourceLocation(array.get(i).getAsString());
//            }
//            ((AdvancementRewardsAccessor) rewards).setRocketPartRecipeRewards(ids);
//        }
//    }

    @Inject(method = "grant", at = @At("RETURN"))
    private void galacticraft_applyRocketPartsToPlayer(ServerPlayer player, CallbackInfo ci) {
        if (this.rocketPartRecipes != null) {
            ((ServerResearchAccessor) player).galacticraft$unlockRocketPartRecipes(this.rocketPartRecipes);
        }
    }

//    @Inject(method = "serializeToJson", at = @At("RETURN"), cancellable = true)
//    private void galacticraft_writeRocketPartRewardsToJson(CallbackInfoReturnable<JsonElement> cir) {
//        if (this.rocketPartRecipes != null) {
//            JsonObject object = cir.getReturnValue().getAsJsonObject();
//            JsonArray array = new JsonArray();
//            for (ResourceLocation id : this.rocketPartRecipes) {
//                array.add(id.toString());
//            }
//            object.add("rocket_parts", array);
//            cir.setReturnValue(object);
//        }
//    }

    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    private void galacticraft_appendRocketPartsToString(CallbackInfoReturnable<String> cir) {
        String s = cir.getReturnValue();
        cir.setReturnValue(s.substring(0, s.length() - 1) + ", parts=" + Arrays.toString(this.rocketPartRecipes) + '}');
    }

    @Override
    public void setRocketPartRecipeRewards(@NotNull ResourceLocation @Nullable [] recipes) {
        this.rocketPartRecipes = recipes;
    }
}
