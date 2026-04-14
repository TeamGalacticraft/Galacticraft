/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.miccore;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class MicdoodleAccessTransformerDeObf extends AccessTransformer
{

    public MicdoodleAccessTransformerDeObf() throws IOException
    {
        super("META-INF/accesstransformer_deobf.cfg");
    }
}
