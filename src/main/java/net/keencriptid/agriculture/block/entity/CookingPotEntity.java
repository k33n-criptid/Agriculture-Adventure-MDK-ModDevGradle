package net.keencriptid.agriculture.block.entity;

import net.keencriptid.agriculture.recipe.CookingPotRecipeInput;
import net.keencriptid.agriculture.recipe.ModRecipes;
import net.keencriptid.agriculture.screen.custom.CookingPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CookingPotEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(9) {

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public Recipe<CookingPotRecipeInput> getMatchingRecipes(Level level) {
        if (level == null) return null;
        var recipes = level.getRecipeManager().getAllRecipesFor(ModRecipes.COOKING_POT_TYPE.get());

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            inputs.add(inventory.getStackInSlot(i));
        }

        CookingPotRecipeInput recipeInput = new CookingPotRecipeInput(2, 3, inputs, inventory.getStackInSlot(6), inventory.getStackInSlot(8));

        for (var recipeHolder : recipes) {
            Recipe<CookingPotRecipeInput> recipe = recipeHolder.value();
            if (!recipe.matches(recipeInput, level)) continue;
            return recipe;
            }
        return null;
    }

    private int cookTime = 0;
    private int cookTimeTotal = 200;
    private boolean heated = false;

    public boolean isHeated() {
        return heated;
    }

    public void setHeated(boolean heated) {
        this.heated = heated;
        setChanged();
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getCookTimeTotal() {
        return cookTimeTotal;
    }

    public void setCookTimeTotal(int cookTimeTotal) {
        this.cookTimeTotal = cookTimeTotal;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CookingPotEntity entity) {
        if (level.isClientSide()) return;

        // Stop spam by checking if there are any ingredients at all
        boolean hasIngredients = false;
        for (int i = 0; i < 6; i++) {
            if (!entity.inventory.getStackInSlot(i).isEmpty()) {
                hasIngredients = true;
                break;
            }
        }

        // check dishware and liquid
        if (!hasIngredients && entity.inventory.getStackInSlot(6).isEmpty() && entity.inventory.getStackInSlot(8).isEmpty()) {
            entity.cookTime = 0;
            return;
        }

        // If no ingredients, do nothing
        if (!hasIngredients) {
            // Optional: reset any cook progress
            entity.cookTime = 0;
            return;
        }

        // get matching recipe
        Recipe<CookingPotRecipeInput> recipe = entity.getMatchingRecipes(level);
        if (recipe == null) {
            return;
        }

        // build recipe input & Gather the first 6 slots as input
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            inputs.add(entity.inventory.getStackInSlot(i));
        }
        CookingPotRecipeInput recipeInput = new CookingPotRecipeInput(
                2, 3,
                inputs,
                entity.inventory.getStackInSlot(6), // dishware
                entity.inventory.getStackInSlot(8)  // liquid
        );

        // step 4: assemble recipe
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        ItemStack outputStack = entity.inventory.getStackInSlot(7);

        //check if output can fit
        if (!outputStack.isEmpty() && (!(outputStack.is(result.getItem()) && ItemStack.isSameItem(outputStack, result))
                || outputStack.getCount() + result.getCount() > outputStack.getMaxStackSize())) {
            return;
        }

        // insert result
        if (outputStack.isEmpty()) {
            entity.inventory.setStackInSlot(7, result);
        } else {
            outputStack.grow(result.getCount());
        }

        // consume inputs
        for (int i = 0; i < 6; i++) {
            entity.inventory.getStackInSlot(i).shrink(1);
        }

        // handle dishware and liquid
        //consume dishware
        if (!recipeInput.getDishware().isEmpty()) {
            entity.inventory.getStackInSlot(6).shrink(1);
        }

        //consume liquid and return bucket if water bucket
        ItemStack liquid = recipeInput.getLiquid();
        if (!liquid.isEmpty()) {
            entity.inventory.getStackInSlot(8).shrink(1);
            if (liquid.is(Items.WATER_BUCKET)) {
                //return empty bucket
                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                ItemStack current = entity.inventory.getStackInSlot(8);
                if (current.isEmpty()) {
                    entity.inventory.setStackInSlot(8, emptyBucket);
                } else {
                    current.grow(1);
                }
            }
        }
        entity.setChanged();
    }


    public CookingPotEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COOKINGPOT_BE.get(), pos, blockState);
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("cookTime", cookTime);
        tag.putInt("cookTimeTotal", cookTimeTotal);
        tag.putBoolean("heated", heated);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        cookTime = tag.getInt("cookTime");
        cookTimeTotal = tag.getInt("cookTimeTotal");
        heated = tag.getBoolean("heated");
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Cooking Pot");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CookingPotMenu(i, inventory, this);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public int getCookProgressScaled(int pixels) {
        if (cookTimeTotal == 0) return 0;
        return (int) ((float) cookTime / cookTimeTotal * pixels);
    }
}
