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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;

public class BlockFluidCreative extends BlockContainer
{
    public BlockFluidCreative()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.FLUID_CREATIVE);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.FLUID_CREATIVE;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        boolean success = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        if (!worldIn.isRemote)
        {
            if (!playerIn.getHeldItem(hand).isEmpty())
            {
                IFluidHandlerItem handler = playerIn.getHeldItem(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (handler != null)
                {
                    FluidStack stack = handler.drain(1000, false);
                    if (stack != null)
                    {
                        TileEntity te = worldIn.getTileEntity(pos);
                        if (te != null && te instanceof TileEntityFluidCreative)
                        {
                            ((TileEntityFluidCreative) te).setFluid(stack.getFluid());
                            return true;
                        }
                    }
                }
            }
        }
        return success;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityFluidCreative();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
}
