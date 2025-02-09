package net.minecraft.client.renderer.block.statemap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.Map;

public interface IStateMapper
{
    Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn);
}
