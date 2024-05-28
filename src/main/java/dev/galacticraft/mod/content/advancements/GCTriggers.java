/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.advancements;

import dev.galacticraft.mod.Constant.Triggers;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.content.advancements.critereon.CreateSpaceStationTrigger;
import dev.galacticraft.mod.content.advancements.critereon.FindMoonBossTrigger;
import dev.galacticraft.mod.content.advancements.critereon.LaunchRocketTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;

public class GCTriggers {
    public static final GCRegistry<CriterionTrigger<?>> TRIGGERS = new GCRegistry<>(BuiltInRegistries.TRIGGER_TYPES);
    public static final LaunchRocketTrigger LAUNCH_ROCKET = TRIGGERS.register(Triggers.ROCKET_LAUNCH, new LaunchRocketTrigger());
    public static final FindMoonBossTrigger FIND_MOON_BOSS = TRIGGERS.register(Triggers.FIND_MOON_BOSS, new FindMoonBossTrigger());
    public static final CreateSpaceStationTrigger CREATE_SPACE_STATION = TRIGGERS.register(Triggers.CREATE_SPACE_STATION, new CreateSpaceStationTrigger());

    public static void register() {}
}
