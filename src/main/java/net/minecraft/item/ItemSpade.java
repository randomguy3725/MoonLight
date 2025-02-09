package net.minecraft.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Set;

public class ItemSpade extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand);

    public ItemSpade(Item.ToolMaterial material)
    {
        super(1.0F, material, EFFECTIVE_ON);
    }

    public boolean canHarvestBlock(Block blockIn)
    {
        return blockIn == Blocks.snow_layer || blockIn == Blocks.snow;
    }
}
