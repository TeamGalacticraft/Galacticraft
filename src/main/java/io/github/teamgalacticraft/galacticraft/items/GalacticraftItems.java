package io.github.teamgalacticraft.galacticraft.items;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
//import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftItems {

    public static ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP)).icon(() -> new ItemStack(GalacticraftItems.RAW_SILICON)).build();

    // MATERIALS
    public static final Item ALUMINUM_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COPPER_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TIN_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item LEAD_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item RAW_SILICON = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item LUNAR_SAPPHIRE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item UNREFINED_DESH = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item DESH_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item DESH_STICK = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item CARBON_FRAGMENTS = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item IRON_SHARD = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TITANIUM_SHARD = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TITANIUM_INGOT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TITANIUM_DUST = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item BASIC_WAFER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ADVANCED_WAFER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item BEAM_CORE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item CANVAS = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_ALUMINUM = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_COPPER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_TIN = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_BRONZE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_IRON = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_STEEL = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_DESH = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item COMPRESSED_TITANIUM = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item OXYGEN_FAN = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item OXYGEN_VENT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item SENSOR_LENS = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item STEEL_POLE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item CANISTER_COPPER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item CANISTER_TIN = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item THERMAL_CLOTH = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ORION_DRIVE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));

    ////FOOD
    //public static final Item CHEESE_CURD = new FoodItem(1, 0.1F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item CHEESE_SLICE = new FoodItem(2, 0.1F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item BURGER_BUN = new FoodItem(2, 0.3F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item GROUND_BEEF = new FoodItem(3, 0.6F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item BEEF_PATTY = new FoodItem(4, 0.8F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item CHEESEBURGER = new FoodItem(14, 4.0F, false, new Item.Settings().itemGroup(ITEMS_GROUP));

    //public static final Item DEHYDRATED_APPLE = new CannedFoodItem(8, 0.3F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item DEHYDRATED_CARROT = new CannedFoodItem(8, 0.6F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item DEHYDRATED_MELON = new CannedFoodItem(4, 0.3F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item DEHYDRATED_POTATO = new CannedFoodItem(2, 0.3F, false, new Item.Settings().itemGroup(ITEMS_GROUP));
    //public static final Item CANNED_BEEF = new CannedFoodItem(8, 0.6F, false, new Item.Settings().itemGroup(ITEMS_GROUP));

    //ROCKET PARTS

    public static final Item TIER_1_HEAVY_DUTY_PLATE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item NOSE_CONE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item HEAVY_NOSE_CONE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ROCKET_ENGINE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item HEAVY_ROCKET_ENGINE = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item ROCKET_FIN = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item HEAVY_ROCKET_FIN = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item TIER_1_BOOSTER = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));

    //BUGGY PARTS
    public static final Item BUGGY_SEAT = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item BUGGY_STORAGE_BOX = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));
    public static final Item BUGGY_WHEEL = new Item(new Item.Settings().itemGroup(ITEMS_GROUP));

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ALUMINUM_INGOT), ALUMINUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COPPER_INGOT), COPPER_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIN_INGOT), TIN_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LEAD_INGOT), LEAD_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RAW_SILICON), RAW_SILICON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RAW_METEORIC_IRON), RAW_METEORIC_IRON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.METEORIC_IRON_INGOT), METEORIC_IRON_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LUNAR_SAPPHIRE), LUNAR_SAPPHIRE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.UNREFINED_DESH), UNREFINED_DESH);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_INGOT), DESH_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_STICK), DESH_STICK);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CARBON_FRAGMENTS), CARBON_FRAGMENTS);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.IRON_SHARD), IRON_SHARD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SHARD), TITANIUM_SHARD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_INGOT), TITANIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_DUST), TITANIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BASIC_WAFER), BASIC_WAFER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ADVANCED_WAFER), ADVANCED_WAFER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BEAM_CORE), BEAM_CORE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANVAS), CANVAS);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_ALUMINUM), COMPRESSED_ALUMINUM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_COPPER), COMPRESSED_COPPER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_TIN), COMPRESSED_TIN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_BRONZE), COMPRESSED_BRONZE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_IRON), COMPRESSED_IRON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_STEEL), COMPRESSED_STEEL);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_METEORIC_IRON), COMPRESSED_METEORIC_IRON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_DESH), COMPRESSED_DESH);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_TITANIUM), COMPRESSED_TITANIUM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FLUID_MANIPULATOR), FLUID_MANIPULATOR);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_CONCENTRATOR), OXYGEN_CONCENTRATOR);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_FAN), OXYGEN_FAN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_VENT), OXYGEN_VENT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SENSOR_LENS), SENSOR_LENS);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLUE_SOLAR_WAFER), BLUE_SOLAR_WAFER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_POLE), STEEL_POLE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANISTER_COPPER), CANISTER_COPPER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANISTER_TIN), CANISTER_TIN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ORION_DRIVE), ORION_DRIVE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);
        //FOOD
        /*Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_CURD), CHEESE_CURD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_SLICE), CHEESE_SLICE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BURGER_BUN), BURGER_BUN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GROUND_BEEF), GROUND_BEEF);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BEEF_PATTY), BEEF_PATTY);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESEBURGER), CHEESEBURGER);
        //CANNED FOOD
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DEHYDRATED_APPLE), DEHYDRATED_APPLE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DEHYDRATED_CARROT), DEHYDRATED_CARROT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DEHYDRATED_MELON), DEHYDRATED_MELON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DEHYDRATED_POTATO), DEHYDRATED_POTATO);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_BEEF), CANNED_BEEF);*/
        //ROCKET PARTS
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_1_HEAVY_DUTY_PLATE), TIER_1_HEAVY_DUTY_PLATE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_2_HEAVY_DUTY_PLATE), TIER_2_HEAVY_DUTY_PLATE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_3_HEAVY_DUTY_PLATE), TIER_3_HEAVY_DUTY_PLATE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.NOSE_CONE), NOSE_CONE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_NOSE_CONE), HEAVY_NOSE_CONE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ROCKET_ENGINE), ROCKET_ENGINE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_ROCKET_ENGINE), HEAVY_ROCKET_ENGINE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ROCKET_FIN), ROCKET_FIN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_ROCKET_FIN), HEAVY_ROCKET_FIN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_1_BOOSTER), TIER_1_BOOSTER);
        //BUGGY
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_SEAT), BUGGY_SEAT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_STORAGE_BOX), BUGGY_STORAGE_BOX);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_WHEEL), BUGGY_WHEEL);
    }
}
