package com.hrznstudio.galacticraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.structure.GalacticraftStructures;
import com.hrznstudio.galacticraft.world.poi.GalacticraftPointOfInterestType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.gen.feature.StructureFeature;

public class GalacticraftVillagerProfessions {
    public static final VillagerProfession LUNAR_CARTOGRAPHER = Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier(Constants.MOD_ID, "lunar_cartographer"), Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(Constants.MOD_ID, "lunar_cartographer"), new VillagerProfession(new Identifier(Constants.MOD_ID, "lunar_cartographer").toString(), GalacticraftPointOfInterestType.LUNAR_CARTOGRAPHER, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER)));

    public static void register() {
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("none"), VillagerProfession.NONE);
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("nitwit"), VillagerProfession.NITWIT);

        TradeOffers.PROFESSION_TO_LEVELED_TRADE.put(GalacticraftVillagerProfessions.LUNAR_CARTOGRAPHER, copyToFastUtilMap(ImmutableMap.of(1, new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.PAPER, 24, 16, 2), new TradeOffers.SellItemFactory(Items.MAP, 7, 1, 1)}, 2, new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.GLASS_PANE, 11, 16, 10), new TradeOffers.SellMapFactory(13, GalacticraftStructures.MOON_RUINS, MapIcon.Type.RED_X, 12, 5)}, 3, new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.COMPASS, 1, 12, 20), new TradeOffers.SellMapFactory(14, StructureFeature.MANSION, MapIcon.Type.MANSION, 12, 10)}, 4, new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.ITEM_FRAME, 7, 1, 15), new TradeOffers.SellItemFactory(Items.WHITE_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.BLUE_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.LIGHT_BLUE_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.RED_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.PINK_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.GREEN_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.LIME_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.GRAY_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.BLACK_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.PURPLE_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.MAGENTA_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.CYAN_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.BROWN_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.YELLOW_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.ORANGE_BANNER, 3, 1, 15), new TradeOffers.SellItemFactory(Items.LIGHT_GRAY_BANNER, 3, 1, 15)}, 5, new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.GLOBE_BANNER_PATTERN, 8, 1, 30)})));
    }

    private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> map) {
        return new Int2ObjectOpenHashMap<>(map);
    }
}
