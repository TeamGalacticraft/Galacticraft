package dev.galacticraft.mod.mixin;

import com.google.gson.stream.JsonWriter;
import net.minecraft.data.DataProvider;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DataProvider.class)
public interface DataProviderMixin {
    @Dynamic
    @Redirect(method = "method_46567", at = @At(value = "INVOKE", target = "Lcom/google/gson/stream/JsonWriter;setIndent(Ljava/lang/String;)V", remap = false))
    private static void compressJson(JsonWriter writer, String indent) {
        writer.setIndent("");
    }
}
