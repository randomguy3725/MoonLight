package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.settings.KeyBinding;

public class KeyUtils
{
    public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio)
    {
        IntSet set = new IntOpenHashSet(keysPrio.length);

        for (KeyBinding keybinding : keysPrio) {
            set.add(keybinding.getKeyCode());
        }

        Set<KeyBinding> set1 = new HashSet<>(Arrays.asList(keys));
        for (KeyBinding keyBinding : keysPrio) {
            set1.remove(keyBinding);
        }

        for (KeyBinding keybinding1 : set1) {
            int integer = keybinding1.getKeyCode();
            if (set.contains(integer)) {
                keybinding1.setKeyCode(0);
            }
        }
    }
}
