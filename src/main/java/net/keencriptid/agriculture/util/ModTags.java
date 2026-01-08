package net.keencriptid.agriculture.util;

import net.keencriptid.agriculture.Agriculture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks{
        public static final TagKey<Block> TOOL_BLOCKS = createTag("tool_blocks");
        public static final TagKey<Block> AGRI_CROPS = createTag("agri_crops");


        private static TagKey<Block> createTag(String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, name));
        }

    }

    public static class Items {
        public static final TagKey<Item> FOOD_ITEMS = createTag("food_items");

        private static TagKey<Item> createTag(String name){
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Agriculture.MOD_ID, name));
        }
    }

}
