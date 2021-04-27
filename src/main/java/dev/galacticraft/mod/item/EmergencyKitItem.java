package dev.galacticraft.mod.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


// https://wiki.micdoodle8.com/wiki/Space_Emergency_Kit
public class EmergencyKitItem extends Item {
    public EmergencyKitItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isCreative() || user.isSpectator()) {
            return super.use(world, user, hand);
        }

        giveItems(user);
        return new TypedActionResult<>(ActionResult.SUCCESS, new ItemStack(GalacticraftItems.HEAVY_DUTY_PICKAXE));

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("tooltip.galacticraft.emergency_kit"));

        super.appendTooltip(stack, world, tooltip, context);
    }

    private void giveItems(PlayerEntity user) {
        user.giveItemStack(new ItemStack(GalacticraftItems.OXYGEN_MASK));
        user.giveItemStack(new ItemStack(GalacticraftItems.OXYGEN_GEAR));
        user.giveItemStack(new ItemStack(GalacticraftItems.SMALL_OXYGEN_TANK, 2));
        user.giveItemStack(new ItemStack(GalacticraftItems.RED_PARACHUTE));
        user.giveItemStack(new ItemStack(GalacticraftItems.CANNED_DEHYDRATED_POTATO));
        user.giveItemStack(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.HEALING));
        user.giveItemStack(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.LONG_NIGHT_VISION));

    }

}