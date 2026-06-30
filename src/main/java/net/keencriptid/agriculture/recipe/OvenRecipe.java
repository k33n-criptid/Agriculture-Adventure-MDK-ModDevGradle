package net.keencriptid.agriculture.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public class OvenRecipe {
    private final List<Item> ingredients;
    private final ItemStack output;

    public OvenRecipe(List<Item> ingredients, ItemStack output) {
        this.ingredients = ingredients;
        this.output = output;
    }

    public List<Item> getIngredients() {
        return ingredients;
    }

    public ItemStack getOutput(){
        return output;
    }

    public boolean matches(ItemContainerContents inventory) {
        boolean[] found = new boolean[ingredients.size()];

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            for (int j = 0; j < ingredients.size(); j++) {
                if (!found[j] && stack.is(ingredients.get(j))) {
                    found[j] = true;
                    break;
                }
            }
        }
        for (boolean b : found) if (!b) return false;
        return true;
    }
}
