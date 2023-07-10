/*
 *
 *  * Copyright (c) 2019-2023 Team Galacticraft
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIfDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package dev.galacticraft.mod.mixin;

import com.mojang.serialization.Dynamic;
import dev.galacticraft.mod.data.fixer.fixes.GCLegacyBlockStateData;
import net.minecraft.util.datafix.fixes.BlockStateData;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(BlockStateData.class)
public abstract class BlockStateDataMixin {
    @Shadow
    private static void register(int i, String string, String... strings) {
    }

    @Mutable
    @Shadow @Final private static Dynamic<?>[] MAP;

    @Mutable
    @Shadow @Final private static Dynamic<?>[] BLOCK_DEFAULTS;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "bootstrap15", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/BlockStateData;finalizeMaps()V", shift = At.Shift.BEFORE))
    private static void upgradeGCLegacyData(CallbackInfo ci) {
        MAP = Arrays.copyOfRange(MAP, 0, 4609);
        BLOCK_DEFAULTS = Arrays.copyOfRange(BLOCK_DEFAULTS, 0, 289);
        register(4608, "{Name:'galacticraft:rocket_launch_pad'}", "{Name:'galacticraftcore:block_multi',Properties:{rendertyoe:'0',type:'rocket_pad'}}");
        register(4419, "{Name:'galacticraft:detailed_tin_decoration'}", "{Name:'galacticraftcore:basic_block_core',Properties:{basictype:'deco_block_0'}}");
        register(4416, "{Name:'galacticraft:tin_decoration'}", "{Name:'galacticraftcore:basic_block_core',Properties:{basictype:'deco_block_1'}}");
    }


    @Inject(method = "getTag", at = @At("RETURN"))
    private static void changeArrayBounds(int i, CallbackInfoReturnable<Dynamic<?>> cir) {
        if (cir.getReturnValue() == null)
            System.out.println("Unhandled block id: " + i);

    }
}
