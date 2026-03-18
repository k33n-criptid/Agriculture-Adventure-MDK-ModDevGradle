package net.keencriptid.agriculture.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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

    private final String group;
    private final List<Ingredient> ingredients;  // 2x3 grid
    private final Ingredient dishware;           // optional
    private final Ingredient liquid;             // optional
    private final ItemStack result;

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
