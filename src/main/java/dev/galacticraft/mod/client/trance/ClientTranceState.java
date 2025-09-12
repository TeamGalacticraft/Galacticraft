package dev.galacticraft.mod.client.trance;

public final class ClientTranceState {
    private static volatile boolean HALLUCINATING;

    private ClientTranceState() {}

    public static boolean isHallucinating() { return HALLUCINATING; }
    public static void setHallucinating(boolean v) { HALLUCINATING = v; }
}