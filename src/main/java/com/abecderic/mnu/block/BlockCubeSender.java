package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class BlockCubeSender extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool REDSTONE = PropertyBool.create("redstone");

    protected BlockCubeSender()
    {
        super(Material.IRON);
        setUnlocalizedName(MNUBlocks.CUBE_SENDER);
        setHardness(2.2f);
        setResistance(5.0f);
        setCreativeTab(MNU.TAB);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + MNU.MODID + ":" + MNUBlocks.CUBE_SENDER;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, REDSTONE);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facingIn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, facingIn);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getFront(meta & 0x07);
        boolean redstone = (meta & 0x08) != 0;
        return this.getDefaultState().withProperty(FACING, facing).withProperty(REDSTONE, redstone);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumFacing facing = state.getValue(FACING);
        return facing.getIndex() + (state.getValue(REDSTONE) ? 8 : 0);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack item = playerIn.getHeldItem(hand);
            if (item.isEmpty() && playerIn.isSneaking())
            {
                item = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            }
            NonNullList<ItemStack> itemWrench = OreDictionary.getOres("itemWrench");
            if (OreDictionary.containsMatch(false, itemWrench, item))
            {
                if (playerIn.isSneaking())
                {
                    facing = facing.getOpposite();
                }
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(FACING, facing));
                return true;
            }
            else if (hand == EnumHand.MAIN_HAND)
            {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof TileEntityCubeSender)
                {
                    TileEntityCubeSender cubeSender = (TileEntityCubeSender) te;
                    playerIn.sendMessage(cubeSender.getEnergyString());
                }
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCubeSender();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
}
