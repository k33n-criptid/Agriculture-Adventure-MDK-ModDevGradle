package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.item.ModItems;
import net.keencriptid.agriculture.recipe.CookingPotRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;


import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FERTILIZER.get(), 3)
                .pattern("   ")
                .pattern("ADP")
                .pattern("ADP")
                .define('D', Blocks.DIRT)
                .define('P', ModItems.PHOSPHORITE_PEBBLE.get())
                .define('A', ModItems.POTASH_PEBBLE.get())
                .unlockedBy("has_potash_pebble", has(ModItems.POTASH_PEBBLE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CUCUMBER_SLICE.get(), 4)
                .requires(ModItems.CUCUMBER.get())
                .unlockedBy("has_cucumber", has(ModItems.CUCUMBER.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WATERING_CAN.get(), 1)
                .pattern("C C")
                .pattern("CBC")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .define('B', Items.BUCKET)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COOKING_POT.get().asItem(), 1)
                .pattern("  O")
                .pattern("I I")
                .pattern("SSS")
                .define('I', Items.IRON_INGOT)
                .define('O', Blocks.OAK_PLANKS)
                .define('S', Blocks.STONE_SLAB)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);


        SpecialRecipeBuilder.special(craftingBookCategory -> new CookingPotRecipe(
                "stews", List.of(
                Ingredient.of(Items.COOKED_RABBIT),
                Ingredient.of(Items.BROWN_MUSHROOM),
                Ingredient.of(Items.BAKED_POTATO),
                Ingredient.of(Items.CARROT)
        ),
                Ingredient.of(Items.BOWL),
                Ingredient.of(Items.WATER_BUCKET),
                new ItemStack(Items.RABBIT_STEW)
        )).save(recipeOutput, ResourceLocation.fromNamespaceAndPath("agriculture", "rabbit_stew"));

        SpecialRecipeBuilder.special(craftingBookCategory -> new CookingPotRecipe(
                "soups", List.of(
                Ingredient.of(Items.RED_MUSHROOM),
                Ingredient.of(Items.BROWN_MUSHROOM),
                Ingredient.of(ModItems.CUCUMBER_SLICE.get()),
                Ingredient.of(Items.CARROT)
        ),
                Ingredient.of(Items.BOWL),
                Ingredient.EMPTY,
                new ItemStack(Items.SUSPICIOUS_STEW)
        )).save(recipeOutput, ResourceLocation.fromNamespaceAndPath("agriculture", "veggie_soup"));
    }

}
