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

package com.hrznstudio.galacticraft.entity.rocket.tier1;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.space.RocketEntity;
import com.hrznstudio.galacticraft.api.space.RocketTier;
import com.hrznstudio.galacticraft.misc.RocketTiers;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Tier1RocketEntity extends Entity implements RocketEntity, FluidInsertable {
    private SimpleFixedFluidInv fuel = new SimpleFixedFluidInv(1, 10000);

    private float[] color = {1, 1, 1, 0};

    public Tier1RocketEntity(EntityType<Tier1RocketEntity> type, World world_1) {
        super(type, world_1);
    }

    @Override
    public float[] getColor() {
        return color;
    }

    @Override
    public void setColor(float[] rgba) {
        this.color = rgba;
    }

    @Override
    public ActionResult interactAt(PlayerEntity playerEntity_1, Vec3d vec3d_1, Hand hand_1) {
        playerEntity_1.startRiding(this);
        System.out.println("interactAt");
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean interact(PlayerEntity playerEntity_1, Hand hand_1) {
        playerEntity_1.startRiding(this);
        System.out.println("interact");
        return true;
    }


    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.fuel.fromTag(tag);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        this.fuel.toTag(tag);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "t1_rocket_spawn"), new PacketByteBuf(new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType())).writeVarInt(this.getEntityId()).writeUuid(this.uuid).writeDouble(x).writeDouble(y).writeDouble(z).writeByte((int) (pitch / 360F * 256F))).writeByte((int) (pitch / 360F * 256F))));
    }

    @Override
    public void handleFallDamage(float float_1, float float_2) {
        super.handleFallDamage(float_1, float_2);
        this.world.createExplosion(this, this.x, this.y + (double) (this.getHeight() / 16.0F), this.z, this.fuel.getTank(0).get().getAmount() / 3000.0F + 2, Explosion.DestructionType.BREAK);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return () -> Lists.asList(ItemStack.EMPTY, new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY}).iterator();
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
//        System.out.println("TICK! " + this.y);
//        double velY = Math.min(1, this.getVelocity().y + 1);

//        this.setVelocity(this.getVelocity().x, velY, this.getVelocity().z);
//        this.velocityModified = true;
//        this.velocityDirty = true;
        //this.setPosition(this.x, this.y + 1, this.z);

        if (this.y >= 200) {
            this.world.createExplosion(this, this.x, this.y + (double) (this.getHeight() / 16.0F), this.z, this.fuel.getTank(0).get().getAmount() / 3000.0F + 2, Explosion.DestructionType.BREAK);
            this.remove();
        }
        super.tick();
    }

    @Override
    public RocketTier getRocketTier() {
        return RocketTiers.tierOne;
    }

    @Override
    public SimpleFixedFluidInv getFuel() {
        return this.fuel;
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
        return this.fuel.attemptInsertion(fluid, simulation);
    }
}