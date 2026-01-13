package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.block.custom.CucumberCropBlock;
import net.keencriptid.agriculture.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    protected LootTable.Builder createCropLoot(Block cropBlock, Item cropItem, Item seedItem, float minCrop, float maxCrop, int maxAge, float minSeeds, float maxSeeds){
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootItemCondition.Builder matureCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(cropBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CucumberCropBlock.AGE, maxAge));

        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(cropItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minCrop, maxCrop)))
                                        .apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
                                        .when(matureCondition))
                )
                .withPool(
                        LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(seedItem)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minSeeds, maxSeeds))))
                );
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.COOKING_POT.get());
        dropSelf(ModBlocks.NUTRIENT_SOIL_BLOCK.get());

        add(ModBlocks.PHOSPHORITE.get(),
                block -> createMultipleOreDrops(ModBlocks.PHOSPHORITE.get(), ModItems.PHOSPHORITE_PEBBLE.get(), 3, 8));
        add(ModBlocks.POTASH.get(),
                block -> createMultipleOreDrops(ModBlocks.POTASH.get(), ModItems.POTASH_PEBBLE.get(), 3, 8));

        this.add(ModBlocks.CUCUMBER_CROP.get(),
                createCropLoot(ModBlocks.CUCUMBER_CROP.get(), ModItems.CUCUMBER.get(), ModItems.CUCUMBER_SEEDS.get(), 1f, 3f, 7, 2, 4));

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
