package io.github.teamgalacticraft.galacticraft.energy;

import io.github.cottonmc.energy.api.EnergyComponent;
import io.github.cottonmc.energy.impl.SimpleEnergyComponent;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.nbt.Tag;

//Energy component that will convert Cotton Work Units to Galacticraft Joules.
// Wrap around a SimpleEnergyComponent or other energy component.
public class ConversionWrapper implements EnergyComponent {
    private EnergyComponent component;
    private int conversionRate;

    public ConversionWrapper(EnergyComponent component, int conversionRate) {
        this.component = component;
        this.conversionRate = conversionRate;
    }

    @Override
    public int insertEnergy(int amount, ActionType action) {
        //converts WU to GJ
        int convertedAmount = amount * conversionRate;
        return component.insertEnergy(convertedAmount, action);
    }

    @Override
    public int extractEnergy(int amount, ActionType action) {
        //converts GJ to WU
        int convertedAmount = Math.floorDiv(amount, conversionRate);
        return component.extractEnergy(convertedAmount, action);
    }

    @Override
    public void emp(int strength) {
        //remove EMP behavior for Galacricraft machines
    }


    @Override
    public int getMaxEnergy() {
        return component.getMaxEnergy();
    }

    @Override
    public int getCurrentEnergy() {
        return component.getCurrentEnergy();
    }

    @Override
    public boolean canExtractEnergy() {
        return component.canExtractEnergy();
    }

    @Override
    public boolean canInsertEnergy() {
        return component.canInsertEnergy();
    }

    @Override
    public void fromTag(Tag tag) {
        component.fromTag(tag);
    }

    @Override
    public Tag toTag() {
        return component.toTag();
    }
}
