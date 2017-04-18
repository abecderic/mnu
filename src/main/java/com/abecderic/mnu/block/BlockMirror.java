package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMirror extends BlockNotifySlave
{
    protected BlockMirror()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.MIRROR);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
        setTickRandomly(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.MIRROR;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityMirror();
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.randomTick(worldIn, pos, state, random);
        if (check(worldIn, pos))
        {
            notifyBlockRemoved(worldIn, pos);
        }
    }

    private boolean check(World world, BlockPos pos)
    {
        return world.canBlockSeeSky(pos);
    }
}
