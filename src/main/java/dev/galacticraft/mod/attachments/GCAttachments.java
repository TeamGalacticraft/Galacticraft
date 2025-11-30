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

package dev.galacticraft.mod.attachments;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Constant.Attachments;
import dev.galacticraft.mod.machine.SealerManager;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@SuppressWarnings("UnstableApiUsage")
public final class GCAttachments {
    public static final AttachmentType<GCServerPlayer> SERVER_PLAYER = AttachmentRegistry.create(Constant.id(Attachments.SERVER_PLAYER), builder -> builder
            .persistent(GCServerPlayer.CODEC)
            .copyOnDeath());

    public static final AttachmentType<GCClientPlayer> CLIENT_PLAYER = AttachmentRegistry.create(Constant.id(Attachments.CLIENT_PLAYER));

    public static final AttachmentType<SealerManager> SEALER_MANAGER = AttachmentRegistry.create(Constant.id(Attachments.SEALER_MANAGER));

    public static final AttachmentType<GCFootprintTracker> FOOTPRINT_TRACKER = AttachmentRegistry.createDefaulted(
            Constant.id(Attachments.FOOTPRINT_TRACKER), GCFootprintTracker::new);

    public static final AttachmentType<FootprintManager> FOOTPRINT_MANAGER = AttachmentRegistry.create(Constant.id(Attachments.FOOTPRINT_MANAGER));

    public static void init() {
    }
}
