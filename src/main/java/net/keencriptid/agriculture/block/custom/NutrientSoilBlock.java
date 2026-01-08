package net.keencriptid.agriculture.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class NutrientSoilBlock extends FarmBlock {
    private static final int DRY_CHANCE = 10;
    public static final int MAX_DRY_TICKS = 20;
    public static final IntegerProperty DRY_TICKS = IntegerProperty.create("dry_ticks", 0, MAX_DRY_TICKS);

    public NutrientSoilBlock(Properties properties){
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MOISTURE, 0)
                .setValue(DRY_TICKS, 0));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);

        if (level.isRainingAt(pos.above())){
            if (moisture < MAX_MOISTURE) {
                level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE).setValue(DRY_TICKS, 0), 2);
            }
        }
        if (moisture > 0 && random.nextInt(DRY_CHANCE) == 0){
            level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
        }
        if (moisture == 0){
            int current = state.getValue(DRY_TICKS) + 1;
            int dryTicks = Math.min(current + 1, MAX_DRY_TICKS);
            if (dryTicks >= 5){
                BlockPos above = pos.above();
                BlockState cropState = level.getBlockState(above);
                if (cropState.getBlock() instanceof CropBlock){
                    level.destroyBlock(above, true);
                }
            }
            if (dryTicks != current) {
                level.setBlock(pos, state.setValue(DRY_TICKS, dryTicks), 2);
            }
        }
        else {
            if (state.getValue(DRY_TICKS) != 0){
                level.setBlock(pos, state.setValue(DRY_TICKS, 0), 2);
            }
        }

    }

    public static void water(Level level, BlockPos pos, BlockState state){
        if (level.isClientSide) return;

        level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE).setValue(DRY_TICKS, 0), 2);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide){
            randomTick(state, level, pos, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DRY_TICKS);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {

        if (!level.isClientSide){
            float chance = fallDistance - 0.5f;
            if (level.getRandom().nextFloat() < chance){
                turnToDirt(entity, state, level, pos);
            }

        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

}