package dev.galacticraft.mod.content;
import dev.galacticraft.mod.content.multiblocks.FluidTank;
import dev.galacticraft.multiblocklib.api.MultiblockLibApi;

import java.util.List;

import static dev.galacticraft.mod.Constant.id;

public class GCMultiblocks {
    public static void register() {
        MultiblockLibApi.register(
                "fluid_tank", //multiblock name
                FluidTank.class, //multiblock class
                List.of(id("block/fluid_tank/face_opaque_1")), //opaque faces
                List.of(id("block/fluid_tank/edge_opaque_1"), id("block/fluid_tank/edge_opaque_2")), //opaque edges
                List.of(id("block/fluid_tank/inner_corner_opaque_1")), //inner opaque corners
                List.of(id("block/fluid_tank/outer_corner_opaque_1")), //outer opaque corners
                List.of(id("block/fluid_tank/face_translucent_1")), //translucent faces
                List.of(id("block/fluid_tank/edge_translucent_1"), id("block/fluid_tank/edge_translucent_2")), //translucent edges
                List.of(id("block/fluid_tank/inner_corner_translucent_1")), //inner translucent corners
                List.of(id("block/fluid_tank/outer_corner_translucent_1")), //outer translucent corners
                id("block/fluid_tank/valve_input"), //valve in
                id("block/fluid_tank/valve_output") //valve out
        );
    }
}
