package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Agriculture.MOD_ID);
    }

    @Override
    protected void start() {

    }
}

//for methods:
//this.add("rotten_flesh_to_pig",
//               new AddItemModifier(new LootItemCondition[] {
//               new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("entities/pig")).build()
//               }, Items.ROTTEN_FLESH));

// this.add("radish_seeds_to_short_grass",
//                new AddItemModifier(new LootItemCondition[] {
//                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
//                        LootItemRandomChanceCondition.randomChance(0.25f).build() }, ModItems.RADISH_SEEDS.get()));

//        this.add("radish_seeds_to_tall_grass",
//                new AddItemModifier(new LootItemCondition[] {
//                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
//                        LootItemRandomChanceCondition.randomChance(0.25f).build() }, ModItems.RADISH_SEEDS.get()));
//
//        this.add("chisel_from_jungle_temple",
//                new AddItemModifier(new LootItemCondition[] {
//                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/jungle_temple")).build()
//                }, ModItems.CHISEL.get()));
