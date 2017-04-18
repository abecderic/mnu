package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.BlockNotifySlave;
import com.abecderic.mnu.block.INotifyMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemWrench extends Item
{
    public ItemWrench()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(1);
        setUnlocalizedName(MNUItems.WRENCH);
        setHarvestLevel("wrench", 1);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.WRENCH;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            if (player.isSneaking())
            {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te != null && te instanceof INotifyMaster)
                {
                    NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
                    if (compound == null)
                    {
                        compound = new NBTTagCompound();
                    }
                    compound.setInteger("mx", pos.getX());
                    compound.setInteger("my", pos.getY());
                    compound.setInteger("mz", pos.getZ());
                    player.getHeldItem(hand).setTagCompound(compound);
                    player.sendMessage(new TextComponentTranslation("item.mnu:wrench.link.master"));
                }
                return EnumActionResult.SUCCESS;
            }
            else
            {
                NBTTagCompound compound = player.getHeldItem(hand).getTagCompound();
                if (compound == null || !compound.hasKey("mx") || !compound.hasKey("my") || !compound.hasKey("mz"))
                {
                    player.sendMessage(new TextComponentTranslation("item.mnu:wrench.link.nomaster"));
                    return EnumActionResult.SUCCESS;
                }
                BlockPos master = new BlockPos(compound.getInteger("mx"), compound.getInteger("my"), compound.getInteger("mz"));
                if (worldIn.getBlockState(pos).getBlock() instanceof BlockNotifySlave)
                {
                    ((BlockNotifySlave) worldIn.getBlockState(pos).getBlock()).tryLink(worldIn, pos, master, player);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
}
