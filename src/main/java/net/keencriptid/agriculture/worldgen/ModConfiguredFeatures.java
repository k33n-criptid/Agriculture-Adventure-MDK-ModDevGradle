package net.keencriptid.agriculture.worldgen;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_POTASH_KEY = registerKey("potash");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_PHOSPHORITE_KEY = registerKey("phosphorite");

    public static final ResourceKey<ConfiguredFeature<?, ?>> CUCUMBER_WILDCROP_KEY = registerKey("cucumber_wildcrop");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest graniteReplaceables = new BlockMatchTest(Blocks.GRANITE);

        List<OreConfiguration.TargetBlockState> overworldPhosporiteOre = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.PHOSPHORITE.get().defaultBlockState()));
        register(context, OVERWORLD_PHOSPHORITE_KEY, Feature.ORE, new OreConfiguration(overworldPhosporiteOre, 12));
        register(context, OVERWORLD_POTASH_KEY, Feature.ORE, new OreConfiguration(graniteReplaceables,
                ModBlocks.POTASH.get().defaultBlockState(), 14));

        register(context, CUCUMBER_WILDCROP_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(
                                BlockStateProvider.simple(ModBlocks.CUCUMBER_WILDCROP.get())
                        ), List.of(Blocks.GRASS_BLOCK)
                )
        );
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

}
