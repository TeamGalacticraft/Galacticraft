/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.tile;

/**
 * Currently only used internally when enable/disable buttons are clicked.
 */
public interface IDisableableMachine
{

    /**
     * Sets the machine to disabled or enabled.
     *
     * @param index the index
     * @param disabled whether or not the machine should be set to the enabled
     *        or disabled state
     */
    void setDisabled(int index, boolean disabled);

    /**
     * Get the current state of the machine (enabled/disabled).
     *
     * @param index the index
     * @return the current state of the machine (enabled/disabled)
     */
    boolean getDisabled(int index);
}
