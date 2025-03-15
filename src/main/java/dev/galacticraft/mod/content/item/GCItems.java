/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCJukeboxSongs;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.Direction;
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

    // TORCHES
    public static final Item GLOWSTONE_TORCH = ITEMS.register(Constant.Block.GLOWSTONE_TORCH, new StandingAndWallBlockItem(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    public static final Item UNLIT_TORCH = ITEMS.register(Constant.Block.UNLIT_TORCH, new StandingAndWallBlockItem(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    
    // MATERIALS
    public static final Item SILICON = registerGeneric(Constant.Item.SILICON);
    
    public static final Item RAW_METEORIC_IRON = registerGeneric(Constant.Item.RAW_METEORIC_IRON);
    public static final Item METEORIC_IRON_INGOT = registerGeneric(Constant.Item.METEORIC_IRON_INGOT);
    public static final Item METEORIC_IRON_NUGGET = registerGeneric(Constant.Item.METEORIC_IRON_NUGGET);
    public static final Item COMPRESSED_METEORIC_IRON = registerGeneric(Constant.Item.COMPRESSED_METEORIC_IRON);

    public static final Item OLIVINE_SHARD = registerGeneric(Constant.Item.OLIVINE_SHARD);

    public static final Item RAW_DESH = registerGeneric(Constant.Item.RAW_DESH);
    public static final Item DESH_INGOT = registerGeneric(Constant.Item.DESH_INGOT);
    public static final Item DESH_NUGGET = registerGeneric(Constant.Item.DESH_NUGGET);
    public static final Item COMPRESSED_DESH = registerGeneric(Constant.Item.COMPRESSED_DESH);

    public static final Item RAW_LEAD = registerGeneric(Constant.Item.RAW_LEAD);
    public static final Item LEAD_INGOT = registerGeneric(Constant.Item.LEAD_INGOT);
    public static final Item LEAD_NUGGET = registerGeneric(Constant.Item.LEAD_NUGGET);
    
    public static final Item RAW_ALUMINUM = registerGeneric(Constant.Item.RAW_ALUMINUM);
    public static final Item ALUMINUM_INGOT = registerGeneric(Constant.Item.ALUMINUM_INGOT);
    public static final Item ALUMINUM_NUGGET = registerGeneric(Constant.Item.ALUMINUM_NUGGET);
    public static final Item COMPRESSED_ALUMINUM = registerGeneric(Constant.Item.COMPRESSED_ALUMINUM);

    public static final Item RAW_TIN = registerGeneric(Constant.Item.RAW_TIN);
    public static final Item TIN_INGOT = registerGeneric(Constant.Item.TIN_INGOT);
    public static final Item TIN_NUGGET = registerGeneric(Constant.Item.TIN_NUGGET);
    public static final Item COMPRESSED_TIN = registerGeneric(Constant.Item.COMPRESSED_TIN);

    public static final Item RAW_TITANIUM = registerGeneric(Constant.Item.RAW_TITANIUM);
    public static final Item TITANIUM_INGOT = registerGeneric(Constant.Item.TITANIUM_INGOT);
    public static final Item TITANIUM_NUGGET = registerGeneric(Constant.Item.TITANIUM_NUGGET);
    public static final Item COMPRESSED_TITANIUM = registerGeneric(Constant.Item.COMPRESSED_TITANIUM);

    public static final Item COMPRESSED_BRONZE = registerGeneric(Constant.Item.COMPRESSED_BRONZE);
    public static final Item COMPRESSED_COPPER = registerGeneric(Constant.Item.COMPRESSED_COPPER);
    public static final Item COMPRESSED_IRON = registerGeneric(Constant.Item.COMPRESSED_IRON);
    public static final Item COMPRESSED_STEEL = registerGeneric(Constant.Item.COMPRESSED_STEEL);
    
    public static final Item LUNAR_SAPPHIRE = registerGeneric(Constant.Item.LUNAR_SAPPHIRE);
    public static final Item DESH_STICK = registerGeneric(Constant.Item.DESH_STICK);
    public static final Item CARBON_FRAGMENTS = registerGeneric(Constant.Item.CARBON_FRAGMENTS);
    public static final Item SOLAR_DUST = registerGeneric(Constant.Item.SOLAR_DUST);
    public static final Item BASIC_WAFER = registerGeneric(Constant.Item.BASIC_WAFER);
    public static final Item ADVANCED_WAFER = registerGeneric(Constant.Item.ADVANCED_WAFER);
    public static final Item BEAM_CORE = registerGeneric(Constant.Item.BEAM_CORE);
    public static final Item CANVAS = registerGeneric(Constant.Item.CANVAS);
    
    public static final Item FLUID_MANIPULATOR = registerGeneric(Constant.Item.FLUID_MANIPULATOR);
    public static final Item OXYGEN_CONCENTRATOR = registerGeneric(Constant.Item.OXYGEN_CONCENTRATOR);
    public static final Item OXYGEN_FAN = registerGeneric(Constant.Item.OXYGEN_FAN);
    public static final Item OXYGEN_VENT = registerGeneric(Constant.Item.OXYGEN_VENT);
    public static final Item SENSOR_LENS = registerGeneric(Constant.Item.SENSOR_LENS);
    public static final Item BLUE_SOLAR_WAFER = registerGeneric(Constant.Item.BLUE_SOLAR_WAFER);
    public static final Item SINGLE_SOLAR_MODULE = registerGeneric(Constant.Item.SINGLE_SOLAR_MODULE);
    public static final Item FULL_SOLAR_PANEL = registerGeneric(Constant.Item.FULL_SOLAR_PANEL);
    public static final Item SOLAR_ARRAY_WAFER = registerGeneric(Constant.Item.SOLAR_ARRAY_WAFER);
    public static final Item SOLAR_ARRAY_PANEL = registerGeneric(Constant.Item.SOLAR_ARRAY_PANEL);
    public static final Item STEEL_POLE = registerGeneric(Constant.Item.STEEL_POLE);
    public static final Item COPPER_CANISTER = registerGeneric(Constant.Item.COPPER_CANISTER);
    public static final Item TIN_CANISTER = registerGeneric(Constant.Item.TIN_CANISTER);
    public static final Item THERMAL_CLOTH = registerGeneric(Constant.Item.THERMAL_CLOTH);
    public static final Item ISOTHERMAL_FABRIC = registerGeneric(Constant.Item.ISOTHERMAL_FABRIC);
    public static final Item ORION_DRIVE = registerGeneric(Constant.Item.ORION_DRIVE);
    public static final Item ATMOSPHERIC_VALVE = registerGeneric(Constant.Item.ATMOSPHERIC_VALVE);
    public static final Item AMBIENT_THERMAL_CONTROLLER = registerGeneric(Constant.Item.AMBIENT_THERMAL_CONTROLLER);
    
    // FOOD
    public static final Item MOON_CHEESE_WHEEL = ITEMS.register(Constant.Item.MOON_CHEESE_WHEEL, new BlockItem(GCBlocks.MOON_CHEESE_WHEEL, new Item.Properties())); // Special case
    public static final Item MOON_CHEESE_CURD = ITEMS.register(Constant.Item.MOON_CHEESE_CURD, new Item(new Item.Properties().food(GCFoodComponent.MOON_CHEESE_CURD)));
    public static final Item MOON_CHEESE_SLICE = ITEMS.register(Constant.Item.MOON_CHEESE_SLICE, new Item(new Item.Properties().food(GCFoodComponent.MOON_CHEESE_SLICE)));
    public static final Item BURGER_BUN = ITEMS.register(Constant.Item.BURGER_BUN, new Item(new Item.Properties().food(GCFoodComponent.BURGER_BUN)));
    public static final Item GROUND_BEEF = ITEMS.register(Constant.Item.GROUND_BEEF, new Item(new Item.Properties().food(GCFoodComponent.GROUND_BEEF)));
    public static final Item BEEF_PATTY = ITEMS.register(Constant.Item.BEEF_PATTY, new Item(new Item.Properties().food(GCFoodComponent.BEEF_PATTY)));
    public static final Item CHEESEBURGER = ITEMS.register(Constant.Item.CHEESEBURGER, new Item(new Item.Properties().food(GCFoodComponent.CHEESEBURGER)));
    
    public static final Item CANNED_DEHYDRATED_APPLE = ITEMS.register(Constant.Item.CANNED_DEHYDRATED_APPLE, new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_APPLE)));
    public static final Item CANNED_DEHYDRATED_CARROT = ITEMS.register(Constant.Item.CANNED_DEHYDRATED_CARROT, new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_CARROT)));
    public static final Item CANNED_DEHYDRATED_MELON = ITEMS.register(Constant.Item.CANNED_DEHYDRATED_MELON, new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_MELON)));
    public static final Item CANNED_DEHYDRATED_POTATO = ITEMS.register(Constant.Item.CANNED_DEHYDRATED_POTATO, new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_POTATO)));
    public static final Item CANNED_BEEF = ITEMS.register(Constant.Item.CANNED_BEEF, new CannedFoodItem(new Item.Properties().food(GCFoodComponent.CANNED_BEEF)));
    
    // ROCKET PLATES
    public static final Item TIER_1_HEAVY_DUTY_PLATE = registerGeneric(Constant.Item.TIER_1_HEAVY_DUTY_PLATE);
    public static final Item TIER_2_HEAVY_DUTY_PLATE = registerGeneric(Constant.Item.TIER_2_HEAVY_DUTY_PLATE);
    public static final Item TIER_3_HEAVY_DUTY_PLATE = registerGeneric(Constant.Item.TIER_3_HEAVY_DUTY_PLATE);

    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = ITEMS.register(Constant.Item.THROWABLE_METEOR_CHUNK, new ThrowableMeteorChunkItem(new Item.Properties().stacksTo(16)));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = ITEMS.register(Constant.Item.HOT_THROWABLE_METEOR_CHUNK, new HotThrowableMeteorChunkItem(new Item.Properties().stacksTo(16)));

    // ARMOR
    public static final Item HEAVY_DUTY_HELMET = ITEMS.register(Constant.Item.HEAVY_DUTY_HELMET, new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
    public static final Item HEAVY_DUTY_CHESTPLATE = ITEMS.register(Constant.Item.HEAVY_DUTY_CHESTPLATE, new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
    public static final Item HEAVY_DUTY_LEGGINGS = ITEMS.register(Constant.Item.HEAVY_DUTY_LEGGINGS, new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
    public static final Item HEAVY_DUTY_BOOTS = ITEMS.register(Constant.Item.HEAVY_DUTY_BOOTS, new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

    public static final Item DESH_HELMET = ITEMS.register(Constant.Item.DESH_HELMET, new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
    public static final Item DESH_CHESTPLATE = ITEMS.register(Constant.Item.DESH_CHESTPLATE, new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
    public static final Item DESH_LEGGINGS = ITEMS.register(Constant.Item.DESH_LEGGINGS, new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
    public static final Item DESH_BOOTS = ITEMS.register(Constant.Item.DESH_BOOTS, new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

    public static final Item TITANIUM_HELMET = ITEMS.register(Constant.Item.TITANIUM_HELMET, new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));
    public static final Item TITANIUM_CHESTPLATE = ITEMS.register(Constant.Item.TITANIUM_CHESTPLATE, new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1)));
    public static final Item TITANIUM_LEGGINGS = ITEMS.register(Constant.Item.TITANIUM_LEGGINGS, new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1)));
    public static final Item TITANIUM_BOOTS = ITEMS.register(Constant.Item.TITANIUM_BOOTS, new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS, new Item.Properties().stacksTo(1)));

    public static final Item SENSOR_GLASSES = ITEMS.register(Constant.Item.SENSOR_GLASSES, new ArmorItem(GCArmorMaterials.SENSOR_GLASSES, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)));

    // TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = ITEMS.register(Constant.Item.HEAVY_DUTY_SWORD, new BrittleSwordItem(GCTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.STEEL, 3, -2.4F))));
    public static final Item HEAVY_DUTY_SHOVEL = ITEMS.register(Constant.Item.HEAVY_DUTY_SHOVEL, new ShovelItem(GCTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.STEEL, -1.5F, -3.0F))));
    public static final Item HEAVY_DUTY_PICKAXE = ITEMS.register(Constant.Item.HEAVY_DUTY_PICKAXE, new PickaxeItem(GCTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.STEEL, 1, -2.8F))));
    public static final Item HEAVY_DUTY_AXE = ITEMS.register(Constant.Item.HEAVY_DUTY_AXE, new AxeItem(GCTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.STEEL, 6.0F, -3.1F))));
    public static final Item HEAVY_DUTY_HOE = ITEMS.register(Constant.Item.HEAVY_DUTY_HOE, new HoeItem(GCTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.STEEL, -2, -1.0F))));

    public static final Item DESH_SWORD = ITEMS.register(Constant.Item.DESH_SWORD, new SwordItem(GCTiers.DESH, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.DESH, 3, -2.4F))));
    public static final Item DESH_SHOVEL = ITEMS.register(Constant.Item.DESH_SHOVEL, new ShovelItem(GCTiers.DESH, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.DESH, -1.5F, -3.0F))));
    public static final Item DESH_PICKAXE = ITEMS.register(Constant.Item.DESH_PICKAXE, new PickaxeItem(GCTiers.DESH, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.DESH, 1.0F, -2.8F))));
    public static final Item DESH_AXE = ITEMS.register(Constant.Item.DESH_AXE, new AxeItem(GCTiers.DESH, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.DESH, 6.0F, -3.1F))));
    public static final Item DESH_HOE = ITEMS.register(Constant.Item.DESH_HOE, new HoeItem(GCTiers.DESH, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.DESH, -3.0F, -1.0F))));

    public static final Item TITANIUM_SWORD = ITEMS.register(Constant.Item.TITANIUM_SWORD, new BrittleSwordItem(GCTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.TITANIUM, 3, -2.4F))));
    public static final Item TITANIUM_SHOVEL = ITEMS.register(Constant.Item.TITANIUM_SHOVEL, new ShovelItem(GCTiers.TITANIUM, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.TITANIUM, -1.5F, -3.0F))));
    public static final Item TITANIUM_PICKAXE = ITEMS.register(Constant.Item.TITANIUM_PICKAXE, new PickaxeItem(GCTiers.TITANIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.TITANIUM, 1.0F, -2.8F))));
    public static final Item TITANIUM_AXE = ITEMS.register(Constant.Item.TITANIUM_AXE, new AxeItem(GCTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.TITANIUM, 6.0F, -3.1F))));
    public static final Item TITANIUM_HOE = ITEMS.register(Constant.Item.TITANIUM_HOE, new HoeItem(GCTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.TITANIUM, -3.0F, -1.0F))));

    public static final Item STANDARD_WRENCH = ITEMS.register(Constant.Item.STANDARD_WRENCH, new StandardWrenchItem(new Item.Properties().durability(256)));

    // SMITHING TEMPLATES
    public static final Item TITANTIUM_UPGRADE_SMITHING_TEMPLATE = ITEMS.register(Constant.Item.TITANTIUM_UPGRADE_SMITHING_TEMPLATE, new SmithingTemplateItem(
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_APPLIES_TO).withStyle(Constant.Text.BLUE_STYLE),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_INGREDIENTS).withStyle(Constant.Text.BLUE_STYLE),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_DESCRIPTION).withStyle(Constant.Text.GRAY_STYLE),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON),
            List.of(ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_hoe"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe")),
            List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"))
    ));
    // 		this.appliesTo = component;
    //		this.ingredients = component2;
    //		this.upgradeDescription = component3;
    //		this.baseSlotDescription = component4;
    //		this.additionsSlotDescription = component5;

    // BATTERIES
    public static final Item BATTERY = ITEMS.register(Constant.Item.BATTERY, new BatteryItem(new Item.Properties().stacksTo(1), 15000, 500));
    public static final Item INFINITE_BATTERY = ITEMS.register(Constant.Item.INFINITE_BATTERY, new InfiniteBatteryItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // FLUID BUCKETS
    public static final Item CRUDE_OIL_BUCKET = ITEMS.register(Constant.Item.CRUDE_OIL_BUCKET, new BucketItem(GCFluids.CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item FUEL_BUCKET = ITEMS.register(Constant.Item.FUEL_BUCKET, new BucketItem(GCFluids.FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Item SULFURIC_ACID_BUCKET = ITEMS.register(Constant.Item.SULFURIC_ACID_BUCKET, new BucketItem(GCFluids.SULFURIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // GALACTICRAFT INVENTORY
    public static final GCRegistry.ColorSet<ParachuteItem> PARACHUTE = ITEMS.registerColored(Constant.Item.PARACHUTE, color -> new ParachuteItem(color, new Item.Properties().stacksTo(1)));

    public static final Item OXYGEN_MASK = ITEMS.register(Constant.Item.OXYGEN_MASK, new OxygenMaskItem(new Item.Properties()));
    public static final Item OXYGEN_GEAR = ITEMS.register(Constant.Item.OXYGEN_GEAR, new OxygenGearItem(new Item.Properties()));

    public static final Item SMALL_OXYGEN_TANK = ITEMS.register(Constant.Item.SMALL_OXYGEN_TANK, new OxygenTankItem(new Item.Properties(), 1620 * 10)); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = ITEMS.register(Constant.Item.MEDIUM_OXYGEN_TANK, new OxygenTankItem(new Item.Properties(), 1620 * 20)); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = ITEMS.register(Constant.Item.LARGE_OXYGEN_TANK, new OxygenTankItem(new Item.Properties(), 1620 * 30)); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = ITEMS.register(Constant.Item.INFINITE_OXYGEN_TANK, new InfiniteOxygenTankItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final Item SHIELD_CONTROLLER = ITEMS.register(Constant.Item.SHIELD_CONTROLLER, new AccessoryItem(new Item.Properties()));
    public static final Item FREQUENCY_MODULE = ITEMS.register(Constant.Item.FREQUENCY_MODULE, new FrequencyModuleItem(new Item.Properties()));

    public static final Item EMERGENCY_KIT = registerGeneric(Constant.Item.EMERGENCY_KIT);

    public static final Item THERMAL_PADDING_HELMET = ITEMS.register(Constant.Item.THERMAL_PADDING_HELMET, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.HELMET));
    public static final Item THERMAL_PADDING_CHESTPIECE = ITEMS.register(Constant.Item.THERMAL_PADDING_CHESTPIECE, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.CHESTPLATE));
    public static final Item THERMAL_PADDING_LEGGINGS = ITEMS.register(Constant.Item.THERMAL_PADDING_LEGGINGS, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.LEGGINGS));
    public static final Item THERMAL_PADDING_BOOTS = ITEMS.register(Constant.Item.THERMAL_PADDING_BOOTS, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.BOOTS));

    public static final Item ISOTHERMAL_PADDING_HELMET = ITEMS.register(Constant.Item.ISOTHERMAL_PADDING_HELMET, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.HELMET));
    public static final Item ISOTHERMAL_PADDING_CHESTPIECE = ITEMS.register(Constant.Item.ISOTHERMAL_PADDING_CHESTPIECE, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.CHESTPLATE));
    public static final Item ISOTHERMAL_PADDING_LEGGINGS = ITEMS.register(Constant.Item.ISOTHERMAL_PADDING_LEGGINGS, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.LEGGINGS));
    public static final Item ISOTHERMAL_PADDING_BOOTS = ITEMS.register(Constant.Item.ISOTHERMAL_PADDING_BOOTS, new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.BOOTS));

    // VEHICLES
    public static final Item BUGGY = ITEMS.register(Constant.Item.BUGGY, new BuggyItem(new Item.Properties().stacksTo(1)));
    public static final Item ROCKET = ITEMS.register(Constant.Item.ROCKET, new RocketItem(new Item.Properties()
            .component(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1)
            .stacksTo(1)));

    // ROCKET PARTS
    public static final Item NOSE_CONE = registerGeneric(Constant.Item.NOSE_CONE);
    public static final Item HEAVY_NOSE_CONE = registerGeneric(Constant.Item.HEAVY_NOSE_CONE);
    public static final Item ROCKET_FIN = registerGeneric(Constant.Item.ROCKET_FIN);
    public static final Item HEAVY_ROCKET_FIN = registerGeneric(Constant.Item.HEAVY_ROCKET_FIN);
    public static final Item ROCKET_ENGINE = registerGeneric(Constant.Item.ROCKET_ENGINE);
    public static final Item HEAVY_ROCKET_ENGINE = registerGeneric(Constant.Item.HEAVY_ROCKET_ENGINE);
    public static final Item ROCKET_BOOSTER = registerGeneric(Constant.Item.ROCKET_BOOSTER);

    // BUGGY PARTS
    public static final Item BUGGY_WHEEL = registerGeneric(Constant.Item.BUGGY_WHEEL);
    public static final Item BUGGY_SEAT = registerGeneric(Constant.Item.BUGGY_SEAT);
    public static final Item BUGGY_STORAGE = registerGeneric(Constant.Item.BUGGY_STORAGE);

    // SCHEMATICS
    public static final Item BASIC_ROCKET_CONE_SCHEMATIC = ITEMS.register(Constant.Item.BASIC_ROCKET_CONE_SCHEMATIC, new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_CONE));
    public static final Item BASIC_ROCKET_BODY_SCHEMATIC = ITEMS.register(Constant.Item.BASIC_ROCKET_BODY_SCHEMATIC, new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_BODY));
    public static final Item BASIC_ROCKET_FINS_SCHEMATIC = ITEMS.register(Constant.Item.BASIC_ROCKET_FINS_SCHEMATIC, new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_FIN));
    public static final Item BASIC_ROCKET_ENGINE_SCHEMATIC = ITEMS.register(Constant.Item.BASIC_ROCKET_ENGINE_SCHEMATIC, new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_ENGINE));

    public static final Item TIER_2_ROCKET_SCHEMATIC = ITEMS.register(Constant.Item.TIER_2_ROCKET_SCHEMATIC, new SchematicItem(new Item.Properties()));
    public static final Item CARGO_ROCKET_SCHEMATIC = ITEMS.register(Constant.Item.CARGO_ROCKET_SCHEMATIC, new SchematicItem(new Item.Properties()));
    public static final Item MOON_BUGGY_SCHEMATIC = ITEMS.register(Constant.Item.MOON_BUGGY_SCHEMATIC, new SchematicItem(new Item.Properties()));
    public static final Item TIER_3_ROCKET_SCHEMATIC = ITEMS.register(Constant.Item.TIER_3_ROCKET_SCHEMATIC, new SchematicItem(new Item.Properties()));
    public static final Item ASTRO_MINER_SCHEMATIC = ITEMS.register(Constant.Item.ASTRO_MINER_SCHEMATIC, new SchematicItem(new Item.Properties()));

    // LEGACY MUSIC DISCS
    public static final Item LEGACY_MUSIC_DISC_MARS = ITEMS.register(Constant.Item.LEGACY_MUSIC_DISC_MARS, new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(GCJukeboxSongs.MARS)));
    public static final Item LEGACY_MUSIC_DISC_MIMAS = ITEMS.register(Constant.Item.LEGACY_MUSIC_DISC_MIMAS, new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(GCJukeboxSongs.MIMAS)));
    public static final Item LEGACY_MUSIC_DISC_ORBIT = ITEMS.register(Constant.Item.LEGACY_MUSIC_DISC_ORBIT, new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(GCJukeboxSongs.ORBIT)));
    public static final Item LEGACY_MUSIC_DISC_SPACERACE = ITEMS.register(Constant.Item.LEGACY_MUSIC_DISC_SPACERACE, new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(GCJukeboxSongs.SPACERACE)));

    // SPAWN EGGS
    public static final Item MOON_VILLAGER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.MOON_VILLAGER, new SpawnEggItem(GCEntityTypes.MOON_VILLAGER, 0x74a3cf, 0xba2500, new Item.Properties()));
    public static final Item EVOLVED_ZOMBIE_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_ZOMBIE, new SpawnEggItem(GCEntityTypes.EVOLVED_ZOMBIE, 0x00afaf, 0x463aa5, new Item.Properties()));
    public static final Item EVOLVED_CREEPER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_CREEPER, new SpawnEggItem(GCEntityTypes.EVOLVED_CREEPER, 0x0da70b, 0xa8d0d9, new Item.Properties()));
    public static final Item EVOLVED_SKELETON_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_SKELETON, new SpawnEggItem(GCEntityTypes.EVOLVED_SKELETON, 0xc1c1c1, 0xff9600, new Item.Properties()));
    public static final Item EVOLVED_SPIDER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_SPIDER, new SpawnEggItem(GCEntityTypes.EVOLVED_SPIDER, 0x342d27, 0x5aff0e, new Item.Properties()));
    public static final Item EVOLVED_ENDERMAN_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_ENDERMAN, new SpawnEggItem(GCEntityTypes.EVOLVED_ENDERMAN, 0x161616, 0xcc00fa, new Item.Properties()));
    public static final Item EVOLVED_WITCH_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_WITCH, new SpawnEggItem(GCEntityTypes.EVOLVED_WITCH, 0x30144d, 0x51a03e, new Item.Properties()));
    public static final Item EVOLVED_PILLAGER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_PILLAGER, new SpawnEggItem(GCEntityTypes.EVOLVED_PILLAGER, 0x532f36, 0x264747, new Item.Properties()));
    public static final Item EVOLVED_EVOKER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_EVOKER, new SpawnEggItem(GCEntityTypes.EVOLVED_EVOKER, 0x1e1c1a, 0xd3cf99, new Item.Properties()));
    public static final Item EVOLVED_VINDICATOR_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.EVOLVED_VINDICATOR, new SpawnEggItem(GCEntityTypes.EVOLVED_VINDICATOR, 0x3f3b37, 0x275e61, new Item.Properties()));
    public static final Item GAZER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.GAZER, new SpawnEggItem(GCEntityTypes.GAZER, 0xdbdddb, 0x5c5c5c, new Item.Properties()));
    public static final Item RUMBLER_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.RUMBLER, new SpawnEggItem(GCEntityTypes.RUMBLER, 0x5c5c5c, 0x36383e, new Item.Properties()));
    public static final Item COMET_CUBE_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.COMET_CUBE, new SpawnEggItem(GCEntityTypes.COMET_CUBE, 0xd5d8d8, 0x92b9fe, new Item.Properties()));
    public static final Item OLI_GRUB_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.OLI_GRUB, new SpawnEggItem(GCEntityTypes.OLI_GRUB, 0xd4dd7e, 0xa4bf63, new Item.Properties()));
    public static final Item GREY_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.GREY, new SpawnEggItem(GCEntityTypes.GREY, 0x656463, 0x769e41, new Item.Properties()));
    public static final Item ARCH_GREY_SPAWN_EGG = ITEMS.register(Constant.SpawnEgg.ARCH_GREY, new SpawnEggItem(GCEntityTypes.ARCH_GREY, 0x656463, 0x2d8563, new Item.Properties()));

    private static Item registerGeneric(String id) {
        return ITEMS.register(id, new Item(new Item.Properties()));
    }
    
    public static void register() {
        DispenserBlock.registerBehavior(FUEL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        DispenserBlock.registerBehavior(CRUDE_OIL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        DispenserBlock.registerBehavior(SULFURIC_ACID_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
    }
}
