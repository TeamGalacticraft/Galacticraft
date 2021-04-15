/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.items;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.block.GalacticraftBlocks;
import dev.galacticraft.mod.entity.GalacticraftEntityTypes;
import dev.galacticraft.mod.fluids.GalacticraftFluids;
import dev.galacticraft.mod.sounds.GalacticraftSounds;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GalacticraftItems {
    public static final List<Item> HIDDEN_ITEMS = new LinkedList<>();

    public static final Item GLOWSTONE_TORCH = registerItem(Constants.Blocks.GLOWSTONE_TORCH, new WallStandingBlockItem(GalacticraftBlocks.GLOWSTONE_TORCH, GalacticraftBlocks.GLOWSTONE_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));
    public static final Item UNLIT_TORCH = registerItem(Constants.Blocks.UNLIT_TORCH, new WallStandingBlockItem(GalacticraftBlocks.UNLIT_TORCH, GalacticraftBlocks.UNLIT_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));

    public static final ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItems.CANVAS))
            .build();

    // MATERIALS
    public static final Item LEAD_INGOT = registerItem(Constants.Items.LEAD_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_SILICON = registerItem(Constants.Items.RAW_SILICON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_METEORIC_IRON = registerItem(Constants.Items.RAW_METEORIC_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item METEORIC_IRON_INGOT = registerItem(Constants.Items.METEORIC_IRON_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LUNAR_SAPPHIRE = registerItem(Constants.Items.LUNAR_SAPPHIRE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item UNREFINED_DESH = registerItem(Constants.Items.UNREFINED_DESH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_INGOT = registerItem(Constants.Items.DESH_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_STICK = registerItem(Constants.Items.DESH_STICK, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARBON_FRAGMENTS = registerItem(Constants.Items.CARBON_FRAGMENTS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item IRON_SHARD = registerItem(Constants.Items.IRON_SHARD, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SHARD = registerItem(Constants.Items.TITANIUM_SHARD, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_INGOT = registerItem(Constants.Items.TITANIUM_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_DUST = registerItem(Constants.Items.TITANIUM_DUST, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_DUST = registerItem(Constants.Items.SOLAR_DUST, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BASIC_WAFER = registerItem(Constants.Items.BASIC_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ADVANCED_WAFER = registerItem(Constants.Items.ADVANCED_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BEAM_CORE = registerItem(Constants.Items.BEAM_CORE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CANVAS = registerItem(Constants.Items.CANVAS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_ALUMINUM = registerItem(Constants.Items.COMPRESSED_ALUMINUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TIN = registerItem(Constants.Items.COMPRESSED_TIN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_BRONZE = registerItem(Constants.Items.COMPRESSED_BRONZE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_COPPER = registerItem(Constants.Items.COMPRESSED_COPPER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_IRON = registerItem(Constants.Items.COMPRESSED_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_STEEL = registerItem(Constants.Items.COMPRESSED_STEEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_METEORIC_IRON = registerItem(Constants.Items.COMPRESSED_METEORIC_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_DESH = registerItem(Constants.Items.COMPRESSED_DESH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TITANIUM = registerItem(Constants.Items.COMPRESSED_TITANIUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FLUID_MANIPULATOR = registerItem(Constants.Items.FLUID_MANIPULATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_CONCENTRATOR = registerItem(Constants.Items.OXYGEN_CONCENTRATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_FAN = registerItem(Constants.Items.OXYGEN_FAN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_VENT = registerItem(Constants.Items.OXYGEN_VENT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SENSOR_LENS = registerItem(Constants.Items.SENSOR_LENS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_SOLAR_WAFER = registerItem(Constants.Items.BLUE_SOLAR_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SINGLE_SOLAR_MODULE = registerItem(Constants.Items.SINGLE_SOLAR_MODULE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FULL_SOLAR_PANEL = registerItem(Constants.Items.FULL_SOLAR_PANEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_ARRAY_WAFER = registerItem(Constants.Items.SOLAR_ARRAY_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STEEL_POLE = registerItem(Constants.Items.STEEL_POLE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COPPER_CANISTER = registerItem(Constants.Items.COPPER_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIN_CANISTER = registerItem(Constants.Items.TIN_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item THERMAL_CLOTH = registerItem(Constants.Items.THERMAL_CLOTH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ISOTHERMAL_FABRIC = registerItem(Constants.Items.ISOTHERMAL_FABRIC, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORION_DRIVE = registerItem(Constants.Items.ORION_DRIVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ATMOSPHERIC_VALVE = registerItem(Constants.Items.ATMOSPHERIC_VALVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //FOOD
    public static final Item MOON_BERRIES = registerItem(Constants.Items.MOON_BERRIES, new Item(new Item.Settings().food(GalacticraftFoodComponents.MOON_BERRIES).group(ITEMS_GROUP)));
    public static final Item CHEESE_CURD = registerItem(Constants.Items.CHEESE_CURD, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESE_CURD).group(ITEMS_GROUP)));
    public static final Item CHEESE_SLICE = registerItem(Constants.Items.CHEESE_SLICE, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESE_SLICE).group(ITEMS_GROUP)));
    public static final Item BURGER_BUN = registerItem(Constants.Items.BURGER_BUN, new Item(new Item.Settings().food(GalacticraftFoodComponents.BURGER_BUN).group(ITEMS_GROUP)));
    public static final Item GROUND_BEEF = registerItem(Constants.Items.GROUND_BEEF, new Item(new Item.Settings().food(GalacticraftFoodComponents.GROUND_BEEF).group(ITEMS_GROUP)));
    public static final Item BEEF_PATTY = registerItem(Constants.Items.BEEF_PATTY, new Item(new Item.Settings().food(GalacticraftFoodComponents.BEEF_PATTY).group(ITEMS_GROUP)));
    public static final Item CHEESEBURGER = registerItem(Constants.Items.CHEESEBURGER, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESEBURGER).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_APPLE = registerItem(Constants.Items.CANNED_DEHYDRATED_APPLE, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_APPLE).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_CARROT = registerItem(Constants.Items.CANNED_DEHYDRATED_CARROT, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_CARROT).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_MELON = registerItem(Constants.Items.CANNED_DEHYDRATED_MELON, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_MELON).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_POTATO = registerItem(Constants.Items.CANNED_DEHYDRATED_POTATO, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_POTATO).group(ITEMS_GROUP)));
    public static final Item CANNED_BEEF = registerItem(Constants.Items.CANNED_BEEF, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.CANNED_BEEF).group(ITEMS_GROUP)));
    //ROCKET PARTS
    public static final Item TIER_1_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_1_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_2_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_3_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //ARMOR
    public static final Item HEAVY_DUTY_HELMET = registerItem(Constants.Items.HEAVY_DUTY_HELMET, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_CHESTPLATE = registerItem(Constants.Items.HEAVY_DUTY_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_LEGGINGS = registerItem(Constants.Items.HEAVY_DUTY_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_BOOTS = registerItem(Constants.Items.HEAVY_DUTY_BOOTS, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_HELMET = registerItem(Constants.Items.DESH_HELMET, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_CHESTPLATE = registerItem(Constants.Items.DESH_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_LEGGINGS = registerItem(Constants.Items.DESH_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_BOOTS = registerItem(Constants.Items.DESH_BOOTS, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_HELMET = registerItem(Constants.Items.TITANIUM_HELMET, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_CHESTPLATE = registerItem(Constants.Items.TITANIUM_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_LEGGINGS = registerItem(Constants.Items.TITANIUM_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_BOOTS = registerItem(Constants.Items.TITANIUM_BOOTS, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item SENSOR_GLASSES = registerItem(Constants.Items.SENSOR_GLASSES, new ArmorItem(GalacticraftArmorMaterials.SENSOR_GLASSES, EquipmentSlot.HEAD, new Item.Settings().group(ITEMS_GROUP)));
    //TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = registerItem(Constants.Items.HEAVY_DUTY_SWORD, new SwordItem(GalacticraftToolMaterials.STEEL, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(world, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item HEAVY_DUTY_SHOVEL = registerItem(Constants.Items.HEAVY_DUTY_SHOVEL, new ShovelItem(GalacticraftToolMaterials.STEEL, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_PICKAXE = registerItem(Constants.Items.HEAVY_DUTY_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.STEEL, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_AXE = registerItem(Constants.Items.HEAVY_DUTY_AXE, new AxeItem(GalacticraftToolMaterials.STEEL, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_HOE = registerItem(Constants.Items.HEAVY_DUTY_HOE, new HoeItem(GalacticraftToolMaterials.STEEL, -2, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SWORD = registerItem(Constants.Items.DESH_SWORD, new SwordItem(GalacticraftToolMaterials.DESH, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SHOVEL = registerItem(Constants.Items.DESH_SHOVEL, new ShovelItem(GalacticraftToolMaterials.DESH, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_PICKAXE = registerItem(Constants.Items.DESH_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.DESH, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_AXE = registerItem(Constants.Items.DESH_AXE, new AxeItem(GalacticraftToolMaterials.DESH, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_HOE = registerItem(Constants.Items.DESH_HOE, new HoeItem(GalacticraftToolMaterials.DESH, -3, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SWORD = registerItem(Constants.Items.TITANIUM_SWORD, new SwordItem(GalacticraftToolMaterials.TITANIUM, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(world, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item TITANIUM_SHOVEL = registerItem(Constants.Items.TITANIUM_SHOVEL, new ShovelItem(GalacticraftToolMaterials.TITANIUM, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_PICKAXE = registerItem(Constants.Items.TITANIUM_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.TITANIUM, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_AXE = registerItem(Constants.Items.TITANIUM_AXE, new AxeItem(GalacticraftToolMaterials.TITANIUM, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_HOE = registerItem(Constants.Items.TITANIUM_HOE, new HoeItem(GalacticraftToolMaterials.TITANIUM, -3, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STANDARD_WRENCH = registerItem(Constants.Items.STANDARD_WRENCH, new StandardWrenchItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BATTERY = registerItem(Constants.Items.BATTERY, new BatteryItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item INFINITE_BATTERY = registerItem(Constants.Items.INFINITE_BATTERY, new InfiniteBatteryItem(new Item.Settings().group(ITEMS_GROUP).rarity(Rarity.EPIC)));
    //Fluid buckets
    public static final BucketItem CRUDE_OIL_BUCKET = registerItem(Constants.Items.CRUDE_OIL_BUCKET, new BucketItem(GalacticraftFluids.CRUDE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ITEMS_GROUP)));
    public static final BucketItem FUEL_BUCKET = registerItem(Constants.Items.FUEL_BUCKET, new BucketItem(GalacticraftFluids.FUEL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ITEMS_GROUP)));
    //GC INVENTORY
    private static final Item.Settings PARACHUTE_SETTINGS = new Item.Settings().group(ITEMS_GROUP).maxCount(1);
    public static final Item PARACHUTE = registerItem(Constants.Items.PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item ORANGE_PARACHUTE = registerItem(Constants.Items.ORANGE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item MAGENTA_PARACHUTE = registerItem(Constants.Items.MAGENTA_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIGHT_BLUE_PARACHUTE = registerItem(Constants.Items.LIGHT_BLUE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item YELLOW_PARACHUTE = registerItem(Constants.Items.YELLOW_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIME_PARACHUTE = registerItem(Constants.Items.LIME_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item PINK_PARACHUTE = registerItem(Constants.Items.PINK_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item GRAY_PARACHUTE = registerItem(Constants.Items.GRAY_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIGHT_GRAY_PARACHUTE = registerItem(Constants.Items.LIGHT_GRAY_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item CYAN_PARACHUTE = registerItem(Constants.Items.CYAN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item PURPLE_PARACHUTE = registerItem(Constants.Items.PURPLE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BLUE_PARACHUTE = registerItem(Constants.Items.BLUE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BROWN_PARACHUTE = registerItem(Constants.Items.BROWN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item GREEN_PARACHUTE = registerItem(Constants.Items.GREEN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item RED_PARACHUTE = registerItem(Constants.Items.RED_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BLACK_PARACHUTE = registerItem(Constants.Items.BLACK_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item OXYGEN_MASK = registerItem(Constants.Items.OXYGEN_MASK, new OxygenMaskItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_GEAR = registerItem(Constants.Items.OXYGEN_GEAR, new OxygenGearItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SHIELD_CONTROLLER = registerItem(Constants.Items.SHIELD_CONTROLLER, new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FREQUENCY_MODULE = registerItem(Constants.Items.FREQUENCY_MODULE, new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SMALL_OXYGEN_TANK = registerItem(Constants.Items.SMALL_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(1620 * 10))); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = registerItem(Constants.Items.MEDIUM_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(1620 * 20))); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = registerItem(Constants.Items.LARGE_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(1620 * 30))); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = registerItem(Constants.Items.INFINITE_OXYGEN_TANK, new InfiniteOxygenTankItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item THERMAL_PADDING_HELMET = registerItem(Constants.Items.THERMAL_PADDING_HELMET, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.HEAD));
    public static final Item THERMAL_PADDING_CHESTPIECE = registerItem(Constants.Items.THERMAL_PADDING_CHESTPIECE, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.CHEST));
    public static final Item THERMAL_PADDING_LEGGINGS = registerItem(Constants.Items.THERMAL_PADDING_LEGGINGS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.LEGS));
    public static final Item THERMAL_PADDING_BOOTS = registerItem(Constants.Items.THERMAL_PADDING_BOOTS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.FEET));
    public static final Item TIER_2_ROCKET_SCHEMATIC = registerItem(Constants.Items.TIER_2_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARGO_ROCKET_SCHEMATIC = registerItem(Constants.Items.CARGO_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MOON_BUGGY_SCHEMATIC = registerItem(Constants.Items.MOON_BUGGY_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_ROCKET_SCHEMATIC = registerItem(Constants.Items.TIER_3_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ASTRO_MINER_SCHEMATIC = registerItem(Constants.Items.ASTRO_MINER_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    // SPAWN EGGS
    public static final Item MOON_VILLAGER_SPAWN_EGG = registerItem(Constants.Items.MOON_VILLAGER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.MOON_VILLAGER, 0xC0C9C0, 0x5698D8, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_ZOMBIE_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_ZOMBIE_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_ZOMBIE, 0xC0CCC0, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_EVOKER_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_EVOKER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_EVOKER, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_VINDICATOR_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_VINDICATOR_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_VINDICATOR, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_CREEPER_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_CREEPER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_CREEPER, 0x6AFF8A, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_SPIDER_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_SPIDER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_SPIDER, 0xEE9999, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_PILLAGER_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_PILLAGER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_PILLAGER, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_SKELETON_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_SKELETON_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_SKELETON, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = registerItem(Constants.Items.THROWABLE_METEOR_CHUNK, new ThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = registerItem(Constants.Items.HOT_THROWABLE_METEOR_CHUNK, new HotThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));

    public static final Item LEGACY_MUSIC_DISC_MARS = registerItem(Constants.Items.LEGACY_MUSIC_DISC_MARS, new MusicDiscItem(15, GalacticraftSounds.MUSIC_LEGACY_MARS, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_MIMAS = registerItem(Constants.Items.LEGACY_MUSIC_DISC_MIMAS, new MusicDiscItem(15, GalacticraftSounds.MUSIC_LEGACY_MIMAS, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_ORBIT = registerItem(Constants.Items.LEGACY_MUSIC_DISC_ORBIT, new MusicDiscItem(15, GalacticraftSounds.MUSIC_LEGACY_ORBIT, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_SPACERACE = registerItem(Constants.Items.LEGACY_MUSIC_DISC_SPACERACE, new MusicDiscItem(15, GalacticraftSounds.MUSIC_LEGACY_SPACERACE, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));

    private static <T extends Item> T registerItem(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), item);
    }
    
    public static void register() {
    }
}
