package dev.galacticraft.mod.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class PlaceholderItemStorage extends SingleItemStorage {
    public void setItem(@NotNull Item item) {
        this.variant = ItemVariant.of(item);
        this.amount = 1;
    }

    @Override
    public long extract(ItemVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        return super.extract(extractedVariant, maxAmount, transaction);
    }

    @Override
    protected long getCapacity(ItemVariant variant) {
        return variant.isBlank() ? 64 : variant.getItem().getMaxStackSize();
    }
}
