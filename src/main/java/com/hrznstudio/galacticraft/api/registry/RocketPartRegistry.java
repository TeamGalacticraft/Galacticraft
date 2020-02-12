/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.registry;

import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketPartRegistry extends SimpleRegistry<RocketPart> {

    private final HashMap<RocketPartType, List<RocketPart>> parts = new HashMap<>();

    public RocketPartRegistry() {
        super();
        for (RocketPartType type : RocketPartType.values()) {
            parts.put(type, new ArrayList<>());
        }
    }

    @Override
    public <V extends RocketPart> V set(int i, Identifier identifier, V object) {
        Validate.notNull(object.getBlockToRender().asItem(), "Rocket render block must not be null! ID: " + identifier);
        Validate.notNull(identifier);
        Validate.notNull(object);
        parts.get(object.getType()).add(object);
        return super.set(i, identifier, object);
    }

    public List<RocketPart> getPartsForType(RocketPartType type) {
        return parts.get(type);
    }

    public List<RocketPart> getAllEntries() {
        return new ArrayList<>(entries.values());
    }
}
