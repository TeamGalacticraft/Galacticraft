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

package dev.galacticraft.mod.content.rocket.part.type;

import dev.galacticraft.mod.Constant.TieredRocketPart;
import dev.galacticraft.mod.content.GCRocketTypeRegistry;
import dev.galacticraft.mod.content.rocket.part.config.*;

public class GCRocketPartTypes {
    public static final GCRocketTypeRegistry ROCKET_PARTS = new GCRocketTypeRegistry();

    public static final BasicRocketConeType BASIC_CONE = ROCKET_PARTS.registerCone(TieredRocketPart.BASIC, new BasicRocketConeType(BasicRocketConeConfig.CODEC));
    public static final BasicRocketBodyType BASIC_BODY = ROCKET_PARTS.registerBody(TieredRocketPart.BASIC, new BasicRocketBodyType(BasicRocketBodyConfig.CODEC));
    public static final BasicRocketFinType BASIC_FIN = ROCKET_PARTS.registerFin(TieredRocketPart.BASIC, new BasicRocketFinType(BasicRocketFinConfig.CODEC));
    public static final BasicRocketBoosterType BASIC_BOOSTER = ROCKET_PARTS.registerBooster(TieredRocketPart.BASIC, new BasicRocketBoosterType(BasicRocketBoosterConfig.CODEC));
    public static final BasicRocketEngineType BASIC_ENGINE = ROCKET_PARTS.registerEngine(TieredRocketPart.BASIC, new BasicRocketEngineType(BasicRocketEngineConfig.CODEC));
    public static final StorageUpgradeType STORAGE_UPGRADE = ROCKET_PARTS.registerUpgrade(TieredRocketPart.STORAGE, new StorageUpgradeType());

    public static void register() {}
}
