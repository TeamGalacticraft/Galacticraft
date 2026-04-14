package micdoodle8.mods.galacticraft.core.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import net.minecraftforge.oredict.OreDictionary;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

public final class ItemUtil
{
    // Dictionary prefix's
    public static final String        BLOCK      = "block";
    public static final String        ORE        = "ore";
    public static final String        CLUSTER    = "cluster";
    public static final String        DUST       = "dust";
    public static final String        DUST_SMALL = "dustSmall";
    public static final String        DUST_TINY  = "dustTiny";
    public static final String        INGOT      = "ingot";
    public static final String        PLATE      = "plate";
    public static final String        COIN       = "coin";
    public static final String        NUGGET     = "nugget";
    public static final String        LOG        = "log";
    public static final String        GEM        = "gem";
    public static final String        COVER      = "cover";
    public static final String        ROD        = "rod";
    public static final String        COMPRESSED = "compressed";
    public static final String        CROP       = "crop";
    private static final OreDictProxy PROXY      = new OreDictProxy();

    //@noformat
    public static final String[] ALL_TYPES = {
        BLOCK, ORE, CLUSTER, DUST, DUST_SMALL, DUST_TINY,
        INGOT, PLATE, COIN, NUGGET, LOG, GEM, COVER, ROD,
        COMPRESSED, CROP
    };
    //@format

    public static boolean isPlayerHoldingSomething(EntityPlayer player)
    {
        return !player.getHeldItemMainhand().isEmpty() || !player.getHeldItemOffhand().isEmpty();
    }

    public static ItemStack getHeldStack(EntityPlayer player)
    {
        if (isPlayerHoldingSomething(player))
        {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty())
            {
                stack = player.getHeldItemOffhand();
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack cloneStack(Item item)
    {
        return cloneStack(item, 1);
    }

    public static ItemStack cloneStack(Block block)
    {
        return cloneStack(block, 1);
    }

    public static ItemStack cloneStack(Item item, int stackSize)
    {
        if (item == null)
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, stackSize);
    }

    public static ItemStack cloneStack(Block block, int stackSize)
    {
        if (block == null)
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(block, stackSize);
    }

    public static ItemStack cloneStack(ItemStack stack, int stackSize)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        ItemStack retStack = stack.copy();
        retStack.setCount(stackSize);
        return retStack;
    }

    public static ItemStack cloneStack(ItemStack stack)
    {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    public static ItemStack readItemStackFromNBT(NBTTagCompound nbt)
    {
        ItemStack stack = new ItemStack(Item.getItemById(nbt.getShort("id")));
        stack.setCount(nbt.getInteger("Count"));
        stack.setItemDamage(Math.max(0, nbt.getShort("Damage")));
        if (nbt.hasKey("tag", 10))
        {
            stack.setTagCompound(nbt.getCompoundTag("tag"));
        }
        return stack;
    }

    public static NBTTagCompound writeItemStackToNBT(ItemStack stack, NBTTagCompound nbt)
    {
        nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
        nbt.setInteger("Count", stack.getCount());
        nbt.setShort("Damage", (short) getItemDamage(stack));
        if (stack.hasTagCompound())
        {
            nbt.setTag("tag", stack.getTagCompound());
        }
        return nbt;
    }

    public static NBTTagCompound writeItemStackToNBT(ItemStack stack, int amount, NBTTagCompound nbt)
    {
        nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
        nbt.setInteger("Count", amount);
        nbt.setShort("Damage", (short) getItemDamage(stack));
        if (stack.hasTagCompound())
        {
            nbt.setTag("tag", stack.getTagCompound());
        }
        return nbt;
    }

    public static String getNameFromItemStack(ItemStack stack)
    {
        if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("display"))
        {
            return "";
        }
        return stack.getTagCompound().getCompoundTag("display").getString("Name");
    }

    public static int getItemDamage(ItemStack stack)
    {
        return Items.DIAMOND.getMetadata(stack);
    }

    public static boolean hasOreDictSuffix(ItemStack stack, String oreDictSuffix)
    {
        return PROXY.getOreName(stack).toLowerCase().endsWith(oreDictSuffix.toLowerCase());
    }

    public static ItemStack getOre(String oreName)
    {
        return getOre(oreName, 1);
    }

    public static ItemStack getOre(String oreName, int amount)
    {
        return PROXY.getOre(oreName, amount);
    }

    public static String getOreName(ItemStack stack)
    {
        return PROXY.getOreName(stack);
    }

    public static boolean isOreIDEqual(ItemStack stack, int oreID)
    {
        return PROXY.isOreIDEqual(stack, oreID);
    }

    public static boolean isOreNameEqual(ItemStack stack, String oreName)
    {
        return PROXY.isOreNameEqual(stack, oreName);
    }

    public static boolean oreNameExists(String oreName)
    {
        return PROXY.oreNameExists(oreName);
    }

    public static boolean hasOreName(ItemStack stack)
    {
        return !getOreName(stack).equals("Unknown");
    }

    public static boolean isBlock(ItemStack stack)
    {
        return getOreName(stack).startsWith(BLOCK);
    }

    public static boolean isOre(ItemStack stack)
    {
        return getOreName(stack).startsWith(ORE);
    }

    public static boolean isCluster(ItemStack stack)
    {
        return getOreName(stack).startsWith(CLUSTER);
    }

    public static boolean isDust(ItemStack stack)
    {
        return getOreName(stack).startsWith(DUST);
    }

    public static boolean isIngot(ItemStack stack)
    {
        return getOreName(stack).startsWith(INGOT);
    }

    public static boolean isPlate(ItemStack stack)
    {
        return getOreName(stack).startsWith(PLATE);
    }

    public static boolean isCoin(ItemStack stack)
    {
        return getOreName(stack).startsWith(COIN);
    }

    public static boolean isNugget(ItemStack stack)
    {
        return getOreName(stack).startsWith(NUGGET);
    }

    public static boolean isLog(ItemStack stack)
    {
        return getOreName(stack).startsWith(LOG);
    }

    public static boolean doOreIDsMatch(ItemStack stackA, ItemStack stackB)
    {
        int id = PROXY.getOreID(stackA);
        return id >= 0 && id == PROXY.getOreID(stackB);
    }

    public static NonNullList<ItemStack> getOres(String oreName)
    {
        return PROXY.getOres(oreName);
    }

    public static NonNullList<ItemStack> getOresThatContain(String oreName)
    {
        return PROXY.getOres(oreName);
    }

    static class OreDictProxy
    {
        public ItemStack getOre(String oreName, int amount)
        {
            if (!oreNameExists(oreName))
            {
                return ItemStack.EMPTY;
            }
            return ItemUtil.cloneStack(OreDictionary.getOres(oreName, false).get(0), amount);
        }

        public NonNullList<ItemStack> getOres(String oreName)
        {
            if (!oreNameExists(oreName))
            {
                return NonNullList.create();
            }
            return OreDictionary.getOres(oreName, false);
        }

        public int getOreID(ItemStack stack)
        {
            return getOreID(getOreName(stack));
        }

        public int getOreID(String oreName)
        {
            if (Strings.isNullOrEmpty(oreName))
            {
                return -1;
            }
            return OreDictionary.getOreID(oreName);
        }

        public List<Integer> getAllOreIDs(ItemStack stack)
        {
            return Ints.asList(OreDictionary.getOreIDs(stack));
        }

        public String getOreName(ItemStack stack)
        {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids != null && ids.length >= 1)
            {
                return OreDictionary.getOreName(ids[0]);
            }
            return "";
        }

        public String getOreName(int oreID)
        {
            return OreDictionary.getOreName(oreID);
        }

        public boolean isOreIDEqual(ItemStack stack, int oreID)
        {
            return getOreID(stack) == oreID;
        }

        public boolean isOreNameEqual(ItemStack stack, String oreName)
        {
            return OreDictionary.getOreName(getOreID(stack)).equals(oreName);
        }

        public boolean oreNameExists(String oreName)
        {
            return OreDictionary.doesOreNameExist(oreName) && OreDictionary.getOres(oreName, false).size() > 0;
        }
    }
}
