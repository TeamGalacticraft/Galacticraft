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

package dev.galacticraft.mod.content.entity.vehicle;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.screen.ParachestMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractLanderEntity extends GCFueledVehicleEntity implements IgnoreShift, ControllableEntity, HasCustomInventoryScreen, ExtendedScreenHandlerFactory {

    // **************************************** FIELDS ****************************************

    // **************************************** CONSTRUCTOR ****************************************

    public AbstractLanderEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // **************************************** DATA ****************************************

    public Item getDropItem() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    public float getDamageMultiplier() {
        return 5;
    }

    @Override
    public float getMaxDamage() {
        return 100;
    }

    public abstract boolean shouldMove();

    public abstract boolean shouldSpawnParticles();

    @Override
    public boolean shouldIgnoreShiftExit() {
        return !onGround();
    }

    // **************************************** FUEL ****************************************

    @Override
    public long getFuelTankCapacity() {
        return 100L;
    }

    // **************************************** INTERACTION ****************************************

    @Override
    public boolean isPickable() { // Required to interact with the entity
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.onGround()) {
            return InteractionResult.FAIL;
        }
        if (this.getPassengers().isEmpty()) {
            this.openCustomInventoryScreen(player);
        } else {
            this.ejectPassengers();
        }
        return InteractionResult.SUCCESS;
    }

    // **************************************** TICK ****************************************

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        if (shouldMove())
            super.move(movementType, movement);
    }

    // **************************************** INVENTORY ****************************************

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ParachestMenu(containerId, inventory, this);
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBoolean(false);
        buf.writeVarInt(this.inventory.size());
    }

}
