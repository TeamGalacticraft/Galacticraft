package dev.galacticraft.mod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GCDimensionPhysicsProvider implements DataProvider {

    private record PressurePoint(double altitude, double value, double slope) {}

    private record DimensionPhysicsEntry(
            ResourceLocation dimension,
            int priority,
            double universalDrag,
            double[] baseGravity,
            double basePressure,
            List<PressurePoint> pressureFunction,
            double[] magneticNorth
    ) {}

    private static final List<DimensionPhysicsEntry> ENTRIES = new ArrayList<>();

    private final FabricDataOutput output;

    public GCDimensionPhysicsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
    }

    public static void bootstrapRegistries() {
        // Overworld example from sables docs
//        addWithPressureFunction(
//                Constant.id("overworld"),
//                0,
//                0.09,
//                new double[]{0.0, -11.0, 0.0},
//                1.0,
//                List.of(
//                        new PressurePoint(-38.366277, 1.5,      -0.006),
//                        new PressurePoint(63.0,       1.0,      -0.004),
//                        new PressurePoint(263.0,      0.449329, -0.001797),
//                        new PressurePoint(280.0,      0.419786, -0.001679),
//                        new PressurePoint(320.0,      0.0,      -0.020989)
//                ),
//                new double[]{0.0, 0.0, 0.0}
//        );

        // Gravity = GC gravity value * -11
        // e.g.(Moon = 0.166, 0.166*-11 = -1.826)

        // Moon
        add(
                Constant.id("moon"),
                1001,
                0.0,
                new double[]{0.0, -1.826, 0.0},
                0.0,
                new double[]{0.0, 0.0, 0.0}
        );

        // Venus
        add(
                Constant.id("venus"),
                1001,
                0.09,
                new double[]{0.0, -10.01, 0.0},
                92.0,
                new double[]{0.0, 0.0, 0.0}
        );

        // Asteroid belt / space — zero gravity, zero pressure
        add(
                Constant.id("asteroid"),
                1001,
                0.0,
                new double[]{0.0, 0.0, 0.0},
                0.0,
                new double[]{0.0, 0.0, 0.0}
        );
    }

    public static void add(ResourceLocation dimension, int priority, double universalDrag,
                           double[] baseGravity, double basePressure, double[] magneticNorth) {
        ENTRIES.add(new DimensionPhysicsEntry(dimension, priority, universalDrag, baseGravity, basePressure, List.of(), magneticNorth));
    }

    public static void addWithPressureFunction(ResourceLocation dimension, int priority, double universalDrag,
                                               double[] baseGravity, double basePressure,
                                               List<PressurePoint> pressureFunction, double[] magneticNorth) {
        ENTRIES.add(new DimensionPhysicsEntry(dimension, priority, universalDrag, baseGravity, basePressure, pressureFunction, magneticNorth));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        bootstrapRegistries();

        PackOutput.PathProvider pathProvider = this.output.createPathProvider(PackOutput.Target.DATA_PACK, "dimension_physics");
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (DimensionPhysicsEntry entry : ENTRIES) {
            JsonObject obj = new JsonObject();
            obj.addProperty("dimension", entry.dimension().toString());
            obj.addProperty("priority", entry.priority());
            obj.addProperty("universal_drag", entry.universalDrag());

            JsonArray gravity = new JsonArray();
            for (double v : entry.baseGravity()) gravity.add(v);
            obj.add("base_gravity", gravity);

            obj.addProperty("base_pressure", entry.basePressure());

            if (!entry.pressureFunction().isEmpty()) {
                JsonArray curve = new JsonArray();
                for (PressurePoint point : entry.pressureFunction()) {
                    JsonObject p = new JsonObject();
                    p.addProperty("altitude", point.altitude());
                    p.addProperty("value", point.value());
                    p.addProperty("slope", point.slope());
                    curve.add(p);
                }
                obj.add("pressure_function", curve);
            }

            JsonArray north = new JsonArray();
            for (double v : entry.magneticNorth()) north.add(v);
            obj.add("magnetic_north", north);

            // File goes to data/<dimension_namespace>/dimension_physics/<dimension_path>.json
            // e.g. data/galacticraft/dimension_physics/moon.json
            ResourceLocation filePath = ResourceLocation.fromNamespaceAndPath(
                    entry.dimension().getNamespace(),
                    entry.dimension().getPath()
            );
            futures.add(DataProvider.saveStable(writer, obj, pathProvider.json(filePath)));
        }

        ENTRIES.clear();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Dimension Physics for Galacticraft";
    }
}