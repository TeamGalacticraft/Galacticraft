package dev.galacticraft.mod.content.block.entity.decoration;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FlagBlockEntity extends BannerBlockEntity {
    protected float facingRadians = 0;

    public FlagBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        super(pos, state, baseColor);
    }

    public FlagBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.saveAdditional(compound, registryLookup);

        compound.putFloat("facing", this.facingRadians);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.loadAdditional(compound, registryLookup);

        this.facingRadians = compound.getFloat("facing");
    }

    public float getFacingRadians() {
        return facingRadians;
    }

    public void setFacingRadians(float radians) {
        this.facingRadians = radians;
    }

    @Override
    public @NotNull Component getName() {
        return super.getCustomName() != null ? super.getCustomName() : Component.translatable("block.galacticraft.flag");
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack itemStack = new ItemStack(GCItems.FLAGS.get(this.getBaseColor()));
        itemStack.applyComponents(this.collectComponents());
        return itemStack;
    }

    // Overriding these two to replace the block entity type
    @Override
    public @NotNull BlockEntityType<FlagBlockEntity> getType() {
        return GCBlockEntityTypes.FLAG;
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return GCBlockEntityTypes.FLAG.isValid(state);
    }
}
