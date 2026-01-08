package net.keencriptid.agriculture.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties CUCUMBER = new FoodProperties.Builder().nutrition(1).saturationModifier(0.25f)
            .fast().effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 100), 0.35f).build();

    public static final FoodProperties CUCUMBER_SLICE = new FoodProperties.Builder().nutrition(1).saturationModifier(0.25f)
            .fast().effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 100), 0.35f).build();

}


