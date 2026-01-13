package net.keencriptid.agriculture.item;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.ModBlocks;
import net.keencriptid.agriculture.block.custom.NutrientSoilBlock;
import net.keencriptid.agriculture.item.custom.CompostItem;
import net.keencriptid.agriculture.item.custom.FertilizerItem;
import net.keencriptid.agriculture.item.custom.WaterStickItem;
import net.keencriptid.agriculture.item.custom.WateringCanItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Agriculture.MOD_ID);

    public static final DeferredItem<Item> CUCUMBER = ITEMS.register("cucumber",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CUCUMBER)));

    public static final DeferredItem<Item> CUCUMBER_SLICE = ITEMS.register("cucumber_slice",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CUCUMBER_SLICE)));

    public static final DeferredItem<Item> WATERING_CAN = ITEMS.register("watering_can",
            () -> new WateringCanItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> WATER_STICK = ITEMS.register("water_stick",
            () -> new WaterStickItem(new Item.Properties()));
    public static final DeferredItem<Item> PHOSPHORITE_PEBBLE = ITEMS.register("phosphorite_pebble",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> POTASH_PEBBLE = ITEMS.register("potash_pebble",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COMPOST = ITEMS.register("compost",
            () -> new CompostItem(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> FERTILIZER = ITEMS.register("fertilizer",
            () -> new FertilizerItem(1, new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> COOKING_POT = ITEMS.register("cooking_pot",
            () -> new BlockItem(ModBlocks.COOKING_POT.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> CUCUMBER_SEEDS = ITEMS.register("cucumber_seeds",
            () -> new ItemNameBlockItem(ModBlocks.CUCUMBER_CROP.get(), new Item.Properties()));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
