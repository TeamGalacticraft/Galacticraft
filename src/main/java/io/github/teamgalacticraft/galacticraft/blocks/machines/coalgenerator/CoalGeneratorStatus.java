package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE,
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE,
    /**
     * The generator has no fuel.
     */
    INACTIVE,
    /**
     * The generator is warming up.
     */
    WARMING

}
