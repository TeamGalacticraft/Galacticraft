/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.village;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.structure.GalacticraftStructures;
import com.hrznstudio.galacticraft.village.GalacticraftTradeOffers.BuyForOneEmeraldFactory;
import com.hrznstudio.galacticraft.village.GalacticraftTradeOffers.SellItemFactory;
import com.hrznstudio.galacticraft.village.GalacticraftTradeOffers.SellMapFactory;
import com.hrznstudio.galacticraft.world.poi.GalacticraftPointOfInterestType;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftVillagerProfessions {
    public static final VillagerProfession LUNAR_CARTOGRAPHER = VillagerProfessionBuilder.create().id(new Identifier(Constants.MOD_ID, "lunar_cartographer")).workstation(GalacticraftPointOfInterestType.LUNAR_CARTOGRAPHER).workSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER).build();

    public static void register() {
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("none"), VillagerProfession.NONE);
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("nitwit"), VillagerProfession.NITWIT);
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier(Constants.MOD_ID, "lunar_cartographer"), LUNAR_CARTOGRAPHER);

        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 1, factories -> {
            factories.add(new BuyForOneEmeraldFactory(Items.PAPER, 24, 16, 2));
            factories.add(new SellItemFactory(Items.MAP, 7, 1, 1));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 2, factories -> {
            factories.add(new BuyForOneEmeraldFactory(Items.GLASS_PANE, 11, 16, 10));
            factories.add(new SellMapFactory(13, GalacticraftStructures.MOON_RUINS, MapIcon.Type.RED_X, 12, 5));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 3, factories -> {
            factories.add(new BuyForOneEmeraldFactory(Items.COMPASS, 1, 12, 20));
            //factories.add(new GalacticraftTradeOffers.SellMapFactory(14, GalacticraftStructures.MOON_RUINS, MapIcon.Type.MANSION, 12, 10));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 4, factories -> {
            factories.add(new SellItemFactory(Items.ITEM_FRAME, 7, 1, 15));
            factories.add(new SellItemFactory(Items.WHITE_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.BLUE_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.LIGHT_BLUE_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.RED_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.PINK_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.GREEN_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.LIME_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.GRAY_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.BLACK_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.PURPLE_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.MAGENTA_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.CYAN_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.BROWN_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.YELLOW_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.ORANGE_BANNER, 3, 1, 15));
            factories.add(new SellItemFactory(Items.LIGHT_GRAY_BANNER, 3, 1, 15));
        });
        TradeOfferHelper.registerVillagerOffers(LUNAR_CARTOGRAPHER, 5, factories -> factories.add(new SellItemFactory(Items.GLOBE_BANNER_PATTERN, 8, 1, 30)));
    }
}
