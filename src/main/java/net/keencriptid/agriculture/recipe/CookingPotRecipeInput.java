package net.keencriptid.agriculture.recipe;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class CookingPotRecipeInput implements RecipeInput {

    private final int width;
    private final int height;
    private final List<ItemStack> items;

    private final ItemStack dishware;
    private final ItemStack liquid;

    private final StackedContents stackedContents = new StackedContents();
    private final int ingredientCount;

    public CookingPotRecipeInput(int width, int height, List<ItemStack> items, ItemStack dishware, ItemStack liquid) {
        this.width = width;
        this.height = height;
        this.items = items;
        this.dishware = dishware;
        this.liquid = liquid;

        int count = 0;

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                count++;
                this.stackedContents.accountStack(stack, 1);
            }
        }

        //Include dishware/liquid in count if present
        if (!dishware.isEmpty()) {
            count++;
            this.stackedContents.accountStack(dishware, 1);
        }
        if (!liquid.isEmpty()) {
            count++;
            this.stackedContents.accountStack(liquid, 1);
        }
        this.ingredientCount = count;

    }

    //required

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index); //ONLY grid
    }

    @Override
    public int size() {
        return this.items.size(); //ONLY grid size (6)
    }

    @Override
    public boolean isEmpty() {
        return this.ingredientCount == 0;
    }

    // GRID ACCESS

    public ItemStack getItem(int colum, int row) {
        return this.items.get(colum + row * this.width);
    }

    public List<ItemStack> items() {
        return items;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public StackedContents stackedContents() {
        return this.stackedContents;
    }

    // NEW EXTRA SLOTS

    public ItemStack getDishware() {
        return dishware;
    }

    public ItemStack getLiquid() {
        return liquid;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
