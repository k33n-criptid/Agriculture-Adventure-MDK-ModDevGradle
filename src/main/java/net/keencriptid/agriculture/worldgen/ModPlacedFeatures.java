package net.keencriptid.agriculture.worldgen;

import net.keencriptid.agriculture.Agriculture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> OVERWORLD_PHOSPHORITE_KEY = registerKey("phosphorite_placed");
    public static final ResourceKey<PlacedFeature> OVERWORLD_POTASH_KEY = registerKey("potash_placed");
    public static final ResourceKey<PlacedFeature> CUCUMBER_WILDCROP_PLACED_KEY = registerKey("cucumber_wildcrop_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, OVERWORLD_PHOSPHORITE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_PHOSPHORITE_KEY),
                ModOrePlacement.commonOrePlacement(8, HeightRangePlacement.uniform(VerticalAnchor.absolute(-15), VerticalAnchor.absolute(156))));

        register(context, OVERWORLD_POTASH_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_POTASH_KEY),
                ModOrePlacement.commonOrePlacement(10, HeightRangePlacement.uniform(VerticalAnchor.absolute(-10), VerticalAnchor.absolute(150))));

        register(context, CUCUMBER_WILDCROP_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CUCUMBER_WILDCROP_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
