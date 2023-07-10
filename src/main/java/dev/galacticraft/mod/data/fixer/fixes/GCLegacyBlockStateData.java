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

package dev.galacticraft.mod.data.fixer.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import org.slf4j.Logger;

public class GCLegacyBlockStateData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Dynamic<?>[] MAP = new Dynamic[4096];
    private static final Dynamic<?>[] BLOCK_DEFAULTS = new Dynamic[256];
    private static final Object2IntMap<Dynamic<?>> ID_BY_OLD = DataFixUtils.make(
            new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
    );
    private static final Object2IntMap<String> ID_BY_OLD_NAME = DataFixUtils.make(
            new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
    );
    static final String FILTER_ME = "%%FILTER_ME%%";

    private static void register(int i, String string, String... strings) {
        Dynamic<?> dynamic = parse(string);
        MAP[i] = dynamic;
        int j = i >> 4;
        if (BLOCK_DEFAULTS[j] == null) {
            BLOCK_DEFAULTS[j] = dynamic;
        }

        for(String string2 : strings) {
            Dynamic<?> dynamic2 = parse(string2);
            String string3 = dynamic2.get("Name").asString("");
            ID_BY_OLD_NAME.putIfAbsent(string3, i);
            ID_BY_OLD.put(dynamic2, i);
        }
    }

    private static void finalizeMaps() {
        for(int i = 0; i < MAP.length; ++i) {
            if (MAP[i] == null) {
                MAP[i] = BLOCK_DEFAULTS[i >> 4];
            }
        }
    }

    public static Dynamic<?> upgradeBlockStateTag(Dynamic<?> dynamic) {
        int i = ID_BY_OLD.getInt(dynamic);
        if (i >= 0 && i < MAP.length) {
            Dynamic<?> dynamic2 = MAP[i];
            return dynamic2 == null ? dynamic : dynamic2;
        } else {
            return dynamic;
        }
    }

    public static String upgradeBlock(String string) {
        int i = ID_BY_OLD_NAME.getInt(string);
        if (i >= 0 && i < MAP.length) {
            Dynamic<?> dynamic = MAP[i];
            return dynamic == null ? string : dynamic.get("Name").asString("");
        } else {
            return string;
        }
    }

    public static String upgradeBlock(int i) {
        if (i >= 0 && i < MAP.length) {
            Dynamic<?> dynamic = MAP[i];
            return dynamic == null ? "minecraft:air" : dynamic.get("Name").asString("");
        } else {
            return "minecraft:air";
        }
    }

    public static Dynamic<?> parse(String string) {
        try {
            return new Dynamic<>(NbtOps.INSTANCE, TagParser.parseTag(string.replace('\'', '"')));
        } catch (Exception var2) {
            LOGGER.error("Parsing {}", string, var2);
            throw new RuntimeException(var2);
        }
    }

    public static Dynamic<?> getTag(int i) {
        Dynamic<?> dynamic = null;
        if (i >= 0 && i < MAP.length) {
            dynamic = MAP[i];
        }

        return dynamic == null ? MAP[0] : dynamic;
    }

    public static void init() {
        // "{Name:'minecraft:stone'}", "{Name:'minecraft:stone',Properties:{variant:'stone'}}"
        register(288, "{Name:'galacticraft:rocket_launch_pad'}", "{Name:'galacticraftcore:block_multi',Properties:{rendertyoe:'0',type:'rocket_pad'}}");
        finalizeMaps();
    }
}
