package io.github.teamgalacticraft.galacticraft.energy;

import io.github.cottonmc.energy.impl.SimpleEnergyComponent;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.Constants;

//Energy component that will convert Cotton Work Units to Galacticraft Joules. WIP.
public class ConvertingEnergyComponent extends SimpleEnergyComponent {

    public ConvertingEnergyComponent(int maxEnergy) {
        super(maxEnergy);
    }

    public int insertEnergy(int amount, ActionType action) {
        //converts WU to GJ
        int convertedAmount = amount * Constants.Energy.WU_GJ_CONVERSION;
        return insertNative(convertedAmount, action);
    }

    public int extractEnergy(int amount, ActionType action) {
        //converts GJ to WU
        int convertedAmount = Math.floorDiv(amount, Constants.Energy.WU_GJ_CONVERSION);
       return extractNative(convertedAmount, action);
    }

    public int insertNative(int amount, ActionType action) {
        int insertRoom = this.maxEnergy - this.currentEnergy;
        int insertAmount = amount <= insertRoom ? amount : insertRoom;
        if (action == ActionType.PERFORM) {
            this.currentEnergy += insertAmount;
            if (insertAmount != 0) {
                this.onChanged();
            }
        }

        return insertAmount;
    }

    public int extractNative(int amount, ActionType action) {
        int extractAmount = amount <= this.currentEnergy ? amount : this.currentEnergy;
        if (action == ActionType.PERFORM) {
            this.currentEnergy -= extractAmount;
            if (extractAmount != 0) {
                this.onChanged();
            }
        }

        return extractAmount;
    }

    public void emp(int strength) {
        //remove EMP behavior for Galacricraft machines
    }
}
