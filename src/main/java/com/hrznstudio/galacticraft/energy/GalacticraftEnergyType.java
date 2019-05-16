package com.hrznstudio.galacticraft.energy;

import io.github.cottonmc.energy.api.ElectricalEnergyType;
import io.github.cottonmc.energy.api.EnergyType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEnergyType implements EnergyType {

    @Override
    public int getMaximumTransferSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public TextComponent getDisplayAmount(int amount) {
        float fAmount = (float) amount;

        if (fAmount < 1000) { // x < 1K
            return new TextComponent(new TranslatableComponent("tooltip.galacticraft-rewoven.energy", fAmount).getText());
        } else if (fAmount < 1_000_000) { // 1K < x < 1M
            float tAmount = fAmount / 1000;
            return new TextComponent(new TranslatableComponent("tooltip.galacticraft-rewoven.energy.k", tAmount).getText());
        } else if (fAmount < 1_000_000_000) { // 1M < x < 1G
            float tAmount = fAmount / 1_000_1000;
            return new TextComponent(new TranslatableComponent("tooltip.galacticraft-rewoven.energy.m", tAmount).getText());
        } else { // 1G < x
            float tAmount = fAmount / 1_000_000_000;
            return new TextComponent(new TranslatableComponent("tooltip.galacticraft-rewoven.energy.g", tAmount).getText());
        }
    }

    @Override
    public boolean isCompatibleWith(EnergyType type) {
        return type == this || type instanceof ElectricalEnergyType;
    }

    @Override
    public int convertFrom(EnergyType type, int amount) {
        if (type == this) return amount;
        return (type instanceof ElectricalEnergyType) ? amount * 30 : 0;
    }

    @Override
    public int convertTo(EnergyType type, int amount) {
        if (type == this) return amount;
        return (type instanceof ElectricalEnergyType) ? (int) Math.floor(amount / 30f) : 0;
    }
}
