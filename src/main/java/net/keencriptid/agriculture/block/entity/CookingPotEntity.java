package net.keencriptid.agriculture.block.entity;

import net.keencriptid.agriculture.recipe.CookingPotRecipe;
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
import net.minecraft.world.item.Item;
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
        System.out.println("=== CHECKING COOKING POT RECIPES ===");
        System.out.println("Total recipes found: " + recipes.size());

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            inputs.add(stack);
            System.out.println("Grid Slot " + i + ": " + stack);
        }
        ItemStack dishware = inventory.getStackInSlot(8);
        ItemStack liquid = inventory.getStackInSlot(6);

        System.out.println("Dishware: " + inventory.getStackInSlot(8));
        System.out.println("Liquid: " + inventory.getStackInSlot(6));

        CookingPotRecipeInput recipeInput = new CookingPotRecipeInput(3, 2, inputs, inventory.getStackInSlot(8), inventory.getStackInSlot(6));

        for (var recipeHolder : recipes) {
            Recipe<CookingPotRecipeInput> recipe = recipeHolder.value();

            System.out.println("---- Trying Recipe ----");
            System.out.println("Result: " + ((CookingPotRecipe)recipe).getResult());
            System.out.println("Recipe Dishware: " + ((CookingPotRecipe)recipe).getDishware());
            System.out.println("Recipe Liquid: " + ((CookingPotRecipe)recipe).getLiquid());

            if (recipe.matches(recipeInput, level)) {
                System.out.println("MATCH FOUND ✅");
                return recipe;
            } else {
                System.out.println("No match ❌");
            }
        }

        System.out.println("NO MATCHING RECIPE FOUND ❌❌❌");
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

        // Check if there are any ingredients
        boolean hasIngredients = false;
        for (int i = 0; i < 6; i++) {
            if (!entity.inventory.getStackInSlot(i).isEmpty()) {
                hasIngredients = true;
                break;
            }
        }

        // If nothing to cook and no liquid/dishware, reset cookTime and exit
        if (!hasIngredients && entity.inventory.getStackInSlot(6).isEmpty() && entity.inventory.getStackInSlot(8).isEmpty()) {
            entity.cookTime = 0;
            return;
        }

        Recipe<CookingPotRecipeInput> recipe = entity.getMatchingRecipes(level);
        if (recipe == null) return;

        // Build recipe input
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            inputs.add(entity.inventory.getStackInSlot(i));
        }
        CookingPotRecipeInput recipeInput = new CookingPotRecipeInput(
                3, 2,
                inputs,
                entity.inventory.getStackInSlot(8), // dishware
                entity.inventory.getStackInSlot(6)  // liquid
        );

        // Assemble result
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        ItemStack outputStack = entity.inventory.getStackInSlot(7);

        // Check if output can fit
        if (!outputStack.isEmpty() && (!(outputStack.is(result.getItem()) && ItemStack.isSameItem(outputStack, result))
                || outputStack.getCount() + result.getCount() > outputStack.getMaxStackSize())) {
            return;
        }

        // Insert result
        if (outputStack.isEmpty()) {
            entity.inventory.setStackInSlot(7, result);
        } else {
            outputStack.grow(result.getCount());
        }

        // Consume ingredients
        for (int i = 0; i < 6; i++) {
            ItemStack stack = entity.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) stack.shrink(1);
        }

        // Consume liquid
        ItemStack liquid = entity.inventory.getStackInSlot(6);
        if (!liquid.isEmpty()) {
            boolean isWaterBucket = liquid.is(Items.WATER_BUCKET);
            liquid.shrink(1);
            // Return bucket if water bucket
            if (isWaterBucket) {
                ItemStack bucketStack = entity.inventory.getStackInSlot(6);
                if (bucketStack.isEmpty()) {
                    entity.inventory.setStackInSlot(6, new ItemStack(Items.BUCKET));
                } else {
                    bucketStack.grow(1);
                }
            }
        }

        // Consume dishware
        ItemStack dishware = entity.inventory.getStackInSlot(8);
        if (!dishware.isEmpty()) dishware.shrink(1);

        entity.setChanged();
    }

    public List<ItemStack> getRemainingItems(CookingPotRecipeInput input, CookingPotRecipe recipe) {
        List<ItemStack> remaining = new ArrayList<>();

        for (int i = 0; i <6; i++) {
            ItemStack stack = input.getItems().get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.WATER_BUCKET) {
                remaining.add(new ItemStack(Items.BUCKET));
            } else {
                remaining.add(ItemStack.EMPTY);
            }
        }
        remaining.add(ItemStack.EMPTY); //slot 6 liquid
        remaining.add(ItemStack.EMPTY); //slot 7 output
        remaining.add(ItemStack.EMPTY); //slot 8 dishware
        return remaining;
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
