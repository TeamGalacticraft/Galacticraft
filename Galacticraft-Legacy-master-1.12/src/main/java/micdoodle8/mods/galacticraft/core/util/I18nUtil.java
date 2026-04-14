package micdoodle8.mods.galacticraft.core.util;

import java.util.Locale;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
public final class I18nUtil
{

    public static final I18nUtil instance = new I18nUtil();

    public String getKey(String prefix, ResourceLocation name)
    {
        return prefix + "." + name.getNamespace() + "." + name.getPath();
    }

    public String getKey(IForgeRegistryEntry<?> object, String key)
    {
        String           prefix = getPrefixFor(object);
        ResourceLocation name   = Objects.requireNonNull(object.getRegistryName());
        return prefix + "." + name.getNamespace() + "." + name.getPath() + "." + key;
    }

    /**
     * Check whether or not the key is in the translation file. You do not need to call this in most
     * cases, translation attempts just return the key if it is not found.
     *
     * @param key The key, checked as-is
     * @return If the key exists
     */
    public boolean hasKey(String key)
    {
        return I18n.hasKey(key);
    }

    public String translate(String key, Object... params)
    {
        if (!FMLCommonHandler.instance().getSide().isClient())
        {
            // Go ahead and translate text with deprecated I18n on the server for now...
            return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, params);
        }

        return I18n.format(key, params);
    }

    public String translatedName(Block block)
    {
        return translate(block.getTranslationKey() + ".name");
    }

    public String translatedName(Item item)
    {
        return translate(item.getTranslationKey() + ".name");
    }

    public String translatedName(ItemStack stack)
    {
        return translate(stack.getTranslationKey() + ".name");
    }

    /**
     * Translates the text with key "(prefix).registry_name.key". This uses the object's registry
     * name namespace instead of {@link #modId}. Prefix is determined by the object's type.
     *
     * @param object An {@link IForgeRegistryEntry} of some kind, such as a {@link Block} or {@link
     *               Item}
     * @param key    Key suffix
     * @param params Optional parameters to format into translation
     * @return Translation result, or {@code key} if the key is not found
     */
    public String subText(IForgeRegistryEntry<?> object, String key, Object... params)
    {
        return translate(getKey(object, key), params);
    }

    public String miscText(String key, Object... params)
    {
        return translate("misc", key, params);
    }

    private String getPrefixFor(IForgeRegistryEntry<?> object)
    {
        if (object instanceof Item)
            return "item";
        if (object instanceof Block)
            return "tile";
        return object.getClass().getName().toLowerCase(Locale.ROOT);
    }
}
