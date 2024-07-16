/*
 * Copyright (c) 2019-2024 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GCItems {
    public static final GCRegistry<Item> ITEMS = new GCRegistry<>(BuiltInRegistries.ITEM);
    public static final List<ItemLike> HIDDEN_ITEMS = new ArrayList<>(1);

    // === START BLOCKS ===
    // TORCHES
    public static final Item GLOWSTONE_TORCH = ITEMS.register(Constant.Block.GLOWSTONE_TORCH, new StandingAndWallBlockItem(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    public static final Item UNLIT_TORCH = ITEMS.register(Constant.Block.UNLIT_TORCH, new StandingAndWallBlockItem(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    // === END BLOCKS ===
    
    // MATERIALS
    public static final Item RAW_SILICON = new Item(new Item.Properties());
    
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Properties());

    public static final Item RAW_DESH = new Item(new Item.Properties());
    public static final Item DESH_INGOT = new Item(new Item.Properties());
    public static final Item DESH_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_DESH = new Item(new Item.Properties());

    public static final Item RAW_LEAD = new Item(new Item.Properties());
    public static final Item LEAD_INGOT = new Item(new Item.Properties());
    public static final Item LEAD_NUGGET = new Item(new Item.Properties());
    
    public static final Item RAW_ALUMINUM = new Item(new Item.Properties());
    public static final Item ALUMINUM_INGOT = new Item(new Item.Properties());
    public static final Item ALUMINUM_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_ALUMINUM = new Item(new Item.Properties());

    public static final Item RAW_TIN = new Item(new Item.Properties());
    public static final Item TIN_INGOT = new Item(new Item.Properties());
    public static final Item TIN_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_TIN = new Item(new Item.Properties());

    public static final Item RAW_TITANIUM = new Item(new Item.Properties());
    public static final Item TITANIUM_INGOT = new Item(new Item.Properties());
    public static final Item TITANIUM_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_TITANIUM = new Item(new Item.Properties());

    public static final Item COMPRESSED_BRONZE = new Item(new Item.Properties());
    public static final Item COMPRESSED_COPPER = new Item(new Item.Properties());
    public static final Item COMPRESSED_IRON = new Item(new Item.Properties());
    public static final Item COMPRESSED_STEEL = new Item(new Item.Properties());
    
    public static final Item LUNAR_SAPPHIRE = new Item(new Item.Properties());
    public static final Item DESH_STICK = new Item(new Item.Properties());
    public static final Item CARBON_FRAGMENTS = new Item(new Item.Properties());
    public static final Item IRON_SHARD = new Item(new Item.Properties());
    public static final Item SOLAR_DUST = new Item(new Item.Properties());
    public static final Item BASIC_WAFER = new Item(new Item.Properties());
    public static final Item ADVANCED_WAFER = new Item(new Item.Properties());
    public static final Item BEAM_CORE = new Item(new Item.Properties());
    public static final Item CANVAS = new Item(new Item.Properties());
    
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_FAN = new Item(new Item.Properties());
    public static final Item OXYGEN_VENT = ITEMS.register(Constant.Item.OXYGEN_VENT, new Item(new Item.Properties()));
    public static final Item SENSOR_LENS = new Item(new Item.Properties());
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Properties());
    public static final Item SINGLE_SOLAR_MODULE = new Item(new Item.Properties());
    public static final Item FULL_SOLAR_PANEL = new Item(new Item.Properties());
    public static final Item SOLAR_ARRAY_WAFER = new Item(new Item.Properties());
    public static final Item STEEL_POLE = new Item(new Item.Properties());
    public static final Item COPPER_CANISTER = new Item(new Item.Properties());
    public static final Item TIN_CANISTER = ITEMS.register(Constant.Item.TIN_CANISTER, new Item(new Item.Properties()));
    public static final Item THERMAL_CLOTH = new Item(new Item.Properties());
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Properties());
    public static final Item ORION_DRIVE = new Item(new Item.Properties());
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Properties());
    public static final Item AMBIENT_THERMAL_CONTROLLER = new Item(new Item.Properties());
    
    // FOOD
    public static final Item MOON_BERRIES = new ItemNameBlockItem(GCBlocks.MOON_BERRY_BUSH, new Item.Properties().food(GCFoodComponent.MOON_BERRIES));
    public static final Item CHEESE_CURD = new Item(new Item.Properties().food(GCFoodComponent.CHEESE_CURD));
    
    public static final Item CHEESE_SLICE = ITEMS.register(Constant.Item.CHEESE_SLICE, new Item(new Item.Properties().food(GCFoodComponent.CHEESE_SLICE)));
    public static final Item BURGER_BUN = ITEMS.register(Constant.Item.BURGER_BUN, new Item(new Item.Properties().food(GCFoodComponent.BURGER_BUN)));
    public static final Item GROUND_BEEF = ITEMS.register(Constant.Item.GROUND_BEEF, new Item(new Item.Properties().food(GCFoodComponent.GROUND_BEEF)));
    public static final Item BEEF_PATTY = ITEMS.register(Constant.Item.BEEF_PATTY, new Item(new Item.Properties().food(GCFoodComponent.BEEF_PATTY)));
    public static final Item CHEESEBURGER = ITEMS.register(Constant.Item.CHEESEBURGER, new Item(new Item.Properties().food(GCFoodComponent.CHEESEBURGER)));
    
    public static final Item CANNED_DEHYDRATED_APPLE = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_APPLE));
    public static final Item CANNED_DEHYDRATED_CARROT = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_CARROT));
    public static final Item CANNED_DEHYDRATED_MELON = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_MELON));
    public static final Item CANNED_DEHYDRATED_POTATO = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_POTATO));
    public static final Item CANNED_BEEF = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.CANNED_BEEF));
    
    // ROCKET PLATES
    public static final Item TIER_1_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_1_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_2_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_3_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));

    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = new ThrowableMeteorChunkItem(new Item.Properties().stacksTo(16));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = new HotThrowableMeteorChunkItem(new Item.Properties().stacksTo(16));

    // ARMOR
    public static final Item HEAVY_DUTY_HELMET = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item HEAVY_DUTY_CHESTPLATE = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item HEAVY_DUTY_LEGGINGS = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item HEAVY_DUTY_BOOTS = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item DESH_HELMET = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item DESH_CHESTPLATE = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item DESH_LEGGINGS = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item DESH_BOOTS = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item TITANIUM_HELMET = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item TITANIUM_CHESTPLATE = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item TITANIUM_LEGGINGS = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item TITANIUM_BOOTS = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item SENSOR_GLASSES = new ArmorItem(GCArmorMaterial.SENSOR_GLASSES, ArmorItem.Type.HELMET, new Item.Properties());

    // TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = new BrittleSwordItem(GCToolMaterial.STEEL, 3, -2.4F, new Item.Properties());
    public static final Item HEAVY_DUTY_SHOVEL = new ShovelItem(GCToolMaterial.STEEL, -1.5F, -3.0F, new Item.Properties());
    public static final Item HEAVY_DUTY_PICKAXE = new PickaxeItem(GCToolMaterial.STEEL, 1, -2.8F, new Item.Properties());
    public static final Item HEAVY_DUTY_AXE = new AxeItem(GCToolMaterial.STEEL, 6.0F, -3.1F, new Item.Properties());
    public static final Item HEAVY_DUTY_HOE = new HoeItem(GCToolMaterial.STEEL, -2, -1.0F, new Item.Properties());

    public static final Item DESH_SWORD = new SwordItem(GCToolMaterial.DESH, 3, -2.4F, new Item.Properties());
    public static final Item DESH_SHOVEL = new ShovelItem(GCToolMaterial.DESH, -1.5F, -3.0F, new Item.Properties());
    public static final Item DESH_PICKAXE = new PickaxeItem(GCToolMaterial.DESH, 1, -2.8F, new Item.Properties());
    public static final Item DESH_AXE = new AxeItem(GCToolMaterial.DESH, 6.0F, -3.1F, new Item.Properties());
    public static final Item DESH_HOE = new HoeItem(GCToolMaterial.DESH, -3, -1.0F, new Item.Properties());

    public static final Item TITANIUM_SWORD = new BrittleSwordItem(GCToolMaterial.TITANIUM, 3, -2.4F, new Item.Properties());
    public static final Item TITANIUM_SHOVEL = new ShovelItem(GCToolMaterial.TITANIUM, -1.5F, -3.0F, new Item.Properties());
    public static final Item TITANIUM_PICKAXE = new PickaxeItem(GCToolMaterial.TITANIUM, 1, -2.8F, new Item.Properties());
    public static final Item TITANIUM_AXE = new AxeItem(GCToolMaterial.TITANIUM, 6.0F, -3.1F, new Item.Properties());
    public static final Item TITANIUM_HOE = new HoeItem(GCToolMaterial.TITANIUM, -3, -1.0F, new Item.Properties());

    public static final Item STANDARD_WRENCH = new StandardWrenchItem(new Item.Properties().durability(256));

    // SMITHING TEMPLATES
    public static final Item TITANTIUM_UPGRADE_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_APPLIES_TO),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_INGREDIENTS),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_DESCRIPTION),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON),
            List.of(new ResourceLocation("item/empty_armor_slot_helmet"),
                    new ResourceLocation("item/empty_armor_slot_chestplate"),
                    new ResourceLocation("item/empty_armor_slot_leggings"),
                    new ResourceLocation("item/empty_armor_slot_boots"),
                    new ResourceLocation("item/empty_slot_hoe"),
                    new ResourceLocation("item/empty_slot_axe"),
                    new ResourceLocation("item/empty_slot_sword"),
                    new ResourceLocation("item/empty_slot_shovel"),
                    new ResourceLocation("item/empty_slot_pickaxe")),
            List.of(new ResourceLocation("item/empty_slot_ingot"))
    );
    // 		this.appliesTo = component;
    //		this.ingredients = component2;
    //		this.upgradeDescription = component3;
    //		this.baseSlotDescription = component4;
    //		this.additionsSlotDescription = component5;

    // BATTERIES
    public static final Item BATTERY = new BatteryItem(new Item.Properties(), 15000, 500);
    public static final Item INFINITE_BATTERY = new InfiniteBatteryItem(new Item.Properties().rarity(Rarity.EPIC));

    //FLUID BUCKETS
    public static final Item CRUDE_OIL_BUCKET = new BucketItem(GCFluids.CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final Item FUEL_BUCKET = new BucketItem(GCFluids.FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final Item SULFURIC_ACID_BUCKET = new BucketItem(GCFluids.SULFURIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

    //GALACTICRAFT INVENTORY
    public static final GCRegistry.ColorSet<ParachuteItem> PARACHUTE = ITEMS.registerColored(Constant.Item.PARACHUTE, color -> new ParachuteItem(color, new Item.Properties().stacksTo(1)));

    public static final Item OXYGEN_MASK = new OxygenMaskItem(new Item.Properties());
    public static final Item OXYGEN_GEAR = new OxygenGearItem(new Item.Properties());

    public static final Item SMALL_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 10); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 20); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 30); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = new InfiniteOxygenTankItem(new Item.Properties());

    public static final Item SHIELD_CONTROLLER = new AccessoryItem(new Item.Properties());
    public static final Item FREQUENCY_MODULE = new FrequencyModuleItem(new Item.Properties());

    public static final Item THERMAL_PADDING_HELMET = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.HELMET);
    public static final Item THERMAL_PADDING_CHESTPIECE = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.CHESTPLATE);
    public static final Item THERMAL_PADDING_LEGGINGS = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.LEGGINGS);
    public static final Item THERMAL_PADDING_BOOTS = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.BOOTS);
    // Vehicles
    public static final Item BUGGY = ITEMS.register(Constant.Item.BUGGY, new BuggyItem(new Item.Properties().stacksTo(1)));
    public static final Item ROCKET = ITEMS.register(Constant.Item.ROCKET, new RocketItem(new Item.Properties().stacksTo(1)));

    // ROCKET PIECES
    public static final Item NOSE_CONE = ITEMS.register(Constant.Item.NOSE_CONE, new Item(new Item.Properties()));
    public static final Item HEAVY_NOSE_CONE = ITEMS.register(Constant.Item.HEAVY_NOSE_CONE, new Item(new Item.Properties()));
    public static final Item ROCKET_FIN = ITEMS.register(Constant.Item.ROCKET_FIN, new Item(new Item.Properties()));
    public static final Item ROCKET_ENGINE = ITEMS.register(Constant.Item.ROCKET_ENGINE, new Item(new Item.Properties()));

    // SCHEMATICS
    public static final Item BASIC_ROCKET_CONE_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_CONE);
    public static final Item BASIC_ROCKET_BODY_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_BODY);
    public static final Item BASIC_ROCKET_FINS_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_FIN);
    public static final Item BASIC_ROCKET_ENGINE_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_ENGINE);

    public static final Item TIER_2_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item CARGO_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item MOON_BUGGY_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item TIER_3_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item ASTRO_MINER_SCHEMATIC = new SchematicItem(new Item.Properties());
    
    public static void register() {
        // MATERIALS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_SILICON), RAW_SILICON);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_METEORIC_IRON), RAW_METEORIC_IRON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.METEORIC_IRON_INGOT), METEORIC_IRON_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.METEORIC_IRON_NUGGET), METEORIC_IRON_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_METEORIC_IRON), COMPRESSED_METEORIC_IRON);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_DESH), RAW_DESH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_INGOT), DESH_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_NUGGET), DESH_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_DESH), COMPRESSED_DESH);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_LEAD), RAW_LEAD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LEAD_INGOT), LEAD_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LEAD_NUGGET), LEAD_NUGGET);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_ALUMINUM), RAW_ALUMINUM);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ALUMINUM_INGOT), ALUMINUM_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ALUMINUM_NUGGET), ALUMINUM_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_ALUMINUM), COMPRESSED_ALUMINUM);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_TIN), RAW_TIN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIN_INGOT), TIN_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIN_NUGGET), TIN_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_TIN), COMPRESSED_TIN);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_TITANIUM), RAW_TITANIUM);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_INGOT), TITANIUM_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_NUGGET), TITANIUM_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_TITANIUM), COMPRESSED_TITANIUM);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_BRONZE), COMPRESSED_BRONZE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_COPPER), COMPRESSED_COPPER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_IRON), COMPRESSED_IRON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_STEEL), COMPRESSED_STEEL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LUNAR_SAPPHIRE), LUNAR_SAPPHIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_STICK), DESH_STICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CARBON_FRAGMENTS), CARBON_FRAGMENTS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.IRON_SHARD), IRON_SHARD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_DUST), SOLAR_DUST);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_WAFER), BASIC_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ADVANCED_WAFER), ADVANCED_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BEAM_CORE), BEAM_CORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANVAS), CANVAS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FLUID_MANIPULATOR), FLUID_MANIPULATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_CONCENTRATOR), OXYGEN_CONCENTRATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_FAN), OXYGEN_FAN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SENSOR_LENS), SENSOR_LENS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BLUE_SOLAR_WAFER), BLUE_SOLAR_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SINGLE_SOLAR_MODULE), SINGLE_SOLAR_MODULE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FULL_SOLAR_PANEL), FULL_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_ARRAY_WAFER), SOLAR_ARRAY_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.STEEL_POLE), STEEL_POLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COPPER_CANISTER), COPPER_CANISTER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ORION_DRIVE), ORION_DRIVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.AMBIENT_THERMAL_CONTROLLER), AMBIENT_THERMAL_CONTROLLER);

        // FOOD
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MOON_BERRIES), MOON_BERRIES);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CHEESE_CURD), CHEESE_CURD);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_APPLE), CANNED_DEHYDRATED_APPLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_CARROT), CANNED_DEHYDRATED_CARROT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_MELON), CANNED_DEHYDRATED_MELON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_POTATO), CANNED_DEHYDRATED_POTATO);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_BEEF), CANNED_BEEF);

        // THROWABLE METEOR CHUNKS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THROWABLE_METEOR_CHUNK), THROWABLE_METEOR_CHUNK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HOT_THROWABLE_METEOR_CHUNK), HOT_THROWABLE_METEOR_CHUNK);

        // ARMOR
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HELMET), HEAVY_DUTY_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_CHESTPLATE), HEAVY_DUTY_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_LEGGINGS), HEAVY_DUTY_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_BOOTS), HEAVY_DUTY_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_HELMET), DESH_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_CHESTPLATE), DESH_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_LEGGINGS), DESH_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_BOOTS), DESH_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_HELMET), TITANIUM_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_CHESTPLATE), TITANIUM_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_LEGGINGS), TITANIUM_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_BOOTS), TITANIUM_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SENSOR_GLASSES), SENSOR_GLASSES);

        // TOOLS + WEAPONS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SWORD), HEAVY_DUTY_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SHOVEL), HEAVY_DUTY_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_PICKAXE), HEAVY_DUTY_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_AXE), HEAVY_DUTY_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HOE), HEAVY_DUTY_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_SWORD), DESH_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_SHOVEL), DESH_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_PICKAXE), DESH_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_AXE), DESH_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_HOE), DESH_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_SWORD), TITANIUM_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_SHOVEL), TITANIUM_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_PICKAXE), TITANIUM_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_AXE), TITANIUM_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_HOE), TITANIUM_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.STANDARD_WRENCH), STANDARD_WRENCH);

        // SMITHING TEMPLATES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANTIUM_UPGRADE_SMITHING_TEMPLATE), TITANTIUM_UPGRADE_SMITHING_TEMPLATE);

        // BATTERIES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BATTERY), BATTERY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.INFINITE_BATTERY), INFINITE_BATTERY);

        //FLUID BUCKETS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CRUDE_OIL_BUCKET), CRUDE_OIL_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FUEL_BUCKET), FUEL_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SULFURIC_ACID_BUCKET), SULFURIC_ACID_BUCKET);

        //GALACTICRAFT INVENTORY
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_MASK), OXYGEN_MASK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_GEAR), OXYGEN_GEAR);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SMALL_OXYGEN_TANK), SMALL_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MEDIUM_OXYGEN_TANK), MEDIUM_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LARGE_OXYGEN_TANK), LARGE_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.INFINITE_OXYGEN_TANK), INFINITE_OXYGEN_TANK);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SHIELD_CONTROLLER), SHIELD_CONTROLLER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FREQUENCY_MODULE), FREQUENCY_MODULE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_HELMET), THERMAL_PADDING_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_CHESTPIECE), THERMAL_PADDING_CHESTPIECE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_LEGGINGS), THERMAL_PADDING_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_BOOTS), THERMAL_PADDING_BOOTS);

        // SCHEMATICS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_CONE_SCHEMATIC), BASIC_ROCKET_CONE_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_BODY_SCHEMATIC), BASIC_ROCKET_BODY_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_FINS_SCHEMATIC), BASIC_ROCKET_FINS_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_ENGINE_SCHEMATIC), BASIC_ROCKET_ENGINE_SCHEMATIC);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_2_ROCKET_SCHEMATIC), TIER_2_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CARGO_ROCKET_SCHEMATIC), CARGO_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MOON_BUGGY_SCHEMATIC), MOON_BUGGY_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_3_ROCKET_SCHEMATIC), TIER_3_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ASTRO_MINER_SCHEMATIC), ASTRO_MINER_SCHEMATIC);

        DispenserBlock.registerBehavior(FUEL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        DispenserBlock.registerBehavior(CRUDE_OIL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        DispenserBlock.registerBehavior(SULFURIC_ACID_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
    }
}
