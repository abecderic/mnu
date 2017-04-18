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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSolarFusionController extends BlockContainer
{
    protected BlockSolarFusionController()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.SOLAR_FUSION_CONTROLLER);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.SOLAR_FUSION_CONTROLLER;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking()) return false;
        if (!worldIn.isRemote)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntitySolarFusionController)
            {
                TileEntitySolarFusionController gen = (TileEntitySolarFusionController) te;
                if (!gen.isComplete())
                {
                    if (!checkForStructure(worldIn, pos, gen, playerIn))
                    {
                        return true;
                    }
                    else
                    {
                        gen.setComplete(true);
                    }
                }
                if (gen.isComplete())
                {
                    playerIn.sendMessage(gen.getEnergyString());
                    playerIn.sendMessage(gen.getTankInString());
                    playerIn.sendMessage(gen.getTankOutString());
                }
            }
        }
        return true;
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

    private boolean checkForStructure(World worldIn, BlockPos pos, TileEntitySolarFusionController gen, EntityPlayer playerIn)
    {
        for (int y = 1; y < 4; y++)
        {
            for (int x = -1; x < 2; x++)
            {
                for (int z = -1; z < 2; z++)
                {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (y == 2 && x == 0 && z == 0)
                    {
                        if (!worldIn.isAirBlock(checkPos))
                        {
                            gen.setComplete(false);
                            playerIn.sendMessage(new TextComponentTranslation("msg.solar_fusion.incomplete2", checkPos.getX(), checkPos.getY(), checkPos.getZ()));
                            return false;
                        }
                        continue;
                    }
                    if (worldIn.getBlockState(checkPos).getBlock() == MNUBlocks.solarFusionCasing)
                    {
                        TileEntity checkTE = worldIn.getTileEntity(checkPos);
                        if (checkTE != null && checkTE instanceof TileEntityNotifySlave)
                        {
                            ((TileEntityNotifySlave) checkTE).setMaster(pos);
                        }
                        else
                        {
                            gen.setComplete(false);
                            playerIn.sendMessage(new TextComponentTranslation("msg.solar_fusion.incomplete", checkPos.getX(), checkPos.getY(), checkPos.getZ()));
                            return false;
                        }
                    }
                    else
                    {
                        gen.setComplete(false);
                        playerIn.sendMessage(new TextComponentTranslation("msg.solar_fusion.incomplete", checkPos.getX(), checkPos.getY(), checkPos.getZ()));
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
