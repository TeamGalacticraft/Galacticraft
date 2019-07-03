package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftItems {
    public static ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItems.CANVAS))
            .build();


    // MATERIALS
    public static final Item ALUMINUM_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COPPER_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TIN_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item LEAD_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item RAW_SILICON = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item LUNAR_SAPPHIRE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item UNREFINED_DESH = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_STICK = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item FRAGMENTED_CARBON = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item IRON_SHARD = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_SHARD = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_INGOT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_DUST = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item SOLAR_DUST = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BASIC_WAFER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ADVANCED_WAFER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BEAM_CORE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item CANVAS = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_ALUMINUM = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_TIN = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_BRONZE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_COPPER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_IRON = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_STEEL = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_DESH = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COMPRESSED_TITANIUM = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item OXYGEN_FAN = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item OXYGEN_VENT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item SENSOR_LENS = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item SINGLE_SOLAR_MODULE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item FULL_SOLAR_PANEL = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item SOLAR_ARRAY_WAFER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item STEEL_POLE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item COPPER_CANISTER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TIN_CANISTER = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item THERMAL_CLOTH = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ORION_DRIVE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Settings().group(ITEMS_GROUP));


    //FOOD
    public static final Item MOON_BERRIES = new Item(new Item.Settings().food(GCFoodSettings.MOON_BERRIES).group(ITEMS_GROUP));
    public static final Item CHEESE_CURD = new Item(new Item.Settings().food(GCFoodSettings.CHEESE_CURD).group(ITEMS_GROUP));
    public static final Item CHEESE_SLICE = new Item(new Item.Settings().food(GCFoodSettings.CHEESE_SLICE).group(ITEMS_GROUP));
    public static final Item BURGER_BUN = new Item(new Item.Settings().food(GCFoodSettings.BURGER_BUN).group(ITEMS_GROUP));
    public static final Item GROUND_BEEF = new Item(new Item.Settings().food(GCFoodSettings.GROUND_BEEF).group(ITEMS_GROUP));
    public static final Item BEEF_PATTY = new Item(new Item.Settings().food(GCFoodSettings.BEEF_PATTY).group(ITEMS_GROUP));
    public static final Item CHEESEBURGER = new Item(new Item.Settings().food(GCFoodSettings.CHEESEBURGER).group(ITEMS_GROUP));

    public static final Item CANNED_DEHYDRATED_APPLE = new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_APPLE).group(ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_CARROT = new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_CARROT).group(ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_MELON = new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_MELON).group(ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_POTATO = new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_POTATO).group(ITEMS_GROUP));
    public static final Item CANNED_BEEF = new CannedFoodItem(new Item.Settings().food(GCFoodSettings.CANNED_BEEF).group(ITEMS_GROUP));

    //ROCKET PARTS
    public static final Item TIER_1_HEAVY_DUTY_PLATE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item NOSE_CONE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item HEAVY_NOSE_CONE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ROCKET_ENGINE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item HEAVY_ROCKET_ENGINE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ROCKET_FIN = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item HEAVY_ROCKET_FIN = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item TIER_1_BOOSTER = new Item(new Item.Settings().group(ITEMS_GROUP));

    //BUGGY PARTS
    public static final Item BUGGY_SEAT = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BUGGY_STORAGE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BUGGY_WHEEL = new Item(new Item.Settings().group(ITEMS_GROUP));

    //TOOLS + WEAPONS
    public static final Item STEEL_SWORD = new GCSwordItem(GalacticraftToolMaterials.STEEL, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item STEEL_SHOVEL = new ShovelItem(GalacticraftToolMaterials.STEEL, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item STEEL_PICKAXE = new GCPickaxeItem(GalacticraftToolMaterials.STEEL, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item STEEL_AXE = new GCAxeItem(GalacticraftToolMaterials.STEEL, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item STEEL_HOE = new HoeItem(GalacticraftToolMaterials.STEEL, -1.0F, new Item.Settings().group(ITEMS_GROUP));

    public static final Item DESH_SWORD = new GCSwordItem(GalacticraftToolMaterials.DESH, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_SHOVEL = new ShovelItem(GalacticraftToolMaterials.DESH, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_PICKAXE = new GCPickaxeItem(GalacticraftToolMaterials.DESH, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_AXE = new GCAxeItem(GalacticraftToolMaterials.DESH, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item DESH_HOE = new HoeItem(GalacticraftToolMaterials.DESH, -1.0F, new Item.Settings().group(ITEMS_GROUP));

    public static final Item TITANIUM_SWORD = new GCSwordItem(GalacticraftToolMaterials.TITANIUM, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_SHOVEL = new ShovelItem(GalacticraftToolMaterials.TITANIUM, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_PICKAXE = new GCPickaxeItem(GalacticraftToolMaterials.TITANIUM, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_AXE = new GCAxeItem(GalacticraftToolMaterials.TITANIUM, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP));
    public static final Item TITANIUM_HOE = new HoeItem(GalacticraftToolMaterials.TITANIUM, -1.0F, new Item.Settings().group(ITEMS_GROUP));

    public static final Item STANDARD_WRENCH = new StandardWrenchItem(new Item.Settings().group(ITEMS_GROUP));

    public static final Item BATTERY = new BatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(BatteryItem.MAX_ENERGY));
    public static final Item INFINITE_BATTERY = new InfiniteBatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(BatteryItem.MAX_ENERGY));

    //Fluid buckets
    public static final BucketItem CRUDE_OIL_BUCKET = new BucketItem(GalacticraftFluids.STILL_CRUDE_OIL, new Item.Settings().group(ITEMS_GROUP));

    //GC INVENTORY
    public static final Item PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item ORANGE_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item MAGENTA_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item LIGHT_BLUE_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item YELLOW_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item LIME_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item PINK_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item GRAY_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item LIGHT_GRAY_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item CYAN_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item PURPLE_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BLUE_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BROWN_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item GREEN_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item RED_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));
    public static final Item BLACK_PARACHUTE = new Item(new Item.Settings().group(ITEMS_GROUP));

    public static final Item OXYGEN_MASK = new Item((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item OXYGEN_GEAR = new Item((new Item.Settings()).group(ITEMS_GROUP));

    public static final Item SHIELD_CONTROLLER = new GCAccessories((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item FREQUENCY_MODULE = new GCAccessories((new Item.Settings()).group(ITEMS_GROUP));

    public static final Item SMALL_OXYGEN_TANK = new OxygenTankItem((new Item.Settings()).group(ITEMS_GROUP).maxDamage(900));
    public static final Item MEDIUM_OXYGEN_TANK = new OxygenTankItem((new Item.Settings()).group(ITEMS_GROUP).maxDamage(1800));
    public static final Item LARGE_OXYGEN_TANK = new OxygenTankItem((new Item.Settings()).group(ITEMS_GROUP).maxDamage(2700));

    public static final Item THERMAL_PADDING_HELMET = new ThermalArmorItem((new Item.Settings()).group(ITEMS_GROUP), EquipmentSlot.HEAD);
    public static final Item THERMAL_PADDING_CHESTPIECE = new ThermalArmorItem((new Item.Settings()).group(ITEMS_GROUP), EquipmentSlot.CHEST);
    public static final Item THERMAL_PADDING_LEGGINGS = new ThermalArmorItem((new Item.Settings()).group(ITEMS_GROUP), EquipmentSlot.LEGS);
    public static final Item THERMAL_PADDING_BOOTS = new ThermalArmorItem((new Item.Settings()).group(ITEMS_GROUP), EquipmentSlot.FEET);

    public static final Item TIER_2_ROCKET_SCHEMATIC = new SchematicItem((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item CARGO_ROCKET_SCHEMATIC = new SchematicItem((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item MOON_BUGGY_SCHEMATIC = new SchematicItem((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item TIER_3_ROCKET_SCHEMATIC = new SchematicItem((new Item.Settings()).group(ITEMS_GROUP));
    public static final Item ASTRO_MINER_SCHEMATIC = new SchematicItem((new Item.Settings()).group(ITEMS_GROUP));

    public static final Item ROCKET_SPAWN_EGG = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "rocket_spawn_egg"), new SpawnEggItem(GalacticraftEntityTypes.T1_ROCKET, 15714446, 9794134, (new Item.Settings()).group(ItemGroup.MISC)));


    public static void register() {
        //MATERIALS
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
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CARBON_FRAGMENTS), FRAGMENTED_CARBON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.IRON_SHARD), IRON_SHARD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SHARD), TITANIUM_SHARD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_INGOT), TITANIUM_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_DUST), TITANIUM_DUST);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SOLAR_DUST), SOLAR_DUST);
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
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SINGLE_SOLAR_MODULE), SINGLE_SOLAR_MODULE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FULL_SOLAR_PANEL), FULL_SOLAR_PANEL);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SOLAR_ARRAY_WAFER), SOLAR_ARRAY_WAFER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_POLE), STEEL_POLE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COPPER_CANISTER), COPPER_CANISTER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIN_CANISTER), TIN_CANISTER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ORION_DRIVE), ORION_DRIVE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);

        //FOOD
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MOON_BERRIES), MOON_BERRIES);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_CURD), CHEESE_CURD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_SLICE), CHEESE_SLICE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BURGER_BUN), BURGER_BUN);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GROUND_BEEF), GROUND_BEEF);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BEEF_PATTY), BEEF_PATTY);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESEBURGER), CHEESEBURGER);

        //CANNED FOOD
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_APPLE), CANNED_DEHYDRATED_APPLE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_CARROT), CANNED_DEHYDRATED_CARROT);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_MELON), CANNED_DEHYDRATED_MELON);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_POTATO), CANNED_DEHYDRATED_POTATO);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_BEEF), CANNED_BEEF);

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
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_STORAGE), BUGGY_STORAGE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_WHEEL), BUGGY_WHEEL);

        //TOOLS + WEAPONS
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_SWORD), STEEL_SWORD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_SHOVEL), STEEL_SHOVEL);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_PICKAXE), STEEL_PICKAXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_AXE), STEEL_AXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_HOE), STEEL_HOE);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_SWORD), DESH_SWORD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_SHOVEL), DESH_SHOVEL);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_PICKAXE), DESH_PICKAXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_AXE), DESH_AXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_HOE), DESH_HOE);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SWORD), TITANIUM_SWORD);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SHOVEL), TITANIUM_SHOVEL);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_PICKAXE), TITANIUM_PICKAXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_AXE), TITANIUM_AXE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_HOE), TITANIUM_HOE);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STANDARD_WRENCH), STANDARD_WRENCH);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BATTERY), BATTERY);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.INFINITE_BATTERY), INFINITE_BATTERY);

        //Fluid buckets
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CRUDE_OIL_BUCKET), CRUDE_OIL_BUCKET);

        //GC INVENTORY
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PARACHUTE), PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ORANGE_PARACHUTE), ORANGE_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MAGENTA_PARACHUTE), MAGENTA_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIGHT_BLUE_PARACHUTE), LIGHT_BLUE_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.YELLOW_PARACHUTE), YELLOW_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIME_PARACHUTE), LIME_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PINK_PARACHUTE), PINK_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GRAY_PARACHUTE), GRAY_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIGHT_GRAY_PARACHUTE), LIGHT_GRAY_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CYAN_PARACHUTE), CYAN_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PURPLE_PARACHUTE), PURPLE_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLUE_PARACHUTE), BLUE_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BROWN_PARACHUTE), BROWN_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GREEN_PARACHUTE), GREEN_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RED_PARACHUTE), RED_PARACHUTE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLACK_PARACHUTE), BLACK_PARACHUTE);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_MASK), OXYGEN_MASK);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_GEAR), OXYGEN_GEAR);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SHIELD_CONTROLLER), SHIELD_CONTROLLER);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FREQUENCY_MODULE), FREQUENCY_MODULE);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SMALL_OXYGEN_TANK), SMALL_OXYGEN_TANK);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MEDIUM_OXYGEN_TANK), MEDIUM_OXYGEN_TANK);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LARGE_OXYGEN_TANK), LARGE_OXYGEN_TANK);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_HELMET), THERMAL_PADDING_HELMET);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_CHESTPIECE), THERMAL_PADDING_CHESTPIECE);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_LEGGINGS), THERMAL_PADDING_LEGGINGS);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_BOOTS), THERMAL_PADDING_BOOTS);

        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MOON_BUGGY_SCHEMATIC), MOON_BUGGY_SCHEMATIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_2_ROCKET_SCHEMATIC), TIER_2_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CARGO_ROCKET_SCHEMATIC), CARGO_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_3_ROCKET_SCHEMATIC), TIER_3_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ASTRO_MINER_SCHEMATIC), ASTRO_MINER_SCHEMATIC);
    }

    public static boolean isRocketItem(Item item) {
        return item == GalacticraftItems.ROCKET_SPAWN_EGG;
    }
}
