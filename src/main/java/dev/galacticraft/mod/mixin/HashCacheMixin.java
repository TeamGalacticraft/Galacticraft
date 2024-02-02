package dev.galacticraft.mod.mixin;

import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Set;

@Mixin(HashCache.class)
public class HashCacheMixin {
    @Shadow @Final private Path rootDir;

    @Inject(method = "purgeStaleAndWrite", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onPurgeStaleAndWrite(CallbackInfo ci, Set<Path> set) {
        set.add(this.rootDir.resolve("README.md"));
    }
}
