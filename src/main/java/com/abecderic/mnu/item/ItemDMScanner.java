package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.util.DarkMatterDeposits;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class ItemDMScanner extends Item
{
    public ItemDMScanner()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(1);
        setUnlocalizedName(MNUItems.DM_SCANNER);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.DM_SCANNER;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(new TextComponentTranslation("item.mnu:dm_scanner.tip").getFormattedText());
        tooltip.add(new TextComponentTranslation("item.mnu:dm_scanner.tip2").getFormattedText());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            if (!player.isSneaking())
            {
                int volume = DarkMatterDeposits.getVolumeForCoords(pos.getX(), pos.getZ());
                player.getHeldItem(hand).setItemDamage(DarkMatterDeposits.getAmountCategory(volume));
                player.sendMessage(new TextComponentTranslation("item.mnu:dm_scanner.result", DarkMatterDeposits.getEstimateText(volume)));
                return EnumActionResult.SUCCESS;
            }
            else
            {
                player.getHeldItem(hand).setItemDamage(0);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
}
