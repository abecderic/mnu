package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

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
                ItemStack item = playerIn.getHeldItem(hand);
                if (!item.isEmpty())
                {
                    NonNullList<ItemStack> itemWrench = OreDictionary.getOres("itemWrench");
                    if (item.getItem().getHarvestLevel(item, "wrench", null, null) > 0 || OreDictionary.containsMatch(false, itemWrench, item))
                    {
                        gen.reset();
                        playerIn.sendMessage(new TextComponentTranslation("msg.passenger_cube.reset"));
                        return true;
                    }
                }
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
