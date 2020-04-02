package com.hrznstudio.galacticraft.api.config;

public interface Config {

    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }

    boolean isDebugLogEnabled();

    void setDebugLog(boolean flag);

    int wireTransferLimit();

    void setWireTransferLimit(int amount);

    int heavyWireTransferLimit();

    void setHeavyWireTransferLimit(int amount);

    int coalGeneratorEnergyProductionRate();

    void setCoalGeneratorEnergyProductionRate(int amount);

    int solarPanelEnergyProductionRate();

    void setSolarPanelEnergyProductionRate(int amount);

    int circuitFabricatorEnergyConsumptionRate();

    void setCircuitFabricatorEnergyConsumptionRate(int amount);

    int electricCompressorEnergyConsumptionRate();

    void setElectricCompressorEnergyConsumptionRate(int amount);

    int oxygenCollectorEnergyConsumptionRate();

    void setOxygenCollectorEnergyConsumptionRate(int amount);

    int refineryEnergyConsumptionRate();

    void setRefineryEnergyConsumptionRate(int amount);

    int energyStorageModuleStorageSize();

    void setEnergyStorageModuleStorageSize(int amount);

    int machineEnergyStorageSize();

    void setMachineEnergyStorageSize(int amount);
}
