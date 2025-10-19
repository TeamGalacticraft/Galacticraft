/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.dungeon;

import dev.galacticraft.mod.world.gen.dungeon.util.RoomRegistry;

public final class RoomDefsBootstrap {
    private RoomDefsBootstrap() {
    }

    public static void define(RoomRegistry.Registrar r) {
        // ENTRANCES
        r.entrance(id("entrance_1"), templateId("entrance_1"))
                .tags("start")
                .weight(1)
                .register();
        r.entrance(id("entrance_2"), templateId("entrance_2"))
                .tags("start")
                .weight(1)
                .register();

        // END
        r.end(id("end_1"), templateId("end_room_1"))
                .tags("boss", "goal")
                .register();

        // BASICS
        r.basic(id("room_1"), templateId("room_1"))
                .weight(1).tags("small").register();

        r.basic(id("room_2"), templateId("room_2"))
                .weight(1).tags("small").register();

        r.basic(id("room_3"), templateId("room_3"))
                .weight(1).tags("small").register();

        // QUEENS
        r.queen(id("queen_1"), templateId("queen_room_1"))
                .weight(1).tags("queen").register();

        // TREASURE (entrances only)
        r.treasure(id("treasure_1"), templateId("treasure_room_1"))
                .weight(1).tags("loot").register();
    }

    private static String templateId(String value) {
        return "galacticraft:dungeon/" + value;
    }

    private static String id(String value) {
        return "galacticraft:dungeon/" + value;
    }
}