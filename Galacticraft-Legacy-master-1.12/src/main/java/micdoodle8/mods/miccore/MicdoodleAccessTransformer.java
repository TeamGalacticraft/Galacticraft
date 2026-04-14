/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.miccore;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class MicdoodleAccessTransformer extends AccessTransformer
{

    public MicdoodleAccessTransformer() throws IOException
    {
        super("META-INF/accesstransformer.cfg");
    }
}
