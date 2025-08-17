package dev.galacticraft.impl.gametest;

public @interface AtmosphereDependent {
    boolean breathable() default true;
}
