package dev.galacticraft.mod.block.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MultiBlockMachineBlock;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Supplier;

public class SimpleMultiBlockMachineBlock<T extends MachineBlockEntity, P extends BlockWithEntity> extends MultiBlockMachineBlock<T> {
    private final List<BlockPos> parts;
    private final Supplier<T> supplier;
    private final Text information;
    private final BlockState partState;

    /**
     * Note: BlockEntity of the partBlock must implement {@link MultiBlockPart}
     */
    public static <T extends MachineBlockEntity, P extends BlockWithEntity> SimpleMultiBlockMachineBlock<T, P> create(Supplier<BlockEntityType<T>> type, List<BlockPos> parts, P partBlock, String key) {
        return new SimpleMultiBlockMachineBlock<>(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS), parts, () -> type.get().instantiate(), partBlock, new TranslatableText(key).setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    protected SimpleMultiBlockMachineBlock(Settings settings, List<BlockPos> parts, Supplier<T> supplier, P partBlock, Text information) {
        super(settings);
        this.parts = parts;
        this.supplier = supplier;
        this.information = information;
        this.partState = partBlock.getDefaultState();
    }

    @Override
    public T createBlockEntity(BlockView view) {
        return this.supplier.get();
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean advanced) {
        return this.information;
    }

    @Override
    public void onMultiBlockPlaced(World world, BlockPos pos, BlockState state) {
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.toImmutable().add(pos);
            world.setBlockState(otherPart, this.partState);

            BlockEntity part = world.getBlockEntity(otherPart);
            assert part != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPart) part).setBasePos(pos);
            part.markDirty();
        }
    }

    @Override
    public @Unmodifiable List<BlockPos> getOtherParts(BlockState state) {
        return this.parts;
    }
}
