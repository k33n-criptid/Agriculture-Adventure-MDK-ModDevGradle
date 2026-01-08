package net.keencriptid.agriculture.item.custom;

import net.keencriptid.agriculture.block.custom.NutrientSoilBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;


import java.util.List;

public class WateringCanItem extends Item {

    private static final int MAX_WATER = 50;

    public WateringCanItem(Properties properties) {
        super(properties.durability(MAX_WATER).stacksTo(1).component(DataComponents.DAMAGE, 50).component(DataComponents.MAX_DAMAGE, 50)); // max durability = max water
    }

    private int getWater(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    private void consumeWater(ItemStack stack, int amount) {
        int newDamage = Math.min(stack.getDamageValue() + amount, stack.getMaxDamage());
        stack.setDamageValue(newDamage);
    }

    private void refillWater(ItemStack stack) {
        stack.setDamageValue(0); // full water = zero damage
    }

    public boolean isFull(ItemStack stack){
        return getWater(stack) >= MAX_WATER;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        FluidState fluidState = context.getLevel().getFluidState(pos);
        FluidState aboveFluidState = context.getLevel().getFluidState(pos.above());

        if (!level.isClientSide) {
            if (!isFull(stack)) {

                // --- Refill if clicked on water or water cauldron ---
                if (fluidState.is(FluidTags.WATER) || aboveFluidState.is(FluidTags.WATER)) {
                    if (getWater(stack) < MAX_WATER) {
                        refillWater(stack);
                        if (context.getPlayer() != null) {
                            context.getPlayer().displayClientMessage(Component.literal("Watering can refilled!"), true);
                            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                        }
                    }
                }
                if (clickedState.is(Blocks.WATER_CAULDRON)) {
                    refillWater(stack);

                    if (clickedState.is(Blocks.WATER_CAULDRON)) {
                        int cauldronLevel = clickedState.getValue(BlockStateProperties.LEVEL_CAULDRON);
                        if (cauldronLevel > 1) {
                            level.setBlock(pos, clickedState.setValue(BlockStateProperties.LEVEL_CAULDRON, cauldronLevel - 1), 3);
                        } else {
                            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                        }
                    }

                    if (context.getPlayer() != null) {
                        context.getPlayer().displayClientMessage(Component.literal("Watering can refilled!"), true);
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

            // --- Water farmland in 3x3 area ---
            int radius = 1;
            boolean wateredAny = false;

            for (BlockPos loopPos : BlockPos.betweenClosed(
                    pos.offset(-radius, 0, -radius),
                    pos.offset(radius, 0, radius)
            )) {
                BlockState state = level.getBlockState(loopPos);

                if (state.getBlock() instanceof NutrientSoilBlock) {

                    if (getWater(stack) <= 0) break; // stop if empty

                    NutrientSoilBlock.water(level, loopPos, state);
                    wateredAny = true;

                    consumeWater(stack, 1); // consume 1 water per block
                }
            }

            if (wateredAny) {
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Water: " + getWater(stack) + " / " + MAX_WATER));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.ceil(13.0 * getWater(stack) / MAX_WATER);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00BFFF;
    }
}
