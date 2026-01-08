package net.keencriptid.agriculture.datagen;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Agriculture.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.POTASH_PEBBLE.get());
        basicItem(ModItems.PHOSPHORITE_PEBBLE.get());
        basicItem(ModItems.COMPOST.get());
        basicItem(ModItems.FERTILIZER.get());
        basicItem(ModItems.CUCUMBER.get());
        basicItem(ModItems.CUCUMBER_SLICE.get());

        withExistingParent(
                ModBlocks.COOKING_POT.getId().getPath(),
                modLoc("block/cooking_pot")
        );
    }
}
