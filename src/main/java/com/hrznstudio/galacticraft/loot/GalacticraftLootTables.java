package com.hrznstudio.galacticraft.loot;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.mixin.LootTablesAccessor;
import net.minecraft.util.Identifier;

public class GalacticraftLootTables {
    public static final Identifier BASIC_MOON_RUINS_CHEST = LootTablesAccessor.callRegisterLootTable(new Identifier(Constants.MOD_ID, Constants.LootTables.BASIC_MOON_RUINS_CHEST));

    public static void register() {
    }
}
