package dev.galacticraft.mod.client.sounds.custom_system;

import org.intellij.lang.annotations.Identifier;

public enum TransitionState {
	STARTING("starting_phase"),
	RUNNING("idle_phase"),
	ENDING("ending_phase");

	private final Identifier identifier;

	TransitionState(String name) {
		this.identifier = Identifier.of(ExampleMod.MOD_ID, name);
	}

	public Identifier getIdentifier() {
		return identifier;
	}
}
