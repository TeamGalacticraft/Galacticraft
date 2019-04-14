package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GenericLiquidCanister extends Item {

    private Fluid allowedFluid;

    public GenericLiquidCanister(Settings settings, Fluid allowedFluid) {
        super(settings);
        this.allowedFluid = allowedFluid;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public Rarity getRarity(ItemStack par1ItemStack) {
        return Rarity.RARE;
    }
}
