package net.keencriptid.agriculture.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;


public class CookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    // read in a json file
    // inputItem & output ==> read from json file
    // CookingPotRecipeInput ==> Inventory of block entity

    private final String group;
    private final List<ItemStack> ingredients;  // 2x3 grid
    private final ItemStack dishware;           // optional
    private final ItemStack liquid;             // optional
    private final ItemStack result;

    public CookingPotRecipe(String group, List<ItemStack> ingredients, ItemStack dishware, ItemStack liquid, ItemStack result){
        this.group = group;
        this.ingredients = ingredients;
        this.dishware = dishware;
        this.liquid = liquid;
        this.result = result;
    }

    public String getGroup() { return group; }
    public List<ItemStack> getIngredientsStacks() { return ingredients; }
    public ItemStack getDishware() { return dishware; }
    public ItemStack getLiquid() { return liquid; }
    public ItemStack getResult() { return result; }


    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack stack : ingredients) {
            list.add(Ingredient.of(stack));
        }
        return list;
    }

    @Override
    public boolean matches(CookingPotRecipeInput input, Level level) {
        if (level.isClientSide())
            return false;

        ItemStack inputDishware = input.getDishware();
        ItemStack inputLiquid = input.getLiquid();

            // check grid ingredients
        return matchesGrid(input.getItems())
                && dishwareMatches(inputDishware)
                && liquidMatches(inputLiquid);

        }

    private boolean liquidMatches(ItemStack inputLiquid) {
        if (this.liquid.isEmpty()) return true; // recipe doesn't require liquid
        return !inputLiquid.isEmpty() && ItemStack.isSameItem(this.liquid, inputLiquid);
    }

    private boolean dishwareMatches(ItemStack inputDishware) {
        if (this.dishware.isEmpty()) return true; //recipe doesn't need it
        return !inputDishware.isEmpty() && ItemStack.isSameItem(this.dishware, inputDishware);
    }

    private boolean matchesGrid(List<ItemStack> inputItems) {
        if (inputItems.size() != ingredients.size()) return false;

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack required = ingredients.get(i);
            ItemStack input = inputItems.get(i);

            if (required.isEmpty()) continue; // skip empty slots
            if (input.isEmpty()) return false;
            if (!ItemStack.isSameItem(required, input)) return false;
            if (input.getCount() < required.getCount()) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CookingPotRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COOKING_POT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COOKING_POT_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
        public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(CookingPotRecipe::getGroup),
                        Codec.list(ItemStack.STRICT_CODEC).fieldOf("ingredients").forGetter(CookingPotRecipe::getIngredientsStacks),
                        ItemStack.STRICT_CODEC.optionalFieldOf("dishware", ItemStack.EMPTY).forGetter(CookingPotRecipe::getDishware),
                        ItemStack.STRICT_CODEC.optionalFieldOf("liquid", ItemStack.EMPTY).forGetter(CookingPotRecipe::getLiquid),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CookingPotRecipe::getResult)
                ).apply(instance, CookingPotRecipe::new)
        );

        @Override
        public MapCodec<CookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
            return null;
        }
    }
}
