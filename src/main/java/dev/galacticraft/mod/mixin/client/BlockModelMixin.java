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

package dev.galacticraft.mod.mixin.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.client.accessor.BlockModelAccessor;
import dev.galacticraft.mod.client.model.types.UnbakedObjModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin implements BlockModelAccessor {
    @Shadow
    public abstract Material getMaterial(String spriteName);

    private UnbakedObjModel galacticraft$obj;

    @Override
    public void galacticraft$setObjData(UnbakedObjModel obj) {
        this.galacticraft$obj = obj;
    }

    @Inject(method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Z)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void customBake(ModelBaker baker, BlockModel parent, Function<Material, TextureAtlasSprite> textureGetter, ModelState settings, boolean isGui3d, CallbackInfoReturnable<BakedModel> cir) {
        if (galacticraft$obj != null)
            cir.setReturnValue(galacticraft$obj.bakeVanillaModel(Minecraft.getInstance().getResourceManager(), textureGetter, textureGetter.apply(getMaterial("particle"))));
    }

    @Mixin(BlockModel.Deserializer.class)
    public static class DeserializerMixin {
        // Loading custom block models via fabric api's model plugin is kinda ass so this is easier
        @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"))
        private void loadCustomObjModel(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<BlockModel> cir) {
            JsonObject json = element.getAsJsonObject();
            if (json.has("galacticraft:obj")) {
                ResourceLocation objPath = ResourceLocation.parse(GsonHelper.getAsString(json, "obj"));
                ResourceLocation mtlPath = ResourceLocation.parse(GsonHelper.getAsString(json, "mtl"));

                ((BlockModelAccessor) cir.getReturnValue()).galacticraft$setObjData(new UnbakedObjModel(objPath, mtlPath, Optional.of(TextureAtlas.LOCATION_BLOCKS)));
            }
        }
    }
}
