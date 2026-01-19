package net.keencriptid.agriculture.block.entity;

import net.keencriptid.agriculture.Agriculture;
import net.keencriptid.agriculture.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Agriculture.MOD_ID);

    public static final Supplier<BlockEntityType<CookingPotEntity>> COOKINGPOT_BE =
            BLOCK_ENTITIES.register("cookingpot_be", () -> BlockEntityType.Builder.of(
                    CookingPotEntity::new, ModBlocks.COOKING_POT.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
