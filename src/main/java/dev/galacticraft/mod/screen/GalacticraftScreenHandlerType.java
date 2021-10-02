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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.block.entity.*;
import dev.galacticraft.mod.mixin.ExtendedScreenHandlerTypeAccessor;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.screen.factory.MachineScreenHandlerFactory;
import dev.galacticraft.mod.screen.factory.RecipeMachineScreenHandlerFactory;
import dev.galacticraft.mod.screen.factory.SimpleMachineScreenHandlerFactory;
import io.netty.buffer.ByteBufAllocator;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftScreenHandlerType {

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<CoalGeneratorBlockEntity>> COAL_GENERATOR_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.COAL_GENERATOR_HANDLER, 94)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<BasicSolarPanelBlockEntity>> BASIC_SOLAR_PANEL_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.BASIC_SOLAR_PANEL_HANDLER)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<AdvancedSolarPanelBlockEntity>> ADVANCED_SOLAR_PANEL_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.ADVANCED_SOLAR_PANEL_HANDLER)
    );

    public static final ExtendedScreenHandlerType<RecipeMachineScreenHandler<Inventory, FabricationRecipe, CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR_HANDLER = new ExtendedScreenHandlerType<>(
            RecipeMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.CIRCUIT_FABRICATOR_HANDLER, 94)
    );

    public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR_HANDLER = new ExtendedScreenHandlerType<>(CompressorScreenHandler::new);

    public static final ExtendedScreenHandlerType<RecipeMachineScreenHandler<Inventory, CompressingRecipe, ElectricCompressorBlockEntity>> ELECTRIC_COMPRESSOR_HANDLER = new ExtendedScreenHandlerType<>(
            RecipeMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.ELECTRIC_COMPRESSOR_HANDLER)
    );

    public static final ExtendedScreenHandlerType<RecipeMachineScreenHandler<Inventory, SmeltingRecipe, ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE_HANDLER = new ExtendedScreenHandlerType<>(
            RecipeMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.ELECTRIC_FURNACE_HANDLER)
    );

    public static final ExtendedScreenHandlerType<RecipeMachineScreenHandler<Inventory, BlastingRecipe, ElectricArcFurnaceBlockEntity>> ELECTRIC_ARC_FURNACE_HANDLER = new ExtendedScreenHandlerType<>(
            RecipeMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.ELECTRIC_ARC_FURNACE_HANDLER)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<RefineryBlockEntity>> REFINERY_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.REFINERY_HANDLER)
    );

    public static final ScreenHandlerType<OxygenCollectorScreenHandler> OXYGEN_COLLECTOR_HANDLER = new ExtendedScreenHandlerType<>(OxygenCollectorScreenHandler::new);

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<OxygenCompressorBlockEntity>> OXYGEN_COMPRESSOR_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.OXYGEN_COMPRESSOR_HANDLER)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<OxygenDecompressorBlockEntity>> OXYGEN_DECOMPRESSOR_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.OXYGEN_DECOMPRESSOR_HANDLER)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<OxygenSealerBlockEntity>> OXYGEN_SEALER_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.OXYGEN_SEALER_HANDLER)
    );

    public static final ScreenHandlerType<BubbleDistributorScreenHandler> BUBBLE_DISTRIBUTOR_HANDLER = new ExtendedScreenHandlerType<>(BubbleDistributorScreenHandler::new);

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<OxygenStorageModuleBlockEntity>> OXYGEN_STORAGE_MODULE_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.OXYGEN_STORAGE_MODULE_HANDLER)
    );

    public static final ExtendedScreenHandlerType<SimpleMachineScreenHandler<EnergyStorageModuleBlockEntity>> ENERGY_STORAGE_MODULE_HANDLER = new ExtendedScreenHandlerType<>(
            SimpleMachineScreenHandlerFactory.create(() -> GalacticraftScreenHandlerType.ENERGY_STORAGE_MODULE_HANDLER)
    );

    public static final ScreenHandlerType<GalacticraftPlayerInventoryScreenHandler> PLAYER_INV_GC_HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Constant.MOD_ID, Constant.ScreenHandler.PLAYER_INVENTORY_SCREEN_HANDLER),
            GalacticraftPlayerInventoryScreenHandler::new
    );

    @SuppressWarnings({"ConstantConditions", "unchecked"}) // class will extend accessor
    public static <B extends MachineBlockEntity, T extends MachineScreenHandler<B>> T create(ExtendedScreenHandlerType<T> type, int syncId, PlayerInventory inventory, B machine) {
        ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory = ((ExtendedScreenHandlerTypeAccessor<T>)(Object) type).getFactory();
        if (factory instanceof MachineScreenHandlerFactory) {
            return ((MachineScreenHandlerFactory<B, T>) factory).create(syncId, inventory, machine);
        } else {
            return factory.create(syncId, inventory, new PacketByteBuf(ByteBufAllocator.DEFAULT.buffer(Long.BYTES, Long.BYTES)).writeBlockPos(machine.getPos()));
        }
    }

    public static void register() {
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.COAL_GENERATOR_SCREEN_HANDLER), COAL_GENERATOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.BASIC_SOLAR_PANEL_SCREEN_HANDLER), BASIC_SOLAR_PANEL_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ADVANCED_SOLAR_PANEL_SCREEN_HANDLER), ADVANCED_SOLAR_PANEL_HANDLER);

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.CIRCUIT_FABRICATOR_SCREEN_HANDLER), CIRCUIT_FABRICATOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.COMPRESSOR_SCREEN_HANDLER), COMPRESSOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_COMPRESSOR_SCREEN_HANDLER), ELECTRIC_COMPRESSOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_FURNACE_SCREEN_HANDLER), ELECTRIC_FURNACE_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_ARC_FURNACE_SCREEN_HANDLER), ELECTRIC_ARC_FURNACE_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.REFINERY_SCREEN_HANDLER), REFINERY_HANDLER);

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_COLLECTOR_SCREEN_HANDLER), OXYGEN_COLLECTOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_COMPRESSOR_SCREEN_HANDLER), OXYGEN_COMPRESSOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_DECOMPRESSOR_SCREEN_HANDLER), OXYGEN_DECOMPRESSOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.BUBBLE_DISTRIBUTOR_SCREEN_HANDLER), BUBBLE_DISTRIBUTOR_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_SEALER_SCREEN_HANDLER), OXYGEN_SEALER_HANDLER);

        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ENERGY_STORAGE_MODULE_SCREEN_HANDLER), ENERGY_STORAGE_MODULE_HANDLER);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_STORAGE_MODULE_SCREEN_HANDLER), OXYGEN_STORAGE_MODULE_HANDLER);
    }
}
