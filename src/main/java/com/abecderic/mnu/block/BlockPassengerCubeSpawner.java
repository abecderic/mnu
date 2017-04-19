package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockPassengerCubeSpawner extends BlockContainer
{
    protected BlockPassengerCubeSpawner()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.PASSENGER_CUBE_SPAWNER);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.PASSENGER_CUBE_SPAWNER;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking()) return false;
        if (!worldIn.isRemote)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityPassengerCubeSpawner)
            {
                TileEntityPassengerCubeSpawner gen = (TileEntityPassengerCubeSpawner) te;
                playerIn.sendMessage(gen.getEnergyString());
            }
        }
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPassengerCubeSpawner();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
}
