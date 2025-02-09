package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals
{
    Predicate<Entity> mobSelector = p_apply_1_ -> p_apply_1_ instanceof IMob;
    Predicate<Entity> VISIBLE_MOB_SELECTOR = p_apply_1_ -> p_apply_1_ instanceof IMob && !p_apply_1_.isInvisible();
}
