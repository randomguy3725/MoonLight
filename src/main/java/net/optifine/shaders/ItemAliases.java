package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ItemAliases
{
    private static int[] itemAliases = null;
    private static boolean updateOnResourcesReloaded;
    private static final int NO_ALIAS = Integer.MIN_VALUE;

    public static int getItemAliasId(int itemId)
    {
        if (itemAliases == null)
        {
            return itemId;
        }
        else if (itemId >= 0 && itemId < itemAliases.length)
        {
            int i = itemAliases[itemId];
            return i == Integer.MIN_VALUE ? itemId : i;
        }
        else
        {
            return itemId;
        }
    }

    public static void resourcesReloaded()
    {
        if (updateOnResourcesReloaded)
        {
            updateOnResourcesReloaded = false;
            update(Shaders.getShaderPack());
        }
    }

    public static void update(IShaderPack shaderPack)
    {
        reset();

        if (shaderPack != null)
        {
            if (Reflector.Loader_getActiveModList.exists() && Config.getResourceManager() == null)
            {
                Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
                updateOnResourcesReloaded = true;
            }
            else
            {
                IntList list = new IntArrayList();
                String s = "/shaders/item.properties";
                InputStream inputstream = shaderPack.getResourceAsStream(s);

                if (inputstream != null)
                {
                    loadItemAliases(inputstream, s, list);
                }

                loadModItemAliases(list);

                if (!list.isEmpty())
                {
                    itemAliases = list.toIntArray();
                }
            }
        }
    }

    private static void loadModItemAliases(IntList listItemAliases)
    {
        String[] astring = ReflectorForge.getForgeModIds();

        for (String s : astring) {
            try {
                ResourceLocation resourcelocation = new ResourceLocation(s, "shaders/item.properties");
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadItemAliases(inputstream, resourcelocation.toString(), listItemAliases);
            } catch (IOException var6) {
            }
        }
    }

    private static void loadItemAliases(InputStream in, String path, IntList listItemAliases)
    {
        if (in != null)
        {
            try
            {
                in = MacroProcessor.process(in, path);
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("[Shaders] Parsing item mappings: " + path);
                ConnectedParser connectedparser = new ConnectedParser("Shaders");

                for (Object o : properties.keySet())
                {
                    String s = (String) o;
                    String s1 = properties.getProperty(s);
                    String s2 = "item.";

                    if (!s.startsWith(s2))
                    {
                        Config.warn("[Shaders] Invalid item ID: " + s);
                    }
                    else
                    {
                        String s3 = StrUtils.removePrefix(s, s2);
                        int i = Config.parseInt(s3, -1);

                        if (i < 0)
                        {
                            Config.warn("[Shaders] Invalid item alias ID: " + i);
                        }
                        else
                        {
                            int[] aint = connectedparser.parseItems(s1);

                            if (aint != null && aint.length >= 1)
                            {
                                for (int k : aint) {
                                    addToList(listItemAliases, k, i);
                                }
                            }
                            else
                            {
                                Config.warn("[Shaders] Invalid item ID mapping: " + s + "=" + s1);
                            }
                        }
                    }
                }
            }
            catch (IOException var15)
            {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }

    private static void addToList(IntList list, int index, int val)
    {
        while (list.size() <= index)
        {
            list.add(Integer.MIN_VALUE);
        }

        list.set(index, val);
    }

    public static void reset()
    {
        itemAliases = null;
    }
}
