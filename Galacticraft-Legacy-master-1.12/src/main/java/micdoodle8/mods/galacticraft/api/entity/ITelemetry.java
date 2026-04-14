/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.entity;

/**
 * Implement into entities to allow transmission of data via telemetry
 */
public interface ITelemetry
{

    void transmitData(int[] data);

    void receiveData(int[] data, String[] str);

    void adjustDisplay(int[] data);
}
