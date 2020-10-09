package com.hrznstudio.galacticraft.mixin;

import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootTables.class)
public interface LootTablesAccessor {
    @Invoker
    static Identifier callRegisterLootTable(Identifier id) {
        throw new UnsupportedOperationException("Invoker was not transformed.");
    }
}
