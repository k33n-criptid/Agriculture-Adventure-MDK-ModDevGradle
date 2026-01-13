package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.block.custom.CucumberCropBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;


public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Agriculture.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.PHOSPHORITE);
        blockWithItem(ModBlocks.POTASH);

        cookingPot();
        nutrientSoil();
        
        makeCrop(((CropBlock) ModBlocks.CUCUMBER_CROP.get()), "cucmber_crop_stage", "cucumber_crop_stage");

    }

    public void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((CucumberCropBlock) block).getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, "block/" + textureName + state.getValue(((CucumberCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    private void cookingPot(){
        getVariantBuilder(ModBlocks.COOKING_POT.get())
                .forAllStates(state -> {
                    Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

                    int yRot = switch (direction){
                        case SOUTH -> 180;
                        case WEST -> 270;
                        case EAST -> 90;
                        default -> 0;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(cookingPotModel())
                            .rotationY(yRot)
                            .build();
                });
    }

    private ModelFile cookingPotModel() {
        return new ModelFile.UncheckedModelFile(
                modLoc("block/cooking_pot")
        );
    }

    private void nutrientSoil() {
        getVariantBuilder(ModBlocks.NUTRIENT_SOIL_BLOCK.get())
                .forAllStates(state -> {
                    int moisture = state.getValue(FarmBlock.MOISTURE);
                    boolean isWet = moisture > 0;
                    ModelFile modelFile = isWet ? nutrientSoilWetModel() : nutrientSoilDryModel();

                    return ConfiguredModel.builder()
                            .modelFile(modelFile)
                            .build();
                });

        simpleBlockItem(ModBlocks.NUTRIENT_SOIL_BLOCK.get(), nutrientSoilDryModel());

    }

    private ModelFile nutrientSoilDryModel() {
        return new ModelFile.UncheckedModelFile(
                modLoc("block/nutrient_soil_dry")
        );
    }

    private ModelFile nutrientSoilWetModel() {
        return new ModelFile.UncheckedModelFile(
                modLoc("block/nutrient_soil_wet")
        );
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

}
