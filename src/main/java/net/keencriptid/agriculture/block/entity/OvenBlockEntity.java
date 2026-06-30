package net.keencriptid.agriculture.block.entity;

import net.keencriptid.agriculture.block.custom.DutchOvenBlock;
import net.keencriptid.agriculture.block.custom.OvenBlock;
import net.keencriptid.agriculture.recipe.OvenRecipe;
import net.keencriptid.agriculture.screen.custom.OvenMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OvenBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int FUEL_SLOT = 2;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int burnTime = 0;

    public OvenBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OVEN_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> burnTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        progress = value;
                    case 1:
                        maxProgress = value;
                    case 2:
                        burnTime = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.agriculture.oven");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new OvenMenu(i, inventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("oven.progress", progress);
        pTag.putInt("oven.max_progress", maxProgress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("oven.progress");
        maxProgress = pTag.getInt("oven.max_progress");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack fuel = itemHandler.getStackInSlot(FUEL_SLOT);

        ItemContainerContents contents = getDutchInventory(input);
        OvenRecipe recipe = contents != null ? getMatchingRecipe(contents) : null;

        boolean canCook = recipe != null && canInsertOutput(recipe);

        // -------------------------
        // 1. HANDLE FUEL
        // -------------------------
        if (burnTime <= 0 && canCook && !fuel.isEmpty() && progress == 0) {
            burnTime = getFuelTime(fuel);
            fuel.shrink(1);
            itemHandler.setStackInSlot(FUEL_SLOT, fuel);
        }

        boolean isBurning = burnTime > 0 && canCook;

        // -------------------------
        // 2. HANDLE COOKING
        // -------------------------
        if (burnTime > 0) {
            burnTime--;

            if (canCook) {
                progress++;

                if (progress >= maxProgress) {
                    craftItem(recipe);
                    progress = 0;
                }
            }
        } else {
            if (progress > 0) {
                progress = Math.max(0, progress - 2);
            }
        }

        setChanged(level, pos, state);
        setLit(level, pos, state, burnTime > 0);
    }


    private void craftItem(OvenRecipe recipe) {
        itemHandler.setStackInSlot(OUTPUT_SLOT, recipe.getOutput().copy());

        itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private void  setLit( Level level, BlockPos pos, BlockState state, boolean lit) {
        if (state.getValue(OvenBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(OvenBlock.LIT, lit), 3);
        }
    }

    private int getFuelTime(ItemStack stack) {
        if (stack.is(Items.COAL)) return 200;
        if (stack.is(Items.CHARCOAL)) return 200;
        if (stack.is(Items.LAVA_BUCKET)) return 400;
        return 0; // important: invalid fuel = no burn
    }

    private ItemContainerContents getDutchInventory(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
        if (!(blockItem.getBlock() instanceof DutchOvenBlock)) return null;

        return stack.getComponents()
                .getOrDefault(DataComponents.CONTAINER, null);
    }

    private boolean hasRecipe() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (!(input.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DutchOvenBlock))
            return false;
        if (!input.getComponents().has(DataComponents.CONTAINER)) return false;
        ItemContainerContents dutchInv = input.getComponents().get(DataComponents.CONTAINER);
        return getMatchingRecipe(dutchInv) != null;
    }

    private boolean canInsertOutput(OvenRecipe recipe) {
        ItemStack outputSlot = itemHandler.getStackInSlot(OUTPUT_SLOT);
        ItemStack result = recipe.getOutput();

        if (outputSlot.isEmpty()) return true;

        if (!ItemStack.isSameItemSameComponents(outputSlot, result)) return false;

        return outputSlot.getCount() + result.getCount() <= outputSlot.getMaxStackSize();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();
        return maxCount >= currentCount + count;
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static final List<OvenRecipe> RECIPES = List.of(new OvenRecipe(List.of(Items.RABBIT, Items.CARROT, Items.POTATO, Items.BROWN_MUSHROOM), new ItemStack(Items.RABBIT_STEW)));

    private OvenRecipe getMatchingRecipe(ItemContainerContents inventory) {
        for (OvenRecipe recipe : RECIPES) {
            if (recipe.matches(inventory)) return recipe;

        }
        return null;
    }
}