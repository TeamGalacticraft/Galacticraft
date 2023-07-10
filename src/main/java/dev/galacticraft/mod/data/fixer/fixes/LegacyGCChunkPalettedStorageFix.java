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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.*;

public class LegacyGCChunkPalettedStorageFix extends DataFix {
    private static final int NORTH_WEST_MASK = 128;
    private static final int WEST_MASK = 64;
    private static final int SOUTH_WEST_MASK = 32;
    private static final int SOUTH_MASK = 16;
    private static final int SOUTH_EAST_MASK = 8;
    private static final int EAST_MASK = 4;
    private static final int NORTH_EAST_MASK = 2;
    private static final int NORTH_MASK = 1;
    static final Logger LOGGER = LogUtils.getLogger();
    static final BitSet VIRTUAL = new BitSet(256);
    static final BitSet FIX = new BitSet(256);
    static final Dynamic<?> AIR = BlockStateData.getTag(0);
    private static final int SIZE = 4096;

    public LegacyGCChunkPalettedStorageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public static String getName(Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }

    public static String getProperty(Dynamic<?> dynamic, String string) {
        return dynamic.get("Properties").get(string).asString("");
    }

    public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> crudeIncrementalIntIdentityHashBiMap, Dynamic<?> dynamic) {
        int i = crudeIncrementalIntIdentityHashBiMap.getId(dynamic);
        if (i == -1) {
            i = crudeIncrementalIntIdentityHashBiMap.add(dynamic);
        }

        return i;
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        Optional<? extends Dynamic<?>> optional = dynamic.get("Level").result();
        return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()
                ? dynamic.set("Level", new LegacyGCChunkPalettedStorageFix.UpgradeChunk((Dynamic<?>)optional.get()).write())
                : dynamic;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.CHUNK);
        Type<?> type2 = this.getOutputSchema().getType(References.CHUNK);
        return this.writeFixAndRead("LegacyGCChunkPalettedStorageFix", type, type2, this::fix);
    }

    public static int getSideMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            if (bl2) {
                i |= 2;
            } else if (bl) {
                i |= 128;
            } else {
                i |= 1;
            }
        } else if (bl4) {
            if (bl) {
                i |= 32;
            } else if (bl2) {
                i |= 8;
            } else {
                i |= 16;
            }
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 64;
        }

        return i;
    }

    static {
        FIX.set(2);
        FIX.set(3);
        FIX.set(110);
        FIX.set(140);
        FIX.set(144);
        FIX.set(25);
        FIX.set(86);
        FIX.set(26);
        FIX.set(176);
        FIX.set(177);
        FIX.set(175);
        FIX.set(64);
        FIX.set(71);
        FIX.set(193);
        FIX.set(194);
        FIX.set(195);
        FIX.set(196);
        FIX.set(197);
        VIRTUAL.set(54);
        VIRTUAL.set(146);
        VIRTUAL.set(25);
        VIRTUAL.set(26);
        VIRTUAL.set(51);
        VIRTUAL.set(53);
        VIRTUAL.set(67);
        VIRTUAL.set(108);
        VIRTUAL.set(109);
        VIRTUAL.set(114);
        VIRTUAL.set(128);
        VIRTUAL.set(134);
        VIRTUAL.set(135);
        VIRTUAL.set(136);
        VIRTUAL.set(156);
        VIRTUAL.set(163);
        VIRTUAL.set(164);
        VIRTUAL.set(180);
        VIRTUAL.set(203);
        VIRTUAL.set(55);
        VIRTUAL.set(85);
        VIRTUAL.set(113);
        VIRTUAL.set(188);
        VIRTUAL.set(189);
        VIRTUAL.set(190);
        VIRTUAL.set(191);
        VIRTUAL.set(192);
        VIRTUAL.set(93);
        VIRTUAL.set(94);
        VIRTUAL.set(101);
        VIRTUAL.set(102);
        VIRTUAL.set(160);
        VIRTUAL.set(106);
        VIRTUAL.set(107);
        VIRTUAL.set(183);
        VIRTUAL.set(184);
        VIRTUAL.set(185);
        VIRTUAL.set(186);
        VIRTUAL.set(187);
        VIRTUAL.set(132);
        VIRTUAL.set(139);
        VIRTUAL.set(199);
    }

    static class DataLayer {
        private static final int SIZE = 2048;
        private static final int NIBBLE_SIZE = 4;
        private final byte[] data;

        public DataLayer() {
            this.data = new byte[2048];
        }

        public DataLayer(byte[] bs) {
            this.data = bs;
            if (bs.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
            }
        }

        public int get(int i, int j, int k) {
            int l = this.getPosition(j << 8 | k << 4 | i);
            return this.isFirst(j << 8 | k << 4 | i) ? this.data[l] & 15 : this.data[l] >> 4 & 15;
        }

        private boolean isFirst(int i) {
            return (i & 1) == 0;
        }

        private int getPosition(int i) {
            return i >> 1;
        }
    }

    public static enum Direction {
        DOWN(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.Y),
        UP(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.Y),
        NORTH(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.Z),
        SOUTH(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.Z),
        WEST(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.X),
        EAST(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, LegacyGCChunkPalettedStorageFix.Direction.Axis.X);

        private final LegacyGCChunkPalettedStorageFix.Direction.Axis axis;
        private final LegacyGCChunkPalettedStorageFix.Direction.AxisDirection axisDirection;

        private Direction(LegacyGCChunkPalettedStorageFix.Direction.AxisDirection axisDirection, LegacyGCChunkPalettedStorageFix.Direction.Axis axis) {
            this.axis = axis;
            this.axisDirection = axisDirection;
        }

        public LegacyGCChunkPalettedStorageFix.Direction.AxisDirection getAxisDirection() {
            return this.axisDirection;
        }

        public LegacyGCChunkPalettedStorageFix.Direction.Axis getAxis() {
            return this.axis;
        }

        public static enum Axis {
            X,
            Y,
            Z;
        }

        public static enum AxisDirection {
            POSITIVE(1),
            NEGATIVE(-1);

            private final int step;

            private AxisDirection(int j) {
                this.step = j;
            }

            public int getStep() {
                return this.step;
            }
        }
    }

    static class Section {
        private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = CrudeIncrementalIntIdentityHashBiMap.create(32);
        private final List<Dynamic<?>> listTag;
        private final Dynamic<?> section;
        private final boolean hasData;
        final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap<>();
        final IntList update = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
        private final int[] buffer = new int[4096];

        public Section(Dynamic<?> dynamic) {
            this.listTag = Lists.<Dynamic<?>>newArrayList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasData = dynamic.get("Blocks").result().isPresent();
        }

        public Dynamic<?> getBlock(int i) {
            if (i >= 0 && i <= 4095) {
                Dynamic<?> dynamic = this.palette.byId(this.buffer[i]);
                return dynamic == null ? LegacyGCChunkPalettedStorageFix.AIR : dynamic;
            } else {
                return LegacyGCChunkPalettedStorageFix.AIR;
            }
        }

        public void setBlock(int i, Dynamic<?> dynamic) {
            if (this.seen.add(dynamic)) {
                this.listTag.add("%%FILTER_ME%%".equals(LegacyGCChunkPalettedStorageFix.getName(dynamic)) ? LegacyGCChunkPalettedStorageFix.AIR : dynamic);
            }

            this.buffer[i] = LegacyGCChunkPalettedStorageFix.idFor(this.palette, dynamic);
        }

        public int upgrade(int i) {
            if (!this.hasData) {
                return i;
            } else {
                ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
                LegacyGCChunkPalettedStorageFix.DataLayer dataLayer = this.section
                        .get("Data")
                        .asByteBufferOpt()
                        .map(byteBufferx -> new DataLayer(DataFixUtils.toArray(byteBufferx)))
                        .result()
                        .orElseGet(DataLayer::new);
                LegacyGCChunkPalettedStorageFix.DataLayer dataLayer2 = this.section
                        .get("Add")
                        .asByteBufferOpt()
                        .map(byteBufferx -> new DataLayer(DataFixUtils.toArray(byteBufferx)))
                        .result()
                        .orElseGet(DataLayer::new);
                this.seen.add(LegacyGCChunkPalettedStorageFix.AIR);
                LegacyGCChunkPalettedStorageFix.idFor(this.palette, LegacyGCChunkPalettedStorageFix.AIR);
                this.listTag.add(LegacyGCChunkPalettedStorageFix.AIR);

                for(int j = 0; j < 4096; ++j) {
                    int k = j & 15;
                    int l = j >> 8 & 15;
                    int m = j >> 4 & 15;
                    int n = dataLayer2.get(k, l, m) << 12 | (byteBuffer.get(j) & 255) << 4 | dataLayer.get(k, l, m);
                    if (LegacyGCChunkPalettedStorageFix.FIX.get(n >> 4)) {
                        this.addFix(n >> 4, j);
                    }

                    if (LegacyGCChunkPalettedStorageFix.VIRTUAL.get(n >> 4)) {
                        int o = LegacyGCChunkPalettedStorageFix.getSideMask(k == 0, k == 15, m == 0, m == 15);
                        if (o == 0) {
                            this.update.add(j);
                        } else {
                            i |= o;
                        }
                    }

                    this.setBlock(j, GCLegacyBlockStateData.getTag(n));
                }

                return i;
            }
        }

        private void addFix(int i, int j) {
            IntList intList = this.toFix.get(i);
            if (intList == null) {
                intList = new IntArrayList();
                this.toFix.put(i, intList);
            }

            intList.add(j);
        }

        public Dynamic<?> write() {
            Dynamic<?> dynamic = this.section;
            if (!this.hasData) {
                return dynamic;
            } else {
                dynamic = dynamic.set("Palette", dynamic.createList(this.listTag.stream()));
                int i = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
                PackedBitStorage packedBitStorage = new PackedBitStorage(i, 4096);

                for(int j = 0; j < this.buffer.length; ++j) {
                    packedBitStorage.set(j, this.buffer[j]);
                }

                dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedBitStorage.getRaw())));
                dynamic = dynamic.remove("Blocks");
                dynamic = dynamic.remove("Data");
                return dynamic.remove("Add");
            }
        }
    }

    static final class UpgradeChunk {
        private int sides;
        private final LegacyGCChunkPalettedStorageFix.Section[] sections = new LegacyGCChunkPalettedStorageFix.Section[16];
        private final Dynamic<?> level;
        private final int x;
        private final int z;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

        public UpgradeChunk(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.x = dynamic.get("xPos").asInt(0) << 4;
            this.z = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamicx -> {
                int ix = dynamicx.get("x").asInt(0) - this.x & 15;
                int jx = dynamicx.get("y").asInt(0);
                int k = dynamicx.get("z").asInt(0) - this.z & 15;
                int l = jx << 8 | k << 4 | ix;
                if (this.blockEntities.put(l, dynamicx) != null) {
                    LegacyGCChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, ix, jx, k);
                }
            }));

            dynamic.get("Sections").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamicx -> {
                LegacyGCChunkPalettedStorageFix.Section sectionx = new LegacyGCChunkPalettedStorageFix.Section(dynamicx);
                this.sides = sectionx.upgrade(this.sides);
                this.sections[sectionx.y] = sectionx;
            }));

            for(LegacyGCChunkPalettedStorageFix.Section section : this.sections) {
                if (section != null) {
                    for(java.util.Map.Entry<Integer, IntList> entry : section.toFix.entrySet()) {
                        int i = section.y << 12;
                        switch(entry.getKey()) {

                        }
                    }
                }
            }
        }

        @Nullable
        private Dynamic<?> getBlockEntity(int i) {
            return this.blockEntities.get(i);
        }

        @Nullable
        private Dynamic<?> removeBlockEntity(int i) {
            return this.blockEntities.remove(i);
        }

        public static int relative(int i, LegacyGCChunkPalettedStorageFix.Direction direction) {
            switch(direction.getAxis()) {
                case X:
                    int j = (i & 15) + direction.getAxisDirection().getStep();
                    return j >= 0 && j <= 15 ? i & -16 | j : -1;
                case Y:
                    int k = (i >> 8) + direction.getAxisDirection().getStep();
                    return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
                case Z:
                    int l = (i >> 4 & 15) + direction.getAxisDirection().getStep();
                    return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
                default:
                    return -1;
            }
        }

        private void setBlock(int i, Dynamic<?> dynamic) {
            if (i >= 0 && i <= 65535) {
                LegacyGCChunkPalettedStorageFix.Section section = this.getSection(i);
                if (section != null) {
                    section.setBlock(i & 4095, dynamic);
                }
            }
        }

        @Nullable
        private LegacyGCChunkPalettedStorageFix.Section getSection(int i) {
            int j = i >> 12;
            return j < this.sections.length ? this.sections[j] : null;
        }

        public Dynamic<?> getBlock(int i) {
            if (i >= 0 && i <= 65535) {
                LegacyGCChunkPalettedStorageFix.Section section = this.getSection(i);
                return section == null ? LegacyGCChunkPalettedStorageFix.AIR : section.getBlock(i & 4095);
            } else {
                return LegacyGCChunkPalettedStorageFix.AIR;
            }
        }

        public Dynamic<?> write() {
            Dynamic<?> dynamic = this.level;
            if (this.blockEntities.isEmpty()) {
                dynamic = dynamic.remove("TileEntities");
            } else {
                dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            }

            Dynamic<?> dynamic2 = dynamic.emptyMap();
            List<Dynamic<?>> list = Lists.<Dynamic<?>>newArrayList();

            for(LegacyGCChunkPalettedStorageFix.Section section : this.sections) {
                if (section != null) {
                    list.add(section.write());
                    dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.update.toIntArray())));
                }
            }

            Dynamic<?> dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sides));
            dynamic3 = dynamic3.set("Indices", dynamic2);
            return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
        }
    }
}