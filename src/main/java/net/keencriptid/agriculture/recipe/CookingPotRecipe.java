package net.keencriptid.agriculture.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class CookingPotRecipe implements Recipe<CookingPotRecipeInput> {
    // read in a json file
    // inputItem & output ==> read from json file
    // CookingPotRecipeInput ==> Inventory of block entity

    public String group;
    public List<Ingredient> ingredients;  // 2x3 grid
    public Ingredient dishware;           // optional
    public Ingredient liquid;             // optional
    public ItemStack result;

    public CookingPotRecipe(String group, List<Ingredient> ingredients, Ingredient dishware, Ingredient liquid, ItemStack result){
        this.group = group;
        this.ingredients = ingredients;
        this.dishware = dishware;
        this.liquid = liquid;
        this.result = result;
    }

    public String getGroup() { return group; }
    public List<Ingredient> getIngredientsStacks() { return ingredients; }
    public Ingredient getDishware() { return dishware; }
    public Ingredient getLiquid() { return liquid; }
    public ItemStack getResult() { return result; }


    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (Ingredient ingredient : ingredients) {
            list.add(ingredient);
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
        return liquid.isEmpty() || liquid.test(inputLiquid);
    }

    private boolean dishwareMatches(ItemStack inputDishware) {
        return dishware.isEmpty() || dishware.test(inputDishware);
    }

    private boolean matchesGrid(List<ItemStack> inputItems) {
        List<Ingredient> remaining = new ArrayList<>(ingredients);

        for (ItemStack stack : inputItems) {
            if (stack.isEmpty()) continue;

            boolean matched = false;

            for (Ingredient ingredient : remaining) {
                if (ingredient.test(stack)) {
                    remaining.remove(ingredient);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return remaining.isEmpty();
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

        public void toJson(JsonObject json, CookingPotRecipe recipe) {
            json.addProperty("type", "agriculture:cooking_pot");
            JsonArray ingredientsArray = new JsonArray();
            for (Ingredient ing : recipe.getIngredients()) {
                ingredientsArray.add(ingredientToJson(ing));
            }
            json.add("ingredients", ingredientsArray);
            json.add("dishware", ingredientToJson(recipe.getDishware()));
            json.add("liquid", ingredientToJson(recipe.getLiquid()));

            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("id", BuiltInRegistries.ITEM.getKey(recipe.getResult().getItem()).toString());
            resultJson.addProperty("count", recipe.getResult().getCount());
            json.add("result", resultJson);

            if (recipe.getGroup() != null && !recipe.getGroup().isEmpty()) {
                json.addProperty("group", recipe.getGroup());
            }
        }

        private JsonObject ingredientToJson(Ingredient ing) {
            JsonObject obj = new JsonObject();
            if (ing.getItems().length > 0) {
                obj.addProperty("item", BuiltInRegistries.ITEM.getKey(ing.getItems()[0].getItem()).toString());
            }
            return obj;
        }

        public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(CookingPotRecipe::getGroup),
                        Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(CookingPotRecipe::getIngredients),
                        Ingredient.CODEC.optionalFieldOf("dishware", Ingredient.EMPTY).forGetter(CookingPotRecipe::getDishware),
                        Ingredient.CODEC.optionalFieldOf("liquid", Ingredient.EMPTY).forGetter(CookingPotRecipe::getLiquid),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CookingPotRecipe::getResult)
                ).apply(instance, CookingPotRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CookingPotRecipe::getGroup,

                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                CookingPotRecipe::getIngredients,

                Ingredient.CONTENTS_STREAM_CODEC,
                CookingPotRecipe::getDishware,

                Ingredient.CONTENTS_STREAM_CODEC,
                CookingPotRecipe::getLiquid,

                ItemStack.STREAM_CODEC,
                CookingPotRecipe::getResult,

                CookingPotRecipe::new
        );

        @Override
        public MapCodec<CookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
