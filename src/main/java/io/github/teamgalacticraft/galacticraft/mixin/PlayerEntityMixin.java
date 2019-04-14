package io.github.teamgalacticraft.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.mojang.authlib.GameProfile;
import io.github.teamgalacticraft.galacticraft.accessor.GCPlayerAccessor;
import io.github.teamgalacticraft.galacticraft.container.PlayerInventoryGCContainer;
import io.github.teamgalacticraft.galacticraft.world.dimension.GalacticraftDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements GCPlayerAccessor {
    @Shadow
    @Final
    public PlayerInventory inventory;

    private PlayerInventoryGCContainer gcContainer;
    private SimpleFixedItemInv gearInventory;

    @Override
    public PlayerInventoryGCContainer getGCContainer() {
        return gcContainer;
    }

    @Override
    public SimpleFixedItemInv getGearInventory() {
        return gearInventory;
    }

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(World world_1, GameProfile gameProfile_1, CallbackInfo info) {
        this.gcContainer = new PlayerInventoryGCContainer(this.inventory, !world.isClient, (PlayerEntity) (Object) this);
        this.gearInventory = new SimpleFixedItemInv(12);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (this.world.dimension.getType() == GalacticraftDimensions.MOON) {

        }
    }
}