package io.github.teamgalacticraft.galacticraft.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankItem extends Item {
    public static String MAX_OXYGEN_NBT_KEY = "MaxOxygen";
    public static String OXYGEN_NBT_KEY = "Oxygen";

    private int maxOxygen;

    public OxygenTankItem(Settings settings) {
        super(settings);
        this.maxOxygen = getDurability();
    }

    @Override
    public void appendItemsForGroup(ItemGroup itemGroup_1, DefaultedList<ItemStack> list) {
        if (this.isInItemGroup(itemGroup_1)) {
            list.add(applyDefaultTags(new ItemStack(this), 0));
            list.add(applyDefaultTags(new ItemStack(this), maxOxygen));
        }
    }

    @Override
    public void onCrafted(ItemStack tank, World world_1, PlayerEntity playerEntity_1) {
        applyDefaultTags(tank, 0);
    }

    private ItemStack applyDefaultTags(ItemStack item, int currentOxy) {
        CompoundTag tag = item.getOrCreateTag();
        tag.putInt(MAX_OXYGEN_NBT_KEY, this.maxOxygen);
        tag.putInt(OXYGEN_NBT_KEY, currentOxy);
        item.setDamage(getDurability() - currentOxy);

        return item;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, World world, List<TextComponent> lines, TooltipContext context) {
        lines.add(new TranslatableTextComponent("tooltip.galacticraft-rewoven.oxygen-remaining", getOxygenCount(stack) + "/" + this.maxOxygen));
        super.buildTooltip(stack, world, lines, context);
    }

    public static int getOxygenCount(ItemStack stack) {
        return stack.getOrCreateTag().getInt(OXYGEN_NBT_KEY);
    }

    public static int getMaxOxygen(ItemStack stack) {
        return stack.getOrCreateTag().getInt(MAX_OXYGEN_NBT_KEY);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand)); //TODO Put in GC Slot
    }

}
