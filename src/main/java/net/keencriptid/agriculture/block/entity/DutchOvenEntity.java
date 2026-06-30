package net.keencriptid.agriculture.block.entity;

import net.keencriptid.agriculture.screen.custom.DutchOvenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class DutchOvenEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    private static final int[] SLOTS = new int[9];
    static {
        for (int i = 0; i < 9; i++) SLOTS[i] = i;
    }

    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.get(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            items.set(slot, stack);
            setChanged();
        }

        @Override
        public int getSlots() {
            return items.size();
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private int openCount = 0;
    protected final ContainerData data;

    public DutchOvenEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUTCH_OVEN.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return 0;
            }

            @Override
            public void set(int i, int i1) {

            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return itemStack.canFitInsideContainerItems();
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return true;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("Dutch Oven");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new DutchOvenMenu(i, inventory, this, this.data);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.openCount = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (openCount < 0) openCount = 0;
            ++openCount;
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --openCount;
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, openCount);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
