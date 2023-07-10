/*
 *
 *  * Copyright (c) 2019-2023 Team Galacticraft
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIfDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package dev.galacticraft.mod.data.fixer.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class GCLegacySchema extends NamespacedSchema {
    public GCLegacySchema(int i, Schema schema) {
        super(i, schema);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        registerSimple(map, "galacticraftcore:gc_treasure_chest");
        registerSimple(map, "galacticraftcore:gc_air_distributor");
        registerSimple(map, "galacticraftcore:gc_air_collector");
        registerSimple(map, "galacticraftcore:gc_oxygen_pipe");
        registerSimple(map, "galacticraftcore:gc_air_lock_frame");
        registerSimple(map, "galacticraftcore:gc_refinery");
        registerSimple(map, "galacticraftcore:gc_nasa_workbench");
        registerSimple(map, "galacticraftcore:gc_nasa_workbench_compact");
        registerSimple(map, "galacticraftcore:gc_deconstructor");
        registerSimple(map, "galacticraftcore:gc_air_compressor");
        registerSimple(map, "galacticraftcore:gc_fuel_loader");
        registerSimple(map, "galacticraftcore:gc_landing_pad");
        registerSimple(map, "galacticraftcore:gc_landing_pad_full");
        registerSimple(map, "galacticraftcore:gc_space_station");
        registerSimple(map, "galacticraftcore:gc_dummy_block");
        registerSimple(map, "galacticraftcore:gc_air_sealer");
        registerSimple(map, "galacticraftcore:gc_dungeon_boss_spawner");
        registerSimple(map, "galacticraftcore:gc_oxygen_detector");
        registerSimple(map, "galacticraftcore:gc_buggy_fueler");
        registerSimple(map, "galacticraftcore:gc_buggy_fueler_single");
        registerSimple(map, "galacticraftcore:gc_cargo_loader");
        registerSimple(map, "galacticraftcore:gc_cargo_unloader");
        registerSimple(map, "galacticraftcore:gc_parachest_tile");
        registerSimple(map, "galacticraftcore:gc_solar_panel");
        registerSimple(map, "galacticraftcore:gc_radio_telescope");
        registerSimple(map, "galacticraftcore:gc_magnetic_crafting_table");
        registerSimple(map, "galacticraftcore:gc_energy_storage_module");
        registerSimple(map, "galacticraftcore:gc_coal_generator");
        registerSimple(map, "galacticraftcore:gc_electric_furnace");
        registerSimple(map, "galacticraftcore:gc_aluminum_wire");
        registerSimple(map, "galacticraftcore:gc_switchable_aluminum_wire");
        registerSimple(map, "galacticraftcore:gc_fallen_meteor");
        registerSimple(map, "galacticraftcore:gc_ingot_compressor");
        registerSimple(map, "galacticraftcore:gc_electric_ingot_compressor");
        registerSimple(map, "galacticraftcore:gc_circuit_fabricator");
        registerSimple(map, "galacticraftcore:gc_air_lock_controller");
        registerSimple(map, "galacticraftcore:gc_oxygen_storage_module");
        registerSimple(map, "galacticraftcore:gc_oxygen_decompressor");
        registerSimple(map, "galacticraftcore:gc_space_station_thruster");
        registerSimple(map, "galacticraftcore:gc_arc_lamp");
        registerSimple(map, "galacticraftcore:gc_view_screen");
        registerSimple(map, "galacticraftcore:gc_panel_lighting");
        registerSimple(map, "galacticraftcore:gc_telemetry_unit");
        registerSimple(map, "galacticraftcore:gc_painter");
        registerSimple(map, "galacticraftcore:gc_fluid_tank");
        registerSimple(map, "galacticraftcore:gc_player_detector");
        registerSimple(map, "galacticraftcore:gc_platform");
        registerSimple(map, "galacticraftcore:gc_emergency_post");
        registerSimple(map, "galacticraftcore:gc_null_tile");

        // IC2 stuff
        registerSimple(map, "galacticraftcore:gc_sealed_ic2_cable");
        return map;
    }
}
