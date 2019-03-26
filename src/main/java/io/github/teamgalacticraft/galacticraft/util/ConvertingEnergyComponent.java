package io.github.teamgalacticraft.galacticraft.util;

import io.github.cottonmc.energy.impl.SimpleEnergyComponent;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.Constants;

//Energy component that will convert Cotton Work Units to Galacticraft Joules.
public class ConvertingEnergyComponent extends SimpleEnergyComponent {

	public ConvertingEnergyComponent(int maxEnergy) {
		super(maxEnergy);
	}

	public int insertEnergy(int amount, ActionType actionType) {
		//converts WU to GJ
		int convertedAmount = amount * Constants.Conversion.WU_GJ_CONVERSION;
		int insertRoom = this.maxEnergy - this.currentEnergy;
		int insertAmount = convertedAmount <= insertRoom ? convertedAmount : insertRoom;
		if (actionType == ActionType.PERFORM) {
			this.currentEnergy += insertAmount;
			if (insertAmount != 0) {
				this.onChanged();
			}
		}

		return insertAmount;
	}

	public int extractEnergy(int amount, ActionType actionType) {
		//converts GJ to WU
		int convertedAmount = Math.floorDiv(amount,  Constants.Conversion.WU_GJ_CONVERSION);
		int extractAmount = convertedAmount <= this.currentEnergy ? convertedAmount : this.currentEnergy;
		if (actionType == ActionType.PERFORM) {
			this.currentEnergy -= extractAmount;
			if (extractAmount != 0) {
				this.onChanged();
			}
		}

		return extractAmount;
	}
}
