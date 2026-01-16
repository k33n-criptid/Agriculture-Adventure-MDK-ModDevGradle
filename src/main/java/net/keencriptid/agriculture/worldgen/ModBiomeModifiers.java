package net.keencriptid.agriculture.worldgen;

import net.keencriptid.agriculture.Agriculture;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_PHOSPHORITE = registerKey("add_phosphorite");
    public static final ResourceKey<BiomeModifier> ADD_POTASH = registerKey("add_potash");
    public static final ResourceKey<BiomeModifier> ADD_CUCUMBER_WILDCROP = registerKey("add_cucumber_wildcrop");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_PHOSPHORITE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_PHOSPHORITE_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_POTASH, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_POTASH_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_CUCUMBER_WILDCROP, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS), biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CUCUMBER_WILDCROP_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        //For individual biomes:
        //context.register(ADD_POTASH, new BiomeModifiers.AddFeaturesBiomeModifier(
        //      HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SAVANNA)),
        //      HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.OVERWORLD_POTASH_KEY)),
        //      GenerationStep.Decoration.UNDERGROUND_ORES))

    }



    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, name));
    }
}
