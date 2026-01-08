package net.keencriptid.agriculture.item.custom;

import net.keencriptid.agriculture.util.ModTags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;


public class FertilizerItem extends BoneMealItem {

    private final int radius;

    public FertilizerItem(int radius, Properties properties) {
        super(properties);
        this.radius = radius;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        boolean grown = false;

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, 0, -radius),
                center.offset(radius, 0, radius)
        )) {
            if (isValidBlock(level, center)){
                continue;
            }

            if (level.isEmptyBlock(pos)) {
                continue;
            }

            for (int i = 0; i < 8; i++) {
                if (!BoneMealItem.applyBonemeal(stack, level, pos, player)) {
                    break;
                }
                grown = true;
            }
        }
        if (grown) {
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.levelEvent(1505, center, 0);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private boolean isValidBlock(Level level, BlockPos center) {
        return level.getBlockState(center).is(ModTags.Blocks.AGRI_CROPS);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.agriculture.fertilizer.shift_down"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.agriculture.fertilizer"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
