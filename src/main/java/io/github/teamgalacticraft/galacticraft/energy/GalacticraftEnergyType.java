package io.github.teamgalacticraft.galacticraft.energy;

import io.github.cottonmc.energy.api.ElectricalEnergyType;
import io.github.cottonmc.energy.api.EnergyType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class GalacticraftEnergyType implements EnergyType {

    @Override
    public int getMaximumTransferSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public TextComponent getDisplayAmount(int amount) {
        if (amount < 1000) { // x < 1M
            return new TranslatableTextComponent("tooltip.galacticraft-fabric.energy", amount);
        } else if (amount < 1_000_000) { // 1K < x < 1M
            float tAmount = amount / 1000;
            return new TranslatableTextComponent("tooltip.galacticraft-fabric.energy.k", tAmount);
        } else if (amount < 1_000_000_000) { // 1M < x < 1G
            float tAmount = amount / 1_000_1000;
            return new TranslatableTextComponent("tooltip.galacticraft-fabric.energy.m", tAmount);
        } else { // 1G < x
            float tAmount = amount / 1_000_000_000;
            return new TranslatableTextComponent("tooltip.galacticraft-fabric.energy.g", tAmount);
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
