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

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockRegistry;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.storage.PlaceholderItemStorage;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import static dev.galacticraft.mod.content.item.GCItems.*;

public class GCCreativeModeTabs {
    public static final CreativeModeTab ITEMS_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(GCItems.CANVAS))
            .title(Component.translatable(Translations.ItemGroup.ITEMS))
            .displayItems((parameters, output) -> { // todo: add rockets here
                // GEAR
                output.accept(OXYGEN_MASK);
                output.accept(OXYGEN_GEAR);

                try (Transaction t = Transaction.openOuter()) {
                    PlaceholderItemStorage itemStorage = new PlaceholderItemStorage();
                    ContainerItemContext context = ContainerItemContext.ofSingleSlot(itemStorage);

                    output.accept(SMALL_OXYGEN_TANK);
                    itemStorage.setItem(SMALL_OXYGEN_TANK);
                    context.find(FluidStorage.ITEM).insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, t);
                    output.accept(itemStorage.variant.toStack());

                    output.accept(MEDIUM_OXYGEN_TANK);
                    itemStorage.setItem(MEDIUM_OXYGEN_TANK);
                    context.find(FluidStorage.ITEM).insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, t);
                    output.accept(itemStorage.variant.toStack());

                    output.accept(LARGE_OXYGEN_TANK);
                    itemStorage.setItem(LARGE_OXYGEN_TANK);
                    context.find(FluidStorage.ITEM).insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, t);
                    output.accept(itemStorage.variant.toStack());
                }

                output.accept(INFINITE_OXYGEN_TANK);
                output.accept(SENSOR_GLASSES);
                output.accept(FREQUENCY_MODULE);
                PARACHUTE.colorMap().values().forEach(output::accept);

//                output.accept(SPACE_EMERGENCY_KIT);
                output.accept(SHIELD_CONTROLLER);

                // ROCKETS
                output.accept(ROCKET.getDefaultInstance());

                var rocket = ROCKET.getDefaultInstance();
                CompoundTag tag = rocket.getOrCreateTag();
                tag.putBoolean("creative", true);
                output.accept(rocket);

                // MATERIALS
                output.accept(TIN_NUGGET);
                output.accept(ALUMINUM_NUGGET);
                output.accept(METEORIC_IRON_NUGGET);
                output.accept(DESH_NUGGET);
                output.accept(LEAD_NUGGET);
                output.accept(TITANIUM_NUGGET);

                output.accept(RAW_TIN);
                output.accept(RAW_ALUMINUM);
                output.accept(RAW_METEORIC_IRON);
                output.accept(RAW_DESH);
                output.accept(RAW_LEAD);
                output.accept(RAW_TITANIUM);
                output.accept(RAW_SILICON);
                output.accept(LUNAR_SAPPHIRE);

                output.accept(METEORIC_IRON_INGOT);
                output.accept(DESH_INGOT);
                output.accept(LEAD_INGOT);
                output.accept(ALUMINUM_INGOT);
                output.accept(TIN_INGOT);
                output.accept(TITANIUM_INGOT);

                output.accept(COMPRESSED_COPPER);
                output.accept(COMPRESSED_TIN);
                output.accept(COMPRESSED_ALUMINUM);
                output.accept(COMPRESSED_STEEL);
                output.accept(COMPRESSED_BRONZE);
                output.accept(COMPRESSED_IRON);
                output.accept(COMPRESSED_METEORIC_IRON);
                output.accept(COMPRESSED_DESH);
                output.accept(COMPRESSED_TITANIUM);

                output.accept(TIER_1_HEAVY_DUTY_PLATE);
                output.accept(TIER_2_HEAVY_DUTY_PLATE);
                output.accept(TIER_3_HEAVY_DUTY_PLATE);

                output.accept(DESH_STICK);
                output.accept(CARBON_FRAGMENTS);
                output.accept(IRON_SHARD);
                output.accept(SOLAR_DUST);
                output.accept(BASIC_WAFER);
                output.accept(ADVANCED_WAFER);
                output.accept(BEAM_CORE);
                output.accept(CANVAS);

                output.accept(FLUID_MANIPULATOR);
                output.accept(OXYGEN_CONCENTRATOR);
                output.accept(OXYGEN_FAN);
                output.accept(OXYGEN_VENT);
                output.accept(SENSOR_LENS);
                output.accept(BLUE_SOLAR_WAFER);
                output.accept(SINGLE_SOLAR_MODULE);
                output.accept(FULL_SOLAR_PANEL);
                output.accept(SOLAR_ARRAY_WAFER);
                output.accept(STEEL_POLE);
                output.accept(COPPER_CANISTER);
                output.accept(TIN_CANISTER);
                output.accept(THERMAL_CLOTH);
                output.accept(ISOTHERMAL_FABRIC);
                output.accept(ORION_DRIVE);
                output.accept(ATMOSPHERIC_VALVE);
                output.accept(AMBIENT_THERMAL_CONTROLLER);

                // FOOD
                output.accept(MOON_BERRIES);
                output.accept(CHEESE_CURD);

                output.accept(CHEESE_SLICE);
                output.accept(BURGER_BUN);
                output.accept(GROUND_BEEF);
                output.accept(BEEF_PATTY);
                output.accept(CHEESEBURGER);

                output.accept(CANNED_DEHYDRATED_APPLE);
                output.accept(CANNED_DEHYDRATED_CARROT);
                output.accept(CANNED_DEHYDRATED_MELON);
                output.accept(CANNED_DEHYDRATED_POTATO);
                output.accept(CANNED_BEEF);
                output.accept(THROWABLE_METEOR_CHUNK);
                output.accept(HOT_THROWABLE_METEOR_CHUNK);

                // BATTERIES
                output.accept(BATTERY);
                output.accept(INFINITE_BATTERY);

                //FLUID BUCKETS
                output.accept(CRUDE_OIL_BUCKET);
                output.accept(FUEL_BUCKET);
                output.accept(SULFURIC_ACID_BUCKET);

                // ROCKET PARTS
                output.accept(NOSE_CONE);
                output.accept(HEAVY_NOSE_CONE);

                output.accept(ROCKET_FIN);
                output.accept(ROCKET_ENGINE);

                // SCHEMATICS
                output.accept(TIER_2_ROCKET_SCHEMATIC);
                output.accept(CARGO_ROCKET_SCHEMATIC);
                output.accept(MOON_BUGGY_SCHEMATIC);
                output.accept(TIER_3_ROCKET_SCHEMATIC);
                output.accept(ASTRO_MINER_SCHEMATIC);

                // SMITHING TEMPLATES
                output.accept(TITANTIUM_UPGRADE_SMITHING_TEMPLATE);

                // ARMOR
                output.accept(HEAVY_DUTY_HELMET);
                output.accept(HEAVY_DUTY_CHESTPLATE);
                output.accept(HEAVY_DUTY_LEGGINGS);
                output.accept(HEAVY_DUTY_BOOTS);

                output.accept(DESH_HELMET);
                output.accept(DESH_CHESTPLATE);
                output.accept(DESH_LEGGINGS);
                output.accept(DESH_BOOTS);

                output.accept(TITANIUM_HELMET);
                output.accept(TITANIUM_CHESTPLATE);
                output.accept(TITANIUM_LEGGINGS);
                output.accept(TITANIUM_BOOTS);

                // THERMAL PADDING
                output.accept(THERMAL_PADDING_HELMET);
                output.accept(THERMAL_PADDING_CHESTPIECE);
                output.accept(THERMAL_PADDING_LEGGINGS);
                output.accept(THERMAL_PADDING_BOOTS);

                // TOOLS + WEAPONS
                output.accept(HEAVY_DUTY_SWORD);
                output.accept(HEAVY_DUTY_SHOVEL);
                output.accept(HEAVY_DUTY_PICKAXE);
                output.accept(HEAVY_DUTY_AXE);
                output.accept(HEAVY_DUTY_HOE);

                output.accept(DESH_SWORD);
                output.accept(DESH_SHOVEL);
                output.accept(DESH_PICKAXE);
                output.accept(DESH_AXE);
                output.accept(DESH_HOE);

                output.accept(TITANIUM_SWORD);
                output.accept(TITANIUM_SHOVEL);
                output.accept(TITANIUM_PICKAXE);
                output.accept(TITANIUM_AXE);
                output.accept(TITANIUM_HOE);

                output.accept(STANDARD_WRENCH);
            })
            .build();

    public static final CreativeModeTab BLOCKS_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(GCBlocks.MOON_TURF))
            .title(Component.translatable(Translations.ItemGroup.BLOCKS))
            .displayItems((parameters, output) -> {
                // DECORATION BLOCKS
                for (GCBlockRegistry.DecorationSet decorationSet : GCBlocks.BLOCKS.getDecorations()) {
                    output.accept(decorationSet.item());
                    output.accept(decorationSet.slabItem());
                    output.accept(decorationSet.stairsItem());
                    output.accept(decorationSet.wallItem());
                    output.accept(decorationSet.detailedItem());
                    output.accept(decorationSet.detailedSlabItem());
                    output.accept(decorationSet.detailedStairsItem());
                    output.accept(decorationSet.detailedWallItem());
                }

                // TORCHES
                output.accept(GLOWSTONE_TORCH);
                output.accept(UNLIT_TORCH);

                // LANTERNS
                output.accept(GCBlocks.GLOWSTONE_LANTERN);
                output.accept(GCBlocks.UNLIT_LANTERN);

                // MOON NATURAL
                output.accept(GCBlocks.MOON_TURF);
                output.accept(GCBlocks.MOON_DIRT);
                output.accept(GCBlocks.MOON_DIRT_PATH);
                output.accept(GCBlocks.MOON_SURFACE_ROCK);
                output.accept(GCBlocks.MOON_DUNGEON_BRICK);

                output.accept(GCBlocks.MOON_ROCK.asItem());
                output.accept(GCBlocks.MOON_ROCK_SLAB.asItem().asItem());
                output.accept(GCBlocks.MOON_ROCK_STAIRS.asItem().asItem());
                output.accept(GCBlocks.MOON_ROCK_WALL.asItem().asItem());

                output.accept(GCBlocks.MOON_ROCK_BRICK.asItem());
                output.accept(GCBlocks.MOON_ROCK_BRICK_SLAB.asItem());
                output.accept(GCBlocks.MOON_ROCK_BRICK_STAIRS.asItem());
                output.accept(GCBlocks.MOON_ROCK_BRICK_WALL.asItem());

                output.accept(GCBlocks.CRACKED_MOON_ROCK_BRICK.asItem());
                output.accept(GCBlocks.CRACKED_MOON_ROCK_BRICK_SLAB.asItem());
                output.accept(GCBlocks.CRACKED_MOON_ROCK_BRICK_STAIRS.asItem());
                output.accept(GCBlocks.CRACKED_MOON_ROCK_BRICK_WALL.asItem());

                output.accept(GCBlocks.POLISHED_MOON_ROCK.asItem());
                output.accept(GCBlocks.POLISHED_MOON_ROCK_SLAB.asItem());
                output.accept(GCBlocks.POLISHED_MOON_ROCK_STAIRS.asItem());
                output.accept(GCBlocks.POLISHED_MOON_ROCK_WALL.asItem());

                output.accept(GCBlocks.CHISELED_MOON_ROCK_BRICK.asItem());
                output.accept(GCBlocks.MOON_ROCK_PILLAR.asItem());

                output.accept(GCBlocks.COBBLED_MOON_ROCK);
                output.accept(GCBlocks.COBBLED_MOON_ROCK_SLAB);
                output.accept(GCBlocks.COBBLED_MOON_ROCK_STAIRS);
                output.accept(GCBlocks.COBBLED_MOON_ROCK_WALL);

                output.accept(GCBlocks.LUNASLATE);
                output.accept(GCBlocks.LUNASLATE_SLAB);
                output.accept(GCBlocks.LUNASLATE_STAIRS);
                output.accept(GCBlocks.LUNASLATE_WALL);

                output.accept(GCBlocks.COBBLED_LUNASLATE);
                output.accept(GCBlocks.COBBLED_LUNASLATE_SLAB);
                output.accept(GCBlocks.COBBLED_LUNASLATE_STAIRS);
                output.accept(GCBlocks.COBBLED_LUNASLATE_WALL);

                output.accept(GCBlocks.MOON_BASALT);
                output.accept(GCBlocks.MOON_BASALT_SLAB);
                output.accept(GCBlocks.MOON_BASALT_STAIRS);
                output.accept(GCBlocks.MOON_BASALT_WALL);

                output.accept(GCBlocks.MOON_BASALT_BRICK);
                output.accept(GCBlocks.MOON_BASALT_BRICK_SLAB);
                output.accept(GCBlocks.MOON_BASALT_BRICK_STAIRS);
                output.accept(GCBlocks.MOON_BASALT_BRICK_WALL);

                output.accept(GCBlocks.CRACKED_MOON_BASALT_BRICK);
                output.accept(GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB);
                output.accept(GCBlocks.CRACKED_MOON_BASALT_BRICK_STAIRS);
                output.accept(GCBlocks.CRACKED_MOON_BASALT_BRICK_WALL);

                // MARS NATURAL
                output.accept(GCBlocks.MARS_SURFACE_ROCK);
                output.accept(GCBlocks.MARS_SUB_SURFACE_ROCK);
                output.accept(GCBlocks.MARS_STONE);
                output.accept(GCBlocks.MARS_COBBLESTONE);
                output.accept(GCBlocks.MARS_COBBLESTONE_SLAB);
                output.accept(GCBlocks.MARS_COBBLESTONE_STAIRS);
                output.accept(GCBlocks.MARS_COBBLESTONE_WALL);

                // ASTEROID NATURAL
                output.accept(GCBlocks.ASTEROID_ROCK);
                output.accept(GCBlocks.ASTEROID_ROCK_1);
                output.accept(GCBlocks.ASTEROID_ROCK_2);

                // VENUS NATURAL
                output.accept(GCBlocks.SOFT_VENUS_ROCK);
                output.accept(GCBlocks.HARD_VENUS_ROCK);
                output.accept(GCBlocks.SCORCHED_VENUS_ROCK);
                output.accept(GCBlocks.VOLCANIC_ROCK);
                output.accept(GCBlocks.PUMICE);
                output.accept(GCBlocks.VAPOR_SPOUT);

                // MISC DECOR
                output.accept(GCBlocks.WALKWAY);
                output.accept(GCBlocks.WIRE_WALKWAY);
                output.accept(GCBlocks.FLUID_PIPE_WALKWAY);
                output.accept(GCBlocks.TIN_LADDER);
                output.accept(GCBlocks.GRATING);

                // SPECIAL
                output.accept(GCBlocks.ALUMINUM_WIRE);
                output.accept(GCBlocks.SEALABLE_ALUMINUM_WIRE);
                output.accept(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE);
                output.accept(GCBlocks.GLASS_FLUID_PIPE);
                output.accept(GCBlocks.FUELING_PAD);
                output.accept(GCBlocks.ROCKET_LAUNCH_PAD);

                for (DyeColor color : DyeColor.values()) {
                    ItemStack stack = new ItemStack(GCBlocks.PARACHEST);
                    CompoundTag itemTag = new CompoundTag();
                    CompoundTag blockStateTag = new CompoundTag();
                    itemTag.put("BlockStateTag", blockStateTag);
                    blockStateTag.putString("color", color.getName());

                    stack.setTag(itemTag);
                    output.accept(stack);
                }

                // LIGHT PANELS
                output.accept(GCBlocks.SQUARE_LIGHT_PANEL);
                output.accept(GCBlocks.SPOTLIGHT_LIGHT_PANEL);
                output.accept(GCBlocks.LINEAR_LIGHT_PANEL);
                output.accept(GCBlocks.DASHED_LIGHT_PANEL);
                output.accept(GCBlocks.DIAGONAL_LIGHT_PANEL);

                // VACUUM GLASS
                output.accept(GCBlocks.VACUUM_GLASS);
                output.accept(GCBlocks.CLEAR_VACUUM_GLASS);
                output.accept(GCBlocks.STRONG_VACUUM_GLASS);

                // ORES
                output.accept(GCBlocks.SILICON_ORE);
                output.accept(GCBlocks.DEEPSLATE_SILICON_ORE);

                output.accept(GCBlocks.MOON_COPPER_ORE);
                output.accept(GCBlocks.LUNASLATE_COPPER_ORE);

                output.accept(GCBlocks.TIN_ORE);
                output.accept(GCBlocks.DEEPSLATE_TIN_ORE);
                output.accept(GCBlocks.MOON_TIN_ORE);
                output.accept(GCBlocks.LUNASLATE_TIN_ORE);

                output.accept(GCBlocks.ALUMINUM_ORE);
                output.accept(GCBlocks.DEEPSLATE_ALUMINUM_ORE);

                output.accept(GCBlocks.DESH_ORE);

                output.accept(GCBlocks.ILMENITE_ORE);

                output.accept(GCBlocks.GALENA_ORE);

                // COMPACT MINERAL BLOCKS
                output.accept(GCBlocks.MOON_CHEESE_WHEEL);
                output.accept(GCBlocks.SILICON_BLOCK);
                output.accept(GCBlocks.METEORIC_IRON_BLOCK);
                output.accept(GCBlocks.DESH_BLOCK);
                output.accept(GCBlocks.TITANIUM_BLOCK);
                output.accept(GCBlocks.LEAD_BLOCK);
                output.accept(GCBlocks.LUNAR_SAPPHIRE_BLOCK);

                // MOON VILLAGER SPECIAL
                output.accept(GCBlocks.LUNAR_CARTOGRAPHY_TABLE);

                // MISC WORLD GEN
                output.accept(GCBlocks.CAVERNOUS_VINES);

                // MISC MACHINES
                output.accept(GCBlocks.CRYOGENIC_CHAMBER);
                output.accept(GCBlocks.PLAYER_TRANSPORT_TUBE);

                // MACHINES
            }).build();

    public static final CreativeModeTab MACHINES_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(GCBlocks.COAL_GENERATOR))
            .title(Component.translatable(Translations.ItemGroup.MACHINES))
            .displayItems((parameters, output) -> {
                output.accept(GCBlocks.CIRCUIT_FABRICATOR);
                output.accept(GCBlocks.COMPRESSOR);
                output.accept(GCBlocks.ELECTRIC_COMPRESSOR);
                output.accept(GCBlocks.COAL_GENERATOR);
                output.accept(GCBlocks.BASIC_SOLAR_PANEL);
                output.accept(GCBlocks.ADVANCED_SOLAR_PANEL);
                output.accept(GCBlocks.ENERGY_STORAGE_MODULE);
                output.accept(GCBlocks.ELECTRIC_FURNACE);
                output.accept(GCBlocks.ELECTRIC_ARC_FURNACE);
                output.accept(GCBlocks.REFINERY);
                output.accept(GCBlocks.OXYGEN_COLLECTOR);
                output.accept(GCBlocks.OXYGEN_SEALER);
                output.accept(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR);
                output.accept(GCBlocks.OXYGEN_DECOMPRESSOR);
                output.accept(GCBlocks.OXYGEN_COMPRESSOR);
                output.accept(GCBlocks.OXYGEN_STORAGE_MODULE);
                output.accept(GCBlocks.FUEL_LOADER);
            }).build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id(Constant.Item.ITEM_GROUP), ITEMS_GROUP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id(Constant.Block.ITEM_GROUP_BLOCKS), BLOCKS_GROUP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id(Constant.Block.ITEM_GROUP_MACHINES), MACHINES_GROUP);
    }
}