package net.keencriptid.agriculture.block;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.custom.CookingPotBlock;
import net.keencriptid.agriculture.block.custom.CucumberCropBlock;
import net.keencriptid.agriculture.block.custom.NutrientSoilBlock;
import net.keencriptid.agriculture.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Agriculture.MOD_ID);

    public static final DeferredBlock<Block> COOKING_POT = BLOCKS.register("cooking_pot",
    () -> new CookingPotBlock(BlockBehaviour.Properties.of()
            .noOcclusion().strength(0.5f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> NUTRIENT_SOIL_BLOCK = registerBlock("nutrient_soil",
            () -> new NutrientSoilBlock(BlockBehaviour.Properties.of().sound(SoundType.ROOTED_DIRT).noOcclusion().strength(0.6f).randomTicks()));

    public static final DeferredBlock<Block> POTASH = registerBlock("potash",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> PHOSPHORITE = registerBlock("phosphorite",
            () -> new DropExperienceBlock(UniformInt.of(2, 4),
                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> CUCUMBER_CROP = BLOCKS.register("cucumber_crop",
            () -> new CucumberCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    public static final DeferredBlock<Block> CUCUMBER_WILDCROP = registerBlock("cucumber_wildcrop",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.0f).instabreak().noOcclusion().noCollission().sound(SoundType.GRASS)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }




    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
