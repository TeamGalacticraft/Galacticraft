package com.hrznstudio.galacticraft.api.item;

import net.minecraft.item.ToolMaterial;

public class HoeItem extends net.minecraft.item.HoeItem {
    public HoeItem(ToolMaterial material, float attackDamage, Settings settings) {
        super(material, (int)attackDamage, -3.1F, settings);
    }
}
