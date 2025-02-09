package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.src.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias
{
    private final int blockAliasId;
    private final MatchBlock[] matchBlocks;

    public BlockAlias(int blockAliasId, MatchBlock[] matchBlocks)
    {
        this.blockAliasId = blockAliasId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockAliasId()
    {
        return this.blockAliasId;
    }

    public boolean matches(int id, int metadata)
    {
        for (MatchBlock matchblock : this.matchBlocks) {
            if (matchblock.matches(id, metadata)) {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds()
    {
        IntSet set = new IntOpenHashSet(this.matchBlocks.length);

        for (MatchBlock matchblock : this.matchBlocks) {
            int j = matchblock.getBlockId();
            set.add(j);
        }

        return set.toIntArray();
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId)
    {
        List<MatchBlock> list = new ArrayList<>();

        for (MatchBlock matchblock : this.matchBlocks) {
            if (matchblock.getBlockId() == matchBlockId) {
                list.add(matchblock);
            }
        }

        MatchBlock[] amatchblock = list.toArray(new MatchBlock[0]);
        return amatchblock;
    }

    public String toString()
    {
        return "block." + this.blockAliasId + "=" + Config.arrayToString(this.matchBlocks);
    }
}
