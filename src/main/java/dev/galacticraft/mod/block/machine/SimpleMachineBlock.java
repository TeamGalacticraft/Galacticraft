package dev.galacticraft.mod.block.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.BlockView;

import java.util.function.Supplier;

public class SimpleMachineBlock<T extends MachineBlockEntity> extends MachineBlock<T> {
    public static final Settings MACHINE_DEFAULT_SETTINGS = FabricBlockSettings.of(Material.METAL)
            .strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL);

    private final Supplier<T> supplier;
    private final Text information;

    public static <T extends MachineBlockEntity> SimpleMachineBlock<T> create(BlockEntityType<T> type, String key) {
        return new SimpleMachineBlock<>(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS), type::instantiate, new TranslatableText(key).setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    protected SimpleMachineBlock(Settings settings, Supplier<T> supplier, Text information) {
        super(settings);
        this.supplier = supplier;
        this.information = information;
    }

    @Override
    public T createBlockEntity(BlockView view) {
        return this.supplier.get();
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean advanced) {
        return this.information;
    }
}
