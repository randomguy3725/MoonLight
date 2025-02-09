package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class FrameEvent
{
    private static final Map<String, Integer> mapEventFrames = new HashMap<>();

    public static boolean isActive(String name, int frameInterval)
    {
        synchronized (mapEventFrames)
        {
            int i = Minecraft.getMinecraft().entityRenderer.frameCount;
            Integer integer = mapEventFrames.computeIfAbsent(name, k -> i);

            int j = integer;

            if (i > j && i < j + frameInterval)
            {
                return false;
            }
            else
            {
                mapEventFrames.put(name, i);
                return true;
            }
        }
    }
}
