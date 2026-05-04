/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.village;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCStructureTags;
import dev.galacticraft.mod.world.poi.GCPointOfInterestTypes;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class GCVillagerProfessions {
    public static final VillagerProfession LUNAR_CARTOGRAPHER = register(Constant.id("lunar_cartographer"), GCPointOfInterestTypes.LUNAR_CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession LUNAR_ENGINEER = register(Constant.id("lunar_engineer"), GCPointOfInterestTypes.LUNAR_ENGINEER, SoundEvents.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession LUNAR_MECHANIC = register(Constant.id("lunar_mechanic"), GCPointOfInterestTypes.LUNAR_MECHANIC, SoundEvents.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession LUNAR_CHEESE_MAKER = register(Constant.id("lunar_cheese_maker"), GCPointOfInterestTypes.LUNAR_CHEESE_MAKER, SoundEvents.VILLAGER_WORK_FARMER);
    public static final VillagerProfession LUNAR_PROVISIONER = register(Constant.id("lunar_provisioner"), GCPointOfInterestTypes.LUNAR_PROVISIONER, SoundEvents.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession LUNAR_BOTANIST = register(Constant.id("lunar_botanist"), GCPointOfInterestTypes.LUNAR_BOTANIST, SoundEvents.VILLAGER_WORK_FARMER);

    private static final List<MoonProfessionData> MOON_PROFESSIONS = List.of(
            new MoonProfessionData(LUNAR_CARTOGRAPHER, GCPointOfInterestTypes.LUNAR_CARTOGRAPHER, state -> state.is(GCBlocks.LUNAR_CARTOGRAPHY_TABLE)),
            new MoonProfessionData(LUNAR_ENGINEER, GCPointOfInterestTypes.LUNAR_ENGINEER, state -> state.is(GCBlocks.LUNAR_WORKBENCH)),
            new MoonProfessionData(LUNAR_MECHANIC, GCPointOfInterestTypes.LUNAR_MECHANIC, state -> state.is(GCBlocks.LUNAR_SMITHING_TABLE)),
            new MoonProfessionData(LUNAR_CHEESE_MAKER, GCPointOfInterestTypes.LUNAR_CHEESE_MAKER, state -> state.is(GCBlocks.LUNAR_CHEESE_PRESS)),
            new MoonProfessionData(LUNAR_PROVISIONER, GCPointOfInterestTypes.LUNAR_PROVISIONER, state -> state.is(GCBlocks.FOOD_CANNER)),
            new MoonProfessionData(LUNAR_BOTANIST, GCPointOfInterestTypes.LUNAR_BOTANIST, state -> state.is(GCBlocks.LUNAR_HERBALIST_TABLE))
    );

    public record MoonProfessionData(VillagerProfession profession, ResourceKey<PoiType> poiKey, Predicate<BlockState> workstationPredicate) {
        public boolean matches(BlockState state) {
            return this.workstationPredicate.test(state);
        }
    }

    public static VillagerProfession register(ResourceLocation id, ResourceKey<PoiType> resourceKey, @Nullable SoundEvent soundEvent) {
        return new VillagerProfession(id.toString(), (holder) -> holder.is(resourceKey), (holder) -> holder.is(resourceKey), ImmutableSet.<Item>builder().build(), ImmutableSet.<Block>builder().build(), soundEvent);
    }

    public static @Nullable MoonProfessionData getMoonProfessionData(VillagerProfession profession) {
        for (MoonProfessionData moonProfession : MOON_PROFESSIONS) {
            if (moonProfession.profession() == profession) {
                return moonProfession;
            }
        }

        return null;
    }

    public static List<MoonProfessionData> moonProfessions() {
        return MOON_PROFESSIONS;
    }

    public static void register() {
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_cartographer"), LUNAR_CARTOGRAPHER);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_engineer"), LUNAR_ENGINEER);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_mechanic"), LUNAR_MECHANIC);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_cheese_maker"), LUNAR_CHEESE_MAKER);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_provisioner"), LUNAR_PROVISIONER);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Constant.id("lunar_botanist"), LUNAR_BOTANIST);

        // === LUNAR CARTOGRAPHER ===
        // Focuses on navigation, mapping, exploration, and schematics
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 1, factories -> {
            factories.add(lunarSapphiresForItems(Items.PAPER, 24, 1, 16, 2));
            factories.add(itemsForLunarSapphires(Items.MAP, 2, 1, 16, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 2, factories -> {
            factories.add(lunarSapphiresForItems(Items.GLASS_PANE, 16, 1, 12, 5));
            factories.add(itemsForLunarSapphires(Items.COMPASS, 3, 1, 12, 5));
            factories.add(itemsForLunarSapphires(Items.SPYGLASS, 5, 1, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 3, factories -> {
            factories.add(itemsForLunarSapphires(GCBlocks.LUNAR_CARTOGRAPHY_TABLE.asItem(), 6, 1, 8, 10));
            factories.add(itemsForLunarSapphires(GCItems.SENSOR_GLASSES, 12, 1, 6, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 4, factories -> {
            factories.add(moonExplorerMapForLunarSapphires(GCStructureTags.MOON_RUINS, "item.galacticraft.moon_ruins_explorer_map", 18, 6, 15));
            factories.add(itemsForLunarSapphires(GCItems.MOON_BUGGY_SCHEMATIC, 45, 1, 3, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.TIER_2_ROCKET_SCHEMATIC, 57, 1, 2, 30));
            factories.add(itemsForLunarSapphires(GCItems.CARGO_ROCKET_SCHEMATIC, 48, 1, 2, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });

        // === LUNAR ENGINEER ===
        // Focuses on electronics, oxygen systems, solar, and machines
        TradeOfferHelper.registerVillagerOffers(LUNAR_ENGINEER, 1, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.SILICON, 16, 1, 12, 2));
            factories.add(itemsForLunarSapphires(GCItems.BATTERY, 3, 1, 16, 2));
            factories.add(itemsForLunarSapphires(GCItems.BASIC_WAFER, 3, 2, 16, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_ENGINEER, 2, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.SOLAR_DUST, 12, 1, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_CONCENTRATOR, 6, 1, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_FAN, 6, 1, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_ENGINEER, 3, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.ADVANCED_WAFER, 8, 1, 10, 10));
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_VENT, 6, 1, 10, 10));
            factories.add(itemsForLunarSapphires(GCItems.SENSOR_LENS, 8, 1, 10, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_ENGINEER, 4, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.SINGLE_SOLAR_MODULE, 10, 1, 8, 15));
            factories.add(itemsForLunarSapphires(GCBlocks.BASIC_SOLAR_PANEL.asItem(), 26, 1, 4, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_ENGINEER, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCBlocks.ELECTRIC_FURNACE.asItem(), 29, 1, 3, 30));
            factories.add(itemsForLunarSapphires(GCBlocks.OXYGEN_COMPRESSOR.asItem(), 24, 1, 3, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });

        // === LUNAR MECHANIC ===
        // Focuses on metals, tools, armor, and rocket parts
        TradeOfferHelper.registerVillagerOffers(LUNAR_MECHANIC, 1, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.TIN_INGOT, 16, 1, 12, 2));
            factories.add(lunarSapphiresForItems(GCItems.ALUMINUM_INGOT, 12, 1, 12, 2));
            factories.add(itemsForLunarSapphires(GCItems.STANDARD_WRENCH, 4, 1, 12, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_MECHANIC, 2, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.COMPRESSED_STEEL, 5, 1, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.COMPRESSED_TIN, 4, 1, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.STEEL_POLE, 4, 2, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_MECHANIC, 3, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.TIER_1_HEAVY_DUTY_PLATE, 21, 1, 8, 10));
            factories.add(itemsForLunarSapphires(GCItems.HEAVY_DUTY_PICKAXE, 12, 1, 6, 10));
            factories.add(itemsForLunarSapphires(GCItems.HEAVY_DUTY_AXE, 12, 1, 6, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_MECHANIC, 4, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.NOSE_CONE, 33, 1, 6, 15));
            factories.add(itemsForLunarSapphires(GCItems.ROCKET_FIN, 27, 1, 6, 15));
            factories.add(itemsForLunarSapphires(GCItems.TIN_CANISTER, 6, 2, 8, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_MECHANIC, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.ROCKET_ENGINE, 40, 1, 3, 30));
            factories.add(itemsForLunarSapphires(GCItems.BUGGY_WHEEL, 22, 1, 4, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });

        // === LUNAR CHEESE MAKER ===
        // Focuses on the cheese production chain and cheese products
        TradeOfferHelper.registerVillagerOffers(LUNAR_CHEESE_MAKER, 1, factories -> {
            factories.add(lunarSapphiresForItems(Items.MILK_BUCKET, 1, 2, 16, 2));
            factories.add(itemsForLunarSapphires(GCItems.MOON_CHEESE_CURD, 1, 4, 16, 2));
            factories.add(itemsForLunarSapphires(GCItems.CRACKER, 1, 6, 16, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CHEESE_MAKER, 2, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.MOON_CHEESE_CURD, 10, 1, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.MOON_CHEESE_SLICE, 2, 4, 12, 5));
            factories.add(itemsForLunarSapphires(GCItems.CHEESE_CRACKER, 2, 4, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CHEESE_MAKER, 3, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.MOON_CHEESE_SLICE, 8, 1, 12, 10));
            factories.add(itemsForLunarSapphires(GCBlocks.MOON_CHEESE_BLOCK.asItem(), 6, 1, 10, 10));
            factories.add(itemsForLunarSapphires(GCItems.MOON_CHEESE_WHEEL, 8, 1, 8, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CHEESE_MAKER, 4, factories -> {
            factories.add(itemsForLunarSapphires(GCBlocks.MOON_CHEESE_LOG.asItem(), 8, 2, 8, 15));
            factories.add(cannedFoodForLunarSapphires(GCItems.MOON_CHEESE_SLICE, 8, 8, 6, 15));
            factories.add(itemsForLunarSapphires(GCBlocks.LUNAR_CHEESE_PRESS.asItem(), 10, 1, 4, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CHEESE_MAKER, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.CHEESEBURGER, 10, 2, 6, 30));
            factories.add(cannedFoodForLunarSapphires(GCItems.CHEESEBURGER, 6, 12, 2, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });

        // === LUNAR PROVISIONER ===
        // Focuses on canned food, emergency supplies, and survival gear
        TradeOfferHelper.registerVillagerOffers(LUNAR_PROVISIONER, 1, factories -> {
            factories.add(lunarSapphiresForItems(GCItems.MOON_CHEESE_CURD, 12, 1, 16, 2));
            factories.add(itemsForLunarSapphires(GCItems.EMPTY_CAN, 1, 2, 16, 2));
            factories.add(itemsForLunarSapphires(GCItems.CHEESE_CRACKER, 2, 3, 16, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_PROVISIONER, 2, factories -> {
            factories.add(lunarSapphiresForItems(Items.APPLE, 16, 1, 12, 5));
            factories.add(cannedFoodForLunarSapphires(Items.APPLE, 6, 4, 12, 5));
            factories.add(cannedFoodForLunarSapphires(Items.CARROT, 6, 4, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_PROVISIONER, 3, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_MASK, 8, 1, 6, 10));
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_GEAR, 8, 1, 6, 10));
            factories.add(cannedFoodForLunarSapphires(GCItems.MOON_CHEESE_SLICE, 6, 6, 8, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_PROVISIONER, 4, factories -> {
            factories.add(itemsForLunarSapphires(GCBlocks.FOOD_CANNER.asItem(), 25, 1, 3, 15));
            factories.add(itemsForLunarSapphires(GCItems.EMERGENCY_KIT, 14, 1, 4, 15));
            factories.add(cannedFoodForLunarSapphires(GCItems.CHEESEBURGER, 4, 10, 4, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_PROVISIONER, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.SMALL_OXYGEN_TANK, 14, 1, 6, 30));
            factories.add(cannedFoodForLunarSapphires(GCItems.CHEESEBURGER, 4, 10, 2, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });

        // === LUNAR BOTANIST ===
        // Focuses on seed stock, imported tree saplings, moon-cheese trees, and late oxygen habitat gear.
        TradeOfferHelper.registerVillagerOffers(LUNAR_BOTANIST, 1, factories -> {
            factories.add(itemsForLunarSapphires(Items.WHEAT_SEEDS, 1, 8, 16, 2));
            factories.add(itemsForLunarSapphires(Items.BEETROOT_SEEDS, 1, 8, 16, 2));
            factories.add(itemsForLunarSapphires(Items.OAK_SAPLING, 2, 2, 12, 2));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_BOTANIST, 2, factories -> {
            factories.add(itemsForLunarSapphires(Items.PUMPKIN_SEEDS, 2, 6, 12, 5));
            factories.add(itemsForLunarSapphires(Items.MELON_SEEDS, 2, 6, 12, 5));
            factories.add(itemsForLunarSapphires(Items.SPRUCE_SAPLING, 2, 2, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_BOTANIST, 3, factories -> {
            factories.add(itemsForLunarSapphires(Items.BIRCH_SAPLING, 2, 2, 12, 10));
            factories.add(itemsForLunarSapphires(Items.JUNGLE_SAPLING, 3, 2, 10, 10));
            factories.add(itemsForLunarSapphires(GCBlocks.MOON_CHEESE_LEAVES.asItem(), 2, 4, 12, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_BOTANIST, 4, factories -> {
            factories.add(itemsForLunarSapphires(Items.DARK_OAK_SAPLING, 4, 4, 8, 15));
            factories.add(itemsForLunarSapphires(Items.CHERRY_SAPLING, 5, 2, 8, 15));
            factories.add(itemsForLunarSapphires(GCBlocks.MOON_CHEESE_LOG.asItem(), 6, 2, 8, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_BOTANIST, 5, factories -> {
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_MASK, 15, 1, 6, 30));
            factories.add(itemsForLunarSapphires(GCItems.OXYGEN_GEAR, 15, 1, 6, 30));
            factories.add(itemsForLunarSapphires(GCBlocks.OXYGEN_COLLECTOR.asItem(), 25, 1, 4, 30));
            factories.add(itemsForLunarSapphires(GCItems.VILLAGE_ACCESS_KEY, 30, 1, 4, 30));
        });
    }

    private static VillagerTrades.ItemListing itemsForLunarSapphires(Item item, int sapphireCost, int itemCount, int maxUses, int villagerXp) {
        return (trader, random) -> new MerchantOffer(new ItemCost(GCItems.LUNAR_SAPPHIRE, randomizedSapphireCost(sapphireCost, villagerXp, random)), new ItemStack(item, itemCount), maxUses, villagerXp, 0.05F);
    }

    private static VillagerTrades.ItemListing lunarSapphiresForItems(Item item, int itemCount, int sapphireCount, int maxUses, int villagerXp) {
        return (trader, random) -> new MerchantOffer(new ItemCost(item, itemCount), new ItemStack(GCItems.LUNAR_SAPPHIRE, sapphireCount), maxUses, villagerXp, 0.05F);
    }

    private static VillagerTrades.ItemListing moonExplorerMapForLunarSapphires(TagKey<Structure> destination, String displayNameKey, int sapphireCost, int maxUses, int villagerXp) {
        return (trader, random) -> {
            if (!(trader.level() instanceof ServerLevel serverLevel)) {
                return null;
            }

            var target = serverLevel.findNearestMapStructure(destination, trader.blockPosition(), 100, true);
            if (target == null) {
                return null;
            }

            ItemStack map = MapItem.create(serverLevel, target.getX(), target.getZ(), (byte) 2, true, true);
            MapItem.renderBiomePreviewMap(serverLevel, map);
            MapItemSavedData.addTargetDecoration(map, target, "+", MapDecorationTypes.RED_X);
            map.set(DataComponents.CUSTOM_NAME, Component.translatable(displayNameKey));

            return new MerchantOffer(
                    new ItemCost(GCItems.LUNAR_SAPPHIRE, randomizedSapphireCost(sapphireCost, villagerXp, random)),
                    Optional.of(new ItemCost(Items.COMPASS)),
                    map,
                    maxUses,
                    villagerXp,
                    0.2F
            );
        };
    }

    private static VillagerTrades.ItemListing cannedFoodForLunarSapphires(Item item, int servings, int sapphireCost, int maxUses, int villagerXp) {
        return (trader, random) -> new MerchantOffer(new ItemCost(GCItems.LUNAR_SAPPHIRE, randomizedSapphireCost(sapphireCost, villagerXp, random)), cannedFoodStack(item, servings), maxUses, villagerXp, 0.05F);
    }

    private static int randomizedSapphireCost(int baseCost, int villagerXp, RandomSource random) {
        int baseWiggle = Math.max(1, Math.min(4, baseCost / 8 + 1));
        int tierWiggle = switch (villagerXp) {
            case 30 -> 4;
            case 15 -> 3;
            case 10 -> 2;
            case 5 -> 1;
            default -> 0;
        };
        int wiggle = Math.min(8, baseWiggle + tierWiggle);
        return baseCost + random.nextInt(wiggle + 1);
    }

    private static ItemStack cannedFoodStack(Item item, int servings) {
        ItemStack cannedFood = GCItems.CANNED_FOOD.getDefaultInstance();
        CannedFoodItem.add(cannedFood, new ItemStack(item, servings));
        return cannedFood;
    }
}
