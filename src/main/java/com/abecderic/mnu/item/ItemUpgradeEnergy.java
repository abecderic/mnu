package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class ItemUpgradeEnergy extends Item
{
    public ItemUpgradeEnergy()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(1);
        setUnlocalizedName(MNUItems.UPGRADE_ENERGY);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.UPGRADE_ENERGY;
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
        tooltip.add(new TextComponentTranslation("item.mnu:upgrade_energy.tip").getFormattedText());
        tooltip.add(new TextComponentTranslation("item.mnu:upgrade_energy.tip2").getFormattedText());
    }
}
