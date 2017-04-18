package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockReactorController extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockReactorController()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.REACTOR_CONTROLLER);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.REACTOR_CONTROLLER;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking()) return false;
        if (!worldIn.isRemote)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityReactorController)
            {
                TileEntityReactorController reactor = (TileEntityReactorController) te;
                
            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facingIn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        EnumFacing facing;
        if (placer == null)
        {
            facing = EnumFacing.NORTH;
        }
        else
        {
            facing = placer.getHorizontalFacing();
        }
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumFacing facing = state.getValue(FACING);
        return facing.getHorizontalIndex();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySolarFusionController();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
}