package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockReactorCasing extends BlockNotifySlave
{
    public static final PropertyBool TRANSPARENT = PropertyBool.create("transparent");

    protected BlockReactorCasing()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.REACTOR_CASING);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.REACTOR_CASING;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityReactorCasing();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TRANSPARENT);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facingIn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(TRANSPARENT, false);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TRANSPARENT, meta > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TRANSPARENT) ? 1 : 0;
    }

    @Override
    public void tryLink(World world, BlockPos pos, BlockPos master, EntityPlayer player)
    {
        /* NO-OP */
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !state.getValue(TRANSPARENT);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !blockState.getValue(TRANSPARENT) || blockAccess.getBlockState(pos.offset(side)).getBlock() != MNUBlocks.reactorCasing || !blockAccess.getBlockState(pos.offset(side)).getValue(TRANSPARENT);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        super.getSubBlocks(itemIn, tab, list);
        list.add(new ItemStack(itemIn, 1, 1));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}
