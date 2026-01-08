package net.keencriptid.agriculture.item.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;


public class WaterStickItem extends Item {

    public WaterStickItem(Properties properties) {
        super(properties.durability(250));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        FluidState aboveFluidState = context.getLevel().getFluidState(pos.above());

        if (!level.isClientSide) {
            if (fluidState.is(FluidTags.WATER) || aboveFluidState.is(FluidTags.WATER)) {

                context.getItemInHand().hurtAndBreak(10, ((ServerLevel) level), context.getPlayer(),
                        item -> context.getPlayer().onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
                if (context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(Component.literal("You've given this item water damage"), true);
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}







