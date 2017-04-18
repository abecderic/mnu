package com.abecderic.mnu.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class BlockNotifySlave extends BlockContainer
{
    protected BlockNotifySlave(Material material)
    {
        super(material);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);
        notifyBlockRemoved(worldIn, pos);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        super.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
        notifyBlockRemoved(worldIn, pos);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockDestroyedByPlayer(worldIn, pos, state);
        notifyBlockRemoved(worldIn, pos);
    }

    protected void notifyBlockRemoved(World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityNotifySlave)
        {
            BlockPos master = ((TileEntityNotifySlave) te).getMaster();
            if (master != null)
            {
                TileEntity teMaster = world.getTileEntity(master);
                if (teMaster != null && teMaster instanceof INotifyMaster)
                {
                    ((INotifyMaster) teMaster).removeBlock(pos);
                }
            }
        }
    }

    public abstract void tryLink(World world, BlockPos pos, BlockPos master, EntityPlayer player);
}
