package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.optifine.Log;

public class FieldLocatorTypes implements IFieldLocator
{
    private Field field = null;

    public FieldLocatorTypes(Class cls, Class[] preTypes, Class type, Class[] postTypes, String errorName)
    {
        Field[] afield = cls.getDeclaredFields();
        List<Class> list = new ArrayList<>();

        for (Field field : afield) {
            list.add(field.getType());
        }

        List<Class> list1 = new ArrayList<>(Arrays.asList(preTypes));
        list1.add(type);
        list1.addAll(Arrays.asList(postTypes));
        int l = Collections.indexOfSubList(list, list1);

        if (l < 0)
        {
            Log.log("(Reflector) Field not found: " + errorName);
        }
        else
        {
            int j = Collections.indexOfSubList(list.subList(l + 1, list.size()), list1);

            if (j >= 0)
            {
                Log.log("(Reflector) More than one match found for field: " + errorName);
            }
            else
            {
                int k = l + preTypes.length;
                this.field = afield[k];
            }
        }
    }

    public Field getField()
    {
        return this.field;
    }
}
