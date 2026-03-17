package net.keencriptid.agriculture.recipe;

import net.keencriptid.agriculture.Agriculture;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Agriculture.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Agriculture.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CookingPotRecipe>> COOKING_POT_SERIALIZER =
            SERIALIZERS.register("cooking_pot", CookingPotRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<Recipe<CookingPotRecipeInput>>> COOKING_POT_TYPE =
            TYPES.register("cooking_pot", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "cooking_pot";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
