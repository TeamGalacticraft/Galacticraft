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

package dev.galacticraft.mod.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.sound.GalacticraftSound;
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
public class GalacticraftItem {
    public static final List<Item> HIDDEN_ITEMS = new LinkedList<>();

    public static final Item GLOWSTONE_TORCH = registerItem(Constant.Block.GLOWSTONE_TORCH, new WallStandingBlockItem(GalacticraftBlock.GLOWSTONE_TORCH, GalacticraftBlock.GLOWSTONE_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));
    public static final Item UNLIT_TORCH = registerItem(Constant.Block.UNLIT_TORCH, new WallStandingBlockItem(GalacticraftBlock.UNLIT_TORCH, GalacticraftBlock.UNLIT_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));

    public static final ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Item.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItem.CANVAS))
            .build();

    // MATERIALS
    public static final Item RAW_SILICON = registerItem(Constant.Item.RAW_SILICON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item[] METEORIC_IRON = registerOreItems(Constant.Item.METEORIC_IRON);
    public static final Item[] DESH = registerOreItems(Constant.Item.DESH);
    public static final Item[] LEAD = registerOreItems(Constant.Item.LEAD);
    public static final Item[] ALUMINUM = registerOreItems(Constant.Item.ALUMINUM);
    public static final Item[] TIN = registerOreItems(Constant.Item.TIN);
    public static final Item[] TITANIUM = registerOreItems(Constant.Item.TITANIUM);
    public static final Item LUNAR_SAPPHIRE = registerItem(Constant.Item.LUNAR_SAPPHIRE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_STICK = registerItem(Constant.Item.DESH_STICK, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARBON_FRAGMENTS = registerItem(Constant.Item.CARBON_FRAGMENTS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item IRON_SHARD = registerItem(Constant.Item.IRON_SHARD, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_DUST = registerItem(Constant.Item.SOLAR_DUST, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BASIC_WAFER = registerItem(Constant.Item.BASIC_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ADVANCED_WAFER = registerItem(Constant.Item.ADVANCED_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BEAM_CORE = registerItem(Constant.Item.BEAM_CORE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CANVAS = registerItem(Constant.Item.CANVAS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_ALUMINUM = registerItem(Constant.Item.COMPRESSED_ALUMINUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TIN = registerItem(Constant.Item.COMPRESSED_TIN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_BRONZE = registerItem(Constant.Item.COMPRESSED_BRONZE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_COPPER = registerItem(Constant.Item.COMPRESSED_COPPER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_IRON = registerItem(Constant.Item.COMPRESSED_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_STEEL = registerItem(Constant.Item.COMPRESSED_STEEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_METEORIC_IRON = registerItem(Constant.Item.COMPRESSED_METEORIC_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_DESH = registerItem(Constant.Item.COMPRESSED_DESH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TITANIUM = registerItem(Constant.Item.COMPRESSED_TITANIUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FLUID_MANIPULATOR = registerItem(Constant.Item.FLUID_MANIPULATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_CONCENTRATOR = registerItem(Constant.Item.OXYGEN_CONCENTRATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_FAN = registerItem(Constant.Item.OXYGEN_FAN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_VENT = registerItem(Constant.Item.OXYGEN_VENT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SENSOR_LENS = registerItem(Constant.Item.SENSOR_LENS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_SOLAR_WAFER = registerItem(Constant.Item.BLUE_SOLAR_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SINGLE_SOLAR_MODULE = registerItem(Constant.Item.SINGLE_SOLAR_MODULE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FULL_SOLAR_PANEL = registerItem(Constant.Item.FULL_SOLAR_PANEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_ARRAY_WAFER = registerItem(Constant.Item.SOLAR_ARRAY_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STEEL_POLE = registerItem(Constant.Item.STEEL_POLE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COPPER_CANISTER = registerItem(Constant.Item.COPPER_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIN_CANISTER = registerItem(Constant.Item.TIN_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item THERMAL_CLOTH = registerItem(Constant.Item.THERMAL_CLOTH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ISOTHERMAL_FABRIC = registerItem(Constant.Item.ISOTHERMAL_FABRIC, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORION_DRIVE = registerItem(Constant.Item.ORION_DRIVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ATMOSPHERIC_VALVE = registerItem(Constant.Item.ATMOSPHERIC_VALVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //FOOD
    public static final Item MOON_BERRIES = registerItem(Constant.Item.MOON_BERRIES, new Item(new Item.Settings().food(GalacticraftFoodComponent.MOON_BERRIES).group(ITEMS_GROUP)));
    public static final Item CHEESE_CURD = registerItem(Constant.Item.CHEESE_CURD, new Item(new Item.Settings().food(GalacticraftFoodComponent.CHEESE_CURD).group(ITEMS_GROUP)));
    public static final Item CHEESE_SLICE = registerItem(Constant.Item.CHEESE_SLICE, new Item(new Item.Settings().food(GalacticraftFoodComponent.CHEESE_SLICE).group(ITEMS_GROUP)));
    public static final Item BURGER_BUN = registerItem(Constant.Item.BURGER_BUN, new Item(new Item.Settings().food(GalacticraftFoodComponent.BURGER_BUN).group(ITEMS_GROUP)));
    public static final Item GROUND_BEEF = registerItem(Constant.Item.GROUND_BEEF, new Item(new Item.Settings().food(GalacticraftFoodComponent.GROUND_BEEF).group(ITEMS_GROUP)));
    public static final Item BEEF_PATTY = registerItem(Constant.Item.BEEF_PATTY, new Item(new Item.Settings().food(GalacticraftFoodComponent.BEEF_PATTY).group(ITEMS_GROUP)));
    public static final Item CHEESEBURGER = registerItem(Constant.Item.CHEESEBURGER, new Item(new Item.Settings().food(GalacticraftFoodComponent.CHEESEBURGER).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_APPLE = registerItem(Constant.Item.CANNED_DEHYDRATED_APPLE, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponent.DEHYDRATED_APPLE).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_CARROT = registerItem(Constant.Item.CANNED_DEHYDRATED_CARROT, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponent.DEHYDRATED_CARROT).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_MELON = registerItem(Constant.Item.CANNED_DEHYDRATED_MELON, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponent.DEHYDRATED_MELON).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_POTATO = registerItem(Constant.Item.CANNED_DEHYDRATED_POTATO, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponent.DEHYDRATED_POTATO).group(ITEMS_GROUP)));
    public static final Item CANNED_BEEF = registerItem(Constant.Item.CANNED_BEEF, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponent.CANNED_BEEF).group(ITEMS_GROUP)));
    //ROCKET PARTS
    public static final Item TIER_1_HEAVY_DUTY_PLATE = registerItem(Constant.Item.TIER_1_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = registerItem(Constant.Item.TIER_2_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = registerItem(Constant.Item.TIER_3_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //ARMOR
    public static final Item HEAVY_DUTY_HELMET = registerItem(Constant.Item.HEAVY_DUTY_HELMET, new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_CHESTPLATE = registerItem(Constant.Item.HEAVY_DUTY_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_LEGGINGS = registerItem(Constant.Item.HEAVY_DUTY_LEGGINGS, new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_BOOTS = registerItem(Constant.Item.HEAVY_DUTY_BOOTS, new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_HELMET = registerItem(Constant.Item.DESH_HELMET, new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_CHESTPLATE = registerItem(Constant.Item.DESH_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_LEGGINGS = registerItem(Constant.Item.DESH_LEGGINGS, new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_BOOTS = registerItem(Constant.Item.DESH_BOOTS, new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_HELMET = registerItem(Constant.Item.TITANIUM_HELMET, new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_CHESTPLATE = registerItem(Constant.Item.TITANIUM_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_LEGGINGS = registerItem(Constant.Item.TITANIUM_LEGGINGS, new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_BOOTS = registerItem(Constant.Item.TITANIUM_BOOTS, new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item SENSOR_GLASSES = registerItem(Constant.Item.SENSOR_GLASSES, new ArmorItem(GalacticraftArmorMaterial.SENSOR_GLASSES, EquipmentSlot.HEAD, new Item.Settings().group(ITEMS_GROUP)));
    //TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = registerItem(Constant.Item.HEAVY_DUTY_SWORD, new SwordItem(GalacticraftToolMaterial.STEEL, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(world, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item HEAVY_DUTY_SHOVEL = registerItem(Constant.Item.HEAVY_DUTY_SHOVEL, new ShovelItem(GalacticraftToolMaterial.STEEL, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_PICKAXE = registerItem(Constant.Item.HEAVY_DUTY_PICKAXE, new PickaxeItem(GalacticraftToolMaterial.STEEL, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_AXE = registerItem(Constant.Item.HEAVY_DUTY_AXE, new AxeItem(GalacticraftToolMaterial.STEEL, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_HOE = registerItem(Constant.Item.HEAVY_DUTY_HOE, new HoeItem(GalacticraftToolMaterial.STEEL, -2, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SWORD = registerItem(Constant.Item.DESH_SWORD, new SwordItem(GalacticraftToolMaterial.DESH, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SHOVEL = registerItem(Constant.Item.DESH_SHOVEL, new ShovelItem(GalacticraftToolMaterial.DESH, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_PICKAXE = registerItem(Constant.Item.DESH_PICKAXE, new PickaxeItem(GalacticraftToolMaterial.DESH, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_AXE = registerItem(Constant.Item.DESH_AXE, new AxeItem(GalacticraftToolMaterial.DESH, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_HOE = registerItem(Constant.Item.DESH_HOE, new HoeItem(GalacticraftToolMaterial.DESH, -3, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SWORD = registerItem(Constant.Item.TITANIUM_SWORD, new SwordItem(GalacticraftToolMaterial.TITANIUM, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(world, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item TITANIUM_SHOVEL = registerItem(Constant.Item.TITANIUM_SHOVEL, new ShovelItem(GalacticraftToolMaterial.TITANIUM, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_PICKAXE = registerItem(Constant.Item.TITANIUM_PICKAXE, new PickaxeItem(GalacticraftToolMaterial.TITANIUM, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_AXE = registerItem(Constant.Item.TITANIUM_AXE, new AxeItem(GalacticraftToolMaterial.TITANIUM, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_HOE = registerItem(Constant.Item.TITANIUM_HOE, new HoeItem(GalacticraftToolMaterial.TITANIUM, -3, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STANDARD_WRENCH = registerItem(Constant.Item.STANDARD_WRENCH, new StandardWrenchItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BATTERY = registerItem(Constant.Item.BATTERY, new BatteryItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item INFINITE_BATTERY = registerItem(Constant.Item.INFINITE_BATTERY, new InfiniteBatteryItem(new Item.Settings().group(ITEMS_GROUP).rarity(Rarity.EPIC)));
    //Fluid buckets
    public static final BucketItem CRUDE_OIL_BUCKET = registerItem(Constant.Item.CRUDE_OIL_BUCKET, new BucketItem(GalacticraftFluid.CRUDE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ITEMS_GROUP)));
    public static final BucketItem FUEL_BUCKET = registerItem(Constant.Item.FUEL_BUCKET, new BucketItem(GalacticraftFluid.FUEL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ITEMS_GROUP)));
    //GC INVENTORY
    private static final Item.Settings PARACHUTE_SETTINGS = new Item.Settings().group(ITEMS_GROUP).maxCount(1);
    public static final Item PARACHUTE = registerItem(Constant.Item.PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item ORANGE_PARACHUTE = registerItem(Constant.Item.ORANGE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item MAGENTA_PARACHUTE = registerItem(Constant.Item.MAGENTA_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIGHT_BLUE_PARACHUTE = registerItem(Constant.Item.LIGHT_BLUE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item YELLOW_PARACHUTE = registerItem(Constant.Item.YELLOW_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIME_PARACHUTE = registerItem(Constant.Item.LIME_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item PINK_PARACHUTE = registerItem(Constant.Item.PINK_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item GRAY_PARACHUTE = registerItem(Constant.Item.GRAY_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item LIGHT_GRAY_PARACHUTE = registerItem(Constant.Item.LIGHT_GRAY_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item CYAN_PARACHUTE = registerItem(Constant.Item.CYAN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item PURPLE_PARACHUTE = registerItem(Constant.Item.PURPLE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BLUE_PARACHUTE = registerItem(Constant.Item.BLUE_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BROWN_PARACHUTE = registerItem(Constant.Item.BROWN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item GREEN_PARACHUTE = registerItem(Constant.Item.GREEN_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item RED_PARACHUTE = registerItem(Constant.Item.RED_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item BLACK_PARACHUTE = registerItem(Constant.Item.BLACK_PARACHUTE, new Item(PARACHUTE_SETTINGS));
    public static final Item OXYGEN_MASK = registerItem(Constant.Item.OXYGEN_MASK, new OxygenMaskItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_GEAR = registerItem(Constant.Item.OXYGEN_GEAR, new OxygenGearItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SHIELD_CONTROLLER = registerItem(Constant.Item.SHIELD_CONTROLLER, new AccessoryItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FREQUENCY_MODULE = registerItem(Constant.Item.FREQUENCY_MODULE, new AccessoryItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SMALL_OXYGEN_TANK = registerItem(Constant.Item.SMALL_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP), 1620 * 10)); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = registerItem(Constant.Item.MEDIUM_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP), 1620 * 20)); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = registerItem(Constant.Item.LARGE_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP), 1620 * 30)); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = registerItem(Constant.Item.INFINITE_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP), 0));
    public static final Item THERMAL_PADDING_HELMET = registerItem(Constant.Item.THERMAL_PADDING_HELMET, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.HEAD));
    public static final Item THERMAL_PADDING_CHESTPIECE = registerItem(Constant.Item.THERMAL_PADDING_CHESTPIECE, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.CHEST));
    public static final Item THERMAL_PADDING_LEGGINGS = registerItem(Constant.Item.THERMAL_PADDING_LEGGINGS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.LEGS));
    public static final Item THERMAL_PADDING_BOOTS = registerItem(Constant.Item.THERMAL_PADDING_BOOTS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.FEET));
    public static final Item TIER_2_ROCKET_SCHEMATIC = registerItem(Constant.Item.TIER_2_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARGO_ROCKET_SCHEMATIC = registerItem(Constant.Item.CARGO_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MOON_BUGGY_SCHEMATIC = registerItem(Constant.Item.MOON_BUGGY_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_ROCKET_SCHEMATIC = registerItem(Constant.Item.TIER_3_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ASTRO_MINER_SCHEMATIC = registerItem(Constant.Item.ASTRO_MINER_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    // SPAWN EGGS
    public static final Item MOON_VILLAGER_SPAWN_EGG = registerItem(Constant.Item.MOON_VILLAGER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.MOON_VILLAGER, 0xC0C9C0, 0x5698D8, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_ZOMBIE_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_ZOMBIE_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_ZOMBIE, 0xC0CCC0, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_EVOKER_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_EVOKER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_EVOKER, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_VINDICATOR_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_VINDICATOR_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_VINDICATOR, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_CREEPER_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_CREEPER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_CREEPER, 0x6AFF8A, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_SPIDER_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_SPIDER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_SPIDER, 0xEE9999, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_PILLAGER_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_PILLAGER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_PILLAGER, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item EVOLVED_SKELETON_SPAWN_EGG = registerItem(Constant.Item.EVOLVED_SKELETON_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityType.EVOLVED_SKELETON, 0x888888, 0xDDDDDD, new Item.Settings().group(ITEMS_GROUP)));
    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = registerItem(Constant.Item.THROWABLE_METEOR_CHUNK, new ThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = registerItem(Constant.Item.HOT_THROWABLE_METEOR_CHUNK, new HotThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));

    public static final Item LEGACY_MUSIC_DISC_MARS = registerItem(Constant.Item.LEGACY_MUSIC_DISC_MARS, new MusicDiscItem(15, GalacticraftSound.MUSIC_LEGACY_MARS, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_MIMAS = registerItem(Constant.Item.LEGACY_MUSIC_DISC_MIMAS, new MusicDiscItem(15, GalacticraftSound.MUSIC_LEGACY_MIMAS, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_ORBIT = registerItem(Constant.Item.LEGACY_MUSIC_DISC_ORBIT, new MusicDiscItem(15, GalacticraftSound.MUSIC_LEGACY_ORBIT, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));
    public static final Item LEGACY_MUSIC_DISC_SPACERACE = registerItem(Constant.Item.LEGACY_MUSIC_DISC_SPACERACE, new MusicDiscItem(15, GalacticraftSound.MUSIC_LEGACY_SPACERACE, new Item.Settings().maxCount(1).group(ITEMS_GROUP).rarity(Rarity.RARE)));

    private static <T extends Item> T registerItem(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, id), item);
    }

    private static Item[] registerOreItems(String id) {
        Item[] items = new Item[3];
        items[0] = Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, "raw_" + id), new Item(new Item.Settings().group(ITEMS_GROUP)));
        items[1] = Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, id + "_ingot"), new Item(new Item.Settings().group(ITEMS_GROUP)));
        items[2] = Registry.register(Registry.ITEM, new Identifier(Constant.MOD_ID, id + "_nugget"), new Item(new Item.Settings().group(ITEMS_GROUP)));
        return items;
    }
    
    public static void register() {
    }
}
