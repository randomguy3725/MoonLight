package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item
{
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass != 1)
        {
            return super.getColorFromItemStack(stack, renderPass);
        }
        else
        {
            NBTBase nbtbase = getExplosionTag(stack, "Colors");

            if (!(nbtbase instanceof NBTTagIntArray nbttagintarray))
            {
                return 9079434;
            }
            else
            {
                int[] aint = nbttagintarray.getIntArray();

                if (aint.length == 1)
                {
                    return aint[0];
                }
                else
                {
                    int i = 0;
                    int j = 0;
                    int k = 0;

                    for (int l : aint)
                    {
                        i += (l & 16711680) >> 16;
                        j += (l & 65280) >> 8;
                        k += (l & 255) >> 0;
                    }

                    i = i / aint.length;
                    j = j / aint.length;
                    k = k / aint.length;
                    return i << 16 | j << 8 | k;
                }
            }
        }
    }

    public static NBTBase getExplosionTag(ItemStack stack, String key)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                return nbttagcompound.getTag(key);
            }
        }

        return null;
    }

    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");

            if (nbttagcompound != null)
            {
                addExplosionInfo(nbttagcompound, tooltip);
            }
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip)
    {
        byte b0 = nbt.getByte("Type");

        if (b0 >= 0 && b0 <= 4)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + b0).trim());
        }
        else
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
        }

        int[] aint = nbt.getIntArray("Colors");

        if (aint.length > 0)
        {
            boolean flag = true;
            StringBuilder s = new StringBuilder();

            for (int i : aint)
            {
                if (!flag)
                {
                    s.append(", ");
                }

                flag = false;
                boolean flag1 = false;

                for (int j = 0; j < ItemDye.dyeColors.length; ++j)
                {
                    if (i == ItemDye.dyeColors[j])
                    {
                        flag1 = true;
                        s.append(StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName()));
                        break;
                    }
                }

                if (!flag1)
                {
                    s.append(StatCollector.translateToLocal("item.fireworksCharge.customColor"));
                }
            }

            tooltip.add(s.toString());
        }

        int[] aint1 = nbt.getIntArray("FadeColors");

        if (aint1.length > 0)
        {
            boolean flag2 = true;
            StringBuilder s1 = new StringBuilder(StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ");

            for (int l : aint1)
            {
                if (!flag2)
                {
                    s1.append(", ");
                }

                flag2 = false;
                boolean flag5 = false;

                for (int k = 0; k < 16; ++k)
                {
                    if (l == ItemDye.dyeColors[k])
                    {
                        flag5 = true;
                        s1.append(StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k).getUnlocalizedName()));
                        break;
                    }
                }

                if (!flag5)
                {
                    s1.append(StatCollector.translateToLocal("item.fireworksCharge.customColor"));
                }
            }

            tooltip.add(s1.toString());
        }

        boolean flag3 = nbt.getBoolean("Trail");

        if (flag3)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
        }

        boolean flag4 = nbt.getBoolean("Flicker");

        if (flag4)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}
