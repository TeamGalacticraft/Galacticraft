package dev.galacticraft.mod.api.documentation;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class DocsApi {
    private DocsApi() {}
    public static ResourceLocation homeId() { return DocsManager.HOME_ID; }
    public static void openHome() { DocsManager.openHome(); }
    public static void open(ResourceLocation pageId) { DocsManager.open(pageId); }
    public static ResourceLocation pageForItem(Item item) { return DocsManager.pageFor(item); }
}