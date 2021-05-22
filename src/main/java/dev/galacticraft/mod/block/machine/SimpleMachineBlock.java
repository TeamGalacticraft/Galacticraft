package dev.galacticraft.mod.block.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.BlockView;

import java.util.function.Supplier;

public class SimpleMachineBlock extends MachineBlock {
    public static final Settings MACHINE_DEFAULT_SETTINGS = FabricBlockSettings.of(Material.METAL)
            .strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL);

    private final Supplier<? extends MachineBlockEntity> supplier;
    private final Text information;

    public static SimpleMachineBlock create(Supplier<? extends MachineBlockEntity> supplier, Text information) {
        return new SimpleMachineBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS), supplier, information);
    }

    public static SimpleMachineBlock create(Supplier<? extends MachineBlockEntity> supplier, String key) {
        return new SimpleMachineBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS), supplier, new TranslatableText(key).setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    protected SimpleMachineBlock(Settings settings, Supplier<? extends MachineBlockEntity> supplier, Text information) {
        super(settings);
        this.supplier = supplier;
        this.information = information;
    }

    @Override
    public MachineBlockEntity createBlockEntity(BlockView view) {
        return this.supplier.get();
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean advanced) {
        return this.information;
    }
}
