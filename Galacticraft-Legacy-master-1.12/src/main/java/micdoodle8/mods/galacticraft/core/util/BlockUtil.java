package micdoodle8.mods.galacticraft.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.oredict.OreDictionary;

import micdoodle8.mods.galacticraft.api.vector.BlockTuple;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;

import lombok.Getter;

public class BlockUtil
{

    public static final String FULL_ID_MATCHER   = "^(?<resloc>(?<namespace>[a-z\\d\\._-]+):(?<path>[a-z\\d\\._-]+))(?::(?<meta>[0-9\\*]{1,}))?$";
    public static final String SHORTHAND_MATCHER = "^(?<resloc>[a-z\\d\\._-]+)(?::(?<meta>[0-9\\*]{1,}))?$";

    public static final Pattern FULL_ID_REGEX   = Pattern.compile(FULL_ID_MATCHER);
    public static final Pattern SHORTHAND_REGEX = Pattern.compile(SHORTHAND_MATCHER);

    public static NonNullList<ItemStack> getListFromStringArray(String[] strArr)
    {
        NonNullList<ItemStack> nnl = NonNullList.create();
        for (String str : strArr)
        {
            addToBlockStackList(getResloc(str), nnl);
        }
        return nnl;
    }

    public static NonNullList<MultiBlockStateHolder> getBlockStateHolderList(NonNullList<ItemStack> itemStacks)
    {
        final Map<Block, List<IBlockState>> stateList = new HashMap<>();
        for (ItemStack stack : itemStacks)
        {
            Block b = ((ItemBlock) stack.getItem()).getBlock();
            IBlockState state = b.getStateFromMeta(stack.getItem().getMetadata(stack.getMetadata()));
            if (stateList.keySet().contains(b))
            {
                if (!stateList.get(b).contains(state))
                {
                    stateList.get(b).add(state);
                }
            } else
            {
                List<IBlockState> list = new ArrayList<>();
                list.add(state);
                stateList.put(b, list);
            }
        }

        NonNullList<MultiBlockStateHolder> mbsl = NonNullList.create();
        for (Entry<Block, List<IBlockState>> e : stateList.entrySet())
        {
            mbsl.add(new MultiBlockStateHolder(e.getKey(), e.getValue()));
        }
        return mbsl;
    }

    public static BlockTuple getBlockTupleFromString(String string)
    {
        Block oldFormat = parseBlockFromOldConfigFormat(string);
        if (oldFormat != null)
        {
            GalacticraftCore.logger.debug("oldFormat: " + string);
            return new BlockTuple(oldFormat, parseMetaFromOldConfigFormat(string));
        }
        
        ItemStack s = getItemStackFromString(string);
        return new BlockTuple(((ItemBlock) s.getItem()).getBlock(), s.getMetadata());
    }

    @Nullable
    private static Block parseBlockFromOldConfigFormat(String string)
    {
        return Block.getBlockFromName(string);
    }

    private static int parseMetaFromOldConfigFormat(String string)
    {
        int lastColon = string.lastIndexOf(':');
        int meta      = -1;
        if (lastColon > 0)
        {
            try
            {
                meta = Integer.parseInt(string.substring(lastColon + 1, string.length()));
            } catch (NumberFormatException ex)
            {
            }
        }
        return meta;
    }

    public static ItemStack getItemStackFromString(String string)
    {
        return getItemStackFromResloc(getResloc(string));
    }

    private static void addToBlockStackList(Resloc resloc, NonNullList<ItemStack> nnl)
    {
        GalacticraftCore.logger.debug("Attempting to locate Block with ResourceLocation: " + resloc.toString() + " | Input: " + resloc.rawInput);
        Block block = Block.REGISTRY.getObject(resloc);
        if (resloc.isBlock(block))
        {
            ItemStack i = resloc.toItemStack(block);
            if (resloc.isWildcard() && i.getHasSubtypes())
            {
                i.getItem().getSubItems(CreativeTabs.SEARCH, nnl);
            } else
            {
                nnl.add(i);
            }
        } else
        {
            GalacticraftCore.logger.error("No block with ResourceLocation (" + resloc.rawInput + ") could be located. Skipping");
        }
    }

    private static ItemStack getItemStackFromResloc(Resloc resloc)
    {
        NonNullList<ItemStack> nnl = NonNullList.create();
        addToBlockStackList(resloc, nnl);
        return nnl.get(0);
    }

    public static Resloc getResloc(String string)
    {
        boolean isShorthand = string.matches(SHORTHAND_MATCHER);
        Matcher m           = isShorthand ? SHORTHAND_REGEX.matcher(string) : FULL_ID_REGEX.matcher(string);
        boolean found       = m.find();
        if (found)
        {
            if (m.group("meta") != null)
            {
                return Resloc.of(m.group("resloc"), m.group("meta"));
            }

            return Resloc.of(m.group("resloc"));
        }
        return Resloc.of("null");
    }


    public static class Resloc extends ResourceLocation
    {

        private String rawInput;
        private int meta;

        public static Resloc of(String resourceName, String meta)
        {
            return new Resloc(resourceName, meta);
        }

        public static Resloc of(String resourceName)
        {
            return new Resloc(resourceName);
        }

        public Resloc(String resourceName)
        {
            this(resourceName, "");
        }

        public Resloc(String resourceName, String meta)
        {
            super(resourceName);
            this.rawInput = resourceName;
            setMetaValue(meta);
        }

        private void setMetaValue(String meta)
        {
            this.rawInput = meta.isEmpty() ? rawInput : rawInput + ":" + meta;
            this.meta = (meta.equals("*")) ? OreDictionary.WILDCARD_VALUE : safeParse(meta);
        }

        public boolean isBlock(Block block)
        {
            return block.getRegistryName().equals(this);
        }

        private int safeParse(String str)
        {
            try
            {
                return Integer.parseInt(str);
            } catch (NumberFormatException e)
            {
                return 0;
            }
        }

        public int getMeta()
        {
            return this.meta;
        }

        public boolean isWildcard()
        {
            return this.meta == OreDictionary.WILDCARD_VALUE;
        }

        public ItemStack toItemStack(Block block)
        {
            return meta > 0 ? new ItemStack(block, 1, this.meta) : new ItemStack(block);
        }
    }

    @Getter
    public static class MultiBlockStateHolder
    {

        private Block       block;
        private List<IBlockState> blockstateList = new ArrayList<>();

        private MultiBlockStateHolder()
        {
        }

        public MultiBlockStateHolder(Block block, List<IBlockState> blockstateList)
        {
            this.block = block;
            this.blockstateList = blockstateList;
        }
    }
}
