package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockOldLog extends BlockLog
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, p_apply_1_ -> p_apply_1_.getMetadata() < 4);

    public BlockOldLog()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    public MapColor getMapColor(IBlockState state)
    {
        BlockPlanks.EnumType blockplanks$enumtype = state.getValue(VARIANT);

        return switch (state.getValue(LOG_AXIS)) {
            default -> switch (blockplanks$enumtype) {
                default -> BlockPlanks.EnumType.SPRUCE.getMapColor();
                case SPRUCE -> BlockPlanks.EnumType.DARK_OAK.getMapColor();
                case BIRCH -> MapColor.quartzColor;
                case JUNGLE -> BlockPlanks.EnumType.SPRUCE.getMapColor();
            };
            case Y -> blockplanks$enumtype.getMapColor();
        };
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.OAK.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
        list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
    }

    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) % 4));

        iblockstate = switch (meta & 12) {
            case 0 -> iblockstate.withProperty(LOG_AXIS, EnumAxis.Y);
            case 4 -> iblockstate.withProperty(LOG_AXIS, EnumAxis.X);
            case 8 -> iblockstate.withProperty(LOG_AXIS, EnumAxis.Z);
            default -> iblockstate.withProperty(LOG_AXIS, EnumAxis.NONE);
        };

        return iblockstate;
    }

    @SuppressWarnings("incomplete-switch")
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        switch (state.getValue(LOG_AXIS))
        {
            case X:
                i |= 4;
                break;

            case Z:
                i |= 8;
                break;

            case NONE:
                i |= 12;
        }

        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, VARIANT, LOG_AXIS);
    }

    protected ItemStack createStackedBlock(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
    }

    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }
}
