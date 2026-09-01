/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.screen;

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.MenuData;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AirlockControllerMenu
        extends MachineMenu<AirlockControllerBlockEntity> {

    public byte proximityOpen = 0;

    public AirlockState state =
            AirlockState.NONE;

    public ProximityAccess proximityAccess =
            ProximityAccess.PUBLIC;

    public int keycardOpenSeconds =
            AirlockControllerBlockEntity.DEFAULT_KEYCARD_OPEN_SECONDS;

    public boolean structureManaged = false;

    public boolean permanentlyUnlocked = false;

    public AirlockControllerMenu(
            int syncId,
            Player player,
            AirlockControllerBlockEntity blockEntity
    ) {
        super(
                GCMenuTypes.AIRLOCK_CONTROLLER_MENU,
                syncId,
                player,
                blockEntity
        );

        this.proximityOpen =
                blockEntity.getProximityOpen();

        this.state =
                blockEntity.getAirlockState();

        this.proximityAccess =
                blockEntity.getProximityAccess();

        this.keycardOpenSeconds =
                blockEntity.getKeycardOpenSeconds();

        this.structureManaged =
                blockEntity.isStructureManaged();

        this.permanentlyUnlocked =
                blockEntity.isPermanentlyUnlocked();
    }

    public AirlockControllerMenu(
            int syncId,
            Inventory inventory,
            BlockPos pos
    ) {
        super(
                GCMenuTypes.AIRLOCK_CONTROLLER_MENU,
                syncId,
                inventory,
                pos,
                8,
                89
        );
    }

    @Override
    public void registerData(
            @NotNull MenuData data
    ) {
        super.registerData(data);

        data.registerByte(
                this.be::getProximityOpen,
                value ->
                        this.proximityOpen =
                                (byte) value
        );

        data.registerInt(
                () ->
                        this.be
                                .getAirlockState()
                                .ordinal(),
                value ->
                        this.state =
                                AirlockState.values()[value]
        );

        data.registerInt(
                () ->
                        this.be
                                .getProximityAccess()
                                .ordinal(),
                value ->
                        this.proximityAccess =
                                ProximityAccess.values()[value]
        );

        data.registerInt(
                this.be::getKeycardOpenSeconds,
                value ->
                        this.keycardOpenSeconds =
                                value
        );

        data.registerBoolean(
                this.be::isStructureManaged,
                value ->
                        this.structureManaged =
                                value
        );

        data.registerBoolean(
                this.be::isPermanentlyUnlocked,
                value ->
                        this.permanentlyUnlocked =
                                value
        );
    }
}