package net.minecraft.block.properties;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

public class PropertyInteger extends PropertyHelper<Integer>
{
    private final IntSet allowedValues;

    protected PropertyInteger(String name, int min, int max)
    {
        super(name, Integer.class);

        if (min < 0)
        {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        else if (max <= min)
        {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        else
        {
            IntSet set = new IntOpenHashSet(max - min + 1);

            for (int i = min; i <= max; ++i)
            {
                set.add(i);
            }

            this.allowedValues = IntSets.unmodifiable(set);
        }
    }

    public IntCollection getAllowedValues()
    {
        return this.allowedValues;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            if (!super.equals(p_equals_1_))
            {
                return false;
            }
            else
            {
                PropertyInteger propertyinteger = (PropertyInteger)p_equals_1_;
                return this.allowedValues.equals(propertyinteger.allowedValues);
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int i = super.hashCode();
        i = 31 * i + this.allowedValues.hashCode();
        return i;
    }

    public static PropertyInteger create(String name, int min, int max)
    {
        return new PropertyInteger(name, min, max);
    }

    public String getName(Integer value)
    {
        return value.toString();
    }
}
