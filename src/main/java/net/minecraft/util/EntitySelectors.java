package net.minecraft.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class EntitySelectors
{
    public static final Predicate<Entity> selectAnything = Entity::isEntityAlive;
    public static final Predicate<Entity> IS_STANDALONE = p_apply_1_ -> p_apply_1_.isEntityAlive() && p_apply_1_.riddenByEntity == null && p_apply_1_.ridingEntity == null;
    public static final Predicate<Entity> selectInventories = p_apply_1_ -> p_apply_1_ instanceof IInventory && p_apply_1_.isEntityAlive();
    public static final Predicate<Entity> NOT_SPECTATING = p_apply_1_ -> !(p_apply_1_ instanceof EntityPlayer) || !((EntityPlayer) p_apply_1_).isSpectator();

    public static class ArmoredMob implements Predicate<Entity>
    {
        private final ItemStack armor;

        public ArmoredMob(ItemStack armor)
        {
            this.armor = armor;
        }

        public boolean apply(Entity p_apply_1_)
        {
            if (!p_apply_1_.isEntityAlive())
            {
                return false;
            }
            else if (!(p_apply_1_ instanceof EntityLivingBase entitylivingbase))
            {
                return false;
            }
            else
            {
                return entitylivingbase.getEquipmentInSlot(EntityLiving.getArmorPosition(this.armor)) == null && (entitylivingbase instanceof EntityLiving ? ((EntityLiving) entitylivingbase).canPickUpLoot() : (entitylivingbase instanceof EntityArmorStand || entitylivingbase instanceof EntityPlayer));
            }
        }
    }
}
