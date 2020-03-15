package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.server.command.LocateCommandGC;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/LocateCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"))
    private void locateCommandGC(CommandDispatcher<ServerCommandSource> dispatcher) {
        LocateCommandGC.register(dispatcher);
    }
}
