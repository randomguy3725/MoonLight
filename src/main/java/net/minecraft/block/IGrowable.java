package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public interface IGrowable
{
    boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient);

    boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state);

    void grow(World worldIn, Random rand, BlockPos pos, IBlockState state);
}
