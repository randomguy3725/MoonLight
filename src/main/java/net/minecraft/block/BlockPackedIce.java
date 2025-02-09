package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;

public class BlockPackedIce extends Block
{
    public BlockPackedIce()
    {
        super(Material.packedIce);
        this.slipperiness = 0.98F;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }
}
