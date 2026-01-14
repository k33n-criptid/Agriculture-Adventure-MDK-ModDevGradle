package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

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
    }
}
