/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.world;

import net.minecraft.world.biome.Biome.BiomeProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiomeData
{

    private String biomeName;
    @Builder.Default private float baseHeight = 0.1F;
    @Builder.Default private float heightVariation = 0.2F;
    @Builder.Default private float temperature = 0.5F;
    @Builder.Default private float rainfall = 0.0F;
    @Builder.Default private int waterColor = 16777215;
    @Builder.Default private boolean enableSnow = false;
    @Builder.Default private boolean enableRain = false;
    private String baseBiomeRegName;

    public BiomeProperties toBiomeProperties()
    {
        BiomeProperties biomeProperties = new BiomeProperties(biomeName)
            .setBaseHeight(baseHeight).setHeightVariation(heightVariation)
            .setTemperature(temperature).setRainfall(rainfall)
            .setWaterColor(waterColor).setBaseBiome(baseBiomeRegName);
        if (!enableRain)
        {
            biomeProperties.setRainDisabled();
        }
        if (!enableSnow)
        {
            biomeProperties.setSnowEnabled();
        }
        return biomeProperties;
    }
}
