package net.keencriptid.agriculture.item.custom;

import net.keencriptid.agriculture.block.ModBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;

public class CompostItem extends Item {
    private static final Map<Block, Block> COMPOST_MAP =
            Map.of(
                    Blocks.FARMLAND, ModBlocks.NUTRIENT_SOIL_BLOCK.get()
            );

    public CompostItem(Properties properties){
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(context.getClickedPos()).getBlock();
        BlockPos pos = context.getClickedPos();
        if (COMPOST_MAP.containsKey(clickedBlock)){
            if (!level.isClientSide()) {
                    level.setBlockAndUpdate(context.getClickedPos(), COMPOST_MAP.get(clickedBlock).defaultBlockState());

                    context.getItemInHand().shrink(1);

                    level.playSound(null, context.getClickedPos(), SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS);
                ParticleUtils.spawnParticleInBlock(level, pos, 3, ParticleTypes.COMPOSTER);
                }
            }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.agriculture.compost.shift_down"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.agriculture.compost"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
