package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.api.item.Schematic;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RocketWorkbenchBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public final VariableSizedContainer cone = new VariableSizedContainer(0);
    public final VariableSizedContainer body = new VariableSizedContainer(0);
    public final VariableSizedContainer fins = new VariableSizedContainer(0);
    public final VariableSizedContainer booster = new VariableSizedContainer(0);
    public final VariableSizedContainer bottom = new VariableSizedContainer(0);
    public final VariableSizedContainer upgrades = new VariableSizedContainer(0);

    public final Container inventory = new SimpleContainer(5) {
        @Override
        public boolean canPlaceItem(int i, ItemStack stack) {
            if (stack.getItem() instanceof Schematic schematic) {
                RocketPartRecipe<?, ?> recipe = schematic.getRecipe(RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_PART_RECIPE), stack);
                if (recipe != null) {
                    return switch (i) {
                        case 0 -> recipe.output().isFor(RocketRegistries.ROCKET_CONE);
                        case 1 -> recipe.output().isFor(RocketRegistries.ROCKET_BODY);
                        case 2 -> recipe.output().isFor(RocketRegistries.ROCKET_FIN);
                        case 3 -> recipe.output().isFor(RocketRegistries.ROCKET_BOOSTER);
                        case 4 -> recipe.output().isFor(RocketRegistries.ROCKET_BOTTOM);
                        default -> stack.isEmpty();
                    };
                }
            }

            return stack.isEmpty();
        }

        @Override
        public void setChanged() {
            super.setChanged();
            Registry<RocketPartRecipe<?, ?>> registry = RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_PART_RECIPE);

            this.updateSlots(registry, 0, RocketRegistries.ROCKET_CONE, cone);
            this.updateSlots(registry, 1, RocketRegistries.ROCKET_BODY, body);
            this.updateSlots(registry, 2, RocketRegistries.ROCKET_FIN, fins);
            this.updateSlots(registry, 3, RocketRegistries.ROCKET_BOOSTER, booster);
            this.updateSlots(registry, 4, RocketRegistries.ROCKET_BOTTOM, bottom);

            ItemStack stack = this.getItem(1);
            if (stack.getItem() instanceof Schematic schematic) {
                RocketPartRecipe<?, ?> recipe = schematic.getRecipe(registry, stack);
                if (recipe != null) {
                    recipe.output().cast(RocketRegistries.ROCKET_BODY)
                            .flatMap(key -> RocketWorkbenchBlockEntity.this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_BODY).getOptional(key))
                            .ifPresent(rocketBody -> upgrades.resize(rocketBody.getUpgradeCapacity()));
                }
            }
        }

        private <T> void updateSlots(Registry<RocketPartRecipe<?, ?>> registry, int slot, ResourceKey<Registry<T>> resource, VariableSizedContainer container) {
            ItemStack stack = this.getItem(slot);
            if (stack.getItem() instanceof Schematic schematic) {
                RocketPartRecipe<?, ?> recipe = schematic.getRecipe(registry, stack);
                if (recipe != null && recipe.output().isFor(resource)) {
                    container.resize(recipe.slots().size());
                } else {
                    container.resize(0);
                }
            } else {
                container.resize(0);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return player.position().distanceToSqr(Vec3.atCenterOf(RocketWorkbenchBlockEntity.this.worldPosition)) < (8 * 8);
        }
    };

    public RocketWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.ROCKET_WORKBENCH, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag list = new ListTag();
        for (int i = 0; i < 5; i++) {
            CompoundTag item = new CompoundTag();
            this.inventory.getItem(i).save(item);
            list.add(item);
        }
        tag.put("Schematics", list);
        tag.put("Cone", this.cone.toTag());
        tag.put("Body", this.body.toTag());
        tag.put("Fins", this.fins.toTag());
        tag.put("Booster", this.booster.toTag());
        tag.put("Bottom", this.bottom.toTag());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag list = tag.getList("Schematics", Tag.TAG_COMPOUND);
        for (int i = 0; i < 5; i++) {
            this.inventory.setItem(i, ItemStack.of(list.getCompound(i)));
        }
        this.cone.readTag(tag.getCompound("Cone"));
        this.body.readTag(tag.getCompound("Body"));
        this.fins.readTag(tag.getCompound("Fins"));
        this.booster.readTag(tag.getCompound("Booster"));
        this.bottom.readTag(tag.getCompound("Bottom"));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Rocket workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RocketWorkbenchMenu(i, this, inventory);
    }
}
