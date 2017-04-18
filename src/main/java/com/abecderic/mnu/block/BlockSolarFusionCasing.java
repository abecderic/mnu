package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSolarFusionCasing extends BlockNotifySlave
{
    protected BlockSolarFusionCasing()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.SOLAR_FUSION_CASING);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.SOLAR_FUSION_CASING;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySolarFusionCasing();
    }

    @Override
    public void tryLink(World world, BlockPos pos, BlockPos master, EntityPlayer player)
    {
        /* NO-OP */
    }
}
