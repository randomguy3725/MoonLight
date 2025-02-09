package net.optifine.config;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;

public class Matches
{
    public static boolean block(BlockStateBase blockStateBase, MatchBlock[] matchBlocks)
    {
        if (matchBlocks == null)
        {
            return true;
        }
        else
        {
            for (MatchBlock matchblock : matchBlocks) {
                if (matchblock.matches(blockStateBase)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean block(int blockId, int metadata, MatchBlock[] matchBlocks)
    {
        if (matchBlocks == null)
        {
            return true;
        }
        else
        {
            for (MatchBlock matchblock : matchBlocks) {
                if (matchblock.matches(blockId, metadata)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean blockId(int blockId, MatchBlock[] matchBlocks)
    {
        if (matchBlocks == null)
        {
            return true;
        }
        else
        {
            for (MatchBlock matchblock : matchBlocks) {
                if (matchblock.getBlockId() == blockId) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean metadata(int metadata, int[] metadatas)
    {
        if (metadatas == null)
        {
            return true;
        }
        else
        {
            for (int j : metadatas) {
                if (j == metadata) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean sprite(TextureAtlasSprite sprite, TextureAtlasSprite[] sprites)
    {
        if (sprites == null)
        {
            return true;
        }
        else
        {
            for (TextureAtlasSprite textureAtlasSprite : sprites) {
                if (textureAtlasSprite == sprite) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean biome(BiomeGenBase biome, BiomeGenBase[] biomes)
    {
        if (biomes == null)
        {
            return true;
        }
        else
        {
            for (BiomeGenBase biomeGenBase : biomes) {
                if (biomeGenBase == biome) {
                    return true;
                }
            }

            return false;
        }
    }
}
